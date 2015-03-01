package org.openiam.am.srvc.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.comparator.AuthLevelGroupingXrefComparator;
import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProvider", propOrder = {
        "id",
        "name",
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
        "url",
        "themeId",
        "groupingXrefs",
        "showOnApplicationPage",
        "loginURL",
        "postbackURLParamName"
})
@DozerDTOCorrespondence(ContentProviderEntity.class)
public class ContentProvider implements Serializable {

	private String id;
	private String name;
	private boolean isPublic;
	private String domainPattern;
	private Boolean isSSL;
	private String managedSysId;
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
	private String loginURL;
	private String postbackURLParamName;
	
	/*
	 * federation variables.  Internal use only
	 */
	@XmlTransient
	private int serverIdx = 0;
	
	@XmlTransient
	private List<ContentProviderServer> serverList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
			this.serverList = new ArrayList<ContentProviderServer>(serverSet);
		}
	}
	
	public synchronized ContentProviderServer getNextServer() {
		ContentProviderServer retVal = null;
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
	
	public String getLoginURL() {
		return loginURL;
	}
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	
	public String getPostbackURLParamName() {
		return postbackURLParamName;
	}
	public void setPostbackURLParamName(String postbackURLParamName) {
		this.postbackURLParamName = postbackURLParamName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((loginURL == null) ? 0 : loginURL.hashCode());
		result = prime * result + ((postbackURLParamName == null) ? 0 : postbackURLParamName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentProvider other = (ContentProvider) obj;
		if (domainPattern == null) {
			if (other.domainPattern != null)
				return false;
		} else if (!domainPattern.equals(other.domainPattern))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
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
		
		if (loginURL == null) {
			if (other.loginURL != null)
				return false;
		} else if (!loginURL.equals(other.loginURL))
			return false;
		
		if (postbackURLParamName == null) {
			if (other.postbackURLParamName != null)
				return false;
		} else if (!postbackURLParamName.equals(other.postbackURLParamName))
			return false;
		
		return true;
	}

	
	
}
