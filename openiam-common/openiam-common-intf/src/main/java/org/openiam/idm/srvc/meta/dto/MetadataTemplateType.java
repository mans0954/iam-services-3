package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTemplateType", propOrder = {
	"description",
	"templates",
	"fields"
})
@DozerDTOCorrespondence(MetadataTemplateTypeEntity.class)
public class MetadataTemplateType extends KeyNameDTO {

	private String description;
	private Set<MetadataElementPageTemplate> templates;
	private Set<MetadataTemplateTypeField> fields;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<MetadataElementPageTemplate> getTemplates() {
		return templates;
	}
	public void setTemplates(Set<MetadataElementPageTemplate> templates) {
		this.templates = templates;
	}
	public Set<MetadataTemplateTypeField> getFields() {
		return fields;
	}
	public void setFields(Set<MetadataTemplateTypeField> fields) {
		this.fields = fields;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		MetadataTemplateType other = (MetadataTemplateType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetadataTemplateType [description=" + description + "]";
	}
	
	
}
