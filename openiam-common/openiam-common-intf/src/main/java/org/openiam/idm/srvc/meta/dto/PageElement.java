package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;

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
	
	private List<PageElementValidValue> validValues;
	private Set<PageElementValue> userValues;
	
	public PageElement() {
		
	}
	
	public PageElement(final MetadataElementEntity element, final Integer order) {
		this.order = order;
		this.typeId = element.getMetadataType().getMetadataTypeId();
		this.elementId = element.getId();
		this.attributeName = element.getAttributeName();
		this.required = element.isRequired();
		this.editable = element.isSelfEditable();
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

	public List<PageElementValidValue> getValidValues() {
		return validValues;
	}

	public void setValidValues(List<PageElementValidValue> validValues) {
		this.validValues = validValues;
	}
	
	public void addValidValue(final PageElementValidValue validValue) {
		if(validValue != null) {
			if(this.validValues == null) {
				this.validValues = new LinkedList<PageElementValidValue>();
			}
			this.validValues.add(validValue);
		}
	}
}
