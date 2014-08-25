package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdminResourceDTO", propOrder = {
	"adminResourceId",
	"adminResourceName",
	"adminResourceCoorelatedName"
})
public abstract class AdminResourceDTO extends AbstractMetadataTypeDTO {

	private String adminResourceId;
    private String adminResourceName;
    private String adminResourceCoorelatedName;
    
    public String getAdminResourceId() {
		return adminResourceId;
	}

	public void setAdminResourceId(String adminResourceId) {
		this.adminResourceId = adminResourceId;
	}

	public String getAdminResourceName() {
		return adminResourceName;
	}

	public void setAdminResourceName(String adminResourceName) {
		this.adminResourceName = adminResourceName;
	}

	public String getAdminResourceCoorelatedName() {
		return adminResourceCoorelatedName;
	}

	public void setAdminResourceCoorelatedName(String adminResourceCoorelatedName) {
		this.adminResourceCoorelatedName = adminResourceCoorelatedName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((adminResourceCoorelatedName == null) ? 0
						: adminResourceCoorelatedName.hashCode());
		result = prime * result
				+ ((adminResourceId == null) ? 0 : adminResourceId.hashCode());
		result = prime
				* result
				+ ((adminResourceName == null) ? 0 : adminResourceName
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
		AdminResourceDTO other = (AdminResourceDTO) obj;
		if (adminResourceCoorelatedName == null) {
			if (other.adminResourceCoorelatedName != null)
				return false;
		} else if (!adminResourceCoorelatedName
				.equals(other.adminResourceCoorelatedName))
			return false;
		if (adminResourceId == null) {
			if (other.adminResourceId != null)
				return false;
		} else if (!adminResourceId.equals(other.adminResourceId))
			return false;
		if (adminResourceName == null) {
			if (other.adminResourceName != null)
				return false;
		} else if (!adminResourceName.equals(other.adminResourceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AdminResourceDTO [adminResourceId=%s, adminResourceName=%s, adminResourceCoorelatedName=%s]",
						adminResourceId, adminResourceName,
						adminResourceCoorelatedName);
	}

	
}
