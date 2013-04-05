package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageElementValue", 
	propOrder = { 
		"userAttributeId",
        "value"
})
public class PageElementValue {

	private String userAttributeId;
	private String value;
	
	public PageElementValue() {}
	
	public PageElementValue(final String userAttributeId, final String value) {
		this.userAttributeId = userAttributeId;
		this.value = value;
	}

	public String getUserAttributeId() {
		return userAttributeId;
	}

	public void setUserAttributeId(String userAttributeId) {
		this.userAttributeId = userAttributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
