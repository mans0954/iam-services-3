package org.openiam.idm.srvc.meta.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefIDEntity;

@DozerDTOCorrespondence(MetadataFieldTemplateXrefIDEntity.class)
public class MetadataFieldTemplateXrefID {

	private String fieldId;
	private String templateId;
	
	public MetadataFieldTemplateXrefID() {}
	
	public MetadataFieldTemplateXrefID(final String fieldId, final String templateId) {
		this.fieldId = fieldId;
		this.templateId = templateId;
	}
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldId == null) ? 0 : fieldId.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
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
		MetadataFieldTemplateXrefID other = (MetadataFieldTemplateXrefID) obj;
		if (fieldId == null) {
			if (other.fieldId != null)
				return false;
		} else if (!fieldId.equals(other.fieldId))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetadataFieldTemplateXrefID [fieldId=" + fieldId
				+ ", templateId=" + templateId + "]";
	}
	
	
}
