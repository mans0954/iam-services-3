package org.openiam.idm.srvc.audit.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * DAO interface for IdmAudit
 *
 * @author Suneet Shah
 */
public interface IdmAuditLogDAO extends BaseDao<IdmAuditLogEntity, String> {
    IdmAuditLogEntity findByRequesterId(String requesterId, String correlationID);

    public List<UserEntity>getUsersByAction(String action, Date fromDate, Date toDate);
}
