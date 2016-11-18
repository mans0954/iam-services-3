package org.openiam.elasticsearch.model;

import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.RoleDocumentToEntityConverter;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=RoleEntity.class, converter=RoleDocumentToEntityConverter.class)
@Document(indexName = ESIndexName.ROLE, type= ESIndexType.ROLE)
public class RoleDoc extends AbstractMetdataTypeDoc {

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	private String managedSysId;
	
	@Field(type = FieldType.String, index = FieldIndex.analyzed)
	private String name;
	
	public RoleDoc(){}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

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
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
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
		RoleDoc other = (RoleDoc) obj;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
