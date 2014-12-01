package org.openiam.idm.srvc.user.dto;

// Generated Jun 12, 2007 10:46:13 PM by Hibernate Tools 3.2.0.beta8


import org.openiam.base.AbstractAttributeDTO;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * UserAttribute represents an individual attribute that is associated with a user. A user may
 * have many attributes. A UserAttribute should also be associated
 * with a MetadataElement. This approach is used as a way to extend the attributes associated with
 * a user without having to extend the schema.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAttribute", propOrder = {
        "userId",
        "values",
        "isMultivalued",
        "operation"
})
@DozerDTOCorrespondence(UserAttributeEntity.class)
public class UserAttribute extends AbstractAttributeDTO {
	
    protected List<String> values = new ArrayList<String>();
    protected Boolean isMultivalued = Boolean.FALSE;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
    protected String userId;

    public UserAttribute() {
    }

    public UserAttribute(String id) {
        this.id = id;
    }

    public UserAttribute(String name, String value) {
        this.setName(name);
        this.value = value;
        this.id = null;
    }

    public UserAttribute(String id, String userId,
                         String metadataId, String name, String value) {
        this.id = id;
        this.userId = userId;
        this.metadataId = metadataId;
        this.setName(name);
        this.value = value;
    }


    public void updateUserAttribute(UserAttribute attr) {
        this.metadataId = attr.getMetadataId();
        this.setName(attr.getName());
        this.value = attr.getValue();
    }

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Boolean getIsMultivalued() {
		return isMultivalued;
	}

	public void setIsMultivalued(Boolean isMultivalued) {
		this.isMultivalued = isMultivalued;
	}

	public AttributeOperationEnum getOperation() {
		return operation;
	}

	public void setOperation(AttributeOperationEnum operation) {
		this.operation = operation;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isMultivalued == null) ? 0 : isMultivalued.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		if (isMultivalued == null) {
			if (other.isMultivalued != null)
				return false;
		} else if (!isMultivalued.equals(other.isMultivalued))
			return false;
		if (operation != other.operation)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("UserAttribute [values=%s, isMultivalued=%s, operation=%s, userId=%s, toString()=%s]",
						values, isMultivalued, operation, userId,
						super.toString());
	}

    
}
