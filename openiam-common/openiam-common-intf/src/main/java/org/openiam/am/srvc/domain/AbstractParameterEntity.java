package org.openiam.am.srvc.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.domain.AbstractKeyNameEntity;

@MappedSuperclass
@AttributeOverride(name = "name", column = @Column(name = "PARAM_NAME", length=200, nullable=false))
public abstract class AbstractParameterEntity extends AbstractKeyNameEntity {

	/*
    @Column(name = "IS_MULTIVALUED")
    @Type(type = "yes_no")
    protected boolean multivalued;
    */

    public abstract List<String> getValues();
    public abstract void setValues(final List<String> values);
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		//result = prime * result + (multivalued ? 1231 : 1237);
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
		AbstractParameterEntity other = (AbstractParameterEntity) obj;
		//if (multivalued != other.multivalued)
		//	return false;
		return true;
	}
	@Override
	public String toString() {
		return "AbstractParameterEntity [name=" + name + ", id=" + id + "]";
	}
	
	
}
