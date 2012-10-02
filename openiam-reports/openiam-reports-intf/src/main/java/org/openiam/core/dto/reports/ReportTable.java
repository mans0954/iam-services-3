package org.openiam.core.dto.reports;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportTable", propOrder = {
        "row"
})
public class ReportTable {
    @XmlAttribute
    protected String name;

    protected List<ReportRow> row;

    public List<ReportRow> getRow() {
        return row;
    }

    public void setRow(List<ReportRow> row) {
        this.row = row;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
