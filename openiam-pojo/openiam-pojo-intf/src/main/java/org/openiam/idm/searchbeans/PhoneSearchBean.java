package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.Phone;

import java.io.Serializable;

public class PhoneSearchBean extends AbstractSearchBean<Phone, String> implements SearchBean<Phone, String>,
        Serializable {
    private String parentId;
    private String parentType;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
}
