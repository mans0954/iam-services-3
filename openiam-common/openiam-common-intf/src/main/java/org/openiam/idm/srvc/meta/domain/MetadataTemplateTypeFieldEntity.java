package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;

@Entity
@Table(name = "UI_TEMPLATE_FIELDS")
@DozerDTOCorrespondence(MetadataTemplateTypeField.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "UI_FIELD_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable = false))
})
public class MetadataTemplateTypeFieldEntity extends AbstractKeyNameEntity {
	
	@Column(name = "DESCRIPTION", length = 200, nullable=true)
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "TEMPLATE_TYPE_ID", insertable=true, updatable=false, nullable=false)
    @Fetch(FetchMode.JOIN)
	private MetadataTemplateTypeEntity templateType;
	
    @Column(name = "IS_REQUIRED")
    @Type(type = "yes_no")
	private boolean required;
    
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "field", fetch = FetchType.LAZY)
    private Set<MetadataFieldTemplateXrefEntity> fieldXrefs;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MetadataTemplateTypeEntity getTemplateType() {
		return templateType;
	}

	public void setTemplateType(MetadataTemplateTypeEntity templateType) {
		this.templateType = templateType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Set<MetadataFieldTemplateXrefEntity> getFieldXrefs() {
		return fieldXrefs;
	}

	public void setFieldXrefs(Set<MetadataFieldTemplateXrefEntity> fieldXrefs) {
		this.fieldXrefs = fieldXrefs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result
				+ ((templateType == null) ? 0 : templateType.hashCode());
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
		MetadataTemplateTypeFieldEntity other = (MetadataTemplateTypeFieldEntity) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (required != other.required)
			return false;
		if (templateType == null) {
			if (other.templateType != null)
				return false;
		} else if (!templateType.equals(other.templateType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetadataTemplateTypeFieldEntity [description=" + description
				+ ", templateType=" + templateType + ", required=" + required
				+ "]";
	}

	
}
