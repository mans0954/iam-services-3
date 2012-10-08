package org.openiam.core.dao;

import org.hibernate.Criteria;

import org.hibernate.criterion.Restrictions;
import org.openiam.core.domain.ReportInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;


@Repository
public class ReportDataDaoImpl extends BaseDaoImpl<ReportInfo> implements ReportDataDao {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataDaoImpl.class);

    @Override
    public ReportInfo findByName(String name) {
        Criteria criteria = getSession().createCriteria(ReportInfo.class).add(Restrictions.eq("reportName", name));
        return (ReportInfo) criteria.uniqueResult();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
