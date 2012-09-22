package org.openiam.srvc.reports.ds.ws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.srvc.reports.ds.dto.RowObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportQueryListResponse", propOrder = {
        "rowList"
})
public class ReportQueryListResponse extends Response {

    @XmlAnyElement(lax = true)
    private List<RowObject> rowList;

    public ReportQueryListResponse() {
    }

    public ReportQueryListResponse(ResponseStatus s) {
        super(s);
    }


    public List<RowObject> getRowList() {
        return rowList;
    }

    public void setRowList(List<RowObject> rowList) {
        this.rowList = rowList;
    }
}
