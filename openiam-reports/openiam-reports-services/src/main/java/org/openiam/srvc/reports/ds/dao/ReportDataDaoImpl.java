package org.openiam.srvc.reports.ds.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.srvc.reports.ds.dto.RowObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDataDaoImpl implements ReportDataDao {
    @Autowired
    protected SessionFactory sessionFactory;

    @Override
    public List<RowObject> getReportData(final String sqlQuery) {
        List<RowObject> result = new LinkedList<RowObject>();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        for (Object aList : query.list()) {
            RowObject row = new RowObject();
            row.setColumns((Map<String,String>) aList);
            result.add(row);
        }
        return result;
    }

    @Override
    public ReportQuery getQueryScriptPath(final String reportName) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ReportQuery.class).add(Restrictions.eq("reportName", reportName));
        return (ReportQuery)criteria.uniqueResult();
    }
}
