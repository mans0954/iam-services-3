package org.openiam.am.srvc.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelAttribute", propOrder = {
        "id",
        "grouping",
        "type",
        "name",
        "valueAsString",
        "valueAsByteArray"
})
@DozerDTOCorrespondence(AuthLevelAttributeEntity.class)
public class AuthLevelAttribute implements Serializable {

	private String id;
	private AuthLevelGrouping grouping;
	private MetadataType type;
	private String name;
	private String valueAsString;
	private byte[] valueAsByteArray;
	
	public AuthLevelAttribute() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		int result = 1;
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((valueAsByteArray == null) ? 0 : valueAsByteArray.hashCode());
		result = prime * result + ((valueAsString == null) ? 0 : valueAsString.hashCode());
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
		AuthLevelAttribute other = (AuthLevelAttribute) obj;
		if (grouping == null) {
			if (other.grouping != null)
				return false;
		} else if (!grouping.equals(other.grouping))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (valueAsByteArray == null) {
			if (other.valueAsByteArray != null)
				return false;
		} else if (!valueAsByteArray.equals(other.valueAsByteArray))
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
				.format("AuthLevelAttribute [id=%s, grouping=%s, type=%s, name=%s]",
						id, grouping, type, name);
	}
	
	
}
