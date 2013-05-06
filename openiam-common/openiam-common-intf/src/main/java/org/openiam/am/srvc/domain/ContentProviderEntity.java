package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CONTENT_PROVIDER")
@DozerDTOCorrespondence(ContentProvider.class)
public class ContentProviderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CONTENT_PROVIDER_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name = "CONTENT_PROVIDER_NAME", length = 100, nullable = false)
	private String name;
	
	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="MIN_AUTH_LEVEL", referencedColumnName = "AUTH_LEVEL_ID")
	private AuthLevelEntity minAuthLevel;
	
	@Column(name = "DOMAIN_PATTERN", length = 100, nullable = false)
	private String domainPattern;
	
	@Column(name = "IS_SSL", nullable = true)
	@Type(type = "yes_no")
	private Boolean isSSL;

	/*
    @Column(name = "CONTEXT_PATH", nullable = false)
    private String contextPath;
    */

	/*
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    */
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "contentProvider")
	private Set<ContentProviderServerEntity> serverSet;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "contentProvider")
	private Set<URIPatternEntity> patternSet;

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

	public AuthLevelEntity getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(AuthLevelEntity minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
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

	public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	public Set<ContentProviderServerEntity> getServerSet() {
		return serverSet;
	}

	public void setServerSet(Set<ContentProviderServerEntity> serverSet) {
		this.serverSet = serverSet;
	}

	public Set<URIPatternEntity> getPatternSet() {
		return patternSet;
	}

	public void setPatternSet(Set<URIPatternEntity> patternSet) {
		this.patternSet = patternSet;
	}

	/*
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    */

    /*
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    */

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
		/*
        result = prime * result
                 + ((contextPath == null) ? 0 : contextPath.hashCode());
		*/
		result = prime * result
				+ ((minAuthLevel == null) ? 0 : minAuthLevel.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		ContentProviderEntity other = (ContentProviderEntity) obj;
		if (domainPattern == null) {
			if (other.domainPattern != null)
				return false;
		} else if (!domainPattern.equals(other.domainPattern))
			return false;
		/*
        if (contextPath == null) {
            if (other.contextPath != null)
                return false;
        } else if (!contextPath.equals(other.contextPath))
            return false;
		*/
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
		if (minAuthLevel == null) {
			if (other.minAuthLevel != null)
				return false;
		} else if (!minAuthLevel.equals(other.minAuthLevel))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
}
