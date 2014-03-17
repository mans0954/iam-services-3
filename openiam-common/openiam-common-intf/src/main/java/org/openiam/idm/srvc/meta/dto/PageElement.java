package org.openiam.idm.srvc.meta.dto;

import org.openiam.idm.srvc.meta.comparator.PageElementComparator;
import org.openiam.idm.srvc.meta.comparator.PageElementValidValueComparator;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageElement", 
	propOrder = { 
		"order",
        "typeId",
        "elementId",
        "displayName",
        "defaultValue",
        "attributeName",
        "required",
        "editable",
        "validValues",
        "userValues"
})
public class PageElement implements Serializable {

	private Integer order;
	private String typeId;
	private String elementId;
	private String displayName;
	private String defaultValue;
	private String attributeName;
	private boolean required;
	private boolean editable;
	
	private TreeSet<PageElementValidValue> validValues =  new TreeSet<PageElementValidValue>(PageElementValidValueComparator.INSTANCE);
	private Set<PageElementValue> userValues;
	
	public PageElement() {
		
	}
	
	public PageElement(final MetadataElementEntity element, final Integer order) {
		this.order = order;
		this.typeId = element.getMetadataType().getId();
		this.elementId = element.getId();
		this.attributeName = element.getAttributeName();
		this.required = element.isRequired();
		this.editable = element.getSelfEditable();
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public Set<PageElementValue> getUserValues() {
		return userValues;
	}

	public void setUserValues(Set<PageElementValue> userValues) {
		this.userValues = userValues;
	}

	public void addUserValue(final PageElementValue value) {
		if(value != null) {
			if(this.userValues == null) {
				this.userValues = new HashSet<PageElementValue>();
			}
			this.userValues.add(value);
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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

	public Set<PageElementValidValue> getValidValues() {
		return validValues;
	}

	public void setValidValues(List<PageElementValidValue> validValues) {
		this.validValues = new TreeSet<PageElementValidValue>(PageElementValidValueComparator.INSTANCE);
		if(validValues != null) {
			this.validValues.addAll(validValues);
		}
	}
	
	public void addValidValue(final PageElementValidValue validValue) {
		if(validValue != null) {
			if(this.validValues == null) {
				this.validValues = new TreeSet<PageElementValidValue>(PageElementValidValueComparator.INSTANCE);
			}
			this.validValues.add(validValue);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + (editable ? 1231 : 1237);
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		result = prime * result
				+ ((userValues == null) ? 0 : userValues.hashCode());
		result = prime * result
				+ ((validValues == null) ? 0 : validValues.hashCode());
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
		PageElement other = (PageElement) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (editable != other.editable)
			return false;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (required != other.required)
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		if (userValues == null) {
			if (other.userValues != null)
				return false;
		} else if (!userValues.equals(other.userValues))
			return false;
		if (validValues == null) {
			if (other.validValues != null)
				return false;
		} else if (!validValues.equals(other.validValues))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageElement [order=" + order + ", typeId=" + typeId
				+ ", elementId=" + elementId + ", displayName=" + displayName
				+ ", defaultValue=" + defaultValue + ", attributeName="
				+ attributeName + ", required=" + required + ", editable="
				+ editable + ", validValues=" + validValues + ", userValues="
				+ userValues + "]";
	}
	
	
}
