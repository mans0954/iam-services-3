package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;

@Entity
@Table(name = "UI_FIELD_TEMPLATE_XREF")
@DozerDTOCorrespondence(MetadataFieldTemplateXref.class)
public class MetadataFieldTemplateXrefEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "XREF_ID", length = 32)
    private String id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="UI_FIELD_ID", referencedColumnName = "UI_FIELD_ID", insertable = true, updatable = true)
	private MetadataTemplateTypeFieldEntity field;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="TEMPLATE_ID", referencedColumnName = "ID", insertable = true, updatable = true)
	private MetadataElementPageTemplateEntity template;
	
	@Column(name = "IS_REQUIRED")
    @Type(type = "yes_no")
	private boolean required;

	@Column(name = "IS_EDITABLE")
    @Type(type = "yes_no")
	private boolean editable = true;
	
	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;
	
    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="referenceId", orphanRemoval=true)
	@Where(clause="REFERENCE_TYPE='MetadataFieldTemplateXrefEntity'")
    @MapKey(name = "languageId")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, LanguageMappingEntity> languageMap;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Map<String, LanguageMappingEntity> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, LanguageMappingEntity> languageMap) {
		this.languageMap = languageMap;
	}
	
	
	public String getDisplayName(final LanguageEntity language) {
		String name = null;
		if(language != null) {
			if(languageMap != null) {
				final LanguageMappingEntity entity = languageMap.get(language.getLanguageId());
				if(entity != null) {
					name = entity.getValue();
				}
			}
		}
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
		result = prime * result + (editable ? 1231 : 1237);
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
				+ ", template=" + template + ", required=" + required
				+ ", editable=" + editable + ", displayOrder=" + displayOrder
				+ "]";
	}


	
}
