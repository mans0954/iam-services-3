package org.openiam.idm.srvc.org.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by: Alexander Duckardt
 * Date: 2/14/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organization", propOrder = {
        "organizationTypeId",
        "memberOrganizationTypeId"
})
public class OrgType2OrgTypeXref {
    private String organizationTypeId;
    private String memberOrganizationTypeId;

    public String getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(String organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public String getMemberOrganizationTypeId() {
        return memberOrganizationTypeId;
    }

    public void setMemberOrganizationTypeId(String memberOrganizationTypeId) {
        this.memberOrganizationTypeId = memberOrganizationTypeId;
    }
}
