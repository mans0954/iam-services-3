package org.openiam.am.srvc.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderSearchBean", propOrder = {
        "managedSysId",
        "providerType",
        "defaultAuthProvider",
        "contentProviderIds",
        "uriPatternIds",
        "linkableToContentProvider"
})
public class AuthProviderSearchBean extends AbstractKeyNameSearchBean<AuthProvider, String> {
    private String managedSysId;
    private String providerType;
    private Boolean defaultAuthProvider;
    private Boolean linkableToContentProvider;
    private Set<String> contentProviderIds;
    private Set<String> uriPatternIds;

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

	public Boolean getDefaultAuthProvider() {
		return defaultAuthProvider;
	}

	public void setDefaultAuthProvider(Boolean defaultAuthProvider) {
		this.defaultAuthProvider = defaultAuthProvider;
	}

    public Set<String> getContentProviderIds() {
        return contentProviderIds;
    }

    public void setContentProviderIds(Set<String> contentProviderIds) {
        this.contentProviderIds = contentProviderIds;
    }

    public Set<String> getUriPatternIds() {
        return uriPatternIds;
    }

    public void setUriPatternIds(Set<String> uriPatternIds) {
        this.uriPatternIds = uriPatternIds;
    }

    public void addContentProviderId(String id){
        if(contentProviderIds==null)
            contentProviderIds = new HashSet<>();
        contentProviderIds.add(id);
    }

    public void addUriPatternId(String id){
        if(uriPatternIds==null)
            uriPatternIds = new HashSet<>();
        uriPatternIds.add(id);
    }

	public Boolean getLinkableToContentProvider() {
		return linkableToContentProvider;
	}

	public void setLinkableToContentProvider(Boolean linkableToContentProvider) {
		this.linkableToContentProvider = linkableToContentProvider;
	}
    
    
}
