package org.openiam.am.srvc.domain;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_METHOD_META")
@DozerDTOCorrespondence(URIPatternMethodMeta.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_METHOD_META_ID"))
})
public class URIPatternMethodMetaEntity extends AbstractMetaEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_URI_METHOD_ID", referencedColumnName = "URI_PATTERN_URI_METHOD_ID")
	private URIPatternMethodEntity patternMethod;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "metaEntity", orphanRemoval=true)
	private Set<URIPatternMethodMetaValueEntity> metaValueSet;
	
	public Set<URIPatternMethodMetaValueEntity> getMetaValueSet() {
		return metaValueSet;
	}

	public void setMetaValueSet(Set<URIPatternMethodMetaValueEntity> metaValueSet) {
		this.metaValueSet = metaValueSet;
	}

	public URIPatternMethodEntity getPatternMethod() {
		return patternMethod;
	}

	public void setPatternMethod(URIPatternMethodEntity patternMethod) {
		this.patternMethod = patternMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patternMethod == null) ? 0 : patternMethod.hashCode());
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
		URIPatternMethodMetaEntity other = (URIPatternMethodMetaEntity) obj;
		if (patternMethod == null) {
			if (other.patternMethod != null)
				return false;
		} else if (!patternMethod.equals(other.patternMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMethodMetaEntity [patternMethod=" + patternMethod
				+ ", metaType=" + metaType + ", name=" + name + ", id=" + id
				+ "]";
	}
	
	
}
