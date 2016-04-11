package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.Address;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressSearchBean", propOrder = {
        "parentId",
        "metadataTypeId"/*,
        "parentType"*/
})
public class AddressSearchBean  extends AbstractSearchBean<Address, String> implements SearchBean<Address, String>,
        Serializable {
    private String parentId;
    private String metadataTypeId;
    //private String parentType;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /*
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
    */

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(parentId != null ? parentId : "")
                .append(metadataTypeId != null ? metadataTypeId : "")
                .append(getKey() != null ? getKey() : "")
                .toString();    }
}
