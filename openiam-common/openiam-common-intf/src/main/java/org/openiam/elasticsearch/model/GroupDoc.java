package org.openiam.elasticsearch.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.GroupDocumentToEntityConverter;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=GroupEntity.class, converter=GroupDocumentToEntityConverter.class)
@Document(indexName = ESIndexName.GROUP, type= ESIndexType.GROUP)
public class GroupDoc extends AbstractMetdataTypeDoc {
	
	public GroupDoc() {}

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	private String managedSysId;
	
	@Field(type = FieldType.String, index = FieldIndex.analyzed)
	private String name;
	
	@Field(type = FieldType.Nested)
	private Map<String, Set<String>> attributes;
	
	

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

	public Map<String, Set<String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Set<String>> attributes) {
		this.attributes = attributes;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
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
		GroupDoc other = (GroupDoc) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
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
