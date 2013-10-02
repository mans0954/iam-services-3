package org.openiam.idm.srvc.base;

import org.openiam.idm.srvc.audit.service.AuditLogProvider;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 2:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBaseService {
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected AuditLogProvider auditLogProvider;
}
