package org.openiam.elasticsearch.model;

import org.openiam.base.domain.AbstractKeyNameEntity;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public abstract class AbstractKeyNameDoc extends AbstractKeyDoc {
	
	protected AbstractKeyNameDoc() {
		super();
	}
	
	protected AbstractKeyNameDoc(final AbstractKeyNameEntity entity) {
		super(entity);
		this.name = entity.getName();
	}

	@Field(type = FieldType.String, index = FieldIndex.analyzed, store=false)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AbstractKeyNameDoc other = (AbstractKeyNameDoc) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractKeyNameDoc [name=" + name + ", getId()=" + getId()
				+ "]";
	}
	
	
}
