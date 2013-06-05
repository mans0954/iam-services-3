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

    List<IdmAuditLogEntity> findPasswordEvents() throws DataException;

    List<IdmAuditLogEntity> search(SearchAudit search) throws DataException;
    List<IdmAuditLogEntity> search(SearchAudit search, Integer from, Integer size) throws DataException;
    Integer countEvents(SearchAudit search);

    List<IdmAuditLogEntity> findEventsAboutUser(String principal, Date startDate);

    List<IdmAuditLogEntity> findEventsAboutIdentityList(List<String> principal, Date startDate);
    List<IdmAuditLogEntity> findEventsAboutIdentityList(List<String> principalList, Date startDate, Date endDate);
    List<IdmAuditLogEntity> findEventsAboutIdentityList(List<String> principalList, Date startDate, Date endDate, Integer from, Integer size);
    Integer countEventsAboutIdentity(List<String> principal, Date startDate);
    Integer countEventsAboutIdentity(List<String> principal, Date startDate, Date endDate);
}
