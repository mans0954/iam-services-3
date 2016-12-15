package org.openiam.base.request;

import org.openiam.idm.srvc.report.dto.ReportQueryDto;

/**
 * Created by aduckardt on 2016-12-14.
 */
public class ReportRequest extends BaseServiceRequest {
    private ReportQueryDto reportQuery;
    private String taskName;
    private String reportBaseUrl;
    private String locale;

    public ReportQueryDto getReportQuery() {
        return reportQuery;
    }

    public void setReportQuery(ReportQueryDto reportQuery) {
        this.reportQuery = reportQuery;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getReportBaseUrl() {
        return reportBaseUrl;
    }

    public void setReportBaseUrl(String reportBaseUrl) {
        this.reportBaseUrl = reportBaseUrl;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReportRequest{");
        sb.append(super.toString());
        sb.append(",                 reportQuery=").append(reportQuery);
        sb.append(",                 taskName='").append(taskName).append('\'');
        sb.append(",                 reportBaseUrl='").append(reportBaseUrl).append('\'');
        sb.append(",                 locale='").append(locale).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
