package org.openiam.idm.srvc.report.service;

import java.util.List;

import org.hibernate.Criteria;

import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

/**
 * DAO service for ReportSubscriptionEntity implementation
 *
 * @author vitaly.yakunin
 */
@Repository
public class ReportSubscriptionDaoImpl extends BaseDaoImpl<ReportSubscriptionEntity, String> implements ReportSubscriptionDao {

    private static final Logger LOG = LoggerFactory.getLogger(ReportSubscriptionDaoImpl.class);

    @Override
    public ReportSubscriptionEntity findByName(String name) {
        Criteria criteria = getSession().createCriteria(ReportSubscriptionEntity.class).add(Restrictions.eq("reportName", name));
        return (ReportSubscriptionEntity) criteria.uniqueResult();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public void createOrUpdateSubscribedReportInfo(ReportSubscriptionEntity reportSubscriptionEntity) {
        ReportSubscriptionEntity reportSubscription = findByName(reportSubscriptionEntity.getReportName());
        if(reportSubscription == null) {
           reportSubscription = reportSubscriptionEntity;
           getSession().save(reportSubscriptionEntity);
        }else{
        	getSession().update(reportSubscriptionEntity);
        }
    }
    
    @Override
    public List<ReportSubscriptionEntity> getAllActiveSubscribedReports(){
        Criteria criteria = getSession().createCriteria(ReportSubscriptionEntity.class).add(Restrictions.eq("status", "ACTIVE"));
        return (List<ReportSubscriptionEntity>) criteria.list();
    }

}
