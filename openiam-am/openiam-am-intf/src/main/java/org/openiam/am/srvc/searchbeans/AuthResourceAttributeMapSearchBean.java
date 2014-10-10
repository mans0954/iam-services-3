package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMapSearchBean", propOrder = {
        "providerId",
        "amAttributeId"
})
public class AuthResourceAttributeMapSearchBean extends AbstractKeyNameSearchBean<AuthResourceAttributeMap, String> {
    private String providerId;
    private String amAttributeId;

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
}
