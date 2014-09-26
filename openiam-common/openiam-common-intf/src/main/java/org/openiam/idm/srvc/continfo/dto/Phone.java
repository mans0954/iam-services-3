package org.openiam.idm.srvc.continfo.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

// Generated Jun 12, 2007 10:46:13 PM by Hibernate Tools 3.2.0.beta8

/**
 * Phone transfer object
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phone", propOrder = {
        "isActive",
        "areaCd",
        "countryCd",
        "description",
        "isDefault",
        "parentId",
        "parentType",
        "phoneExt",
        "phoneNbr",
        //"phoneType",
        "name",
        "operation",
        "lastUpdate",
        "createDate",
        "metadataTypeId",
        "typeDescription"
})
@DozerDTOCorrespondence(PhoneEntity.class)
public class Phone extends KeyDTO {

    // Fields
	private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    private boolean isActive = true;

    private String areaCd;

    private String countryCd;

    private String description;

    private boolean isDefault = false;

    private String parentType;

    private String phoneExt;

    private String phoneNbr;

    private String name;

    //private String phoneType;

    private String parentId;
    
    private Date lastUpdate;
    
    @XmlSchemaType(name = "dateTime")
    private Date createDate;

    private String metadataTypeId;

    private String typeDescription;
    // Constructors

    /**
     * default constructor
     */
    public Phone() {
    }

    /**
     * minimal constructor
     */
    public Phone(String phoneId) {
        setId(phoneId);
    }


//    public Phone(PhoneEntity phoneEntity) {
//        this.phoneId = phoneEntity.getPhoneId();
//        this.isActive = phoneEntity.getActive();
//        this.areaCd = phoneEntity.getAreaCd();
//        this.countryCd = phoneEntity.getCountryCd();
//        this.description = phoneEntity.getDescription();
//        this.isDefault = phoneEntity.getDefault();
//        this.parentType = phoneEntity.getParentType();
//        this.phoneExt = phoneEntity.getPhoneExt();
//        this.name = phoneEntity.getName();
//        this.phoneType = phoneEntity.getPhoneType();
//        this.parentId = phoneEntity.getParent() != null ? phoneEntity.getParent().getUserId() : "";
//    }

    /**
     * full constructor
     */
    public Phone(String phoneId, String areaCd, String countryCd,
                 String description, String phoneNbr, String phoneExt,
                 boolean isDefault, String addressId) {
        setId(phoneId);
        this.areaCd = areaCd;
        this.countryCd = countryCd;
        this.description = description;
        this.phoneNbr = phoneNbr;
        this.phoneExt = phoneExt;
        this.isDefault = isDefault;
    }

    public void updatePhone(Phone ph) {
        this.areaCd = ph.getAreaCd();
        this.countryCd = ph.getCountryCd();
        this.description = ph.getDescription();
        this.isActive = ph.isActive;
        this.isDefault = ph.getIsDefault();
        this.name = ph.getName();
        this.phoneExt = ph.getPhoneExt();
        this.phoneNbr = ph.getPhoneNbr();
        //this.phoneType = ph.getPhoneType();
        this.metadataTypeId=ph.getMetadataTypeId();
    }

    // Property accessors
    public String getAreaCd() {
        return this.areaCd;
    }

    public void setAreaCd(String areaCd) {
        this.areaCd = areaCd;
    }

    public String getCountryCd() {
        return this.countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNbr() {
        return this.phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    public String getPhoneExt() {
        return this.phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

    /**
     * Returns the Id of the parent that owns this address. The parent may be another entity like a
     * USER, ORGANIZATION, etc
     *
     * @return
     */
    public String getParentId() {
        return this.parentId;
    }

    /**
     * Returns the type of the parent.
     *
     * @return
     */
    public String getParentType() {
        return parentType;
    }

    /**
     * Sets the type of the parent.  While the parent type can be anything you choose, a few
     * constants are defined in the ContactConstants clss.
     *
     * @param parentType
     */
    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    /**
     * Indicates if the address is currently active if the value is
     * true and inactive if the value false.
     *
     * @return
     */
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
    */

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    /**
     * Associates the address with a parent entity, such as USER or ORGANIZATION that owns this address.
     *
     * @return
     */


    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((areaCd == null) ? 0 : areaCd.hashCode());
		result = prime * result
				+ ((countryCd == null) ? 0 : countryCd.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((parentType == null) ? 0 : parentType.hashCode());
		result = prime * result
				+ ((phoneExt == null) ? 0 : phoneExt.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
		/*
		result = prime * result
				+ ((phoneType == null) ? 0 : phoneType.hashCode());\
		*/
        result = prime * result
                + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
        if (!super.equals(obj))
            return false;
		if (getClass() != obj.getClass())
			return false;
		Phone other = (Phone) obj;
		if (areaCd == null) {
			if (other.areaCd != null)
				return false;
		} else if (!areaCd.equals(other.areaCd))
			return false;
		if (countryCd == null) {
			if (other.countryCd != null)
				return false;
		} else if (!countryCd.equals(other.countryCd))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isActive != other.isActive)
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operation != other.operation)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (parentType == null) {
			if (other.parentType != null)
				return false;
		} else if (!parentType.equals(other.parentType))
			return false;
		if (phoneExt == null) {
			if (other.phoneExt != null)
				return false;
		} else if (!phoneExt.equals(other.phoneExt))
			return false;
		if (phoneNbr == null) {
			if (other.phoneNbr != null)
				return false;
		} else if (!phoneNbr.equals(other.phoneNbr))
			return false;
		/*
		if (phoneType == null) {
			if (other.phoneType != null)
				return false;
		} else if (!phoneType.equals(other.phoneType))
			return false;
		*/
        if (metadataTypeId == null) {
            if (other.metadataTypeId != null)
                return false;
        } else if (!metadataTypeId.equals(other.metadataTypeId))
            return false;
		return true;
	}

    @Override
    public String toString() {
        return "Phone{" +
               "operation=" + operation +
               ", isActive=" + isActive +
               ", areaCd='" + areaCd + '\'' +
               ", countryCd='" + countryCd + '\'' +
               ", description='" + description + '\'' +
               ", isDefault=" + isDefault +
               ", parentType='" + parentType + '\'' +
               ", phoneExt='" + phoneExt + '\'' +
               ", phoneNbr='" + phoneNbr + '\'' +
               ", name='" + name + '\'' +
               ", parentId='" + parentId + '\'' +
               ", lastUpdate=" + lastUpdate +
               ", createDate=" + createDate +
               ", metadataTypeId='" + metadataTypeId + '\'' +
               ", typeDescription='" + typeDescription + '\'' +
               ", " + super.toString()+"}";
    }
}
