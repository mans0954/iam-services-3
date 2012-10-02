package org.openiam.core.service.reports;

import java.util.List;
import java.util.Map;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.core.dto.reports.ReportDataDto;
import org.openiam.exception.ScriptEngineException;

public interface ReportDataService {
    ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException;
    List<ReportQuery> getAllReports();
    ReportQuery getReportByName(String name);
}
