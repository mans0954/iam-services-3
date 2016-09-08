package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentitySearchBean", propOrder = {
        "identity",
        "referredObjectId",
        "status",
        "type",
        "createFromDate",
        "createToDate",
        "createdBy",
        "managedSysId"
})
public class IdentitySearchBean extends AbstractSearchBean<IdentityDto, String> {
    private static final long serialVersionUID = 1L;

    private String identity;

    private String managedSysId;

    private String referredObjectId;

    @XmlSchemaType(name = "dateTime")
    private Date createFromDate;

    @XmlSchemaType(name = "dateTime")
    private Date createToDate;

    private String createdBy;

    private LoginStatusEnum status;

    private IdentityTypeEnum type;

    public IdentitySearchBean() {
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getReferredObjectId() {
        return referredObjectId;
    }

    public void setReferredObjectId(String referredObjectId) {
        this.referredObjectId = referredObjectId;
    }

    public Date getCreateFromDate() {
        return createFromDate;
    }

    public void setCreateFromDate(Date createFromDate) {
        this.createFromDate = createFromDate;
    }

    public Date getCreateToDate() {
        return createToDate;
    }

    public void setCreateToDate(Date createToDate) {
        this.createToDate = createToDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LoginStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LoginStatusEnum status) {
        this.status = status;
    }

    public IdentityTypeEnum getType() {
        return type;
    }

    public void setType(IdentityTypeEnum type) {
        this.type = type;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((createFromDate == null) ? 0 : createFromDate.hashCode());
		result = prime * result
				+ ((createToDate == null) ? 0 : createToDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime
				* result
				+ ((referredObjectId == null) ? 0 : referredObjectId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		IdentitySearchBean other = (IdentitySearchBean) obj;
		if (createFromDate == null) {
			if (other.createFromDate != null)
				return false;
		} else if (!createFromDate.equals(other.createFromDate))
			return false;
		if (createToDate == null) {
			if (other.createToDate != null)
				return false;
		} else if (!createToDate.equals(other.createToDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (referredObjectId == null) {
			if (other.referredObjectId != null)
				return false;
		} else if (!referredObjectId.equals(other.referredObjectId))
			return false;
		if (status != other.status)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

    
}
