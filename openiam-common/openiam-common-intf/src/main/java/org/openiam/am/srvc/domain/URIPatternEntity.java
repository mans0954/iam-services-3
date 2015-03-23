package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.springframework.http.HttpMethod;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "URI_PATTERN")
@DozerDTOCorrespondence(URIPattern.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_ID"))
})
public class URIPatternEntity extends AbstractMatchModeEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="CONTENT_PROVIDER_ID", referencedColumnName = "CONTENT_PROVIDER_ID")
	private ContentProviderEntity contentProvider;
	
	@Column(name = "PATTERN", length = 100, nullable = false)
	private String pattern;
	
	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic;
	
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "UI_THEME_ID", referencedColumnName = "UI_THEME_ID", insertable = true, updatable = true, nullable=true)
    private UIThemeEntity uiTheme;
    
	@Column(name = "SHOW_ON_APP_PAGE", nullable = false)
	@Type(type = "yes_no")
	private boolean showOnApplicationPage;
	
	@Column(name = "IGNORE_XSS", nullable = false)
	@Type(type = "yes_no")
	private boolean ignoreXSS;
	
	@Column(name="APPLICATION_NAME", length=100)
	private String applicationName;

	/*
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    */
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<URIPatternMetaEntity> metaEntitySet;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	private Set<URIPatternMethodEntity> methods;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	private Set<URIPatternParameterEntity> params;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	private Set<URIPatternSubstitutionEntity> substitutions;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	private Set<URIPatternErrorMappingEntity> errorMappings;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern", orphanRemoval=true)
	private Set<URIParamXSSRuleEntity> xssRules;

	//@OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy="uriPattern")
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "METADATA_URI_XREF",
            joinColumns = {@JoinColumn(name = "URI_PATTERN_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TEMPLATE_ID")})
    @Fetch(FetchMode.SUBSELECT)
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<MetadataElementPageTemplateEntity> pageTemplates;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "pattern", fetch = FetchType.LAZY)
	@OrderBy("order ASC")
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "pattern", fetch = FetchType.LAZY)
	private Set<URIPatternServerEntity> servers;
	
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=true)
    private AuthProviderEntity authProvider;
    
    @Column(name="REDIRECT_TO", length=400)
    private String redirectTo;
    
    @Column(name="REDIRECT_TO_GROOVY_SCRIPT", length=400)
    private String redirectToGroovyScript;
    
	public Set<URIPatternParameterEntity> getParams() {
		return params;
	}

	public void setParams(Set<URIPatternParameterEntity> params) {
		this.params = params;
	}

	public Set<URIPatternMethodEntity> getMethods() {
		return methods;
	}

	public void setMethods(Set<URIPatternMethodEntity> methods) {
		this.methods = methods;
	}

	public ContentProviderEntity getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ContentProviderEntity contentProvider) {
		this.contentProvider = contentProvider;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	public Set<URIPatternMetaEntity> getMetaEntitySet() {
		return metaEntitySet;
	}

	public void setMetaEntitySet(Set<URIPatternMetaEntity> metaEntitySet) {
		this.metaEntitySet = metaEntitySet;
	}
	
	public void addMetaEntity(final URIPatternMetaEntity enitity) {
		if(metaEntitySet == null) {
			metaEntitySet = new LinkedHashSet<URIPatternMetaEntity>();
		}
		metaEntitySet.add(enitity);
	}
	
	public URIPatternMethodEntity getMethod(final String id) {
		URIPatternMethodEntity retVal = null;
		if(id != null) {
			if(methods != null) {
				for(final URIPatternMethodEntity method : methods) {
					if(id.equals(method.getId())) {
						retVal = method;
						break;
					}
				}
			}
		}
		return retVal;
	}
	
	public URIParamXSSRuleEntity getXssRule(final String paramName) {
		if(paramName != null && this.xssRules != null) {
			final Optional<URIParamXSSRuleEntity> optional = this.xssRules.stream().filter(e -> StringUtils.equalsIgnoreCase(paramName, e.getParamName())).findFirst();
			if(optional.isPresent()) {
				return optional.get();
			}
		}
		return null;
	}
	
	public boolean hasXssRule(final String paramName) {
		boolean hasRule = false;
		if(paramName != null) {
			if(this.xssRules != null) {
				hasRule = this.xssRules
							  .stream()
							  .filter(e -> StringUtils.equalsIgnoreCase(e.getParamName(), paramName))
							  .findFirst()
							  .isPresent();
			}
		}
		return hasRule;
	}
	
	public void addXssRule(final URIParamXSSRuleEntity entity) {
		if(entity != null) {
			if(this.xssRules == null) {
				this.xssRules = new HashSet<URIParamXSSRuleEntity>();
			}
			this.xssRules.add(entity);
		}
	}
	
	public URIPatternMetaEntity getMetaEntity(final String id) {
		URIPatternMetaEntity retVal = null;
		if(id != null) {
			if(metaEntitySet != null) {
				for(final URIPatternMetaEntity meta : metaEntitySet) {
					if(id.equals(meta.getId())) {
						retVal = meta;
						break;
					}
				}
			}
		}
		return retVal;
	}
	
	public UIThemeEntity getUiTheme() {
		return uiTheme;
	}

	public void setUiTheme(UIThemeEntity uiTheme) {
		this.uiTheme = uiTheme;
	}

	/*
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    */

    public Set<MetadataElementPageTemplateEntity> getPageTemplates() {
		return pageTemplates;
	}

	public void setPageTemplates(
			Set<MetadataElementPageTemplateEntity> pageTemplates) {
		this.pageTemplates = pageTemplates;
	}

	public Set<AuthLevelGroupingURIPatternXrefEntity> getGroupingXrefs() {
		return groupingXrefs;
	}
	
	public void addGroupingXref(final AuthLevelGroupingURIPatternXrefEntity xref) {
		if(xref != null) {
			if(this.groupingXrefs == null) {
				this.groupingXrefs = new HashSet<>();
			}
			this.groupingXrefs.add(xref);
		}
	}
	
	public boolean hasAuthGrouping(final String groupingId) {
		boolean retVal = false;
		if(groupingId != null) {
			if(this.groupingXrefs != null) {
				for(final AuthLevelGroupingURIPatternXrefEntity xref : this.groupingXrefs) {
					if(StringUtils.equals(xref.getId().getGroupingId(), groupingId)) {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	public void setGroupingXrefs(
			Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}

	public Set<URIPatternServerEntity> getServers() {
		return servers;
	}

	public void setServers(Set<URIPatternServerEntity> servers) {
		this.servers = servers;
	}
	
	public AuthProviderEntity getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthProviderEntity authProvider) {
		this.authProvider = authProvider;
	}

	public Set<URIPatternSubstitutionEntity> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(Set<URIPatternSubstitutionEntity> substitutions) {
		this.substitutions = substitutions;
	}
	
	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public Set<URIPatternErrorMappingEntity> getErrorMappings() {
		return errorMappings;
	}

	public void setErrorMappings(Set<URIPatternErrorMappingEntity> errorMappings) {
		this.errorMappings = errorMappings;
	}
	
	public String getRedirectToGroovyScript() {
		return redirectToGroovyScript;
	}
	public void setRedirectToGroovyScript(String redirectToGroovyScript) {
		this.redirectToGroovyScript = redirectToGroovyScript;
	}
	
	public boolean isShowOnApplicationPage() {
		return showOnApplicationPage;
	}
	public void setShowOnApplicationPage(boolean showOnApplicationPage) {
		this.showOnApplicationPage = showOnApplicationPage;
	}
	
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public boolean isIgnoreXSS() {
		return ignoreXSS;
	}

	public void setIgnoreXSS(boolean ignoreXSS) {
		this.ignoreXSS = ignoreXSS;
	}

	public Set<URIParamXSSRuleEntity> getXssRules() {
		return xssRules;
	}

	public void setXssRules(Set<URIParamXSSRuleEntity> xssRules) {
		this.xssRules = xssRules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authProvider == null) ? 0 : authProvider.hashCode());
		result = prime * result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + (ignoreXSS ? 1231 : 1237);
		
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((uiTheme == null) ? 0 : uiTheme.hashCode());
		result = prime * result
				+ ((redirectTo == null) ? 0 : redirectTo.hashCode());
		
		result = prime * result
				+ ((redirectToGroovyScript == null) ? 0 : redirectToGroovyScript.hashCode());
		
		result = prime * result
				+ ((applicationName == null) ? 0 : applicationName.hashCode());
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
		URIPatternEntity other = (URIPatternEntity) obj;
		if (authProvider == null) {
			if (other.authProvider != null)
				return false;
		} else if (!authProvider.equals(other.authProvider))
			return false;
		if (contentProvider == null) {
			if (other.contentProvider != null)
				return false;
		} else if (!contentProvider.equals(other.contentProvider))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if(showOnApplicationPage != other.showOnApplicationPage) {
			return false;
		}
		if(ignoreXSS != other.ignoreXSS) {
			return false;
		}
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
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
		if (redirectTo == null) {
			if (other.redirectTo != null)
				return false;
		} else if (!redirectTo.equals(other.redirectTo))
			return false;
		
		if (redirectToGroovyScript == null) {
			if (other.redirectToGroovyScript != null)
				return false;
		} else if (!redirectToGroovyScript.equals(other.redirectToGroovyScript))
			return false;
		
		if (applicationName == null) {
			if (other.applicationName != null)
				return false;
		} else if (!applicationName.equals(other.applicationName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("URIPatternEntity [contentProvider=%s, pattern=%s, isPublic=%s, uiTheme=%s, resource=%s, toString()=%s]",
						contentProvider, pattern, isPublic, uiTheme, resource,
						super.toString());
	}

	
}
