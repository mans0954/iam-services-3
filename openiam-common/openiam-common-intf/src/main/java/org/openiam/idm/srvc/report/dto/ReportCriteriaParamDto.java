package org.openiam.idm.srvc.report.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This DTO used in reporting system to transferring parameters of criteria for DataSetBuilder via WS
 *
 * @author vitaly.yakunin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportCriteriaParamDto", propOrder = {
        "id",
        "reportId",
        "name",
        "value",
        "typeId",
        "typeName",
        "metaTypeId",
        "isMultiple"
})
@DozerDTOCorrespondence(ReportCriteriaParamEntity.class)
public class ReportCriteriaParamDto {

    private String id;
    private String reportId;
    private String name;
    private String value;
    private String typeId;
    private String typeName;
    private String metaTypeId;
    private Boolean isMultiple;


    public ReportCriteriaParamDto() {
    }

    public ReportCriteriaParamDto(String reportId, String name, String value, String typeId, String typeName) {
        this.reportId = reportId;
        this.name = name;
        this.value = value;
        this.typeId = typeId;
        this.typeName=typeName;
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

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public String getMetaTypeId() {
        return metaTypeId;
    }

    public void setMetaTypeId(String metaTypeId) {
        this.metaTypeId = metaTypeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportCriteriaParamDto that = (ReportCriteriaParamDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (typeName != null ? !typeName.equals(that.typeName) : that.typeName != null) return false;
        if (metaTypeId != null ? !metaTypeId.equals(that.metaTypeId) : that.metaTypeId != null) return false;
        if (isMultiple != null ? !isMultiple.equals(that.isMultiple) : that.isMultiple != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (reportId != null ? reportId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        result = 31 * result + (metaTypeId != null ? metaTypeId.hashCode() : 0);
        result = 31 * result + (isMultiple != null ? isMultiple.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "ReportCriteriaParamDto [id=" + id + ", reportId=" + reportId
				+ ", name=" + name + ", value=" + value + ", typeId=" + typeId
                + ", metaTypeId=" + metaTypeId + ", isMultiple=" + isMultiple
                + "]";
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
