package org.openiam.idm.srvc.report.service;

import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.idm.srvc.report.dto.ReportQueryDto;
import org.springframework.context.ApplicationContextAware;

public interface ReportDataSetBuilder extends ApplicationContextAware {
    ReportDataDto getReportData(ReportQueryDto reportQuery);
}
