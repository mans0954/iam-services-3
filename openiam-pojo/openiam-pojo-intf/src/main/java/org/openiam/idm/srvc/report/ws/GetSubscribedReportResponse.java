package org.openiam.idm.srvc.report.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "report"
})
public class GetSubscribedReportResponse extends Response {

    protected ReportSubscriptionDto report;

    public ReportSubscriptionDto getReport() {
        return report;
    }

    public void setReport(ReportSubscriptionDto report) {
        this.report = report;
    }
}