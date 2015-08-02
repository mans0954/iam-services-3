package org.openiam.util.an;

/**
 * Created by Vitaly on 8/2/2015.
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * Akzo Nobel Encryption
 */
public class ANEncryption {
    private int KEY_SIZE = 256/8;
    private String ALGORITHM = "AES";
    private String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public ANEncryption() {
        System.out.println("================ an_extAttribute13.groovy Encryption construct. ");
    }

    public String encrypt(String plainText, byte[] password, byte[] iv) throws Exception {
        System.out.println("================ an_extAttribute13.groovy encrypt: " + plainText);
        ANPasswordDeriveBytes pdb = new ANPasswordDeriveBytes(password, null);
        byte[] secretBytes = pdb.getBytes(KEY_SIZE);
        System.out.println("================ an_extAttribute13.groovy secretBytes");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretBytes, ALGORITHM), new IvParameterSpec(iv));
        byte[] cipherTextBytes = cipher.doFinal(plainText.getBytes());
        System.out.println("================ an_extAttribute13.groovy cipherTextBytes");
        return encodeBase64String(cipherTextBytes);
    }

    public String decrypt(String cipherText, byte[] password, byte[] iv) throws Exception {
        ANPasswordDeriveBytes pdb = new ANPasswordDeriveBytes(password, null);
        byte[] secretBytes = pdb.getBytes(KEY_SIZE);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretBytes, ALGORITHM), new IvParameterSpec(iv));
        byte[] cipherTextBytes = decodeBase64(cipherText);
        byte[] plainTextBytes = cipher.doFinal(cipherTextBytes);
        return new String(plainTextBytes);
    }
}
