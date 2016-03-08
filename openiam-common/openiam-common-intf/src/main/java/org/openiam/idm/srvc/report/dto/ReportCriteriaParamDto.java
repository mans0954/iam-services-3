package org.openiam.idm.srvc.report.dto;

import org.openiam.base.KeyNameDTO;
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
        "reportId",
        "caption",
        "value",
        "typeId",
        "typeName",
        "metaTypeName",
        "metaTypeId",
        "isMultiple",
        "isRequired",
		"displayOrder",
		"requestParameters"
})
@DozerDTOCorrespondence(ReportCriteriaParamEntity.class)
public class ReportCriteriaParamDto extends KeyNameDTO {

    private String reportId;
    private String caption;
    private String value;
    private String typeId;
    private String typeName;
    private String metaTypeName;
    private String metaTypeId;
    private boolean isMultiple;
    private boolean isRequired;
	private Integer displayOrder;
    private String requestParameters;

    public ReportCriteriaParamDto() {
    }

    
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result
				+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
		result = prime * result + (isMultiple ? 1231 : 1237);
		result = prime * result + (isRequired ? 1231 : 1237);
		result = prime * result
				+ ((metaTypeId == null) ? 0 : metaTypeId.hashCode());
		result = prime * result
				+ ((metaTypeName == null) ? 0 : metaTypeName.hashCode());
		result = prime * result
				+ ((reportId == null) ? 0 : reportId.hashCode());
		result = prime
				* result
				+ ((requestParameters == null) ? 0 : requestParameters
						.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportCriteriaParamDto other = (ReportCriteriaParamDto) obj;
		if (caption == null) {
			if (other.caption != null)
				return false;
		} else if (!caption.equals(other.caption))
			return false;
		if (displayOrder == null) {
			if (other.displayOrder != null)
				return false;
		} else if (!displayOrder.equals(other.displayOrder))
			return false;
		if (isMultiple != other.isMultiple)
			return false;
		if (isRequired != other.isRequired)
			return false;
		if (metaTypeId == null) {
			if (other.metaTypeId != null)
				return false;
		} else if (!metaTypeId.equals(other.metaTypeId))
			return false;
		if (metaTypeName == null) {
			if (other.metaTypeName != null)
				return false;
		} else if (!metaTypeName.equals(other.metaTypeName))
			return false;
		if (reportId == null) {
			if (other.reportId != null)
				return false;
		} else if (!reportId.equals(other.reportId))
			return false;
		if (requestParameters == null) {
			if (other.requestParameters != null)
				return false;
		} else if (!requestParameters.equals(other.requestParameters))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "ReportCriteriaParamDto [reportId=" + reportId + ", caption="
				+ caption + ", value=" + value + ", typeId=" + typeId
				+ ", typeName=" + typeName + ", metaTypeName=" + metaTypeName
				+ ", metaTypeId=" + metaTypeId + ", isMultiple=" + isMultiple
				+ ", isRequired=" + isRequired + ", displayOrder="
				+ displayOrder + ", requestParameters=" + requestParameters
				+ "]";
	}

	
}
