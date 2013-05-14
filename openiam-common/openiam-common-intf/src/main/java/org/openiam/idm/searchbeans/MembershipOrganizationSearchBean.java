package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MembershipOrganizationSearchBean", propOrder = {
        "memberShipOrganisationId"
})
public class MembershipOrganizationSearchBean extends OrganizationSearchBean {
    private String memberShipOrganisationId;

    public String getMemberShipOrganisationId() {
        return memberShipOrganisationId;
    }

    public void setMemberShipOrganisationId(String memberShipOrganisationId) {
        this.memberShipOrganisationId = memberShipOrganisationId;
    }
}
