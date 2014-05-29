package org.openiam.idm.srvc.org.domain;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.Org2OrgXref;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COMPANY_TO_COMPANY_MEMBERSHIP")
@DozerDTOCorrespondence(Org2OrgXref.class)
public class Org2OrgXrefEntity {
    @EmbeddedId
    private Org2OrgXrefKey id;

    public Org2OrgXrefEntity() {
    }

    public Org2OrgXrefEntity(final Org2OrgXrefKey id) {
        this.id = id;
    }

    public Org2OrgXrefEntity(final String organizationId, final String memberOrganizationId) {
        this.id = new Org2OrgXrefKey(organizationId, memberOrganizationId);
    }

    public Org2OrgXrefKey getId() {
        return id;
    }

    public void setId(Org2OrgXrefKey id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Org2OrgXrefEntity that = (Org2OrgXrefEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Org2OrgXrefEntity");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
