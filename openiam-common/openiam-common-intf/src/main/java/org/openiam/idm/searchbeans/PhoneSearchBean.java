package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.Phone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhoneSearchBean", propOrder = {
        "parentId",
        /*"parentType",*/
        "phoneAreaCd",
        "phoneNbr",
        "metadataTypeId",
        "isDefault"
})
public class PhoneSearchBean extends AbstractSearchBean<Phone, String> {
    private String parentId;
    //private String parentType;
    private String phoneNbr;
    private String phoneAreaCd;
    private String metadataTypeId;
    private Boolean isDefault;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /*
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
    */
    
    public String getPhoneAreaCd() {
        return phoneAreaCd;
    }

    public void setPhoneAreaCd(String phoneAreaCd) {
        this.phoneAreaCd = phoneAreaCd;
    }

    public String getPhoneNbr() {
        return phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isDefault == null) ? 0 : isDefault.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((phoneAreaCd == null) ? 0 : phoneAreaCd.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
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
		PhoneSearchBean other = (PhoneSearchBean) obj;
		if (isDefault == null) {
			if (other.isDefault != null)
				return false;
		} else if (!isDefault.equals(other.isDefault))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (phoneAreaCd == null) {
			if (other.phoneAreaCd != null)
				return false;
		} else if (!phoneAreaCd.equals(other.phoneAreaCd))
			return false;
		if (phoneNbr == null) {
			if (other.phoneNbr != null)
				return false;
		} else if (!phoneNbr.equals(other.phoneNbr))
			return false;
		return true;
	}

    
}
