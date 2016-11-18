package org.openiam.elasticsearch.model;

import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public abstract class AbstractKeyDoc implements BaseIdentity {
	
	protected AbstractKeyDoc() {
		super();
	}
	
	protected AbstractKeyDoc(final KeyEntity entity) {
		this.id = entity.getId();
	}

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed)
	private String id;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AbstractKeyDoc other = (AbstractKeyDoc) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractKeyDoc [id=" + id + "]";
	}

	
}
