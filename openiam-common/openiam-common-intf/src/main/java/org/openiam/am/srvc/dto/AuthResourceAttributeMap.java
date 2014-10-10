package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMap", propOrder = {
        "providerId",
        "amResAttributeId",
        "amReflectionKey",
        "amResAttributeName",
        "amPolicyUrl"
})
@DozerDTOCorrespondence(AuthResourceAttributeMapEntity.class)
public class AuthResourceAttributeMap extends SSOAttribute {
    private String providerId;
    private String amResAttributeId;
    private String amReflectionKey;
    private String amResAttributeName;
    private String amPolicyUrl;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAmResAttributeId() {
        return amResAttributeId;
    }

    public void setAmResAttributeId(String amResAttributeId) {
        this.amResAttributeId = amResAttributeId;
    }

    public String getAmReflectionKey() {
        return amReflectionKey;
    }

    public void setAmReflectionKey(String amReflectionKey) {
        this.amReflectionKey = amReflectionKey;
    }

    public String getAmResAttributeName() {
        return amResAttributeName;
    }

    public void setAmResAttributeName(String amResAttributeName) {
        this.amResAttributeName = amResAttributeName;
    }

    public String getAmPolicyUrl() {
        return amPolicyUrl;
    }

    public void setAmPolicyUrl(String amPolicyUrl) {
        this.amPolicyUrl = amPolicyUrl;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amPolicyUrl == null) ? 0 : amPolicyUrl.hashCode());
		result = prime * result
				+ ((amReflectionKey == null) ? 0 : amReflectionKey.hashCode());
		result = prime
				* result
				+ ((amResAttributeId == null) ? 0 : amResAttributeId.hashCode());
		result = prime
				* result
				+ ((amResAttributeName == null) ? 0 : amResAttributeName
						.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
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
		AuthResourceAttributeMap other = (AuthResourceAttributeMap) obj;
		if (amPolicyUrl == null) {
			if (other.amPolicyUrl != null)
				return false;
		} else if (!amPolicyUrl.equals(other.amPolicyUrl))
			return false;
		if (amReflectionKey == null) {
			if (other.amReflectionKey != null)
				return false;
		} else if (!amReflectionKey.equals(other.amReflectionKey))
			return false;
		if (amResAttributeId == null) {
			if (other.amResAttributeId != null)
				return false;
		} else if (!amResAttributeId.equals(other.amResAttributeId))
			return false;
		if (amResAttributeName == null) {
			if (other.amResAttributeName != null)
				return false;
		} else if (!amResAttributeName.equals(other.amResAttributeName))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		return true;
	}
    
    
}
