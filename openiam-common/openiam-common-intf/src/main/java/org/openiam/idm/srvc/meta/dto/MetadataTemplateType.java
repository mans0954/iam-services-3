package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTemplateType", propOrder = {
	"id",
	"name",
	"description",
	"templates",
	"fields"
})
@DozerDTOCorrespondence(MetadataTemplateTypeEntity.class)
public class MetadataTemplateType implements Serializable {

	private String id;
	private String name;
	private String description;
	private Set<MetadataElementPageTemplate> templates;
	private Set<MetadataTemplateTypeField> fields;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MetadataTemplateType other = (MetadataTemplateType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetadataTemplateType [id=" + id + ", name=" + name
				+ ", description=" + description + "]";
	}
	
	
}
