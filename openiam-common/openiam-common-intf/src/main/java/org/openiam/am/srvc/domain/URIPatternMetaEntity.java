package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "URI_PATTERN_META")
@DozerDTOCorrespondence(URIPatternMeta.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_META_ID"))
})
public class URIPatternMetaEntity extends AbstractMetaEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID")
	private URIPatternEntity pattern;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "metaEntity", orphanRemoval=true)
	private Set<URIPatternMetaValueEntity> metaValueSet;

	public URIPatternEntity getPattern() {
		return pattern;
	}
	
	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

    public Set<URIPatternMetaValueEntity> getMetaValueSet() {
        return metaValueSet;
    }

    public void setMetaValueSet(Set<URIPatternMetaValueEntity> metaValueSet) {
        this.metaValueSet = metaValueSet;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		URIPatternMetaEntity other = (URIPatternMetaEntity) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMetaEntity [pattern=" + pattern + ", metaType="
				+ metaType + ", name=" + name + ", id=" + id + "]";
	}

	
}
