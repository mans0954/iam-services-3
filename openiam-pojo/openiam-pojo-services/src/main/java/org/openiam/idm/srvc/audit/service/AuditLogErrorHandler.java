package org.openiam.idm.srvc.audit.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component("auditLogErrorHandler")
public class AuditLogErrorHandler implements ErrorHandler {

	private static final Log LOG = LogFactory.getLog(AuditLogErrorHandler.class);
	
    @Override
    public void handleError(Throwable t) {
    	LOG.error("Audit log error", t);
    }
}