package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ManagedSysSearchBean", propOrder = {
        "name"
})
public class ManagedSysSearchBean extends AbstractSearchBean<ProvisionConnectorDto, String> {
    private String name;

    public ManagedSysSearchBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
