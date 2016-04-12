package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

/**
 * Created by alexander on 24/01/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSysSearchBean", propOrder = {
        "name",
        "connectorId",
        "resourceId",
        "status"
})
public class ManagedSysSearchBean extends AbstractSearchBean<ManagedSysDto, String> implements SearchBean<ManagedSysDto, String>, Serializable {

    private String name;
    private String connectorId;
    private String resourceId;
    private String status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((connectorId == null) ? 0 : connectorId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		ManagedSysSearchBean other = (ManagedSysSearchBean) obj;
		if (connectorId == null) {
			if (other.connectorId != null)
				return false;
		} else if (!connectorId.equals(other.connectorId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

    
}
