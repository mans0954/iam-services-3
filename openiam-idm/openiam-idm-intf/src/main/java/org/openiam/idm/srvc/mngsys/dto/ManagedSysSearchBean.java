package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ManagedSysSearchBean", propOrder = {
        "name",
        "domainId"
})
public class ManagedSysSearchBean extends AbstractSearchBean<ProvisionConnectorDto, String> {
    private String name;
    private String domainId;

    public ManagedSysSearchBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }
}
