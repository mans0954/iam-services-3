package org.openiam.elasticsearch.model;

import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.MetadataTypeDocToEntityConverter;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=MetadataTypeEntity.class, converter=MetadataTypeDocToEntityConverter.class)
@Document(indexName = ESIndexName.METADATA_TYPE, type= ESIndexType.METADATA_TYPE)
public class MetadataTypeDoc extends AbstractKeyNameDoc {

	public MetadataTypeDoc() {}
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	private String grouping;

	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
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
		MetadataTypeDoc other = (MetadataTypeDoc) obj;
		if (grouping == null) {
			if (other.grouping != null)
				return false;
		} else if (!grouping.equals(other.grouping))
			return false;
		return true;
	}
	
	
}
