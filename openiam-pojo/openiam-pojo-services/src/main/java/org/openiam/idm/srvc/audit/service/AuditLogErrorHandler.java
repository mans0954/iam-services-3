package org.openiam.idm.srvc.audit.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component("auditLogErrorHandler")
public class AuditLogErrorHandler implements ErrorHandler {

	private static Logger LOG = Logger.getLogger(AuditLogErrorHandler.class);
	
    @Override
    public void handleError(Throwable t) {
    	LOG.error("Audit log error", t);
    }
}