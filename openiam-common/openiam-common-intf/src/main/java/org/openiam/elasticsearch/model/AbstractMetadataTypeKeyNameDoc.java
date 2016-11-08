package org.openiam.elasticsearch.model;

import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public abstract class AbstractMetadataTypeKeyNameDoc extends AbstractKeyNameDoc {
	
	protected AbstractMetadataTypeKeyNameDoc() {
		super();
	}
	
	protected AbstractMetadataTypeKeyNameDoc(final AbstractKeyNameEntity entity) {
		super(entity);
	}
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	private String metadataTypeId;

	public String getMetadataTypeId() {
		return metadataTypeId;
	}

	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
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
		AbstractMetadataTypeKeyNameDoc other = (AbstractMetadataTypeKeyNameDoc) obj;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractMetadataTypeKeyNameDoc [metadataTypeId="
				+ metadataTypeId + ", getName()=" + getName() + ", toString()="
				+ super.toString() + ", getId()=" + getId() + "]";
	}
	
	
}
