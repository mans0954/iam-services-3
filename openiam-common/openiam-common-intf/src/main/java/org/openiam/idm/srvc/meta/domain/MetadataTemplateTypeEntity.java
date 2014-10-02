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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;

@Entity
@Table(name = "UI_TEMPLATE_TYPE")
@DozerDTOCorrespondence(MetadataTemplateType.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "TEMPLATE_TYPE_ID", length = 32)),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable=false))
})
public class MetadataTemplateTypeEntity extends AbstractKeyNameEntity {

	@Column(name = "DESCRIPTION", length = 200, nullable=true)
	private String description;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "templateType", fetch = FetchType.LAZY)
	private Set<MetadataElementPageTemplateEntity> templates;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "templateType", fetch = FetchType.LAZY)
	private Set<MetadataTemplateTypeFieldEntity> fields;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<MetadataElementPageTemplateEntity> getTemplates() {
		return templates;
	}

	public void setTemplates(Set<MetadataElementPageTemplateEntity> templates) {
		this.templates = templates;
	}
	
	public MetadataTemplateTypeFieldEntity getField(final String id) {
		MetadataTemplateTypeFieldEntity retVal = null;
		if(this.fields != null) {
			for(final MetadataTemplateTypeFieldEntity entity : fields) {
				if(StringUtils.equals(id, entity.getId())) {
					retVal = entity;
					break;
				}
			}
		}
		return retVal;
	}

	public Set<MetadataTemplateTypeFieldEntity> getFields() {
		return fields;
	}

	public void setFields(Set<MetadataTemplateTypeFieldEntity> fields) {
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
		MetadataTemplateTypeEntity other = (MetadataTemplateTypeEntity) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetadataTemplateTypeEntity [description=" + description + "]";
	}

	
}
