/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openiam.util.encrypt
;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;


/**
 * SHA1Hash provides hashing capabilty using the SHA1
 * @author Suneet Shah
 *
 */
public class SHA1Hash implements HashDigest, InitializingBean {
	
	private byte[] key = null;
	
	@Value("${org.openiam.ms.key.location}")
	private String keyLocation;

	@Override
	public void afterPropertiesSet() throws Exception {
		readKey();
	}
	
	 public void readKey() throws Exception {
		String filename = "cayo.dat";
		BufferedInputStream stream =  new BufferedInputStream(new FileInputStream(keyLocation + "/" + filename));
		int len = stream.available();
		key = new byte[len];
		stream.read(key, 0,len);
		stream.close();
	}	

	public byte[] hash(String msg) {
		//get instance of the SHA Message Digest object.
		HMac hmac = new HMac(new SHA1Digest());
		byte[] result = new byte[hmac.getMacSize()];
		byte[] msgAry =  msg.getBytes() ;
		KeyParameter kp = new KeyParameter( key  );			
		hmac.init(kp);
		hmac.update(msgAry,0, msgAry.length);
		hmac.doFinal(result, 0);
		return result;
	}
	
	public String HexEncodedHash(String msg) {
       return encodeBase64String(hash(msg));
	}
 }



