package org.openiam.idm.srvc.auth.spi;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.util.SpringContextProvider;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public abstract class AbstractTOTPModule {

	protected static final Log log = LogFactory.getLog(AbstractTOTPModule.class);
	
	public AbstractTOTPModule() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public abstract String generateSecret(final Phone phone, final LoginEntity login);
	public abstract boolean validateToken(final String secret, final int code);
}
