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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ManagedSysSearchBean that = (ManagedSysSearchBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (connectorId != null ? !connectorId.equals(that.connectorId) : that.connectorId != null) return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (connectorId != null ? connectorId.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(connectorId != null ? connectorId : "")
                .append(resourceId != null ? resourceId : "")
                .append(status != null ? status : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
