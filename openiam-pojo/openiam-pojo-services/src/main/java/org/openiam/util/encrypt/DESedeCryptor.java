/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openiam.util.encrypt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.util.encoders.*;
import org.openiam.exception.EncryptionException;


/**
 * DESedeCryptor provides 3DES Encryption
 * @author Suneet Shah
 * 
 */
public class DESedeCryptor implements Cryptor {

	private BufferedBlockCipher cipher = null;
	
	private static final Log log = LogFactory.getLog(DESedeCryptor.class);
	
	public String encrypt(byte[] key,String input) throws EncryptionException {

		KeyParameter kp = new KeyParameter(key);
		cipher = new PaddedBufferedBlockCipher(	new CBCBlockCipher(new DESedeEngine()));
		cipher.init(true, kp);
		
		byte[] inputByteAry = input.getBytes();
		byte[] result = new byte[cipher.getOutputSize(inputByteAry.length)];
		int len = cipher.processBytes(inputByteAry, 0, inputByteAry.length, result, 0);

		try {
		 len += cipher.doFinal(result, len);
		}catch (Exception e) {
			log.error(e.getMessage());
			throw new EncryptionException(e);
		}
		
		String encValue = new String(Hex.encode(result, 0, len));
		return encValue;
	
	}
	
	public byte[] encryptTobyte(byte[] key, String input) {
		KeyParameter kp = new KeyParameter(key);
		cipher = new PaddedBufferedBlockCipher(	new CBCBlockCipher(new DESedeEngine()));
		cipher.init(true, kp);
		
		byte[] inputByteAry = input.getBytes();
		byte[] result = new byte[cipher.getOutputSize(inputByteAry.length)];
		int len = cipher.processBytes(inputByteAry, 0, inputByteAry.length, result, 0);

		try {
		 len += cipher.doFinal(result, len);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String decrypt(byte[] key, String input) throws EncryptionException {
		byte[] result = null;
		byte[] inputByteAry = null;
		int len = 0;
	
		KeyParameter kp = new KeyParameter(key);
		cipher = new PaddedBufferedBlockCipher(	new CBCBlockCipher(new DESedeEngine()));
		cipher.init(false, kp);
		try {
			inputByteAry =  Hex.decode(input);
			result = new byte[cipher.getOutputSize(inputByteAry.length)];
	        len = cipher.processBytes(inputByteAry, 0, inputByteAry.length, result, 0);
        	len += cipher.doFinal(result,len);        	
        }catch(Exception e) {
			log.error(e.getMessage());
			throw new EncryptionException(e);
        }      
 		return new String(result,0,len);
	}
	

	

}
