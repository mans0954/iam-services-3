package org.openiam.idm.srvc.report.service;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ReportSubCriteriaParamDaoImpl extends BaseDaoImpl<ReportSubCriteriaParamEntity, String> implements ReportSubCriteriaParamDao {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReportSubCriteriaParamEntity> findByReportInfoId(String reportInfoId) {
        Criteria criteria = getSession().createCriteria(ReportSubCriteriaParamEntity.class)
                .add(Restrictions.eq("report.id", reportInfoId))
                .addOrder(Order.asc("name"));

        return criteria.list();
    }

    @Override
    public List<ReportSubCriteriaParamEntity> findByReportInfoName(String reportInfoName) {
        Criteria criteria = getSession().createCriteria(ReportSubCriteriaParamEntity.class)
                .createAlias("report","r")
                .add(Restrictions.eq("r.reportName", reportInfoName))
                .addOrder(Order.asc("name"));

        return criteria.list();
    }
}
