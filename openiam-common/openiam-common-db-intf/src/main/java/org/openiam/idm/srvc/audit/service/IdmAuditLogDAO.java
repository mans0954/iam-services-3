package org.openiam.idm.srvc.audit.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

import java.util.Date;

/**
 * DAO interface for IdmAudit
 *
 * @author Suneet Shah
 */
public interface IdmAuditLogDAO extends BaseDao<IdmAuditLogEntity, String> {
    IdmAuditLogEntity findByRequesterId(String requesterId, String correlationID);
    public void deleteOlderThan(Date date);
}
