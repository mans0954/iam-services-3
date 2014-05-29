package org.openiam.idm.srvc.org.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Org2OrgXrefKey implements Serializable {

    @Column(name = "COMPANY_ID")
    private String organizationId;

    @Column(name = "MEMBER_COMPANY_ID")
    private String memberOrganizationId;

    public Org2OrgXrefKey() {
    }

    public Org2OrgXrefKey(final String organizationId, final String memberOrganizationId) {
        this.organizationId = organizationId;
        this.memberOrganizationId = memberOrganizationId;
    }

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

        Org2OrgXrefKey that = (Org2OrgXrefKey) o;

        if (memberOrganizationId != null ? !memberOrganizationId.equals(that.memberOrganizationId) : that.memberOrganizationId != null)
            return false;
        if (organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null)
            return false;

        return true;
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
        sb.append("Org2OrgXrefKey");
        sb.append("{organizationId='").append(organizationId).append('\'');
        sb.append(", memberOrganizationId='").append(memberOrganizationId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
