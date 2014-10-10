package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthAttribute", propOrder = {
        "providerType",
        "description",
        "dataType",
        "isRequired",
        "defaultValue"
})
@DozerDTOCorrespondence(AuthAttributeEntity.class)
public class AuthAttribute extends KeyNameDTO {
    private String providerType;
    private String description;
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;
    private boolean isRequired = false;
    private String defaultValue;

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isRequired ? 1231 : 1237);
		result = prime * result
				+ ((providerType == null) ? 0 : providerType.hashCode());
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
		AuthAttribute other = (AuthAttribute) obj;
		if (dataType != other.dataType)
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isRequired != other.isRequired)
			return false;
		if (providerType == null) {
			if (other.providerType != null)
				return false;
		} else if (!providerType.equals(other.providerType))
			return false;
		return true;
	}
    
    
}
