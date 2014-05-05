package org.openiam.idm.srvc.org.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by: Alexander Duckardt
 * Date: 1/31/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organization", propOrder = {
        "organizationId",
        "memberOrganizationId"
})
public class Org2OrgXref {
    private String organizationId;
    private String memberOrganizationId;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getMemberOrganizationId() {
        return memberOrganizationId;
    }

    public void setMemberOrganizationId(String memberOrganizationId) {
        this.memberOrganizationId = memberOrganizationId;
    }
}
