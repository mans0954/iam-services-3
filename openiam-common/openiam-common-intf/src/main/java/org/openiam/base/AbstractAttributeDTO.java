package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAttributeDTO", propOrder = {
        "metadataId"
})
public abstract class AbstractAttributeDTO extends AbstactKeyNameValueDTO {

	protected String metadataId;
	
	public String getMetadataId() {
		return this.metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metadataId == null) ? 0 : metadataId.hashCode());
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
		AbstractAttributeDTO other = (AbstractAttributeDTO) obj;
		if (metadataId == null) {
			if (other.metadataId != null)
				return false;
		} else if (!metadataId.equals(other.metadataId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"AbstractAttributeDTO [metadataId=%s, toString()=%s]",
				metadataId, super.toString());
	}
	
	
}
