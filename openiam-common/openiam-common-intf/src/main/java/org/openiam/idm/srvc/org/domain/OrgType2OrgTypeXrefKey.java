package org.openiam.idm.srvc.org.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OrgType2OrgTypeXrefKey implements Serializable {

    @Column(name = "ORG_TYPE_ID")
    private String organizationTypeId;

    @Column(name = "MEMBER_ORG_TYPE_ID")
    private String memberOrganizationTypeId;

    public OrgType2OrgTypeXrefKey() {
    }

    public OrgType2OrgTypeXrefKey(final String organizationTypeId, final String memberOrganizationTypeId) {
        this.organizationTypeId = organizationTypeId;
        this.memberOrganizationTypeId = memberOrganizationTypeId;
    }

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

        OrgType2OrgTypeXrefKey that = (OrgType2OrgTypeXrefKey) o;

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
        sb.append("OrgType2OrgTypeXrefKey");
        sb.append("{organizationTypeId='").append(organizationTypeId).append('\'');
        sb.append(", memberOrganizationTypeId='").append(memberOrganizationTypeId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
