package org.openiam.elasticsearch.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.OrganizationDocumentToEntityConverter;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=OrganizationEntity.class, converter=OrganizationDocumentToEntityConverter.class)
@Document(indexName = ESIndexName.ORGANIZATION, type= ESIndexType.ORGANIZATION)
public class OrganizationDoc extends AbstractMetadataTypeKeyNameDoc {
	
	public OrganizationDoc() {
		super();
	}

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	private String organizationTypeId;
	
	@Field(type = FieldType.Object, index = FieldIndex.not_analyzed, store= true)
	private List<String> parentIds;
	
	@Field(type = FieldType.Object, index = FieldIndex.not_analyzed, store= true)
	private List<String> parentOrganizationTypeIds;
	
	

	public String getOrganizationTypeId() {
		return organizationTypeId;
	}

	public void setOrganizationTypeId(String organizationTypeId) {
		this.organizationTypeId = organizationTypeId;
	}

	public List<String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}

	public List<String> getParentOrganizationTypeIds() {
		return parentOrganizationTypeIds;
	}

	public void setParentOrganizationTypeIds(List<String> parentOrganizationTypeIds) {
		this.parentOrganizationTypeIds = parentOrganizationTypeIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((organizationTypeId == null) ? 0 : organizationTypeId
						.hashCode());
		result = prime * result
				+ ((parentIds == null) ? 0 : parentIds.hashCode());
		result = prime
				* result
				+ ((parentOrganizationTypeIds == null) ? 0
						: parentOrganizationTypeIds.hashCode());
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
		OrganizationDoc other = (OrganizationDoc) obj;
		if (organizationTypeId == null) {
			if (other.organizationTypeId != null)
				return false;
		} else if (!organizationTypeId.equals(other.organizationTypeId))
			return false;
		if (parentIds == null) {
			if (other.parentIds != null)
				return false;
		} else if (!parentIds.equals(other.parentIds))
			return false;
		if (parentOrganizationTypeIds == null) {
			if (other.parentOrganizationTypeIds != null)
				return false;
		} else if (!parentOrganizationTypeIds
				.equals(other.parentOrganizationTypeIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrganizationDoc [organizationTypeId=" + organizationTypeId
				+ ", parentIds=" + parentIds + ", parentOrganizationTypeIds="
				+ parentOrganizationTypeIds + ", getMetadataTypeId()="
				+ getMetadataTypeId() + ", toString()=" + super.toString()
				+ ", getName()=" + getName() + ", getId()=" + getId() + "]";
	}
	
	
}
