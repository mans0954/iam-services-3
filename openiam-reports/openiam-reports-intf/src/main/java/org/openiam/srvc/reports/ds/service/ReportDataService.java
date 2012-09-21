package org.openiam.srvc.reports.ds.service;

import java.util.List;
import java.util.Map;
import org.openiam.exception.ScriptEngineException;
import org.openiam.srvc.reports.ds.dto.RowObject;

public interface ReportDataService {
    List<RowObject> getReportData(final String reportName, final Map<String, String> queryParams) throws ClassNotFoundException, ScriptEngineException;
}
