package org.openiam.am.srvc.domain;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

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
    @JoinColumn(name="CONTENT_PROVIDER_ID", referencedColumnName = "CONTENT_PROVIDER_ID", insertable = false, updatable = false)
	private ContentProviderEntity contentProvider;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="MIN_AUTH_LEVEL", referencedColumnName = "AUTH_LEVEL_ID", insertable = false, updatable = false)
	private AuthLevelEntity minAuthLevel;
	
	@Column(name = "PATTERN", length = 100, nullable = false)
	private String pattern;
	
	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = false, updatable = false)
	private ResourceEntity resource;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "pattern")
	private Set<URIPatternMetaEntity> metaEntitySet;

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

	public AuthLevelEntity getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(AuthLevelEntity minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result
				+ ((minAuthLevel == null) ? 0 : minAuthLevel.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		if (minAuthLevel == null) {
			if (other.minAuthLevel != null)
				return false;
		} else if (!minAuthLevel.equals(other.minAuthLevel))
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
		return true;
	}
	
	
}
