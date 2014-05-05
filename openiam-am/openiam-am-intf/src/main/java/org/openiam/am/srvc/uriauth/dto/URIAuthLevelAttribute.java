package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIAuthLevelAttribute", propOrder = {
	"name",
	"valueAsString",
	"valueAsByteArray",
	"typeName",
	"typeId"
})
public class URIAuthLevelAttribute implements Serializable {

	private String name;
	private String valueAsString;
	private byte[] valueAsByteArray;
	private String typeName;
	private String typeId;
	
	public URIAuthLevelAttribute() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValueAsString() {
		return valueAsString;
	}

	public void setValueAsString(String valueAsString) {
		this.valueAsString = valueAsString;
	}

	public byte[] getValueAsByteArray() {
		return valueAsByteArray;
	}

	public void setValueAsByteArray(byte[] valueAsByteArray) {
		this.valueAsByteArray = valueAsByteArray;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		result = prime * result + Arrays.hashCode(valueAsByteArray);
		result = prime * result
				+ ((valueAsString == null) ? 0 : valueAsString.hashCode());
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
		URIAuthLevelAttribute other = (URIAuthLevelAttribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		if (!Arrays.equals(valueAsByteArray, other.valueAsByteArray))
			return false;
		if (valueAsString == null) {
			if (other.valueAsString != null)
				return false;
		} else if (!valueAsString.equals(other.valueAsString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("URIAuthLevelAttribute [name=%s, valueAsString=%s, valueAsByteArray=%s, typeName=%s, typeId=%s]",
						name, valueAsString, Arrays.toString(valueAsByteArray),
						typeName, typeId);
	}

	
}
