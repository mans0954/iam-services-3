package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ProvisionConnectorSearchBean", propOrder = {
        "connectorName",
        "connectorTypeId"
})
public class ProvisionConnectorSearchBean extends AbstractSearchBean<ProvisionConnectorDto, String> {
    private String connectorName;
    private String connectorTypeId;

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getConnectorTypeId() {
        return connectorTypeId;
    }

    public void setConnectorTypeId(String connectorTypeId) {
        this.connectorTypeId = connectorTypeId;
    }
}
