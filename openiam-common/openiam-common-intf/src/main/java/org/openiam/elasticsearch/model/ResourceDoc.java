package org.openiam.elasticsearch.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.annotation.NestedCollectionType;
import org.openiam.elasticsearch.annotation.NestedMapType;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.ListOfStringMapper;
import org.openiam.elasticsearch.converter.ResourceDocumentToEntityConverter;
import org.openiam.elasticsearch.converter.StringMapper;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=ResourceEntity.class, converter=ResourceDocumentToEntityConverter.class)
@Document(indexName = ESIndexName.RESOURCE, type= ESIndexType.RESOURCE)
public class ResourceDoc extends AbstractMetadataTypeDoc {
	
	public ResourceDoc() {}
	
	@Field(type = FieldType.Object, index = FieldIndex.not_analyzed, store= true)
	private List<String> parentIds;
	
	@Field(type = FieldType.Object, index = FieldIndex.not_analyzed, store= true)
	private List<String> childIds;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed)
	private String resourceTypeId;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed)
	private String risk;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed)
	private boolean root;
	
	@NestedMapType(keyMapper=StringMapper.class, valueMapper=ListOfStringMapper.class)
	@Field(type = FieldType.Nested)
	private Map<String, Set<String>> attributes;


	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public String getRisk() {
		return risk;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public Map<String, Set<String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Set<String>> attributes) {
		this.attributes = attributes;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public void addAttribute(final String key, final String value) {
		if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			if(this.attributes == null) {
				this.attributes = new HashMap<String, Set<String>>();
			}
			if(!this.attributes.containsKey(key)) {
				this.attributes.put(key, new HashSet<String>());
			}
			this.attributes.get(key).add(value);
		}
	}

	public List<String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}

	public List<String> getChildIds() {
		return childIds;
	}

	public void setChildIds(List<String> childIds) {
		this.childIds = childIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((childIds == null) ? 0 : childIds.hashCode());
		result = prime * result
				+ ((parentIds == null) ? 0 : parentIds.hashCode());
		result = prime * result
				+ ((resourceTypeId == null) ? 0 : resourceTypeId.hashCode());
		result = prime * result + ((risk == null) ? 0 : risk.hashCode());
		result = prime * result + (root ? 1231 : 1237);
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
		ResourceDoc other = (ResourceDoc) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (childIds == null) {
			if (other.childIds != null)
				return false;
		} else if (!childIds.equals(other.childIds))
			return false;
		if (parentIds == null) {
			if (other.parentIds != null)
				return false;
		} else if (!parentIds.equals(other.parentIds))
			return false;
		if (resourceTypeId == null) {
			if (other.resourceTypeId != null)
				return false;
		} else if (!resourceTypeId.equals(other.resourceTypeId))
			return false;
		if (risk == null) {
			if (other.risk != null)
				return false;
		} else if (!risk.equals(other.risk))
			return false;
		if (root != other.root)
			return false;
		return true;
	}

	
}
