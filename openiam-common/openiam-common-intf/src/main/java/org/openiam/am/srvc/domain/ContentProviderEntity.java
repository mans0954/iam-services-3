package org.openiam.am.srvc.domain;

import org.hibernate.annotations.*;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CONTENT_PROVIDER")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
	
	@Column(name = "SHOW_ON_APP_PAGE", nullable = false)
	@Type(type = "yes_no")
	private boolean showOnApplicationPage = true;
	
	@Column(name = "DOMAIN_PATTERN", length = 100, nullable = false)
	private String domainPattern;
	
	@Column(name = "LOGIN_URL", length = 300, nullable = false)
	private String loginURL;
	
	@Column(name = "POSTBACK_URL_PARAM_NAME", length = 50, nullable = false)
	private String postbackURLParamName;
	
	@Column(name = "IS_SSL", nullable = true)
	@Type(type = "yes_no")
	private Boolean isSSL;
	
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "UI_THEME_ID", referencedColumnName = "UI_THEME_ID", insertable = true, updatable = true, nullable=true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UIThemeEntity uiTheme;


	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=false)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private ManagedSysEntity managedSystem;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "contentProvider")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<ContentProviderServerEntity> serverSet;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "contentProvider")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<URIPatternEntity> patternSet;
	
	@OneToMany(orphanRemoval = true, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "contentProvider", fetch = FetchType.LAZY)
	@OrderBy("order ASC")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelGroupingContentProviderXrefEntity> groupingXrefs;

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=false)
	private AuthProviderEntity authProvider;
	

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

	public ManagedSysEntity getManagedSystem() {
		return managedSystem;
	}

	public void setManagedSystem(ManagedSysEntity managedSystem) {
		this.managedSystem = managedSystem;
	}

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

	public AuthProviderEntity getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthProviderEntity authProvider) {
		this.authProvider = authProvider;
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
		/*
        result = prime * result
                 + ((contextPath == null) ? 0 : contextPath.hashCode());
		*/
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((loginURL == null) ? 0 : loginURL.hashCode());
		result = prime * result + ((postbackURLParamName == null) ? 0 : postbackURLParamName.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((uiTheme == null) ? 0 : uiTheme.hashCode());
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
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
		
		if (uiTheme == null) {
			if (other.uiTheme != null)
				return false;
		} else if (!uiTheme.equals(other.uiTheme))
			return false;
		if (showOnApplicationPage != other.showOnApplicationPage)
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
