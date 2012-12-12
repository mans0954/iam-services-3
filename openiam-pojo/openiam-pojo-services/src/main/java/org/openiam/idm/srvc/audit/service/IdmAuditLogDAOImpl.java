package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.springframework.stereotype.Repository;

/**
 * RDMBS implementation the DAO for IdmAudit
 * @see org.openiam.idm.srvc.audit.dto.IdmAuditLog
 * @author Suneet Shah 
 */
@Repository("idmAuditLogDAO")
public class IdmAuditLogDAOImpl extends BaseDaoImpl<IdmAuditLogEntity, String>
        implements IdmAuditLogDAO {

    public List<IdmAuditLogEntity> findPasswordEvents() {

        Criteria criteria = this.getCriteria().add(
                Restrictions.or(Restrictions.eq("actionId", "PASSWORD CHANGE"),
                        Restrictions.eq("actionId", "PASSWORD RESET")));
        return (List<IdmAuditLogEntity>) criteria.list();
    }

    public List<IdmAuditLogEntity> findEventsAboutUser(String principal,
            Date startDate) {

        Criteria criteria = this
                .getCriteria()
                .setFetchMode("customRecords", FetchMode.JOIN)
                .createAlias("customRecords", "cr")
                .add(Restrictions.and(Restrictions.ge("actionDatetime",
                        startDate), Restrictions.or(Restrictions.eq(
                        "principal", principal), Restrictions.and(Restrictions
                        .and(Restrictions.eq("type", "0"),
                                Restrictions.eq("dispayOrder", "3")),
                        Restrictions.eq("value", principal)))))
                .addOrder(Order.asc("actionDatetime"));

        List results = (List<IdmAuditLogEntity>) criteria.list();

        if (results != null) {
            log.debug("- Found audit events for :" + principal + " "
                    + results.size());
        } else {
            log.debug("No audit events found for user " + principal);
        }
        return results;
    }

    public List<IdmAuditLogEntity> findEventsAboutIdentityList(
            List<String> principalList, Date startDate) {
        Criteria criteria = this
                .getCriteria()
                .setFetchMode("customRecords", FetchMode.JOIN)
                .createAlias("customRecords", "cr")
                .add(Restrictions.and(Restrictions.ge("actionDatetime",
                        startDate), Restrictions.or(Restrictions.in(
                        "principal", principalList), Restrictions.and(
                        Restrictions.and(Restrictions.eq("type", "0"),
                                Restrictions.eq("dispayOrder", "3")),
                        Restrictions.in("value", principalList)))))
                .addOrder(Order.asc("actionDatetime"));

        List results = (List<IdmAuditLogEntity>) criteria.list();

        if (results != null) {
            log.debug("- Found audit events for :" + principalList + " "
                    + results.size());
        } else {
            log.debug("No audit events found for user " + principalList);
        }

        return results;
    }

    public List<IdmAuditLogEntity> search(SearchAudit search)
            throws DataException {
        List results = null;

        if (search == null) {
            throw new NullPointerException("Search parameter is null");
        }

        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session
                    .createCriteria(org.openiam.idm.srvc.audit.dto.IdmAuditLog.class);
            // build the criteria list
            if (search.getApplicationName() != null) {
                criteria.add(Restrictions.eq("domainId",
                        search.getApplicationName()));
            }
            if (search.getLoginId() != null) {
                log.info("audit log search: principal = " + search.getLoginId());
                criteria.add(Restrictions.eq("principal", search.getLoginId()));
            }

            if (search.getSrcSystemId() != null) {
                criteria.add(Restrictions.eq("srcSystemId",
                        search.getSrcSystemId()));
            }

            if (search.getUserId() != null) {
                criteria.add(Restrictions.eq("userId", search.getUserId()));
            }
            if (search.getStartDate() != null) {
                criteria.add(Restrictions.ge("actionDatetime",
                        search.getStartDate()));
            }

            if (search.getCustomAttrValue1() != null) {
                criteria.add(Restrictions.eq("customAttrvalue1",
                        search.getCustomAttrValue1()));
            }
            if (search.getCustomAttrname1() != null) {
                criteria.add(Restrictions.eq("customAttrname1",
                        search.getCustomAttrname1()));
            }
            if (search.getCustomAttrValue2() != null) {
                criteria.add(Restrictions.eq("customAttrvalue2",
                        search.getCustomAttrValue2()));
            }
            if (search.getCustomAttrname2() != null) {
                criteria.add(Restrictions.eq("customAttrname2",
                        search.getCustomAttrname2()));
            }

            if (search.getReason() != null) {
                criteria.add(Restrictions.eq("reason", search.getReason()));
            }

            if (search.getObjectTypeId() != null) {
                criteria.add(Restrictions.eq("objectTypeId",
                        search.getObjectTypeId()));
            }
            if (search.getActionId() != null) {
                criteria.add(Restrictions.eq("actionId", search.getActionId()));
            }
            if (search.getObjectId() != null) {
                criteria.add(Restrictions.eq("objectId", search.getObjectId()));
            }
            if (search.getRequestId() != null) {
                criteria.add(Restrictions.eq("requestId", search.getRequestId()));
            }
            if (search.getSessionId() != null) {
                criteria.add(Restrictions.eq("sessionId", search.getSessionId()));
            }

            if (search.getEndDate() != null) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(search.getEndDate());
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                // less than equal may not have the full end date
                criteria.add(Restrictions.lt("actionDatetime",
                        calendar.getTime()));
            }
            // build the action list of criteria
            String[] actionAry = search.getActionList();
            if (actionAry != null) {
                criteria.add(Restrictions.in("actionId", search.getActionList()));
            }
            criteria.addOrder(Order.asc("actionDatetime"));
            results = criteria.list();

        } catch (HibernateException he) {
            log.error("search operation failed.", he);
            throw new DataException(he.getMessage(), he.getCause());
        }

        return results;

    }

    @Override
    protected String getPKfieldName() {

        return "logId";
    }

}
