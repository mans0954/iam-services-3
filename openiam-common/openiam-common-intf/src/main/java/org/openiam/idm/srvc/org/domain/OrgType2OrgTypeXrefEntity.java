package org.openiam.idm.srvc.org.domain;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.OrgType2OrgTypeXref;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ORG_TYPE_VALID_MEMBERSHIP")
@DozerDTOCorrespondence(OrgType2OrgTypeXref.class)
public class OrgType2OrgTypeXrefEntity {

    @EmbeddedId
    private OrgType2OrgTypeXrefKey id;

    public OrgType2OrgTypeXrefEntity() {
    }

    public OrgType2OrgTypeXrefEntity(final String organizationTypeId, final String memberOrganizationTypeId) {
        this.id = new OrgType2OrgTypeXrefKey(organizationTypeId, memberOrganizationTypeId);
    }

    public OrgType2OrgTypeXrefEntity(final OrgType2OrgTypeXrefKey orgType2OrgTypeXrefKey) {
        this.id = orgType2OrgTypeXrefKey;
    }

    public OrgType2OrgTypeXrefKey getId() {
        return id;
    }

    public void setId(OrgType2OrgTypeXrefKey id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgType2OrgTypeXrefEntity that = (OrgType2OrgTypeXrefEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OrgType2OrgTypeXrefEntity");
        sb.append("{orgType2OrgTypeXrefKey=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
