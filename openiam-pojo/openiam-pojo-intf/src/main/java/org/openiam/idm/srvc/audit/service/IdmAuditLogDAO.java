package org.openiam.idm.srvc.audit.service;

import java.util.Date;
import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.exception.data.DataException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;

/**
 * DAO interface for IdmAudit
 *
 * @author Suneet Shah
 */
public interface IdmAuditLogDAO extends BaseDao<IdmAuditLogEntity, String> {

    List<IdmAuditLogEntity> findPasswordEvents() throws DataException;

    List<IdmAuditLogEntity> search(SearchAudit search) throws DataException;

    List<IdmAuditLogEntity> findEventsAboutUser(String principal, Date startDate);

    List<IdmAuditLogEntity> findEventsAboutIdentityList(List<String> principal,
            Date startDate);
}
