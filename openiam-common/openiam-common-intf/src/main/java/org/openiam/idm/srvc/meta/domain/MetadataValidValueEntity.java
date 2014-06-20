package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataValidValue;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name = "MD_ELEMENT_VALID_VALUES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataValidValue.class)
@AttributeOverride(name = "id", column = @Column(name = "ID"))
@Internationalized
public class MetadataValidValueEntity extends KeyEntity {

	@ManyToOne
    @JoinColumn(name = "METADATA_ELEMENT_ID")
	private MetadataElementEntity entity;
	
	@Column(name="UI_VALUE", length=200)
	private String uiValue;

    @Column(name="DISPLAY_ORDER")
    private Integer displayOrder;
    
    @Transient
    private String displayName;
	
    //@OneToMany(cascade={CascadeType.REMOVE}, fetch = FetchType.LAZY)
    //@JoinColumn(name = "REFERENCE_ID", referencedColumnName="ID")
    //@Where(clause="REFERENCE_TYPE='MetadataValidValueEntity'")
    //@MapKey(name = "languageId")
    //@Fetch(FetchMode.SUBSELECT)
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> languageMap;

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
