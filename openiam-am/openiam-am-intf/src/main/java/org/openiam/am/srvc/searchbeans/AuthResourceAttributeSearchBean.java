package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthAttributeSearchBean", propOrder = {
        "resourceId",
        "targetAttributeName",
        "amAttributeName"
})
public class AuthResourceAttributeSearchBean extends AbstractSearchBean<AuthResourceAttributeEntity, String> {
    private String resourceId;
    private String targetAttributeName;
    private String amAttributeName;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTargetAttributeName() {
        return targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
    }

    public String getAmAttributeName() {
        return amAttributeName;
    }

    public void setAmAttributeName(String amAttributeName) {
        this.amAttributeName = amAttributeName;
    }
}
