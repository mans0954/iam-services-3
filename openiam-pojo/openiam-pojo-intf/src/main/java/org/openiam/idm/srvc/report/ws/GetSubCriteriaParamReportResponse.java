package org.openiam.idm.srvc.report.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "parameters"
})
public class GetSubCriteriaParamReportResponse extends Response {

    protected List<ReportSubCriteriaParamDto> parameters;

    public List<ReportSubCriteriaParamDto> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportSubCriteriaParamDto> parameters) {
        this.parameters = parameters;
    }
}