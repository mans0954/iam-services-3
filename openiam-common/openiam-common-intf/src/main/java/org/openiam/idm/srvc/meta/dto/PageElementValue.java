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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userAttributeId == null) ? 0 : userAttributeId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PageElementValue other = (PageElementValue) obj;
		if (userAttributeId == null) {
			if (other.userAttributeId != null)
				return false;
		} else if (!userAttributeId.equals(other.userAttributeId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageElementValue [userAttributeId=" + userAttributeId
				+ ", value=" + value + "]";
	}
	
	
}
