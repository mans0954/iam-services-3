package org.openiam.idm.srvc.meta.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataFieldTemplateXref", propOrder = {
	"field",
	"template",
	"required",
	"editable",
	"displayOrder",
	"languageMap"
})
@DozerDTOCorrespondence(MetadataFieldTemplateXrefEntity.class)
public class MetadataFieldTemplateXref extends KeyDTO {

	private MetadataTemplateTypeField field;
	private MetadataElementPageTemplate template;
	private boolean required;
	private boolean editable = true;
	private Integer displayOrder;
	private Map<String, LanguageMapping> languageMap;
	
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
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public Map<String, LanguageMapping> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, LanguageMapping> languageMap) {
		this.languageMap = languageMap;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
		result = prime * result + (editable ? 1231 : 1237);
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
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
		MetadataFieldTemplateXref other = (MetadataFieldTemplateXref) obj;
		if (displayOrder == null) {
			if (other.displayOrder != null)
				return false;
		} else if (!displayOrder.equals(other.displayOrder))
			return false;
		if (editable != other.editable)
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
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
		return "MetadataFieldTemplateXref [field=" + field + ", template="
				+ template + ", required=" + required + ", editable="
				+ editable + ", displayOrder=" + displayOrder + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
