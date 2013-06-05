package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleSearchBean", propOrder = {
        "name",
        "serviceId",
        "isRootsOnly"
})
public class RoleSearchBean extends AbstractSearchBean<Role, String> implements SearchBean<Role, String>, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String serviceId;
	private Boolean isRootsOnly;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsRootsOnly() {
		return isRootsOnly;
	}

	public void setIsRootsOnly(boolean isRootsOnly) {
		this.isRootsOnly = isRootsOnly;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isRootsOnly == null) ? 0 : isRootsOnly.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
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
		RoleSearchBean other = (RoleSearchBean) obj;
		if (isRootsOnly == null) {
			if (other.isRootsOnly != null)
				return false;
		} else if (!isRootsOnly.equals(other.isRootsOnly))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"RoleSearchBean [name=%s, serviceId=%s, isRootsOnly=%s]", name,
				serviceId, isRootsOnly);
	}

	
}
