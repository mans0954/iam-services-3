package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ManagedSysSearchBean", propOrder = {
        "name",
        "resourceId"
})
public class ManagedSysSearchBean extends AbstractSearchBean<ProvisionConnectorDto, String> {
    private String name;
    private String resourceId;

    public ManagedSysSearchBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
