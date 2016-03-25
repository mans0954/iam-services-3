package org.openiam.idm.searchbeans;


import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MngSysPolicySearchBean", propOrder = {
        "managedSystemId", "metadataTypeId"
})
public class MngSysPolicySearchBean extends AbstractKeyNameSearchBean<MngSysPolicyDto, String> implements SearchBean {

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
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
