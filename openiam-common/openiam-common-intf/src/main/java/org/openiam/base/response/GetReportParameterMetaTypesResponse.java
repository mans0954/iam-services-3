package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.report.dto.ReportParamMetaTypeDto;
import org.openiam.idm.srvc.report.dto.ReportParamTypeDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "types"
})
public class GetReportParameterMetaTypesResponse extends Response {
    private List<ReportParamMetaTypeDto> types;

    public List<ReportParamMetaTypeDto> getMetaTypes() {
        return types;
    }

    public void setTypes(List<ReportParamMetaTypeDto> types) {
        this.types = types;
    }
}
