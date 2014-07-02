package org.openiam.idm.srvc.service.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "REQUEST_FORM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RequestFormEntity implements Serializable {

	@EmbeddedId
    @AttributeOverride(name="serviceId", column=@Column(name="SERVICE_ID"))
    private RequestFormEntityId id;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="SERVICE_ID", referencedColumnName = "SERVICE_ID", insertable = false, updatable = false)
    private ServiceEntity service;
    
    @Column(name = "IS_DEFAULT")
    private Integer isDefault;
    
    @Column(name="CREATE_DATE",length=19)
	@Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name = "CREATED_BY", length=20)
    private String createdBy;
    
    @Column(name = "FORM_TEMPLATE_URL", length=80)
    private String formTemplateUrl;

    public RequestFormEntity() {
    }

    public RequestFormEntityId getId() {
        return this.id;
    }

    public void setId(RequestFormEntityId id) {
        this.id = id;
    }

    public ServiceEntity getService() {
        return this.service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public Integer getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getFormTemplateUrl() {
        return this.formTemplateUrl;
    }

    public void setFormTemplateUrl(String formTemplateUrl) {
        this.formTemplateUrl = formTemplateUrl;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((formTemplateUrl == null) ? 0 : formTemplateUrl.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isDefault == null) ? 0 : isDefault.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestFormEntity other = (RequestFormEntity) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (formTemplateUrl == null) {
			if (other.formTemplateUrl != null)
				return false;
		} else if (!formTemplateUrl.equals(other.formTemplateUrl))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isDefault == null) {
			if (other.isDefault != null)
				return false;
		} else if (!isDefault.equals(other.isDefault))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RequestFormEntity [id=" + id + ", service=" + service
				+ ", isDefault=" + isDefault + ", createDate=" + createDate
				+ ", createdBy=" + createdBy + ", formTemplateUrl="
				+ formTemplateUrl + "]";
	}

    
}
