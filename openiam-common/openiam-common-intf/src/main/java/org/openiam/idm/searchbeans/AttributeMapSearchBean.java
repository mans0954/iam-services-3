package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.mngsys.dto.AttributeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "resourceId",
        "synchConfigId"
})
public class AttributeMapSearchBean extends AbstractSearchBean<AttributeMap, String> implements SearchBean<AttributeMap, String>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private String resourceId;
    private String synchConfigId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

}
