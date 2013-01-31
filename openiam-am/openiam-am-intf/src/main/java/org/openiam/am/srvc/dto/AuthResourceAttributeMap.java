package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAMAttribute", propOrder = {
        "attributeMapId",
        "providerId",
        "amAttributeId",
        "amAttributeName",
        "amPolicyUrl"
})
@DozerDTOCorrespondence(AuthResourceAttributeMapEntity.class)
public class AuthResourceAttributeMap extends Attribute {
    private String attributeMapId;
    private String providerId;
    private String amAttributeId;
    private String amAttributeName;
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

    public String getAmAttributeId() {
        return amAttributeId;
    }

    public void setAmAttributeId(String amAttributeId) {
        this.amAttributeId = amAttributeId;
    }

    public String getAmAttributeName() {
        return amAttributeName;
    }

    public void setAmAttributeName(String amAttributeName) {
        this.amAttributeName = amAttributeName;
    }

    public String getAmPolicyUrl() {
        return amPolicyUrl;
    }

    public void setAmPolicyUrl(String amPolicyUrl) {
        this.amPolicyUrl = amPolicyUrl;
    }
}
