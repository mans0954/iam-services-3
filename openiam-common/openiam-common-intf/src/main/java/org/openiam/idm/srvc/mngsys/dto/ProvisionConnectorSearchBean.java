package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ProvisionConnectorSearchBean", propOrder = {
        "metadataType"
})
public class ProvisionConnectorSearchBean extends AbstractKeyNameSearchBean<ProvisionConnectorDto, String> {
	private static final long serialVersionUID = 2815129890631165531L;
	private MetadataType metadataType;

	public MetadataType getMetadataType() {
		return metadataType;
	}

	public void setMetadataType(MetadataType metadataType) {
		this.metadataType = metadataType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
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
		ProvisionConnectorSearchBean other = (ProvisionConnectorSearchBean) obj;
		if (metadataType == null) {
			if (other.metadataType != null)
				return false;
		} else if (!metadataType.equals(other.metadataType))
			return false;
		return true;
	}
}
