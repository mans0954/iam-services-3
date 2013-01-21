package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderSearchBean", propOrder = {
        "providerName",
        "managedSysId",
        "providerType"
})
public class AuthProviderSearchBean extends AbstractSearchBean<AuthProviderEntity, String> {
    private String providerName;
    private String managedSysId;
    private String providerType;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }
}
