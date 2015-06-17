package org.openiam.idm.srvc.report.dto;

import java.util.HashSet;
import java.util.Set;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This DTO used in reporting system to transferring Report information to WS clients
 *
 * @author vitaly.yakunin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportInfoDto", propOrder = {
        "reportId",
        "reportName",
        "reportDataSource",
        "reportUrl",
        "reportParams",
        "parameterCount",
        "isBuiltIn",
		"resourceId"
})
@DozerDTOCorrespondence(ReportInfoEntity.class)
public class ReportInfoDto {
    private String reportId;
    private String reportName;
    private String reportDataSource;
    private String reportUrl;
    private Set<ReportCriteriaParamDto> reportParams = new HashSet<ReportCriteriaParamDto>();
    private Integer parameterCount;
    private boolean isBuiltIn;
	private String resourceId;

    public ReportInfoDto() {
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportDataSource() {
        return reportDataSource;
    }

    public void setReportDataSource(String reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public Integer getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(Integer parameterCount) {
        this.parameterCount = parameterCount;
    }

    public boolean getIsBuiltIn() {
        return isBuiltIn;
    }

    public void setIsBuiltIn(boolean isBuiltIn) {
        this.isBuiltIn = isBuiltIn;
    }

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportInfoDto reportDto = (ReportInfoDto) o;

        if (reportDataSource != null ? !reportDataSource.equals(reportDto.reportDataSource) : reportDto.reportDataSource != null)
            return false;
        if (reportId != null ? !reportId.equals(reportDto.reportId) : reportDto.reportId != null) return false;
        if (reportName != null ? !reportName.equals(reportDto.reportName) : reportDto.reportName != null) return false;
        if (reportUrl != null ? !reportUrl.equals(reportDto.reportUrl) : reportDto.reportUrl != null) return false;
        if (isBuiltIn != reportDto.isBuiltIn) return false;
        return !(resourceId != null ? !resourceId.equals(reportDto.resourceId) : reportDto.resourceId != null);

    }

    @Override
    public int hashCode() {
        int result = reportId != null ? reportId.hashCode() : 0;
        result = 31 * result + (reportName != null ? reportName.hashCode() : 0);
        result = 31 * result + (reportDataSource != null ? reportDataSource.hashCode() : 0);
        result = 31 * result + (reportUrl != null ? reportUrl.hashCode() : 0);
        result = 31 * result + (isBuiltIn ? 1231 : 1237);
		result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "ReportInfoDto [reportId=" + reportId + ", reportName="
				+ reportName + ", reportDataSource=" + reportDataSource
				+ ", reportUrl=" + reportUrl + "]";
	}

	public Set<ReportCriteriaParamDto> getReportParams() {
		return reportParams;
	}

	public void setReportParams(Set<ReportCriteriaParamDto> reportParams) {
		this.reportParams = reportParams;
	}
}

