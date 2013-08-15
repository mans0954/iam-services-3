package org.openiam.idm.srvc.report.ws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "report"
})
public class GetReportInfoResponse extends Response {

    protected ReportInfoDto report;

    public ReportInfoDto getReport() {
        return report;
    }

    public void setReport(ReportInfoDto report) {
        this.report = report;
    }
}