package org.openiam.idm.srvc.audit.service;

import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
public interface AuditLogService {

    public void enqueue(final IdmAuditLog idmAuditLog);

    public List<IdmAuditLog> findBeans(final AuditLogSearchBean searchBean, final int from, final int size, final boolean isDeep);

    public List<String> findIDs(final AuditLogSearchBean searchBean, final int from, final int size);

    public int count(final AuditLogSearchBean searchBean);

    public IdmAuditLog findById(final String id);

    public IdmAuditLog save(IdmAuditLog auditLogEntity);

    public List<UserEntity> getUsersFromAuditByActionAndDate(String action, Date from, Date to);

}