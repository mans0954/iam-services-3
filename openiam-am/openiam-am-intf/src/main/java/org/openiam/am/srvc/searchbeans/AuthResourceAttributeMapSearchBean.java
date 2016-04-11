package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAttributeMapSearchBean", propOrder = {
        "providerId",
        "amAttributeId"
})
public class AuthResourceAttributeMapSearchBean extends AbstractKeyNameSearchBean<AuthResourceAttributeMap, String> implements SearchBean {
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

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(providerId != null ? providerId : "")
                .append(amAttributeId != null ? amAttributeId : "")
                .append(getKey() != null ? getKey() : "")
                .toString();    }
}
