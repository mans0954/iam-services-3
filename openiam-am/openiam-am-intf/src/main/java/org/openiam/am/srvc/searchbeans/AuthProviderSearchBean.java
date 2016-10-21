package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderSearchBean", propOrder = {
        "providerName",
        "managedSysId",
        "providerType",
        "linkableToContentProvider",
        "nextAuthProviderId"
})
public class AuthProviderSearchBean extends AbstractSearchBean<AuthProvider, String> {
    private String providerName;
    private String managedSysId;
    private String providerType;
    private String nextAuthProviderId;
    private Boolean linkableToContentProvider;

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

    public Boolean getLinkableToContentProvider() {
        return linkableToContentProvider;
    }

    public void setLinkableToContentProvider(Boolean linkableToContentProvider) {
        this.linkableToContentProvider = linkableToContentProvider;
    }

    public String getNextAuthProviderId() {
		return nextAuthProviderId;
	}

	public void setNextAuthProviderId(String nextAuthProviderId) {
		this.nextAuthProviderId = nextAuthProviderId;
	}

	@Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(providerName != null ? providerName : "")
                .append(managedSysId != null ? managedSysId : "")
                .append(providerType != null ? providerType : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }

}
