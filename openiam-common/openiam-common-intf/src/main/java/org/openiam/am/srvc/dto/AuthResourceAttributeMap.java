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
}
