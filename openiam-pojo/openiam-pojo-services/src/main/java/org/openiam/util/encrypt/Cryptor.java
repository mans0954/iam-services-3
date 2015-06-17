package org.openiam.util.encrypt;



import org.openiam.exception.EncryptionException;

/**
 * Highlevel Interface for all classes providing access to encryption algorithms. 
 * @author Suneet Shah
 *
 */
public interface Cryptor {

	String encrypt(byte[] key, String input)  throws EncryptionException;

	byte[] encryptTobyte(byte[] key, String input) throws EncryptionException;

	String decrypt(byte[] key, String input)  throws EncryptionException;

}