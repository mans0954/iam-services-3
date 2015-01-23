package org.openiam.idm.srvc.grp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by alexander on 15.01.15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupOwner", propOrder = {
        "type",
        "id"
})
public class GroupOwner implements Serializable {
    private String type;
    private String id;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
