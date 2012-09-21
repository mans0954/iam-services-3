package org.openiam.srvc.reports.ds.dao;

import java.util.List;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.srvc.reports.ds.dto.RowObject;

public interface ReportDataDao {
    List<RowObject> getReportData(final String sqlQuery);
    ReportQuery getQueryScriptPath(final String reportName);
}
