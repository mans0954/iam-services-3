package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconConfigSearchBean", propOrder = {
        "name",
        "resourceId",
        "managedSysId",
        "reconType"
})
public class ReconConfigSearchBean extends AbstractSearchBean<ReconciliationConfig, String> implements SearchBean<ReconciliationConfig, String>, Serializable {
    private String name;
    private String resourceId;
    private String reconType;
    private String managedSysId;

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

    public String getReconType() {
        return reconType;
    }

    public void setReconType(String reconType) {
        this.reconType = reconType;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(reconType != null ? reconType : "")
                .append(resourceId != null ? resourceId : "")
                .append(managedSysId != null ? managedSysId : "")
                .append(getKey() != null ? getKey() : "")
                .toString();    }
}
