package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "URI_PATTERN")
@DozerDTOCorrespondence(URIPattern.class)
public class URIPatternEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "URI_PATTERN_ID", length = 32, nullable = false)
	private String id;
	
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

	/*
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    */
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern")
	private Set<URIPatternMetaEntity> metaEntitySet;

	//@OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy="uriPattern")
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "METADATA_URI_XREF",
            joinColumns = {@JoinColumn(name = "URI_PATTERN_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TEMPLATE_ID")})
    @Fetch(FetchMode.SUBSELECT)
	private Set<MetadataElementPageTemplateEntity> pageTemplates;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "pattern", fetch = FetchType.LAZY)
	@OrderBy("order ASC")
	private Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setGroupingXrefs(
			Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((uiTheme == null) ? 0 : uiTheme.hashCode());
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
		URIPatternEntity other = (URIPatternEntity) obj;
		if (contentProvider == null) {
			if (other.contentProvider != null)
				return false;
		} else if (!contentProvider.equals(other.contentProvider))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPublic != other.isPublic)
			return false;
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
		return true;
	}
	
	
}
