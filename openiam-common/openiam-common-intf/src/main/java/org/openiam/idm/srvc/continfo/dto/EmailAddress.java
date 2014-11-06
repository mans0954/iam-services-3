package org.openiam.idm.srvc.continfo.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * EmailAddress transfer object
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emailAddress", propOrder = {
        "isActive",
        "description",
        "emailAddress",
        "isDefault",
        "parentId",
        "name",
        "operation",
        "lastUpdate",
        "createDate",
        "metadataTypeId",
        "typeDescription"
})
@DozerDTOCorrespondence(EmailAddressEntity.class)
public class EmailAddress extends KeyDTO {

    // Fields
	private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    private boolean isActive = true;

    private String description;

    private String emailAddress;

    private boolean isDefault = false;

    private String name;

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
    public EmailAddress() {
    }

    /**
     * minimal constructor
     */
    public EmailAddress(String emailId) {
        setId(emailId);
    }

    /**
     * full constructor
     */
    public EmailAddress(String emailId, String description,
                        String emailAddress, boolean isDefault) {
        setId(emailId);
        this.description = description;
        this.emailAddress = emailAddress;
        this.isDefault = isDefault;
    }

    public void updateEmailAddress(EmailAddress emailAdr) {
        this.description = emailAdr.getDescription();
        this.emailAddress = emailAdr.getEmailAddress();
        this.isActive = emailAdr.getIsActive();
        this.isDefault = emailAdr.getIsDefault();
        this.name = emailAdr.getName();
        this.metadataTypeId=emailAdr.getMetadataTypeId();
    }

    public String getParentId() {
        return parentId;
    }

    // Property accessors
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Indicates if the address is currently active if the value is
     * true and inactive if the value false.
     *
     * @return
     */
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
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
		EmailAddress other = (EmailAddress) obj;
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
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
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

        if (metadataTypeId == null) {
            if (other.metadataTypeId != null)
                return false;
        } else if (!metadataTypeId.equals(other.metadataTypeId))
            return false;
		return true;
	}

    @Override
    public String toString() {
        return "EmailAddress{" +
               "operation=" + operation +
               ", isActive=" + isActive +
               ", description='" + description + '\'' +
               ", emailAddress='" + emailAddress + '\'' +
               ", isDefault=" + isDefault +
               ", name='" + name + '\'' +
               ", parentId='" + parentId + '\'' +
               ", lastUpdate=" + lastUpdate +
               ", createDate=" + createDate +
               ", metadataTypeId='" + metadataTypeId + '\'' +
               ", typeDescription='" + typeDescription + '\'' +
               ", " + super.toString()+"}";
    }
}
