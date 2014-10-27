package org.openiam.idm.srvc.auth.spi;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.util.SpringContextProvider;

public abstract class AbstractSMSOTPModule {

	public AbstractSMSOTPModule() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public final String generateSMSToken(final Phone phone, final String userId) throws BasicDataServiceException {
		validate(phone, userId);
		return sendSMSAndReturnToken(phone, userId);
	}
	
	protected abstract void validate(final Phone phone, final String userId) throws BasicDataServiceException;
	protected abstract String sendSMSAndReturnToken(final Phone phone, final String userId) throws BasicDataServiceException;
}
