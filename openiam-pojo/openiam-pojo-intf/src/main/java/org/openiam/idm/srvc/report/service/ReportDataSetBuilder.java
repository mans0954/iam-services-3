package org.openiam.idm.srvc.report.service;

import java.util.List;
import java.util.Map;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.springframework.context.ApplicationContextAware;

public interface ReportDataSetBuilder extends ApplicationContextAware {
    ReportDataDto getReportData(Map<String, List<String>> reportParams);
}
