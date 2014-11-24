package org.openiam.am.srvc.domain;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.internationalization.Internationalized;
import org.springframework.http.HttpMethod;

@Entity
@Table(name = "URI_PATTERN_METHOD_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(URIPatternMethod.class)
@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_URI_METHOD_ID"))
public class URIPatternMethodEntity extends KeyEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID", insertable=true, updatable=false, nullable=false)
	private URIPatternEntity pattern;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
	
    @Column(name = "HTTP_METHOD_ID", length = 32, insertable=true, updatable=false, nullable=false)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;
    
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "patternMethod", orphanRemoval=true)
	private Set<URIPatternMethodParameterEntity> params;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "patternMethod", orphanRemoval=true)
	private Set<URIPatternMethodMetaEntity> metaEntitySet;
	
	public URIPatternMethodMetaEntity getMetaEntity(final String id) {
		URIPatternMethodMetaEntity retVal = null;
		if(id != null) {
			if(this.metaEntitySet != null) {
				for(final URIPatternMethodMetaEntity meta : this.metaEntitySet) {
					if(id.equals(meta.getId())) {
						retVal = meta;
						break;
					}
				}
			}
		}
		return retVal;
	}

	public Set<URIPatternMethodMetaEntity> getMetaEntitySet() {
		return metaEntitySet;
	}

	public void setMetaEntitySet(Set<URIPatternMethodMetaEntity> metaEntitySet) {
		this.metaEntitySet = metaEntitySet;
	}

	public Set<URIPatternMethodParameterEntity> getParams() {
		return params;
	}

	public void setParams(Set<URIPatternMethodParameterEntity> parameters) {
		this.params = parameters;
	}

	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	public HttpMethod getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
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
		URIPatternMethodEntity other = (URIPatternMethodEntity) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (method != other.method)
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMethodEntity [pattern=" + pattern + ", method=" + method + ", resource=" + resource + "]";
	}
    
    
}
