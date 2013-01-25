package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderAttribute", propOrder = {
        "providerAttributeId",
        "providerId",
        "attributeId",
        "attributeName",
        "value",
        "dataType"
})
@DozerDTOCorrespondence(AuthProviderAttributeEntity.class)
public class AuthProviderAttribute implements Serializable {
    private String providerAttributeId;
    private String providerId;
    private String attributeId;
    private String attributeName;
    private String value;
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;

    public String getProviderAttributeId() {
        return providerAttributeId;
    }

    public void setProviderAttributeId(String providerAttributeId) {
        this.providerAttributeId = providerAttributeId;
    }

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeId == null) ? 0 : attributeId.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthProviderAttribute other = (AuthProviderAttribute) obj;
		if (attributeId == null) {
			if (other.attributeId != null)
				return false;
		} else if (!attributeId.equals(other.attributeId))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		return true;
	}
}
