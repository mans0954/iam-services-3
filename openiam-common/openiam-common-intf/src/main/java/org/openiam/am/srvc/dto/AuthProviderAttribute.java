package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderAttribute", propOrder = {
        "providerId",
        "attributeId",
        "attributeName",
        "value",
        "defaultValue",
        "dataType"
})
@DozerDTOCorrespondence(AuthProviderAttributeEntity.class)
public class AuthProviderAttribute extends KeyDTO {
    private String providerId;
    private String attributeId;
    private String attributeName;
    private String value;
    private String defaultValue;
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
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
				+ ((attributeId == null) ? 0 : attributeId.hashCode());
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
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
		AuthProviderAttribute other = (AuthProviderAttribute) obj;
		if (attributeId == null) {
			if (other.attributeId != null)
				return false;
		} else if (!attributeId.equals(other.attributeId))
			return false;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (dataType != other.dataType)
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthProviderAttribute [providerId=" + providerId
				+ ", attributeId=" + attributeId + ", attributeName="
				+ attributeName + ", value=" + value + ", defaultValue="
				+ defaultValue + ", dataType=" + dataType + ", id=" + id
				+ ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}

    
}
