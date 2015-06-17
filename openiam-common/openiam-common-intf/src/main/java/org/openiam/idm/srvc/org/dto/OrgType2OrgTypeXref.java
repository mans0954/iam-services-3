package org.openiam.idm.srvc.org.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;

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
@DozerDTOCorrespondence(OrgType2OrgTypeXrefEntity.class)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgType2OrgTypeXref that = (OrgType2OrgTypeXref) o;

        if (memberOrganizationTypeId != null ? !memberOrganizationTypeId.equals(that.memberOrganizationTypeId) : that.memberOrganizationTypeId != null)
            return false;
        return !(organizationTypeId != null ? !organizationTypeId.equals(that.organizationTypeId) : that.organizationTypeId != null);

    }

    @Override
    public int hashCode() {
        int result = organizationTypeId != null ? organizationTypeId.hashCode() : 0;
        result = 31 * result + (memberOrganizationTypeId != null ? memberOrganizationTypeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OrgType2OrgTypeXref");
        sb.append("{organizationTypeId='").append(organizationTypeId).append('\'');
        sb.append(", memberOrganizationTypeId='").append(memberOrganizationTypeId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
