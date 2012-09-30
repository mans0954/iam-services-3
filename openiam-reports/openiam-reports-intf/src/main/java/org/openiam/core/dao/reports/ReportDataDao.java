package org.openiam.core.dao.reports;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.core.domain.reports.ReportQuery;

public interface ReportDataDao extends BaseDao<ReportQuery> {
    List<Object> getReportData(final String sqlQuery, final Class<?> resultObjectClass);
    ReportQuery findByName(String name);
}
