package org.openiam.idm.srvc.auth.spi;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

/* 
 *  This class is a prototype, however, it is autowired int he abstract class.
 *  The private variables are PER-THREAD!!!
 */
public class GoogleAuthTOTPModule extends AbstractTOTPModule {
	
	private static final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
	
	public GoogleAuthTOTPModule() {
		super();
	}
	

	@Override
	public boolean validateToken(final String secret, final int code) {
		return googleAuthenticator.authorize(secret, code);
	}



	@Override
	public String generateSecret(Phone phone, LoginEntity login) {
		final GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
		return key.getKey();
	}
}
