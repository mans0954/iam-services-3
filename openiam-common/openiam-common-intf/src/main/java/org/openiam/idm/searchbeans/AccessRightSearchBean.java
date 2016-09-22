package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.continfo.dto.Address;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessRightSearchBean", propOrder = {

})
public class AccessRightSearchBean extends AbstractKeyNameSearchBean<AccessRight, String> {

    String metadataTypeId1;
    String metadataTypeId2;

    public String getMetadataTypeId1() {
        return metadataTypeId1;
    }

    public void setMetadataTypeId1(String metadataTypeId1) {
        this.metadataTypeId1 = metadataTypeId1;
    }

    public String getMetadataTypeId2() {
        return metadataTypeId2;
    }

    public void setMetadataTypeId2(String metadataTypeId2) {
        this.metadataTypeId2 = metadataTypeId2;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return String.format("AbstractKeyNameSearchBean [name=%s]", name);
    }
}
