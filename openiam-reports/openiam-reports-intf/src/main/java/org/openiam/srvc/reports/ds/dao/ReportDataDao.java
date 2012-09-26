package org.openiam.srvc.reports.ds.dao;

import java.util.List;
import org.openiam.core.domain.reports.ReportQuery;

public interface ReportDataDao {
    List<Object> getReportData(final String sqlQuery, final Class<?> resultObjectClass);
    ReportQuery getQueryScriptPath(final String reportName);
}
