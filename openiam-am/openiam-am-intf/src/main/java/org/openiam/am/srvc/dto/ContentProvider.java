package org.openiam.am.srvc.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProvider", propOrder = {
        "id",
        "name",
        "isPublic",
        "authLevel",
        "domainPattern",
        "isSSL",
        "contextPath",
        "resourceId",
        "resourceName",
        "patternSet",
        "serverSet"
})
@DozerDTOCorrespondence(ContentProviderEntity.class)
public class ContentProvider implements Serializable {

	private String id;
	private String name;
	private boolean isPublic;
	private AuthLevel authLevel;
	private String domainPattern;
	private Boolean isSSL;
    private String contextPath;
	private String resourceId;
    private String resourceName;
	private Set<URIPattern> patternSet;
	private Set<ContentProviderServer> serverSet;
	
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
	public AuthLevel getAuthLevel() {
		return authLevel;
	}
	public void setAuthLevel(AuthLevel authLevel) {
		this.authLevel = authLevel;
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

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authLevel == null) ? 0 : authLevel.hashCode());
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
        result = prime * result
                 + ((contextPath == null) ? 0 : contextPath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
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
		if (authLevel == null) {
			if (other.authLevel != null)
				return false;
		} else if (!authLevel.equals(other.authLevel))
			return false;
		if (domainPattern == null) {
			if (other.domainPattern != null)
				return false;
		} else if (!domainPattern.equals(other.domainPattern))
			return false;
        if (contextPath == null) {
            if (other.contextPath != null)
                return false;
        } else if (!contextPath.equals(other.contextPath))
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
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("ContentProvider [id=%s, name=%s, isPublic=%s, authLevel=%s, domainPattern=%s, isSSL=%s, resourceId=%s]",
						id, name, isPublic, authLevel, domainPattern, isSSL,
						resourceId);
	}
	
	
}
