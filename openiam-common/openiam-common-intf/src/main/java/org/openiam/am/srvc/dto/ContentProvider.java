package org.openiam.am.srvc.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.comparator.AuthLevelGroupingXrefComparator;
import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProvider", propOrder = {
        "isPublic",
        "domainPattern",
        "isSSL",
        /*"contextPath",*/
        "resourceId",
        "resourceName",
        "resourceCoorelatedName",
        "patternSet",
        "serverSet",
        "managedSysId",
        "managedSysName",
        "url",
        "themeId",
        "groupingXrefs",
        "showOnApplicationPage",
        "authProviderId",
        "authCookieName",
        "authCookieDomain"
})
@DozerDTOCorrespondence(ContentProviderEntity.class)
public class ContentProvider extends KeyNameDTO {
	private boolean isPublic;
	private String domainPattern;
	private Boolean isSSL;
	private String managedSysId;
	private String managedSysName;
    //private String contextPath;
	private String resourceId;
    private String resourceName;
    private String resourceCoorelatedName;
	private Set<URIPattern> patternSet;
	private String url;
	private Set<ContentProviderServer> serverSet;
	private String themeId;
	private boolean showOnApplicationPage = true;
	private Set<AuthLevelGroupingContentProviderXref> groupingXrefs;
	private String authProviderId;
	private String authCookieName;
	private String authCookieDomain;
	
	/*
	 * federation variables.  Internal use only
	 */
	@XmlTransient
	private int serverIdx = 0;
	
	@XmlTransient
	private List<RoundRobinServer> serverList;
	
	public boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public String getDomainPattern() {
		return domainPattern;
	}
	public void setDomainPattern(String domainPattern) {
		this.domainPattern = domainPattern;
	}
	public Boolean getIsSSL() {
		return isSSL;
	}
	public void setIsSSL(Boolean isSSL) {
		this.isSSL = isSSL;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Set<URIPattern> getPatternSet() {
		return patternSet;
	}
	public void setPatternSet(Set<URIPattern> patternSet) {
		this.patternSet = patternSet;
	}

	/*
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    */

    public Set<ContentProviderServer> getServerSet() {
		return serverSet;
	}
	public void setServerSet(Set<ContentProviderServer> serverSet) {
		this.serverSet = serverSet;
		if(serverSet != null) {
			this.serverList = new LinkedList<>();
			for(final ContentProviderServer server : this.serverSet) {
				this.serverList.add(new RoundRobinServer(server));
			}
		}
	}
	
	public synchronized RoundRobinServer getNextServer() {
		RoundRobinServer retVal = null;
		if(CollectionUtils.isNotEmpty(serverList)) {
			final int size = serverList.size();
			retVal = serverList.get(serverIdx % size);
			serverIdx++;
		}
		return retVal;
	}
	
	public String getManagedSysId() {
		return managedSysId;
	}
	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getThemeId() {
		return themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
	
	public List<AuthLevelGroupingContentProviderXref> getOrderedGroupingXrefs() {
		List<AuthLevelGroupingContentProviderXref> sorted = null;
		if(groupingXrefs != null) {
			sorted = new ArrayList<AuthLevelGroupingContentProviderXref>(groupingXrefs);
			Collections.sort(sorted, new AuthLevelGroupingXrefComparator());
		}
		return sorted;
	}
	
	public Set<AuthLevelGroupingContentProviderXref> getGroupingXrefs() {
		return groupingXrefs;
	}
	public void setGroupingXrefs(
			Set<AuthLevelGroupingContentProviderXref> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}
	public boolean isShowOnApplicationPage() {
		return showOnApplicationPage;
	}
	public void setShowOnApplicationPage(boolean showOnApplicationPage) {
		this.showOnApplicationPage = showOnApplicationPage;
	}
	
	public String getResourceCoorelatedName() {
		return resourceCoorelatedName;
	}
	public void setResourceCoorelatedName(String resourceCoorelatedName) {
		this.resourceCoorelatedName = resourceCoorelatedName;
	}
	public String getAuthProviderId() {
		return authProviderId;
	}
	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}
	
	public String getManagedSysName() {
		return managedSysName;
	}
	public void setManagedSysName(String managedSysName) {
		this.managedSysName = managedSysName;
	}
	
	public String getAuthCookieName() {
		return authCookieName;
	}

	public void setAuthCookieName(String authCookieName) {
		this.authCookieName = authCookieName;
	}

	public String getAuthCookieDomain() {
		return authCookieDomain;
	}

	public void setAuthCookieDomain(String authCookieDomain) {
		this.authCookieDomain = authCookieDomain;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authProviderId == null) ? 0 : authProviderId.hashCode());
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
		result = prime * result + ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((managedSysName == null) ? 0 : managedSysName.hashCode());
		result = prime
				* result
				+ ((resourceCoorelatedName == null) ? 0
						: resourceCoorelatedName.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((resourceName == null) ? 0 : resourceName.hashCode());
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((authCookieName == null) ? 0 : authCookieName.hashCode());
		result = prime * result + ((authCookieDomain == null) ? 0 : authCookieDomain.hashCode());
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
		ContentProvider other = (ContentProvider) obj;
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		if (domainPattern == null) {
			if (other.domainPattern != null)
				return false;
		} else if (!domainPattern.equals(other.domainPattern))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (isSSL == null) {
			if (other.isSSL != null)
				return false;
		} else if (!isSSL.equals(other.isSSL))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (managedSysName == null) {
			if (other.managedSysName != null)
				return false;
		} else if (!managedSysName.equals(other.managedSysName))
			return false;
		if (resourceCoorelatedName == null) {
			if (other.resourceCoorelatedName != null)
				return false;
		} else if (!resourceCoorelatedName.equals(other.resourceCoorelatedName))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		if (showOnApplicationPage != other.showOnApplicationPage)
			return false;
		if (themeId == null) {
			if (other.themeId != null)
				return false;
		} else if (!themeId.equals(other.themeId))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (authCookieName == null) {
			if (other.authCookieName != null)
				return false;
		} else if (!authCookieName.equals(other.authCookieName))
			return false;
		if (authCookieDomain == null) {
			if (other.authCookieDomain != null)
				return false;
		} else if (!authCookieDomain.equals(other.authCookieDomain))
			return false;
		return true;
	}
	
	
}
