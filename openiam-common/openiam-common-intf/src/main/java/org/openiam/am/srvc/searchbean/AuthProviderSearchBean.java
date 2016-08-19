package org.openiam.am.srvc.searchbean;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;

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
        "linkableToContentProvider",
        "resourceId",
        "nextAuthProviderId"
})
public class AuthProviderSearchBean extends AbstractKeyNameSearchBean<AuthProvider, String> {
    private String managedSysId;
    private String providerType;
    private Boolean defaultAuthProvider;
    private Boolean linkableToContentProvider;
    private Set<String> contentProviderIds;
    private Set<String> uriPatternIds;
    private String resourceId;
    private String nextAuthProviderId;

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

    public String getNextAuthProviderId() {
		return nextAuthProviderId;
	}

	public void setNextAuthProviderId(String nextAuthProviderId) {
		this.nextAuthProviderId = nextAuthProviderId;
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

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((contentProviderIds == null) ? 0 : contentProviderIds
						.hashCode());
		result = prime
				* result
				+ ((defaultAuthProvider == null) ? 0 : defaultAuthProvider
						.hashCode());
		result = prime
				* result
				+ ((linkableToContentProvider == null) ? 0
						: linkableToContentProvider.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime
				* result
				+ ((nextAuthProviderId == null) ? 0 : nextAuthProviderId
						.hashCode());
		result = prime * result
				+ ((providerType == null) ? 0 : providerType.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((uriPatternIds == null) ? 0 : uriPatternIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthProviderSearchBean other = (AuthProviderSearchBean) obj;
		if (contentProviderIds == null) {
			if (other.contentProviderIds != null)
				return false;
		} else if (!contentProviderIds.equals(other.contentProviderIds))
			return false;
		if (defaultAuthProvider == null) {
			if (other.defaultAuthProvider != null)
				return false;
		} else if (!defaultAuthProvider.equals(other.defaultAuthProvider))
			return false;
		if (linkableToContentProvider == null) {
			if (other.linkableToContentProvider != null)
				return false;
		} else if (!linkableToContentProvider
				.equals(other.linkableToContentProvider))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (nextAuthProviderId == null) {
			if (other.nextAuthProviderId != null)
				return false;
		} else if (!nextAuthProviderId.equals(other.nextAuthProviderId))
			return false;
		if (providerType == null) {
			if (other.providerType != null)
				return false;
		} else if (!providerType.equals(other.providerType))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (uriPatternIds == null) {
			if (other.uriPatternIds != null)
				return false;
		} else if (!uriPatternIds.equals(other.uriPatternIds))
			return false;
		return true;
	}

	
}
