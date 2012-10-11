package org.openiam.core.key.manager;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.KeySpec;
import java.util.Random;

/**
 * Created by: Alexander Duckardt
 * Date: 11.10.12
 */
public class KeyManager {
    private static final String PRIVATE_KEY_ALGORITHM = BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId();
    private static final String KEYSTORE_TYPE = "JCEKS";    //
    private static final String KEYSTORE_ALIAS = "openiam";
    private static final int KEY_SIZE_IN_BITS = 192; //24 bytes
    private static final int MIN_INERATION_COUNT = 20; //24 bytes
    private int inerationCount = 0;
    private String keyStoreName;

    public KeyManager(String keyStoreName, int inerationCount){
        this.inerationCount=inerationCount;
        this.keyStoreName = keyStoreName;

        if (Security.getProvider("BC") == null){
            Security.addProvider(new BouncyCastleProvider());
        }else{
            System.out.println("BC is installed.");
        }
    }
    public KeyManager(String keyStoreName){
        this(keyStoreName, MIN_INERATION_COUNT);
    }

    private KeyStore getKeyStore(char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        // clean keyStore instance
        keyStore.load(null, password);
        File ksFile = new File(keyStoreName);
        if(!ksFile.exists()){
            // create keystore file
            keyStore.store(new FileOutputStream(ksFile), password);
        }
        System.out.println("Using keystore-file : " + keyStoreName);
        keyStore.load(new FileInputStream(ksFile), password);
        return keyStore;
    }

    private byte[] getNewPrivateKey() throws Exception {
        byte[] privateKey = new byte[KEY_SIZE_IN_BITS/8];

        SecureRandom random = new SecureRandom();
        random.nextBytes(privateKey);
        return privateKey;
    }

    private byte[] encryptPrivateKey(char[] password, byte[] privateKey) throws Exception{
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[KEY_SIZE_IN_BITS/8];
        random.nextBytes(salt);

        // Create PBE parameter set
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, inerationCount);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PRIVATE_KEY_ALGORITHM);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher pbeCipher = Cipher.getInstance(PRIVATE_KEY_ALGORITHM);

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        // Encrypt the encoded Private Key with the PBE key
        byte[] ciphertext = pbeCipher.doFinal(privateKey);

        // Now construct  PKCS #8 EncryptedPrivateKeyInfo object
        AlgorithmParameters algparms = AlgorithmParameters.getInstance(PRIVATE_KEY_ALGORITHM, "BC");
        algparms.init(pbeParamSpec);
        EncryptedPrivateKeyInfo encinfo = new EncryptedPrivateKeyInfo(algparms, ciphertext);

        // DER encoded PKCS#8 encrypted key
        return encinfo.getEncoded();
    }

    private byte[] decryptKey(char[] password, byte[] encryptedKey)throws Exception{
        // this is a encoded PKCS#8 encrypted private key
        EncryptedPrivateKeyInfo ePKInfo = new EncryptedPrivateKeyInfo(encryptedKey);

        // first we have to read algorithm name and parameters (salt, iterations) used
        // to encrypt the file
        Cipher cipher = Cipher.getInstance(ePKInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory skFac = SecretKeyFactory.getInstance(ePKInfo
                .getAlgName());
        Key pbeKey = skFac.generateSecret(pbeKeySpec);

        // Extract the iteration count and the salt
        AlgorithmParameters algParams = ePKInfo.getAlgParameters();
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);

        System.out.println("ePKInfo.getEncoded():       " + new String(ePKInfo.getEncoded(), "UTF-8"));
        System.out.println("ePKInfo.getEncryptedData(): " + new String(ePKInfo.getEncryptedData(), "UTF-8"));

        return cipher.doFinal(ePKInfo.getEncryptedData());
    }

    public void generateKey(char[] password) throws Exception {
        KeyStore ks = getKeyStore(password);
        byte[] rawKey = getNewPrivateKey();
        System.out.println("RAW KEY: " + new String(rawKey, "UTF-8"));
        byte[] key =  encryptPrivateKey(password, rawKey);
        System.out.println("ENCRYPTED KEY: " + new String(key, "UTF-8"));

        ks.setEntry(KEYSTORE_ALIAS, new KeyStore.SecretKeyEntry(new SecretKeySpec(key,PRIVATE_KEY_ALGORITHM)), new KeyStore.PasswordProtection(password));
        ks.store(new FileOutputStream(keyStoreName), password);

    }

    public byte[] getPrimaryKeyFromJKS(char[] password)throws Exception{
        KeyStore ks = getKeyStore(password);

        KeyStore.SecretKeyEntry secretKey= (KeyStore.SecretKeyEntry)ks.getEntry(KEYSTORE_ALIAS, new KeyStore.PasswordProtection(password));

        byte[] encryptedKey = secretKey.getSecretKey().getEncoded();
        System.out.println("ENCRYPTED KEY FROM JSK: " + new String(encryptedKey, "UTF-8"));
        byte[] rawKey = decryptKey(password,encryptedKey);
        System.out.println("RAW KEY FROM JSK: " + new String(rawKey, "UTF-8"));
        return rawKey;
    }
}
