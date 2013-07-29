package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataFieldTemplateXref", propOrder = {
	"id",
	"field",
	"template",
	"required"
})
@DozerDTOCorrespondence(MetadataFieldTemplateXrefEntity.class)
public class MetadataFieldTemplateXref {

	private MetadataFieldTemplateXref id;
	private MetadataTemplateTypeField field;
	private MetadataElementPageTemplate template;
	private boolean required;
	public MetadataTemplateTypeField getField() {
		return field;
	}
	public void setField(MetadataTemplateTypeField field) {
		this.field = field;
	}
	public MetadataElementPageTemplate getTemplate() {
		return template;
	}
	public void setTemplate(MetadataElementPageTemplate template) {
		this.template = template;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public MetadataFieldTemplateXref getId() {
		return id;
	}
	public void setId(MetadataFieldTemplateXref id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
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
		MetadataFieldTemplateXref other = (MetadataFieldTemplateXref) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (required != other.required)
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetadataFieldTemplateXref [id=" + id + ", field=" + field
				+ ", template=" + template + ", required=" + required + "]";
	}
	
	
}
