package org.openiam.util.encrypt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.openiam.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alexander on 07/12/15.
 */
public class RijndaelCryptor implements Cryptor {
    private static String TRANSFORMATION = "Rijndael/CBC/PKCS7Padding";
    private static String ALGORITHM = "Rijndael";

    private static final Log log = LogFactory.getLog(RijndaelCryptor.class);
    private PasswordDeriveBytes generator = new PasswordDeriveBytes(new SHA1Digest());

    private Cipher getChipper(SecretKeySpec password, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(TRANSFORMATION);
    }

    private SecretKeySpec getPassword(byte[] passBytes){
        PasswordDeriveBytes generator = new PasswordDeriveBytes(new SHA1Digest());
        generator.init(passBytes, null, 100);

        byte[]  key= ((KeyParameter) generator.generateDerivedParameters(256)).getKey();
        return new SecretKeySpec(key, ALGORITHM);
    }

    @Override
    public String encrypt(byte[] key, String input) throws EncryptionException {
        return encrypt(key, null, input);
    }

    @Override
    public byte[] encryptTobyte(byte[] key, String input) throws EncryptionException {
        return new byte[0];
    }

    public String encrypt(byte[] key, byte[] iv, String input) throws EncryptionException {
        generator.init(key, null, 100);
        byte[]  passKey= ((KeyParameter) generator.generateDerivedParameters(256)).getKey();

        final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine()));
        if(iv!=null){
            cipher.init(true, new ParametersWithIV(new KeyParameter(passKey), iv));
        } else {
            cipher.init(true, new KeyParameter(passKey));
        }



        byte[] inputBytes = input.getBytes();

        final byte[] output = new byte[cipher.getOutputSize(inputBytes.length)];
        int len = cipher.processBytes(inputBytes, 0, inputBytes.length, output, 0);

        try {
            len += cipher.doFinal(output, len);
        }catch (Exception e) {
            throw new EncryptionException(e);
        }

        return new String(Base64.encode(output));
    }

    @Override
    public String decrypt(byte[] key, String input) throws EncryptionException {
        return null;
    }

    public String decrypt(byte[] key, byte[] iv, String input) throws EncryptionException {
        generator.init(key, null, 100);
        byte[]  passKey= ((KeyParameter) generator.generateDerivedParameters(256)).getKey();

        final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine()));
        if(iv!=null){
            cipher.init(false, new ParametersWithIV(new KeyParameter(passKey), iv));
        } else {
            cipher.init(false, new KeyParameter(passKey));
        }


        byte[] decodedValue = Base64.decode(input.getBytes());

        byte[] output = new byte[cipher.getOutputSize(decodedValue.length)];
        try {
            int len = cipher.processBytes(decodedValue, 0, decodedValue.length, output, 0);
            len += cipher.doFinal(output,len);
        } catch (InvalidCipherTextException e) {
            log.error(e.getMessage(), e);
            throw new EncryptionException(e);
        }
        return new String(output);
    }
}
