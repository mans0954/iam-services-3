package org.openiam.core.ws.reports;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.core.dto.reports.ReportParameterDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "parameters"
})
public class GetReportParametersResponse {
    List<ReportParameterDto> parameters;

    public List<ReportParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportParameterDto> parameters) {
        this.parameters = parameters;
    }
}
