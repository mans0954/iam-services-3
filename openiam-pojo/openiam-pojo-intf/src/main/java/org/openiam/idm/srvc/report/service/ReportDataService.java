package org.openiam.idm.srvc.report.service;

import java.util.List;
import java.util.Map;
import org.openiam.core.domain.ReportInfo;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.dto.ReportDataDto;

public interface ReportDataService {
    ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException;
    List<ReportInfo> getAllReports();
    ReportInfo getReportByName(String name);
}