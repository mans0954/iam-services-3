package org.openiam.property.domain;

import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;
import org.openiam.property.dto.PropertyValue;

@Entity
@Table(name = "PROPERTY_FILE_VALUES")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(PropertyValue.class)
@AttributeOverride(name = "id", column = @Column(name = "PROPERTY_ID"))
@Internationalized
public class PropertyValueEntity extends KeyEntity {
	
	@Column(name = "PROPERTY_VALUE", length=400)
	private String value;
	 
	@Column(name = "PROPERTY_TYPE", length=400)
	@Enumerated(EnumType.STRING)
	private PropertyType type;
	
    @Column(name = "IS_EMPTY_VALUE_ALLOWED")
    @Type(type = "yes_no")
    private boolean emptyValueAllowed;
    
    @Column(name = "IS_MULTILANGUAL")
    @Type(type = "yes_no")
    private boolean multilangual;
    
    @Column(name = "IS_READ_ONLY")
    @Type(type = "yes_no")
    private boolean readOnly;
    
    @Column(name = "CATEGORY", length=100)
	@Enumerated(EnumType.STRING)
    private PropertyCategory category;
    
    @Transient
    @InternationalizedCollection
    private Map<String, LanguageMappingEntity> internationalizedValues;

	public Map<String, LanguageMappingEntity> getInternationalizedValues() {
		return internationalizedValues;
	}

	public void setInternationalizedValues(
			Map<String, LanguageMappingEntity> internationalizedValues) {
		this.internationalizedValues = internationalizedValues;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public boolean isEmptyValueAllowed() {
		return emptyValueAllowed;
	}

	public void setEmptyValueAllowed(boolean emptyValueAllowed) {
		this.emptyValueAllowed = emptyValueAllowed;
	}

	public boolean isMultilangual() {
		return multilangual;
	}

	public void setMultilangual(boolean multilangual) {
		this.multilangual = multilangual;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public PropertyCategory getCategory() {
		return category;
	}

	public void setCategory(PropertyCategory category) {
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (emptyValueAllowed ? 1231 : 1237);
		result = prime * result + (multilangual ? 1231 : 1237);
		result = prime * result
				+ ((value == null) ? 0 : value.hashCode());
		result = prime * result + (readOnly ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
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
		PropertyValueEntity other = (PropertyValueEntity) obj;
		if (emptyValueAllowed != other.emptyValueAllowed)
			return false;
		if (multilangual != other.multilangual)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (readOnly != other.readOnly)
			return false;
		if (type != other.type)
			return false;
		return category == other.category;
	}

	@Override
	public String toString() {
		return "PropertyValueEntity [value=" + value
				+ ", type=" + type + ", emptyValueAllowed=" + emptyValueAllowed
				+ ", multilangual=" + multilangual + ", readOnly=" + readOnly
				+ ", id=" + id + "]";
	}

	
}
