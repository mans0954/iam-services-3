package org.openiam.connector.common.scim;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;





/**
 * Some useful commands for public/private keys' generation.
 * 
 * # generate a 2048-bit RSA private key $ openssl genrsa -out private_key.pem
 * 2048
 * 
 * # convert private Key to PKCS#8 format (so Java can read it) $ openssl pkcs8
 * -topk8 -inform PEM -outform DER -in private_key.pem \ -out private_key.der
 * -nocrypt
 * 
 * # output public key portion in DER format (so Java can read it) $ openssl rsa
 * -in private_key.pem -pubout -outform DER -out public_key.der
 * 
 * 
 */
public class TestRSA {

	public static String encrypt(S token) throws Exception {
		return encrypt(token, getKeyBytes("C:/data/openiam/conf/public_key.der"));

	}

	public static String encrypt(S token, byte[] publicKey) throws Exception {
		String algorithm = "RSA";
        javax.crypto.Cipher cipher
                = javax.crypto.Cipher.getInstance(algorithm);

        java.security.spec.X509EncodedKeySpec keySpec
                = new java.security.spec.X509EncodedKeySpec(publicKey);

        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE,
                java.security.KeyFactory.getInstance(
                        algorithm).generatePublic(keySpec));

        java.io.ByteArrayOutputStream tokenByteStream
                = new java.io.ByteArrayOutputStream();

        java.io.ObjectOutputStream tokenObjectStream
                = new java.io.ObjectOutputStream(tokenByteStream);
        tokenObjectStream.writeObject(token);
        tokenObjectStream.close();
        // System.out.println("Byte array length before encryption: " + tokenByteStream.toByteArray().length);
        java.io.ByteArrayOutputStream encryptedTokenStream
                = new java.io.ByteArrayOutputStream();
        javax.crypto.CipherOutputStream cipherOutStream
                = new javax.crypto.CipherOutputStream(
                        encryptedTokenStream, cipher);
        cipherOutStream.write(tokenByteStream.toByteArray());
        cipherOutStream.close();

        return javax.xml.bind.DatatypeConverter
                .printBase64Binary(encryptedTokenStream.toByteArray());
	}

	private static byte[] getKeyBytes(String key) throws Exception {

		File keyFile = new File(key);
		FileInputStream fis = new FileInputStream(keyFile);
		byte[] keyBytes = new byte[(int) keyFile.length()];
		fis.read(keyBytes);
		fis.close();
		
		// java.io.InputStream is = ClassLoader.getSystemResourceAsStream(key);
		// byte[] keyBytes = new byte[is.available()];
		// System.out.println("Bytes="+keyBytes);
		// is.read(keyBytes);
		// is.close();
		return keyBytes;

	}
}
