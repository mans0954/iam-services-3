package org.openiam.idm.srvc.meta.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.KeyValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageElementValidValue", 
	propOrder = {
		"id",
		"value",
        "displayName",
        "displayOrder"
})
public class PageElementValidValue {

	private String id;
	private String value;
	private String displayName;
	private Integer displayOrder;
	
	public PageElementValidValue() {}
	
	public PageElementValidValue(final String id, final String value, final String displayName, final Integer displayOrder) {
		this.id = id;
		this.value = value;
		this.displayName = displayName;
		this.displayOrder = displayOrder;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		PageElementValidValue other = (PageElementValidValue) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (displayOrder == null) {
			if (other.displayOrder != null)
				return false;
		} else if (!displayOrder.equals(other.displayOrder))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "PageElementValidValue [id=" + id + ", value=" + value
				+ ", displayName=" + displayName + ", displayOrder="
				+ displayOrder + "]";
	}

	
	
}
