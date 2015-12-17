package org.openiam.idm.srvc.report.domain;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;

/**
 * This entity used in reporting system to define parameters of criteria for DataSetBuilder
 *
 * @author vitaly.yakunin
 */
@Entity
@Table(name = "REPORT_CRITERIA_PARAMETER")
@DozerDTOCorrespondence(ReportCriteriaParamDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "RCP_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "PARAM_NAME"))
})
public class ReportCriteriaParamEntity extends AbstractKeyNameEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_INFO_ID", referencedColumnName = "REPORT_INFO_ID", insertable = true, updatable = false)
    private ReportInfoEntity report;

    @Column(name = "CAPTION")
    private String caption;

    @Column(name = "PARAM_VALUE")
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RCPT_ID", nullable = false, insertable = true, updatable = true)
    private ReportParamTypeEntity type;

    @ManyToOne
    @JoinColumn(name = "PARAM_META_TYPE_ID", nullable = true, insertable = true, updatable = true)
    private ReportParamMetaTypeEntity metaType;

    @Column(name = "IS_MULTIPLE")
    @Type(type = "yes_no")
    private boolean isMultiple;

    @Column(name = "IS_REQUIRED")
    @Type(type = "yes_no")
    private boolean isRequired;

	@Column(name="DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;

	@Column(name="REQUEST_PARAMS")
	private String requestParameters;

	public ReportCriteriaParamEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReportInfoEntity getReport() {
        return report;
    }

    public void setReport(ReportInfoEntity report) {
        this.report = report;
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

    public ReportParamTypeEntity getType() {
        return type;
    }

    public void setType(ReportParamTypeEntity type) {
        this.type = type;
    }

    public ReportParamMetaTypeEntity getMetaType() {
        return metaType;
    }

    public void setMetaType(ReportParamMetaTypeEntity metaType) {
        this.metaType = metaType;
    }

    public boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean required) {
        isRequired = required;
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
				+ ((metaType == null) ? 0 : metaType.hashCode());
		result = prime * result + ((report == null) ? 0 : report.hashCode());
		result = prime
				* result
				+ ((requestParameters == null) ? 0 : requestParameters
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ReportCriteriaParamEntity other = (ReportCriteriaParamEntity) obj;
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
		if (metaType == null) {
			if (other.metaType != null)
				return false;
		} else if (!metaType.equals(other.metaType))
			return false;
		if (report == null) {
			if (other.report != null)
				return false;
		} else if (!report.equals(other.report))
			return false;
		if (requestParameters == null) {
			if (other.requestParameters != null)
				return false;
		} else if (!requestParameters.equals(other.requestParameters))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
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
		return "ReportCriteriaParamEntity [report=" + report + ", caption="
				+ caption + ", value=" + value + ", type=" + type
				+ ", metaType=" + metaType + ", isMultiple=" + isMultiple
				+ ", isRequired=" + isRequired + ", displayOrder="
				+ displayOrder + ", requestParameters=" + requestParameters
				+ "]";
	}

	
	
}
