package org.openiam.idm.srvc.report.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportSubCriteriaParamDto", propOrder = {
        "id",
        "reportId",
        "name",
        "value",
        "typeId"
})
@DozerDTOCorrespondence(ReportSubCriteriaParamEntity.class)
public class ReportSubCriteriaParamDto {

    private String id;
    private String reportId;
    private String name;
    private String value;
    private String typeId;

    public ReportSubCriteriaParamDto() {
    }

    public ReportSubCriteriaParamDto(String reportId, String name, String value, String typeId) {
        this.reportId = reportId;
        this.name = name;
        this.value = value;
        this.typeId = typeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportSubCriteriaParamDto that = (ReportSubCriteriaParamDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (reportId != null ? reportId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportCriteriaParamDto{" +
                "id='" + id + '\'' +
                ", reportId='" + reportId + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
