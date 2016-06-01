package org.openiam.idm.searchbeans;


import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MngSysPolicySearchBean", propOrder = {
        "managedSystemId", 
        "metadataTypeId"
})
public class MngSysPolicySearchBean extends AbstractKeyNameSearchBean<MngSysPolicyDto, String> {

    private String managedSystemId;
    private String metadataTypeId;

    public String getManagedSysId() {
        return managedSystemId;
    }

    public void setManagedSystemId(String managedSystemId) {
        this.managedSystemId = managedSystemId;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((managedSystemId == null) ? 0 : managedSystemId.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
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
		MngSysPolicySearchBean other = (MngSysPolicySearchBean) obj;
		if (managedSystemId == null) {
			if (other.managedSystemId != null)
				return false;
		} else if (!managedSystemId.equals(other.managedSystemId))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		return true;
	}

    
}
