package org.openiam.idm.srvc.audit.service;

import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;

import java.util.Date;
import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
public interface AuditLogService {

    public void save(final IdmAuditLogEntity log);
    public void enqueue(final IdmAuditLogEntity log);
}