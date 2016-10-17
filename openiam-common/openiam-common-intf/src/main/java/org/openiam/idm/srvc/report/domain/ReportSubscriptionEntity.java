package org.openiam.idm.srvc.report.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;


@Entity
@Table(name = "REPORT_SUBSCRIPTIONS")
@DozerDTOCorrespondence(ReportSubscriptionDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReportSubscriptionEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REPORT_SUB_ID")
    private String reportId;
    
    @Column(name = "REPORT_INFO_ID")
    private String reportInfoId;

    @Column(name = "REPORT_NAME")
    private String reportName;

    @Column(name = "DELIVERY_METHOD")
    private String deliveryMethod;
    @Column(name = "DELIVERY_FORMAT")
    private String deliveryFormat;
    @Column(name = "DELIVERY_AUDIENCE")
    private String deliveryAudience;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "USERID")
    private String userId;

    public ReportSubscriptionEntity() {
    }



    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }


    @Override
	public String toString() {
		return "ReportSubscriptionEntity [reportId=" + reportId + ", reportName="
				+ reportName + ", deliveryMethod=" + deliveryMethod
				+ ", deliveryFormat=" + deliveryFormat + ", deliveryAudience="
				+ deliveryAudience + ", status=" + status + ", userId="
				+ userId + ",reportInfoId="+reportInfoId+"]";
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getDeliveryFormat() {
		return deliveryFormat;
	}

	public void setDeliveryFormat(String deliveryFormat) {
		this.deliveryFormat = deliveryFormat;
	}

	public String getDeliveryAudience() {
		return deliveryAudience;
	}

	public void setDeliveryAudience(String deliveryAudience) {
		this.deliveryAudience = deliveryAudience;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}



	public String getReportInfoId() {
		return reportInfoId;
	}



	public void setReportInfoId(String reportInfoId) {
		this.reportInfoId = reportInfoId;
	}
}