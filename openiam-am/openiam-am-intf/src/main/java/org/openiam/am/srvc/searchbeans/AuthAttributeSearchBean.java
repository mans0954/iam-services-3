package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "AuthAttributeSearchBean", propOrder = {
        "attributeName",
        "providerType"
})
public class AuthAttributeSearchBean extends AbstractSearchBean<AuthAttributeEntity, String> {
    private String attributeName;
    private String providerType;

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
