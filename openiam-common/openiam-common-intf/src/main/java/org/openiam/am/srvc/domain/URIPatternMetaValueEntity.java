package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "URI_PATTERN_META_VALUE")
@DozerDTOCorrespondence(URIPatternMetaValue.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_META_VALUE_ID"))
})
public class URIPatternMetaValueEntity extends AbstractMetaValueEntity {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_ID", referencedColumnName = "URI_PATTERN_META_ID", insertable=true, updatable=true, nullable=false)
	private URIPatternMetaEntity metaEntity;

	public URIPatternMetaEntity getMetaEntity() {
		return metaEntity;
	}

	public void setMetaEntity(URIPatternMetaEntity metaEntity) {
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
		URIPatternMetaValueEntity other = (URIPatternMetaValueEntity) obj;
		if (metaEntity == null) {
			if (other.metaEntity != null)
				return false;
		} else if (!metaEntity.equals(other.metaEntity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMetaValueEntity [metaEntity=" + metaEntity
				+ ", propagateThroughProxy=" + propagateThroughProxy
				+ ", propagateOnError=" + propagateOnError + ", staticValue="
				+ staticValue + ", groovyScript=" + groovyScript
				+ ", emptyValue=" + emptyValue + ", amAttribute=" + amAttribute
				+ ", name=" + name + ", id=" + id + "]";
	}

	
}
