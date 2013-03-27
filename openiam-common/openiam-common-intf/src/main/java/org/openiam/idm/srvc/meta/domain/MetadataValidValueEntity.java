package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataValidValue;

@Entity
@Table(name = "METADATA_ELEMENT_VALID_VALUES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataValidValue.class)
public class MetadataValidValueEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", length = 32)
    private String id;
	
	@ManyToOne
    @JoinColumn(name = "METADATA_ELEMENT_ID")
	private MetadataElementEntity entity;
	
	@Column(name="UI_VALUE", length=200)
	private String uiValue;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE_ID", insertable = true, updatable = true)
	@Where(clause="REFERENCE_TYPE='MetadataValidValueEntity'")
    @MapKey(name = "languageId")
    private Map<String, LanguageMappingEntity> languageMap;

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MetadataElementEntity getEntity() {
		return entity;
	}

	public void setEntity(MetadataElementEntity entity) {
		this.entity = entity;
	}

	public String getUiValue() {
		return uiValue;
	}

	public void setUiValue(String uiValue) {
		this.uiValue = uiValue;
	}

	public Map<String, LanguageMappingEntity> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, LanguageMappingEntity> languageMap) {
		this.languageMap = languageMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((uiValue == null) ? 0 : uiValue.hashCode());
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
		MetadataValidValueEntity other = (MetadataValidValueEntity) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (uiValue == null) {
			if (other.uiValue != null)
				return false;
		} else if (!uiValue.equals(other.uiValue))
			return false;
		return true;
	}
	
	
}
