package org.openiam.idm.srvc.audit.service;

import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
public interface AuditLogService {

    public void enqueue(final AuditLogBuilder builder);
    
    public List<IdmAuditLogEntity> findBeans(final AuditLogSearchBean searchBean, final int from, final int size);
    public int count(final AuditLogSearchBean searchBean);
    public IdmAuditLogEntity findById(final String id);
    public String save(IdmAuditLogEntity auditLogEntity);
    public void prepare(final IdmAuditLog log, final String coorelationId);

}