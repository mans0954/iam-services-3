package org.openiam.idm.srvc.auth.spi;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.encrypt.OneTimePasswordAlgorithm;

public abstract class AbstractSMSOTPModule {
	
	 private static final Log log = LogFactory.getLog(AbstractSMSOTPModule.class);

	public AbstractSMSOTPModule() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public final String generateSMSToken(final Phone phone, final LoginEntity login) throws BasicDataServiceException, InvalidKeyException, NoSuchAlgorithmException {
		validate(phone, login);
		final String token = generateRFC4226Token(login);
		final String text = getText(phone, login, token);
		sendSMS(phone, login, text);
		return token;
	}
	
	public final String generateRFC4226Token(final LoginEntity login) throws InvalidKeyException, NoSuchAlgorithmException {
		return OneTimePasswordAlgorithm.generateOTP(DigestUtils.sha512(login.getId()), login.getSmsCodeExpiration().getTime(), 8, true, 4);
	}
	
	protected abstract void validate(final Phone phone, final LoginEntity login) throws BasicDataServiceException;
	protected abstract void sendSMS(final Phone phone, final LoginEntity login, final String text) throws BasicDataServiceException;
	protected abstract String getText(final Phone phone, final LoginEntity login, final String token);
}
