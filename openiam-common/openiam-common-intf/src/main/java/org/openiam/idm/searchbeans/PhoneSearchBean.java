package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.Phone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhoneSearchBean", propOrder = {
        "parentId",
        /*"parentType",*/
        "phoneAreaCd",
        "phoneNbr",
        "metadataTypeId"
})
public class PhoneSearchBean extends AbstractSearchBean<Phone, String> implements SearchBean<Phone, String>,
        Serializable {
    private String parentId;
    //private String parentType;
    private String phoneNbr;
    private String phoneAreaCd;
    private String metadataTypeId;

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
    
    public String getPhoneAreaCd() {
        return phoneAreaCd;
    }

    public void setPhoneAreaCd(String phoneAreaCd) {
        this.phoneAreaCd = phoneAreaCd;
    }

    public String getPhoneNbr() {
        return phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }
}
