package org.openiam.idm.srvc.report.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;

import javax.persistence.*;

@Entity
@Table(name = "REPORT_SUB_CRITERIA_PARAM")
@DozerDTOCorrespondence(ReportSubCriteriaParamDto.class)
public class ReportSubCriteriaParamEntity {
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "RSCP_ID")
	private String rscpId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RCP_ID", referencedColumnName = "RCP_ID", insertable = true, updatable = false)
	private ReportCriteriaParamEntity param;
	// @Column(name = "RCP_ID", updatable = false)
	// private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORT_SUB_ID", referencedColumnName = "REPORT_SUB_ID", insertable = true, updatable = false)
	private ReportSubscriptionEntity report;

	@Column(name = "PARAM_NAME", updatable = false)
	private String name;

	@Column(name = "PARAM_VALUE")
	private String value;

	public ReportSubCriteriaParamEntity() {
	}


	public ReportSubscriptionEntity getReport() {
		return report;
	}

	public void setReport(ReportSubscriptionEntity report) {
		this.report = report;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportSubCriteriaParamEntity other = (ReportSubCriteriaParamEntity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (param == null) {
			if (other.param != null)
				return false;
		} else if (!param.equals(other.param))
			return false;
		if (report == null) {
			if (other.report != null)
				return false;
		} else if (!report.equals(other.report))
			return false;
		if (rscpId == null) {
			if (other.rscpId != null)
				return false;
		} else if (!rscpId.equals(other.rscpId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
		result = prime * result + ((report == null) ? 0 : report.hashCode());
		result = prime * result + ((rscpId == null) ? 0 : rscpId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ReportSubCriteriaParamEntity [rscpId=" + rscpId + ", param="
				+ param + ", report=" + report + ", name=" + name + ", value="
				+ value + "]";
	}

	public String getRscpId() {
		return rscpId;
	}

	public void setRscpId(String rscpId) {
		this.rscpId = rscpId;
	}

	public ReportCriteriaParamEntity getParam() {
		return param;
	}

	public void setParam(ReportCriteriaParamEntity param) {
		this.param = param;
		if (param != null)
			setName(param.getName());
	}
}
