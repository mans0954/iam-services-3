package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMetadataTypeDTO", propOrder = {
	"metadataTypeId",
	"metadataTypeName"
})
public abstract class AbstractMetadataTypeDTO extends KeyNameDTO {

	private String metadataTypeId;
	private String metadataTypeName;
	public String getMetadataTypeId() {
		return metadataTypeId;
	}
	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}
	public String getMetadataTypeName() {
		return metadataTypeName;
	}
	public void setMetadataTypeName(String metadataTypeName) {
		this.metadataTypeName = metadataTypeName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime
				* result
				+ ((metadataTypeName == null) ? 0 : metadataTypeName.hashCode());
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
		AbstractMetadataTypeDTO other = (AbstractMetadataTypeDTO) obj;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (metadataTypeName == null) {
			if (other.metadataTypeName != null)
				return false;
		} else if (!metadataTypeName.equals(other.metadataTypeName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("AbstractMetadataTypeDTO [metadataTypeId=%s, metadataTypeName=%s, toString()=%s]",
						metadataTypeId, metadataTypeName, super.toString());
	}
	
	
}
