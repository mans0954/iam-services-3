package org.openiam.idm.srvc.audit.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.exception.data.DataException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;

import java.util.Date;
import java.util.List;

/**
 * DAO interface for IdmAudit
 *
 * @author Suneet Shah
 */
public interface IdmAuditLogDAO extends BaseDao<IdmAuditLogEntity, String> {

}
