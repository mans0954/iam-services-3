package org.openiam.idm.srvc.report.service;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO service for ReportCriteriaParamEntity implementation
 *
 * @author vitaly.yakunin
 */
@Repository
public class ReportCriteriaParamDaoImpl extends BaseDaoImpl<ReportCriteriaParamEntity, String> implements ReportCriteriaParamDao {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReportCriteriaParamEntity> findByReportInfoId(String reportInfoId) {
        Criteria criteria = getSession().createCriteria(ReportCriteriaParamEntity.class)
                .add(Restrictions.eq("report.id", reportInfoId))
                .addOrder(Order.asc("name"));

        return criteria.list();
    }

    @Override
    public List<ReportCriteriaParamEntity> findByReportInfoName(String reportInfoName) {
        Criteria criteria = getSession().createCriteria(ReportCriteriaParamEntity.class)
                .createAlias("report","r")
                .add(Restrictions.eq("r.reportName", reportInfoName))
                .addOrder(Order.asc("name"));

        return criteria.list();
    }
    
    @Override
    public ReportCriteriaParamEntity getReportParameterByName(String reportId, String paramName){
    	Criteria criteria = getSession().createCriteria(ReportCriteriaParamEntity.class)
        .createAlias("report","r")
        .add(Restrictions.eq("report.id", reportId))
        .add(Restrictions.eq("name", paramName))
        .addOrder(Order.asc("name"));
    	List<ReportCriteriaParamEntity> list =  criteria.list();
    	if (list != null && list.size() > 0){
    		return list.get(0);
    	}else
    		return null;
    }
}
