package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderSearchBean", propOrder = {
        "managedSysId",
        "providerType"
})
public class AuthProviderSearchBean extends AbstractKeyNameSearchBean<AuthProvider, String> {
    private String managedSysId;
    private String providerType;

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
