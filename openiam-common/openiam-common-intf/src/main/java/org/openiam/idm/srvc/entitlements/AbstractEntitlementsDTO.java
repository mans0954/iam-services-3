package org.openiam.idm.srvc.entitlements;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AbstractMetadataTypeDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEntitlementsDTO", propOrder = {
        "accessRightIds",
        "accessRightStartDate",
        "accessRightEndDate"
})
public abstract class AbstractEntitlementsDTO extends AbstractMetadataTypeDTO {

	private Date accessRightStartDate;
    private Date accessRightEndDate;
	private Set<String> accessRightIds;
	
	public Set<String> getAccessRightIds() {
		return accessRightIds;
	}

	public void setAccessRightIds(Collection<String> accessRightIds) {
		if(accessRightIds != null) {
			this.accessRightIds = new HashSet<String>(accessRightIds);
		}
	}

	public Date getAccessRightStartDate() {
		return accessRightStartDate;
	}

	public void setAccessRightStartDate(Date accessRightStartDate) {
		this.accessRightStartDate = accessRightStartDate;
	}

	public Date getAccessRightEndDate() {
		return accessRightEndDate;
	}

	public void setAccessRightEndDate(Date accessRightEndDate) {
		this.accessRightEndDate = accessRightEndDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((accessRightEndDate == null) ? 0 : accessRightEndDate
						.hashCode());
		result = prime * result
				+ ((accessRightIds == null) ? 0 : accessRightIds.hashCode());
		result = prime
				* result
				+ ((accessRightStartDate == null) ? 0 : accessRightStartDate
						.hashCode());
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
		AbstractEntitlementsDTO other = (AbstractEntitlementsDTO) obj;
		if (accessRightEndDate == null) {
			if (other.accessRightEndDate != null)
				return false;
		} else if (!accessRightEndDate.equals(other.accessRightEndDate))
			return false;
		if (accessRightIds == null) {
			if (other.accessRightIds != null)
				return false;
		} else if (!accessRightIds.equals(other.accessRightIds))
			return false;
		if (accessRightStartDate == null) {
			if (other.accessRightStartDate != null)
				return false;
		} else if (!accessRightStartDate.equals(other.accessRightStartDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractEntitlementsDTO [accessRightStartDate="
				+ accessRightStartDate + ", accessRightEndDate="
				+ accessRightEndDate + ", accessRightIds=" + accessRightIds
				+ ", mdTypeId=" + mdTypeId + ", metadataTypeName="
				+ metadataTypeName + ", name_=" + name_ + ", id=" + id
				+ ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}

	
}
