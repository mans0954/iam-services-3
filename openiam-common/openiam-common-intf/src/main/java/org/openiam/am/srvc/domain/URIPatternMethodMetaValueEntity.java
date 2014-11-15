package org.openiam.am.srvc.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethodMetaValue;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "PATTERN_METHOD_META_VALUE")
@DozerDTOCorrespondence(URIPatternMethodMetaValue.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "PATTERN_METHOD_META_VALUE_ID"))
})
public class URIPatternMethodMetaValueEntity extends AbstractMetaValueEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_METHOD_META_ID", referencedColumnName = "URI_PATTERN_METHOD_META_ID", insertable=true, updatable=true, nullable=false)
	private URIPatternMethodMetaEntity metaEntity;

	public URIPatternMethodMetaEntity getMetaEntity() {
		return metaEntity;
	}

	public void setMetaEntity(URIPatternMethodMetaEntity metaEntity) {
		this.metaEntity = metaEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaEntity == null) ? 0 : metaEntity.hashCode());
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
		URIPatternMethodMetaValueEntity other = (URIPatternMethodMetaValueEntity) obj;
		if (metaEntity == null) {
			if (other.metaEntity != null)
				return false;
		} else if (!metaEntity.equals(other.metaEntity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMethodMetaValueEntity [metaEntity=" + metaEntity
				+ ", propagateThroughProxy=" + propagateThroughProxy
				+ ", propagateOnError=" + propagateOnError + ", staticValue="
				+ staticValue + ", groovyScript=" + groovyScript
				+ ", emptyValue=" + emptyValue + ", amAttribute=" + amAttribute
				+ ", name=" + name + ", id=" + id + "]";
	}
	
	
}
