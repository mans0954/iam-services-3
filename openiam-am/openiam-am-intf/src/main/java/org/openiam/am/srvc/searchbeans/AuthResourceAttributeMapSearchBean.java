package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMapSearchBean", propOrder = {
        "providerId",
        "targetAttributeName",
        "amAttributeId"
})
public class AuthResourceAttributeMapSearchBean extends AbstractSearchBean<AuthResourceAttributeMap, String> {
    private String providerId;
    private String targetAttributeName;
    private String amAttributeId;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTargetAttributeName() {
        return targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
    }

    public String getAmAttributeId() {
        return amAttributeId;
    }

    public void setAmAttributeId(String amAttributeId) {
        this.amAttributeId = amAttributeId;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(providerId != null ? providerId : "")
                .append(targetAttributeName != null ? targetAttributeName : "")
                .append(amAttributeId != null ? amAttributeId : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
