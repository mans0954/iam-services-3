package org.openiam.base.domain;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.internationalization.Internationalized;

@MappedSuperclass
public abstract class AbstractMetdataTypeEntity extends KeyEntity {

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataTypeEntity type;

	public MetadataTypeEntity getType() {
		return type;
	}

	public void setType(MetadataTypeEntity type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AbstractMetdataTypeEntity other = (AbstractMetdataTypeEntity) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"AbstractMetdataTypeEntity [type=%s, toString()=%s]", type,
				super.toString());
	}
    
    
}
