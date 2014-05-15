package org.openiam.idm.srvc.key.service;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;

/**
 * Created by: Alexander Duckardt
 * Date: 11.10.12
 */
public class JksManager {
    public static final String KEYSTORE_FILE_NAME = ".openiam.jks";
    public static final String KEYSTORE_DEFAULT_PASSWORD = "openiamKeyStorePassword";
    public static final String KEYSTORE_DEFAULT_LOCATION = System.getProperty("user.home")+System.getProperty("file.separator");
    public static final String KEYSTORE_ALIAS = "openiam";
    public static final String TMP_KEYSTORE_ALIAS = "openiam_tmp";
    public static final String KEYSTORE_COOKIE_ALIAS = "openiam_cookie";
    public static final String KEYSTORE_COMMON_ALIAS = "openiam_common";

    public static final int MIN_ITERATION_COUNT = 20;


    private  static final String PRIVATE_KEY_ALGORITHM = BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId();
    private static final String KEYSTORE_TYPE = "JCEKS";    //
    private static final int KEY_SIZE_IN_BITS = 192; //24 bytes
    private int iterationCount = 0;
    private String keyStoreName;

    public JksManager(){
        if (Security.getProvider("BC") == null){
            System.out.println("Installing BC provider...");
            Security.addProvider(new BouncyCastleProvider());
        }else{
            System.out.println("BC is installed.");
        }
    }

    public JksManager(String keyStoreName, int iterationCount){
        this();
        this.iterationCount = iterationCount;
        this.keyStoreName = keyStoreName;
    }
    public JksManager(String keyStoreName){
        this(keyStoreName, MIN_ITERATION_COUNT);
    }

    public void generateMasterKey(char[] password, char[] pkPassword) throws Exception {
        generatePrimaryKey(password, pkPassword, KEYSTORE_ALIAS);
    }

    public void generatePrimaryKey(char[] password, char[] pkPassword, String alias) throws Exception {
        KeyStore ks = getKeyStore(password);
        byte[] rawKey = getNewPrivateKey();
//        System.out.println("RAW KEY: " + encodeKey(rawKey));
        byte[] key =  encryptPrivateKey(pkPassword, rawKey);
//        System.out.println("ENCRYPTED KEY: " + encodeKey(key));
        //        // check if there is already key
        //        KeyStore.Entry entry = ks.getEntry(KEYSTORE_ALIAS, new KeyStore.PasswordProtection(password));
        //        if(entry!=null){
        //            // entry is exists, store it. it is necessary to decode ald security data
        //            ks.setEntry(TMP_KEYSTORE_ALIAS, entry, new KeyStore.PasswordProtection(password));
        //        }
        // add new key to jks
        ks.setEntry(alias, new KeyStore.SecretKeyEntry(new SecretKeySpec(key,PRIVATE_KEY_ALGORITHM)), new KeyStore.PasswordProtection(password));
        ks.store(new FileOutputStream(keyStoreName), password);
    }

    public void deletePrimaryKey(String alias, char[] password) throws Exception{
        KeyStore ks = getKeyStore(password);
        ks.deleteEntry(alias);
        ks.store(new FileOutputStream(keyStoreName), password);
    }

    public byte[] getPrimaryKeyFromJKS(char[] password, char[] pkPassword)throws Exception{
        return getPrimaryKeyFromJKS(KEYSTORE_ALIAS,  password,  pkPassword);
    }
    public byte[] getPrimaryKeyFromJKS(String alias, char[] password, char[] pkPassword)throws Exception{
        KeyStore ks = getKeyStore(password);
        KeyStore.SecretKeyEntry secretKey= (KeyStore.SecretKeyEntry)ks.getEntry(alias, new KeyStore.PasswordProtection(password));
        if(secretKey==null)
            return null;
        byte[] encryptedKey = secretKey.getSecretKey().getEncoded();
//        System.out.println("ENCRYPTED KEY FROM JSK: " + encodeKey(encryptedKey));
        byte[] rawKey = decryptKey(pkPassword,encryptedKey);
//        System.out.println("RAW KEY FROM JSK: " + encodeKey(rawKey));
        return rawKey;
    }

    private KeyStore getKeyStore(char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        // clean keyStore instance
        keyStore.load(null, password);
        File ksFile = new File(keyStoreName);
        if(!ksFile.exists()){
            ksFile.createNewFile();
            // create keystore file
            keyStore.store(new FileOutputStream(ksFile), password);
        }
//        System.out.println("Using keystore-file : " + keyStoreName);
        keyStore.load(new FileInputStream(ksFile), password);
        return keyStore;
    }

    public byte[] getNewPrivateKey() throws Exception {
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
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PRIVATE_KEY_ALGORITHM,"BC");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher pbeCipher = Cipher.getInstance(PRIVATE_KEY_ALGORITHM,"BC");

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

//        System.out.println("ePKInfo.getEncoded():       " + encodeKey(ePKInfo.getEncoded()));
//        System.out.println("ePKInfo.getEncryptedData(): " + encodeKey(ePKInfo.getEncryptedData()));

        return cipher.doFinal(ePKInfo.getEncryptedData());
    }



    public String encodeKey(byte[] data) {
        return new String(Hex.encode(data));
    }
    public byte[] decodeKey(String data) {
        return Hex.decode(data);
    }

}
