package org.openiam.idm.srvc.audit.service;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;

/**
 * DAO interface for IdmAudit
 *
 * @author Suneet Shah
 */
public interface IdmAuditLogCustomDAO extends
        BaseDao<IdmAuditLogCustomEntity, String> {

    public List<IdmAuditLogCustomEntity> getByIdmAuditLogId(String LogId);
}
