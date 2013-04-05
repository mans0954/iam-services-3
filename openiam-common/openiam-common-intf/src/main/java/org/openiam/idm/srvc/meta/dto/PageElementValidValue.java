package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.KeyValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageElementValidValue", 
	propOrder = { 
		"value",
        "displayName"
})
public class PageElementValidValue {

	private String value;
	private String displayName;
	
	public PageElementValidValue() {}
	
	public PageElementValidValue(final String value, final String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
}
