package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.internationalization.Internationalized;

@Entity
@Table(name = "METADATA_ELEMENT_PAGE_TEMPLATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElementPageTemplate.class)
@Internationalized
@AttributeOverrides(value={
	@AttributeOverride(name = "id", column = @Column(name = "ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 40))
})
public class MetadataElementPageTemplateEntity extends AbstractKeyNameEntity {
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID", insertable=true, updatable=false)
	private ResourceEntity resource;
	
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "template", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<MetadataElementPageTemplateXrefEntity> metadataElements;
    
    //@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional=true)
    //@JoinColumn(name = "URI_PATTERN_ID", insertable=true, updatable=true, nullable=true)
    
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "METADATA_URI_XREF",
            joinColumns = {@JoinColumn(name = "TEMPLATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "URI_PATTERN_ID")})
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<URIPatternEntity> uriPatterns;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "TEMPLATE_TYPE_ID", insertable=true, updatable=true, nullable=false)
    @Fetch(FetchMode.JOIN)
    private MetadataTemplateTypeEntity templateType;
    
    @Internationalized
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "template", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<MetadataFieldTemplateXrefEntity> fieldXrefs;
    
	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic = true;

	public ResourceEntity getResource() {
		return resource;
	}

	public Set<MetadataElementPageTemplateXrefEntity> getMetadataElements() {
		return metadataElements;
	}

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }

    public void setMetadataElements(Set<MetadataElementPageTemplateXrefEntity> metadataElements) {
        this.metadataElements = metadataElements;
    }

	public Set<URIPatternEntity> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(Set<URIPatternEntity> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}
	
	public void addURIPattern(final URIPatternEntity entity) {
		if(entity != null) {
			if(this.uriPatterns == null) {
				this.uriPatterns = new HashSet<URIPatternEntity>();
			}
			this.uriPatterns.add(entity);
		}
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	

	public MetadataTemplateTypeEntity getTemplateType() {
		return templateType;
	}

	public void setTemplateType(MetadataTemplateTypeEntity templateType) {
		this.templateType = templateType;
	}

	public Set<MetadataFieldTemplateXrefEntity> getFieldXrefs() {
		return fieldXrefs;
	}

	public void setFieldXrefs(Set<MetadataFieldTemplateXrefEntity> fieldXrefs) {
		this.fieldXrefs = fieldXrefs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((templateType == null) ? 0 : templateType.hashCode());
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
		MetadataElementPageTemplateEntity other = (MetadataElementPageTemplateEntity) obj;
		if (isPublic != other.isPublic)
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (templateType == null) {
			if (other.templateType != null)
				return false;
		} else if (!templateType.equals(other.templateType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("MetadataElementPageTemplateEntity [resource=%s, templateType=%s, isPublic=%s, toString()=%s]",
						resource, templateType, isPublic, super.toString());
	}

	
	
}
