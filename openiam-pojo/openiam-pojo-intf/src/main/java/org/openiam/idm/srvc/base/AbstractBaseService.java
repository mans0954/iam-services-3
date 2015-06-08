package org.openiam.idm.srvc.base;

import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public abstract class AbstractBaseService {

    public static final String ORG_ATTRIBUTES = "orgAttributes";

    @Autowired
    protected AuditLogService auditLogService;

	@Autowired
    @Qualifier("entityValidator")
	protected EntityValidator entityValidator;

}
