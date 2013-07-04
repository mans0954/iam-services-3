package org.openiam.idm.srvc.report.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This DTO used in reporting system to transferring Report information to WS clients
 *
 * @author vitaly.yakunin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportSubscriptionDto", propOrder = {
        "reportId",
        "reportName",
        "deliveryMethod",
        "deliveryFormat",
        "deliveryAudience",
        "status",
        "userId"
})
@DozerDTOCorrespondence(ReportSubscriptionEntity.class)
public class ReportSubscriptionDto {
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

	private String reportId;
    private String reportName;
    private String deliveryMethod;
    private String deliveryFormat;
    private String deliveryAudience;
    private String status;
    private String userId;
    

    public ReportSubscriptionDto() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportSubscriptionDto reportDto = (ReportSubscriptionDto) o;

        if (deliveryMethod != null ? !deliveryMethod.equals(reportDto.deliveryMethod) : reportDto.deliveryMethod != null)
            return false;
        if (reportId != null ? !reportId.equals(reportDto.reportId) : reportDto.reportId != null) return false;
        if (reportName != null ? !reportName.equals(reportDto.reportName) : reportDto.reportName != null) return false;
        if (deliveryFormat != null ? !deliveryFormat.equals(reportDto.deliveryFormat) : reportDto.deliveryFormat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reportId != null ? reportId.hashCode() : 0;
        result = 31 * result + (reportName != null ? reportName.hashCode() : 0);
        result = 31 * result + (deliveryMethod != null ? deliveryMethod.hashCode() : 0);
        result = 31 * result + (deliveryFormat != null ? deliveryFormat.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "ReportSubscriptionDto [reportId=" + reportId + ", reportName="
				+ reportName + ", deliveryMethod=" + deliveryMethod
				+ ", deliveryFormat=" + deliveryFormat + ", deliveryAudience="
				+ deliveryAudience + ", status=" + status + "]";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}

