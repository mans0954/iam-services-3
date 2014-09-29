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
        "caption",
        "value",
        "typeId",
        "typeName",
        "metaTypeName",
        "metaTypeId",
        "isMultiple",
        "isRequired",
		"displayOrder"
})
@DozerDTOCorrespondence(ReportCriteriaParamEntity.class)
public class ReportCriteriaParamDto {

    private String id;
    private String reportId;
    private String name;
    private String caption;
    private String value;
    private String typeId;
    private String typeName;
    private String metaTypeName;
    private String metaTypeId;
    private boolean isMultiple;
    private boolean isRequired;
	private Integer displayOrder;

    public ReportCriteriaParamDto(String id, boolean isMultiple, boolean isRequired,
                                  String name, String caption, String reportId, String typeId,
                                  String typeName, String metaTypeId, String metaTypeName,
                                  String value) {
        this.id = id;
        this.isMultiple = isMultiple;
        this.isRequired = isRequired;
        this.name = name;
        this.caption = caption;
        this.reportId = reportId;
        this.typeId = typeId;
        this.typeName = typeName;
        this.metaTypeId = metaTypeId;
        this.metaTypeName = metaTypeName;
        this.value = value;
    }

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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getMetaTypeId() {
        return metaTypeId;
    }

    public void setMetaTypeId(String metaTypeId) {
        this.metaTypeId = metaTypeId;
    }

    public String getMetaTypeName() {
        return metaTypeName;
    }

    public void setMetaTypeName(String metaTypeName) {
        this.metaTypeName = metaTypeName;
    }

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportCriteriaParamDto that = (ReportCriteriaParamDto) o;

        if (isMultiple != that.isMultiple) return false;
        if (isRequired != that.isRequired) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (caption != null ? !caption.equals(that.caption) : that.caption != null) return false;
        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (typeName != null ? !typeName.equals(that.typeName) : that.typeName != null) return false;
        if (metaTypeId != null ? !metaTypeId.equals(that.metaTypeId) : that.metaTypeId != null) return false;
		if (metaTypeName != null ? !metaTypeName.equals(that.metaTypeName) : that.metaTypeName != null) return false;
		if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (reportId != null ? reportId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        result = 31 * result + (metaTypeId != null ? metaTypeId.hashCode() : 0);
        result = 31 * result + (metaTypeName != null ? metaTypeName.hashCode() : 0);
        result = 31 * result + (isMultiple ? 1231 : 1237);
        result = 31 * result + (isRequired ? 1231 : 1237);
		result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "ReportCriteriaParamDto [id=" + id + ", reportId=" + reportId
				+ ", name=" + name
                + ", caption=" + caption
                + ", value=" + value
                + ", typeId=" + typeId
                + ", typeName=" + typeName
                + ", metaTypeId=" + metaTypeId
                + ", metaTypeName=" + metaTypeName
                + ", isMultiple=" + isMultiple
                + ", isRequired=" + isRequired
				+ ", displayOrder=" + displayOrder + "]";
	}
}
