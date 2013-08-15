package org.openiam.idm.srvc.report.ws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "reports" })
public class GetAllSubscribedReportsResponse extends Response {

	protected List<ReportSubscriptionDto> reports;

	public List<ReportSubscriptionDto> getReports() {
		return reports;
	}

	public void setReports(List<ReportSubscriptionDto> reports) {
		this.reports = reports;
	}

}
