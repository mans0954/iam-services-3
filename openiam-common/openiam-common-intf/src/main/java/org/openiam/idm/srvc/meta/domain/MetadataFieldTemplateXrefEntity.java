package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;

@Entity
@Table(name = "UI_FIELD_TEMPLATE_XREF")
@DozerDTOCorrespondence(MetadataFieldTemplateXref.class)
public class MetadataFieldTemplateXrefEntity implements Serializable {

	@EmbeddedId
	private MetadataFieldTemplateXrefIDEntity id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="UI_FIELD_ID", referencedColumnName = "UI_FIELD_ID", insertable = false, updatable = false)
	private MetadataTemplateTypeFieldEntity field;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="TEMPLATE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	private MetadataElementPageTemplateEntity template;
	
	@Column(name = "IS_REQUIRED")
    @Type(type = "yes_no")
	private boolean required;

	public MetadataFieldTemplateXrefIDEntity getId() {
		return id;
	}

	public void setId(MetadataFieldTemplateXrefIDEntity id) {
		this.id = id;
	}

	public MetadataTemplateTypeFieldEntity getField() {
		return field;
	}

	public void setField(MetadataTemplateTypeFieldEntity field) {
		this.field = field;
	}

	public MetadataElementPageTemplateEntity getTemplate() {
		return template;
	}

	public void setTemplate(MetadataElementPageTemplateEntity template) {
		this.template = template;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
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
		MetadataFieldTemplateXrefEntity other = (MetadataFieldTemplateXrefEntity) obj;
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
		return "MetadataFieldTemplateXrefEntity [id=" + id + ", field=" + field
				+ ", template=" + template + ", required=" + required + "]";
	}

	
}
