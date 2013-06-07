package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.hibernate.criterion.Projections.rowCount;

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
        return findEventsAboutIdentityList(principalList, startDate, null);
    }

    public List<IdmAuditLogEntity> findEventsAboutIdentityList(
            List<String> principalList, Date startDate, Date endDate){
        return findEventsAboutIdentityList(principalList, startDate, endDate, -1, -1);
    }

    public List<IdmAuditLogEntity> findEventsAboutIdentityList(
            List<String> principalList, Date startDate, Date endDate, Integer from, Integer size){
        Criteria criteria = getEventAboutPrincipalCriteria(principalList, startDate, endDate);

        if (from > -1) {
            criteria.setFirstResult(from);
        }
        if (size > -1) {
            criteria.setMaxResults(size);
        }

        List results = (List<IdmAuditLogEntity>) criteria.list();

        if (results != null) {
            log.debug("- Found audit events for :" + principalList + " "
                      + results.size());
        } else {
            log.debug("No audit events found for user " + principalList);
        }

        return results;
    }

    public Integer countEventsAboutIdentity(List<String> principalList, Date startDate){
          return countEventsAboutIdentity(principalList, startDate, null);
    }
    public Integer countEventsAboutIdentity(List<String> principalList, Date startDate, Date endDate){
        return ((Number)getEventAboutPrincipalCriteria(principalList, startDate, endDate).setProjection(rowCount())
                .uniqueResult()).intValue();
    }

    public List<IdmAuditLogEntity> search(SearchAudit search)
            throws DataException {
        return search(search, -1, -1);
    }

    public List<IdmAuditLogEntity> search(SearchAudit search, Integer from, Integer size) throws DataException{
        List results = null;
        if (search == null) {
            throw new NullPointerException("Search parameter is null");
        }

        try {
            Criteria criteria = getSearchCriteria(search);
            if (from > -1) {
                criteria.setFirstResult(from);
            }
            if (size > -1) {
                criteria.setMaxResults(size);
            }
            results = criteria.list();

        } catch (HibernateException he) {
            log.error("search operation failed.", he);
            throw new DataException(he.getMessage(), he.getCause());
        }
        return results;
    }

    public  Integer countEvents(SearchAudit search){
        return ((Number)getSearchCriteria(search).setProjection(rowCount())
                .uniqueResult()).intValue();
    }

    private Criteria getEventAboutPrincipalCriteria(List<String> principalList, Date startDate, Date endDate){
        Criterion dateRestriction = null;
        if(endDate!=null){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            dateRestriction = Restrictions.and(Restrictions.ge("actionDatetime", startDate),
                                               Restrictions.lt("actionDatetime", calendar.getTime()));
        }  else{
            dateRestriction = Restrictions.ge("actionDatetime", startDate);
        }

        Criteria criteria = this
                .getCriteria()
                .setFetchMode("customRecords", FetchMode.JOIN)
                .createAlias("customRecords", "cr")
                .add(Restrictions.and(dateRestriction,
                                      Restrictions.or(Restrictions.in("principal", principalList),
                                                      Restrictions.and(Restrictions.and(Restrictions.eq("cr.type", CustomIdmAuditLogType.ATTRIB),
                                                                                        Restrictions.eq("cr.dispayOrder", 3)),
                                                                       Restrictions.in("cr.customValue", principalList))
                                      )
                )
                ).addOrder(Order.asc("actionDatetime"));
        return criteria;
    }

    private Criteria getSearchCriteria(SearchAudit search){
        Criteria criteria = getCriteria();
        // build the criteria list
        if (search.getApplicationName() != null) {
            criteria.add(Restrictions.eq("domainId", search.getApplicationName()));
        }
        if (search.getLoginId() != null) {
            log.info("audit log search: principal = " + search.getLoginId());
            criteria.add(getStringCriterion("principal", search.getLoginId()));
        }
        if (search.getSrcSystemId() != null) {
            criteria.add(Restrictions.eq("srcSystemId", search.getSrcSystemId()));
        }

        if (search.getUserId() != null) {
            criteria.add(Restrictions.eq("userId", search.getUserId()));
        }
        if (search.getStartDate() != null) {
            criteria.add(Restrictions.ge("actionDatetime",  search.getStartDate()));
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

        List<String> values = new ArrayList<String>();
        List<String> names = new ArrayList<String>();

        if (search.getCustomAttrValue1() != null){
            values.add(search.getCustomAttrValue1());
        }
        if(search.getCustomAttrValue2() != null) {
            values.add(search.getCustomAttrValue2());
        }
        if (search.getCustomAttrname1() != null) {
            names.add(search.getCustomAttrname1());
        }
        if (search.getCustomAttrname2() != null) {
            names.add(search.getCustomAttrname2());
        }

        if (!values.isEmpty() || !names.isEmpty()) {
            criteria.createAlias("customRecords", "cr")
            .setFetchMode("customRecords", FetchMode.JOIN)
            .createAlias("customRecords", "cr");
            if(!values.isEmpty()){
                criteria.add(Restrictions.in("cr.customName", names));
            }
            if(!names.isEmpty()){
                criteria.add(Restrictions.in("cr.customValue", values));
            }
        }

        criteria.addOrder(Order.asc("actionDatetime"));
        return criteria;
    }

    private Criterion getStringCriterion(String fieldName, String value){
        Criterion criterion=null;
        MatchMode matchMode = null;
        if (StringUtils.indexOf(value, "*") == 0) {
            matchMode = MatchMode.END;
            value = value.substring(1);
        }
        if (StringUtils.isNotEmpty(value) && StringUtils.indexOf(value, "*") == value.length() - 1) {
            value = value.substring(0, value.length() - 1);
            matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
        }

        if (StringUtils.isNotEmpty(value)) {
            if (matchMode != null) {
                criterion = Restrictions.ilike(fieldName, value, matchMode);
            } else {
                criterion = Restrictions.eq(fieldName, value);
            }
        }
        return criterion;
    }

    @Override
    protected String getPKfieldName() {
        return "logId";
    }

}
