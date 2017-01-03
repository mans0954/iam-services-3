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
public class RoleDoc extends AbstractMetadataTypeDoc {

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
	private String managedSysId;
	
	public RoleDoc(){}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
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
		return true;
	}

}
