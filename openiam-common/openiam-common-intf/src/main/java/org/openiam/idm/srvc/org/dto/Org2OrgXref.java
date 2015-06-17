package org.openiam.idm.srvc.org.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by: Alexander Duckardt
 * Date: 1/31/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organization", propOrder = {
        "organizationId",
        "memberOrganizationId"
})
@DozerDTOCorrespondence(Org2OrgXrefEntity.class)
public class Org2OrgXref implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Org2OrgXref that = (Org2OrgXref) o;

        if (memberOrganizationId != null ? !memberOrganizationId.equals(that.memberOrganizationId) : that.memberOrganizationId != null)
            return false;
        return !(organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null);

    }

    @Override
    public int hashCode() {
        int result = organizationId != null ? organizationId.hashCode() : 0;
        result = 31 * result + (memberOrganizationId != null ? memberOrganizationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Org2OrgXref");
        sb.append("{organizationId='").append(organizationId).append('\'');
        sb.append(", memberOrganizationId='").append(memberOrganizationId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
