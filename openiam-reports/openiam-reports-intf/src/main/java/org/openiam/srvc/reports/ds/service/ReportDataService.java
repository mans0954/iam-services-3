package org.openiam.srvc.reports.ds.service;

import java.util.List;
import java.util.Map;
import org.openiam.exception.ScriptEngineException;

public interface ReportDataService {
    List<Object> getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException;
}
