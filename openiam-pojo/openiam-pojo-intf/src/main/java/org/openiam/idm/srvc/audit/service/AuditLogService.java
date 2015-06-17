package org.openiam.idm.srvc.audit.service;

import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
public interface AuditLogService {

    void enqueue(final IdmAuditLog idmAuditLog);
    
    List<IdmAuditLog> findBeans(final AuditLogSearchBean searchBean, final int from, final int size);
    List<String> findIDs(final AuditLogSearchBean searchBean, final int from, final int size);

    int count(final AuditLogSearchBean searchBean);
    IdmAuditLog findById(final String id);
    IdmAuditLog save(IdmAuditLog auditLogEntity);

}