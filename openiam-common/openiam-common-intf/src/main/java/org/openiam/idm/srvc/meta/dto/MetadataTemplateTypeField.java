package org.openiam.idm.srvc.meta.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTemplateTypeField", propOrder = {
	"description",
	"templateTypeId",
	"required",
	"fieldXrefs"
})
@DozerDTOCorrespondence(MetadataTemplateTypeFieldEntity.class)
public class MetadataTemplateTypeField extends KeyNameDTO  {
	private String description;
	private String templateTypeId;
	private boolean required;
	private Set<MetadataFieldTemplateXref> fieldXrefs;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public Set<MetadataFieldTemplateXref> getFieldXrefs() {
		return fieldXrefs;
	}
	public void setFieldXrefs(Set<MetadataFieldTemplateXref> fieldXrefs) {
		this.fieldXrefs = fieldXrefs;
	}
	public String getTemplateTypeId() {
		return templateTypeId;
	}
	public void setTemplateTypeId(String templateTypeId) {
		this.templateTypeId = templateTypeId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result
				+ ((templateTypeId == null) ? 0 : templateTypeId.hashCode());
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
		MetadataTemplateTypeField other = (MetadataTemplateTypeField) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (required != other.required)
			return false;
		if (templateTypeId == null) {
			if (other.templateTypeId != null)
				return false;
		} else if (!templateTypeId.equals(other.templateTypeId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetadataTemplateTypeField [description=" + description
				+ ", templateTypeId=" + templateTypeId + ", required="
				+ required + ", getName()=" + getName() + ", getId()="
				+ getId() + "]";
	}
	
	
}
