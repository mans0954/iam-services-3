/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.auth.sso;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Module to create and manage the default token structure used by OpenIAM
 * @author suneet
 *
 */
public class DefaultTokenModule implements SSOTokenModule {

	static protected ResourceBundle res = ResourceBundle.getBundle("securityconf");
	private static final Log log = LogFactory.getLog(DefaultTokenModule.class);
	
	protected Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;
	protected int tokenLife;

    static final int MIN_AS_MILLIS = 60000;
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.sso.SSOToken#createToken(java.util.Map)
	 */
	public SSOToken createToken(Map tokenParam) throws Exception {
		long curTime = System.currentTimeMillis();
		long expTime = getExpirationTime(curTime);
		String token = null;
		
		log.info("Cryptor in DefaultTokenModule=" + cryptor);
		
		StringBuffer buf = new StringBuffer();
		String expirationTime = String.valueOf(expTime);
		buf.append((String)tokenParam.get("USER_ID"));
		// add separator between user id and time component
		buf.append(":");
		buf.append( expirationTime  );
		
		try {
			token = cryptor.encrypt(keyManagementService.getUserKey((String)tokenParam.get("USER_ID"), KeyName.token.name()), buf.toString());
		}catch(EncryptionException encExcep) {
			return null;
		}
		
		SSOToken ssoToken = new SSOToken(new Date(curTime), new Date(expTime), token, AuthenticationConstants.OPENIAM_TOKEN  );
		
		return  ssoToken;

	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.sso.SSOToken#isTokenValid(java.lang.String, java.lang.String)
	 */
	public boolean isTokenValid(String userId,String principal, String token) throws Exception {
		String decUserId;		// decrypted userid
		String decTime;			// decrypted time

		String decString = null;
        long curTime = System.currentTimeMillis();

		try {
			
			log.debug("Token=" + token);
			log.debug("cryptor =" + cryptor);
			
			decString = cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.token.name()),token);
		}catch(EncryptionException encExcep) {
			return false;
		}
		log.debug("Parsing token" );
		
		// tokenize this string
		StringTokenizer tokenizer = new StringTokenizer(decString,":");
		if (tokenizer.hasMoreTokens()) {
			decUserId =  tokenizer.nextToken();
		}else {
			return false;
		}
		
		log.debug("- userId = " + decUserId );
		
		if (tokenizer.hasMoreTokens()) {
			decTime =  tokenizer.nextToken();
		}else  {
			return false;
		}
		

		
		if (!decUserId.equalsIgnoreCase(userId))
			return false;
		
		long ldecTime = Long.parseLong( decTime );

        log.debug("- Time found in Token => " + ldecTime );
        log.debug("- Time found in Token as date => " + new Date(ldecTime) );

        log.debug("- Token life in millis => " + getIdleTime());

        // decTime + idleTime = validTime for Token
       // long tokenValidTime = ldecTime + getIdleTime();

        log.debug("Valid token time=" + ldecTime + " curtime = " + curTime);
        log.debug("Token is valid till = " + new Date(ldecTime));
        log.debug("Current time=" + new Date(curTime));
        log.debug("Diff between token and curTime = " + (ldecTime - curTime));

		if ( curTime > ldecTime ) {
			//current time is greater then the allowed idle time
			
			log.debug("Token Failed time check"  );
			return false;
		}
		return true;
	}

    public String getDecryptedToken(String userId, String token) throws Exception{
        try {

            return cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.token.name()), token);
        }catch(EncryptionException encExcep) {
            return null;
        }
    }

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.sso.SSOToken#refreshToken(java.lang.String, java.lang.String)
	 */
	public SSOToken refreshToken(Map tokenParam) throws Exception{
		return createToken(tokenParam);
		
	}

    protected long getIdleTime(){
        return (tokenLife * 60 * 1000);
    }

	/**
	 * Determines when the token will expire
	 * @return
	 */
	protected long getExpirationTime(long curTime) {
		long idleTime = 0l;
		
		//String strIdleTime =  this.tokenLife;;
		//int idleItem = Integer.parseInt(strIdleTime.trim());
		
		idleTime = curTime + ( 1000 * 60 * tokenLife);
		
		return idleTime;
	}



	public void setCryptor(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	public void setTokenLife(int tokenLife) {
		this.tokenLife = tokenLife;
	}

    @Override
    public void setKeyManagementService(KeyManagementService keyManagementService) {
        this.keyManagementService=keyManagementService;
    }
}
