package org.openiam.idm.srvc.report.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;

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
        return "ReportInfo{" +
                "id='" + reportId + '\'' +
                ", reportName='" + reportName + '\'' +
                ", datasourceFilePath='" + reportDataSource + '\'' +
                ", reportFilePath='" + reportUrl + '\'' +
                '}';
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
}