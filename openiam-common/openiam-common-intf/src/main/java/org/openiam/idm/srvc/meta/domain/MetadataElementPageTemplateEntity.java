package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@Entity
@Table(name = "METADATA_ELEMENT_PAGE_TEMPLATE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElementPageTemplate.class)
public class MetadataElementPageTemplateEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", length = 32)
	private String id;
	
	@Column(name = "NAME", length = 40)
	private String name;
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID")
	private ResourceEntity resource;
	
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "template", fetch = FetchType.LAZY)
    private Set<MetadataElementPageTemplateXrefEntity> metadataElements;
    
    //@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional=true)
    //@JoinColumn(name = "URI_PATTERN_ID", insertable=true, updatable=true, nullable=true)
    
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "METADATA_ELEMENT_TEMPLATE_URI_PATTERN_XREF",
            joinColumns = {@JoinColumn(name = "TEMPLATE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "URI_PATTERN_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<URIPatternEntity> uriPatterns;
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ResourceEntity getResource() {
		return resource;
	}


	public Set<MetadataElementPageTemplateXrefEntity> getMetadataElements() {
		return metadataElements;
	}

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MetadataElementPageTemplateEntity other = (MetadataElementPageTemplateEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
