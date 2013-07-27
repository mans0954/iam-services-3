package org.openiam.idm.srvc.report.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "report"
})
public class GetSubCriteriaParamReportResponse extends Response {

    protected ReportSubCriteriaParamDto report;

    public ReportSubCriteriaParamDto getReport() {
        return report;
    }

    public void setReport(ReportSubCriteriaParamDto report) {
        this.report = report;
    }
}