package org.openiam.core.dao.reports;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
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
    public List<Object> getReportData(final String sqlQuery, final Class<?> resultObjectClass) {
        List<Object> result = new LinkedList<Object>();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

        for (Object aList : query.list()) {
            try {
                Object resultObj = resultObjectClass.newInstance();
                for (Map.Entry<String, String> col : ((Map<String, String>) aList).entrySet()) {
                    try {
                        Field field = resultObjectClass.getDeclaredField(col.getKey());
                        field.setAccessible(true);
                        field.set(resultObj, col.getValue());
                    } catch (NoSuchFieldException e) {
                        LOG.warn(e.getMessage());
                    }
                }
                result.add(resultObj);
            } catch (InstantiationException e) {
                LOG.warn(e.getMessage());
            } catch (IllegalAccessException e) {
                LOG.warn(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public ReportQuery findByName(String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ReportQuery.class).add(Restrictions.eq("reportName", name));
        return (ReportQuery) criteria.uniqueResult();
    }
}
