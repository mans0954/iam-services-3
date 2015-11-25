package org.openiam.am.srvc.model;

import java.util.List;

import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;

public class MetadataTemplateFieldJSONWrapper {
	
	public MetadataTemplateFieldJSONWrapper() {}

	private List<MetadataFieldTemplateXref> fields;

	public List<MetadataFieldTemplateXref> getFields() {
		return fields;
	}

	public void setFields(List<MetadataFieldTemplateXref> fields) {
		this.fields = fields;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
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
		MetadataTemplateFieldJSONWrapper other = (MetadataTemplateFieldJSONWrapper) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		return true;
	}
	
	
}
