package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.EmailAddress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserSearchBean", propOrder = {
        "name",
        "parentId",
        "parentType"
})
public class EmailSearchBean extends AbstractSearchBean<EmailAddress, String> implements SearchBean<EmailAddress, String>,
        Serializable {
    private String name;
    private String parentId;
    private String parentType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
