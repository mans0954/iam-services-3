package org.openiam.idm.srvc.report.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;

/**
 * This entity used in reporting system to define Report information
 *
 * @author vitaly.yakunin
 */
@Entity
@Table(name = "REPORT_INFO")
@DozerDTOCorrespondence(ReportInfoDto.class)
public class ReportInfoEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REPORT_INFO_ID")
    private String reportId;

    @Column(name = "REPORT_NAME")
    private String reportName;

    @Column(name = "DATASOURCE_FILE_PATH")
    private String reportDataSource;

    @Column(name = "REPORT_FILE_PATH")
    private String reportUrl;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="REPORT_INFO_ID", referencedColumnName="REPORT_INFO_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ReportCriteriaParamEntity> reportParams = new HashSet<ReportCriteriaParamEntity>();

    public ReportInfoEntity() {
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Override
	public String toString() {
		return "ReportInfoEntity [reportId=" + reportId + ", reportName="
				+ reportName + ", reportDataSource=" + reportDataSource
				+ ", reportUrl=" + reportUrl + "]";
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

	public Set<ReportCriteriaParamEntity> getReportParams() {
		return reportParams;
	}

	public void setReportParams(Set<ReportCriteriaParamEntity> reportParams) {
		this.reportParams = reportParams;
	}
}