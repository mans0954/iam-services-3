package org.openiam.idm.srvc.audit.service;

import java.util.List;

import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
public interface AuditLogService {

    void enqueue(final IdmAuditLogEntity idmAuditLog);
    
    List<IdmAuditLogEntity> findBeans(final AuditLogSearchBean searchBean, final int from, final int size);
    List<String> findIDs(final AuditLogSearchBean searchBean, final int from, final int size);

    int count(final AuditLogSearchBean searchBean);
    IdmAuditLogEntity findById(final String id);
    IdmAuditLogEntity save(IdmAuditLogEntity auditLogEntity);

}