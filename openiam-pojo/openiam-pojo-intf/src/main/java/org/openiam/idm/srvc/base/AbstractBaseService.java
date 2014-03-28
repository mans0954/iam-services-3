package org.openiam.idm.srvc.base;

import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractBaseService {
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected AuditLogProvider auditLogProvider;
    
	@Autowired
    @Qualifier("entityValidator")
	protected EntityValidator entityValidator;
}
