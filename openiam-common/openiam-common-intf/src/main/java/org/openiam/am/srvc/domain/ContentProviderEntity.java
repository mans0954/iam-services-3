package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

import javax.persistence.*;

import java.util.Set;

@Entity
@Table(name = "CONTENT_PROVIDER")
@DozerDTOCorrespondence(ContentProvider.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "CONTENT_PROVIDER_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "CONTENT_PROVIDER_NAME", length = 100, nullable = false))
})
public class ContentProviderEntity extends AbstractKeyNameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic;
	
	@Column(name = "SHOW_ON_APP_PAGE", nullable = false)
	@Type(type = "yes_no")
	private boolean showOnApplicationPage = true;
	
	@Column(name = "DOMAIN_PATTERN", length = 100, nullable = false)
	private String domainPattern;
	
	@Column(name = "IS_SSL", nullable = true)
	@Type(type = "yes_no")
	private Boolean isSSL;
	
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "UI_THEME_ID", referencedColumnName = "UI_THEME_ID", insertable = true, updatable = true, nullable=true)
    private UIThemeEntity uiTheme;

	/*
    @Column(name = "CONTEXT_PATH", nullable = false)
    private String contextPath;
    */

	/*
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    */
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=false)
    private AuthProviderEntity authProvider;
	
    /*
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=false)
	private ManagedSysEntity managedSystem;
	*/
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "contentProvider")
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<ContentProviderServerEntity> serverSet;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "contentProvider")
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<URIPatternEntity> patternSet;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "contentProvider", fetch = FetchType.LAZY)
	@OrderBy("order ASC")
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelGroupingContentProviderXrefEntity> groupingXrefs;
	
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

    public UIThemeEntity getUiTheme() {
		return uiTheme;
	}

	public void setUiTheme(UIThemeEntity uiTheme) {
		this.uiTheme = uiTheme;
	}

	/*
	public ManagedSysEntity getManagedSystem() {
		return managedSystem;
	}

	public void setManagedSystem(ManagedSysEntity managedSystem) {
		this.managedSystem = managedSystem;
	}
	*/

	public Set<AuthLevelGroupingContentProviderXrefEntity> getGroupingXrefs() {
		return groupingXrefs;
	}

	public void setGroupingXrefs(
			Set<AuthLevelGroupingContentProviderXrefEntity> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}

	public boolean isShowOnApplicationPage() {
		return showOnApplicationPage;
	}
	public void setShowOnApplicationPage(boolean showOnApplicationPage) {
		this.showOnApplicationPage = showOnApplicationPage;
	}

	public AuthProviderEntity getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthProviderEntity authProvider) {
		this.authProvider = authProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authProvider == null) ? 0 : authProvider.hashCode());
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
		//result = prime * result + ((managedSystem == null) ? 0 : managedSystem.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
		result = prime * result + ((uiTheme == null) ? 0 : uiTheme.hashCode());
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
		ContentProviderEntity other = (ContentProviderEntity) obj;
		if (authProvider == null) {
			if (other.authProvider != null)
				return false;
		} else if (!authProvider.equals(other.authProvider))
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
		/*
		if (managedSystem == null) {
			if (other.managedSystem != null)
				return false;
		} else if (!managedSystem.equals(other.managedSystem))
			return false;
		*/
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (showOnApplicationPage != other.showOnApplicationPage)
			return false;
		if (uiTheme == null) {
			if (other.uiTheme != null)
				return false;
		} else if (!uiTheme.equals(other.uiTheme))
			return false;
		return true;
	}

	
}
