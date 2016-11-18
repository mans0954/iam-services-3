package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemplateUIField", 
	propOrder = { 
		"id",
        "name",
        "required",
        "editable",
        "displayOrder"
})
public class TemplateUIField implements Serializable {
	
	@XmlTransient
	private static final Integer DEFAULT_DISPLAY_ORDER = Integer.valueOf(0); 

	private String id;
	private String name;
	private boolean required;
	private boolean editable = true;
	private Integer displayOrder = DEFAULT_DISPLAY_ORDER;
	
	public TemplateUIField() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	
}
