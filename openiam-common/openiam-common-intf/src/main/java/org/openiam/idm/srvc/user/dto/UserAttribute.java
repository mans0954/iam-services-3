package org.openiam.idm.srvc.user.dto;

// Generated Jun 12, 2007 10:46:13 PM by Hibernate Tools 3.2.0.beta8


import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;

/**
 * UserAttribute represents an individual attribute that is associated with a user. A user may
 * have many attributes. A UserAttribute should also be associated
 * with a MetadataElement. This approach is used as a way to extend the attributes associated with
 * a user without having to extend the schema.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAttribute", propOrder = {
        "id",
        "metadataElementId",
        "name",
        "userId",
        "value",
        "attrGroup",
        "operation",
        "required"/*,
        "elementId"*/
})
@DozerDTOCorrespondence(UserAttributeEntity.class)
public class UserAttribute extends BaseObject {

    protected String id;

    protected String metadataElementId;

    protected String name;

    protected String value;

    protected String attrGroup;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    protected Boolean required = Boolean.TRUE;

    protected String userId;
    
    //private String elementId;
    // Constructors

    /**
     * default constructor
     */
    public UserAttribute() {
    }

    /**
     * minimal constructor
     */
    public UserAttribute(String id) {
        this.id = id;
    }

//    public UserAttribute(UserAttributeEntity userAtributeEntity) {
//        this.id = userAtributeEntity.getId();
//        this.metadataElementId = userAtributeEntity.getMetadataElementId();
//        this.name = userAtributeEntity.getName();
//        this.value = userAtributeEntity.getValue();
//        this.userId = userAtributeEntity.getUser() != null ? userAtributeEntity.getUser().getUserId() : "";
//    }

    public UserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        this.id = null;
    }

    public UserAttribute(String id, String userId,
                         String metadataElement, String name, String value) {
        this.id = id;
        this.userId = userId;
        this.metadataElementId = metadataElement;
        this.name = name;
        this.value = value;
    }


    public void updateUserAttribute(UserAttribute attr) {
        this.attrGroup = attr.getAttrGroup();
        this.metadataElementId = attr.getMetadataElementId();
        this.name = attr.getName();
        this.value = attr.getValue();
    }

    // Property accessors
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getAttrGroup() {
        return attrGroup;
    }

    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }



    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /*
    public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	*/

	@Override
    public String toString() {
        return "UserAttribute{" +
                "id='" + id + '\'' +
                ", metadataElementId='" + metadataElementId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId +
                ", value='" + value + '\'' +
                ", attrGroup='" + attrGroup + '\'' +
                ", operation=" + operation +
                '}';
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attrGroup == null) ? 0 : attrGroup.hashCode());
		/*
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		*/
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((metadataElementId == null) ? 0 : metadataElementId
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((required == null) ? 0 : required.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		UserAttribute other = (UserAttribute) obj;
		if (attrGroup == null) {
			if (other.attrGroup != null)
				return false;
		} else if (!attrGroup.equals(other.attrGroup))
			return false;
		/*
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		*/
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataElementId == null) {
			if (other.metadataElementId != null)
				return false;
		} else if (!metadataElementId.equals(other.metadataElementId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operation != other.operation)
			return false;
		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

    
}
