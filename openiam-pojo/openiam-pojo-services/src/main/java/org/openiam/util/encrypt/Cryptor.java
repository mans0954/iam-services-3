package org.openiam.util.encrypt;



import org.openiam.exception.EncryptionException;

/**
 * Highlevel Interface for all classes providing access to encryption algorithms. 
 * @author Suneet Shah
 *
 */
public interface Cryptor {

	public abstract String encrypt(byte[] key,String input)  throws EncryptionException;

	public abstract byte[] encryptTobyte(byte[] key, String input) throws EncryptionException;

	public abstract String decrypt(byte[] key,String input)  throws EncryptionException;

}