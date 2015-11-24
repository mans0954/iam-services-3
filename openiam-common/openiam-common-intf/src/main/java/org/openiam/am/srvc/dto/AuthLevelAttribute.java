package org.openiam.am.srvc.dto;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelAttribute", propOrder = {
        "grouping",
        "type",
        "valueAsString",
        "valueAsByteArray"
})
@DozerDTOCorrespondence(AuthLevelAttributeEntity.class)
public class AuthLevelAttribute extends KeyNameDTO {

	private AuthLevelGrouping grouping;
	private MetadataType type;
	private String valueAsString;
	private byte[] valueAsByteArray;
	
	public AuthLevelAttribute() {
		
	}

	public AuthLevelGrouping getGrouping() {
		return grouping;
	}

	public void setGrouping(AuthLevelGrouping grouping) {
		this.grouping = grouping;
	}

	public MetadataType getType() {
		return type;
	}

	public void setType(MetadataType type) {
		this.type = type;
	}

	public byte[] getValueAsByteArray() {
		return valueAsByteArray;
	}

	public void setValueAsByteArray(byte[] valueAsByteArray) {
		this.valueAsByteArray = valueAsByteArray;
	}
	
	public String getValueAsString() {
		return valueAsString;
	}

	public void setValueAsString(String valueAsString) {
		this.valueAsString = valueAsString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + Arrays.hashCode(valueAsByteArray);
		result = prime * result
				+ ((valueAsString == null) ? 0 : valueAsString.hashCode());
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
		AuthLevelAttribute other = (AuthLevelAttribute) obj;
		if (grouping == null) {
			if (other.grouping != null)
				return false;
		} else if (!grouping.equals(other.grouping))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
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
		return "AuthLevelAttribute [grouping=" + grouping + ", type=" + type
				+ ", valueAsString=" + valueAsString + ", valueAsByteArray="
				+ Arrays.toString(valueAsByteArray) + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
