package org.openiam.idm.srvc.report.domain;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
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
public class ReportCriteriaParamEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RCP_ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_INFO_ID", referencedColumnName = "REPORT_INFO_ID", insertable = true, updatable = false)
    private ReportInfoEntity report;

    @Column(name = "PARAM_NAME")
    private String name;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportCriteriaParamEntity that = (ReportCriteriaParamEntity) o;

        if (isMultiple != that.isMultiple) return false;
        if (isRequired != that.isRequired) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (caption != null ? !caption.equals(that.caption) : that.caption!= null) return false;
        if (report != null ? !report.equals(that.report) : that.report != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (metaType != null ? !metaType.equals(that.metaType) : that.metaType != null) return false;
		if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) return false;
        return !(requestParameters != null ? !requestParameters.equals(that.requestParameters) : that.requestParameters != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (report != null ? report.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (metaType != null ? metaType.hashCode() : 0);
        result = 31 * result + (isMultiple ? 1231 : 1237);
        result = 31 * result + (isRequired ? 1231 : 1237);
		result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
		result = 31 * result + (requestParameters != null ? requestParameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportCriteriaParamEntity{" +
                "id='" + id + '\'' +
                ", report=" + report +
                ", name='" + name + '\'' +
                ", caption='" + caption + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                ", metaType=" + (metaType != null ? metaType.getId() : "null") +
                ", isMultiple=" + isMultiple +
				", isRequired=" + isRequired +
				", displayOrder=" + displayOrder +
				", requestParameters='" + requestParameters + '\'' +
                '}';
    }
}
