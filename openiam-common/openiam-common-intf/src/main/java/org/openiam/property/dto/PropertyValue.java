package org.openiam.property.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;
import org.openiam.property.domain.PropertyCategory;
import org.openiam.property.domain.PropertyType;
import org.openiam.property.domain.PropertyValueEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyValue", propOrder = {
	"value",
	"type",
	"emptyValueAllowed",
	"multilangual",
	"readOnly",
	"internationalizedValues",
	"category"
})
@Internationalized
@DozerDTOCorrespondence(PropertyValueEntity.class)
public class PropertyValue extends KeyDTO {

	private String value;
	private PropertyType type;
	private boolean emptyValueAllowed;
	private boolean multilangual;
	private boolean readOnly;
	private PropertyCategory category;
	
    @InternationalizedCollection
    private Map<String, LanguageMapping> internationalizedValues;
	
	public Map<String, LanguageMapping> getInternationalizedValues() {
		return internationalizedValues;
	}

	public void setInternationalizedValues(
			Map<String, LanguageMapping> internationalizedValues) {
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
		PropertyValue other = (PropertyValue) obj;
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
		if (category != other.category)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PropertyValue [value=" + value + ", type="
				+ type + ", emptyValueAllowed=" + emptyValueAllowed
				+ ", multilangual=" + multilangual + ", readOnly=" + readOnly
				+ ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
