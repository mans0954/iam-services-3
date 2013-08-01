package org.openiam.idm.srvc.report.ws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "reports" })
public class GetAllSubCriteriaParamReportsResponse extends Response {

	protected List<ReportSubCriteriaParamDto> reports;

	public List<ReportSubCriteriaParamDto> getReports() {
		return reports;
	}

	public void setReports(List<ReportSubCriteriaParamDto> reports) {
		this.reports = reports;
	}

}
