package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMap", propOrder = {
        "attributeMapId",
        "providerId",
        "amResAttributeId",
        "amReflectionKey",
        "amResAttributeName",
        "amPolicyUrl"
})
@DozerDTOCorrespondence(AuthResourceAttributeMapEntity.class)
public class AuthResourceAttributeMap extends SSOAttribute {
    private String attributeMapId;
    private String providerId;
    private String amResAttributeId;
    private String amReflectionKey;
    private String amResAttributeName;
    private String amPolicyUrl;



    public String getAttributeMapId() {
        return attributeMapId;
    }

    public void setAttributeMapId(String attributeMapId) {
        this.attributeMapId = attributeMapId;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthResourceAttributeMap that = (AuthResourceAttributeMap) o;

        if (attributeMapId != null ? !attributeMapId.equals(that.attributeMapId) : that.attributeMapId != null)
            return false;
        if (providerId != null ? !providerId.equals(that.providerId) : that.providerId != null) return false;
        if (amResAttributeId != null ? !amResAttributeId.equals(that.amResAttributeId) : that.amResAttributeId != null)
            return false;
        if (amReflectionKey != null ? !amReflectionKey.equals(that.amReflectionKey) : that.amReflectionKey != null)
            return false;
        if (amResAttributeName != null ? !amResAttributeName.equals(that.amResAttributeName) : that.amResAttributeName != null)
            return false;
        return !(amPolicyUrl != null ? !amPolicyUrl.equals(that.amPolicyUrl) : that.amPolicyUrl != null);

    }

    @Override
    public int hashCode() {
        int result = attributeMapId != null ? attributeMapId.hashCode() : 0;
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        result = 31 * result + (amResAttributeId != null ? amResAttributeId.hashCode() : 0);
        result = 31 * result + (amReflectionKey != null ? amReflectionKey.hashCode() : 0);
        result = 31 * result + (amResAttributeName != null ? amResAttributeName.hashCode() : 0);
        result = 31 * result + (amPolicyUrl != null ? amPolicyUrl.hashCode() : 0);
        return result;
    }
}
