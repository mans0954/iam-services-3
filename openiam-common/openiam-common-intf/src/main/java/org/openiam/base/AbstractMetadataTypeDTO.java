package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMetadataTypeDTO", propOrder = {
	"mdTypeId",
	"metadataTypeName"
})
public abstract class AbstractMetadataTypeDTO extends KeyNameDTO {

	protected String mdTypeId;
    protected String metadataTypeName;

    public String getMdTypeId() {
        return mdTypeId;
    }

    public void setMdTypeId(String mdTypeId) {
        this.mdTypeId = mdTypeId;
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
				+ ((mdTypeId == null) ? 0 : mdTypeId.hashCode());
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
		if (mdTypeId == null) {
			if (other.mdTypeId != null)
				return false;
		} else if (!mdTypeId.equals(other.mdTypeId))
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
                        mdTypeId, metadataTypeName, super.toString());
	}
	
	
}
