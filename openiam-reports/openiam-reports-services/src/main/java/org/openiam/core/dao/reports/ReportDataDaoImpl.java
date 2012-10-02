package org.openiam.core.dao.reports;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.core.domain.reports.ReportQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDataDaoImpl extends BaseDaoImpl<ReportQuery> implements ReportDataDao {

    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataDaoImpl.class);

    @Override
    public ReportQuery findByName(String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ReportQuery.class).add(Restrictions.eq("reportName", name));
        return (ReportQuery) criteria.uniqueResult();
    }
}
