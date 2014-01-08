package org.openiam.idm.srvc.audit.service;

import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AuditLogProvider {
    public AuditLogBuilder getAuditLogBuilder(String id);
    public AuditLogBuilder getAuditLogBuilder();
    public void updateAuditLogBuilder(AuditLogBuilder value);
    public AuditLogBuilder persist(AuditLogBuilder value);
    public void remove(String auditLogId);
}

