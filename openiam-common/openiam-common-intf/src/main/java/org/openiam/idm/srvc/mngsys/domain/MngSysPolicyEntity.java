package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MNG_SYS_POLICY")
@DozerDTOCorrespondence(MngSysPolicyDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "MNG_SYS_POLICY_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable = true))
})
public class MngSysPolicyEntity extends AbstractMetdataTypeEntity {

    public MngSysPolicyEntity() {
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="mngSysPolicy", orphanRemoval=true)
    private Set<AttributeMapEntity> attributeMaps = new HashSet<>(0);

    @ManyToOne(optional = false)
    @JoinColumn(name = "MANAGED_SYS_ID", nullable = false)
    private ManagedSysEntity managedSystem;

    @Column(name = "NAME", length = 150)
    private String name;

    @Column(name = "LAST_UPDATE", length = 19)
    private Date lastUpdate;

    @Column(name="CREATE_DATE",length=19)
    private Date createDate;

    @Column(name = "IS_PRIMARY", nullable = false)
    @Type(type = "yes_no")
    private boolean primary = false;

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Set<AttributeMapEntity> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(Set<AttributeMapEntity> attributeMaps) {
        this.attributeMaps = attributeMaps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ManagedSysEntity getManagedSystem() {
        return managedSystem;
    }

    public void setManagedSystem(ManagedSysEntity managedSystem) {
        this.managedSystem = managedSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MngSysPolicyEntity that = (MngSysPolicyEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (id == null) {
            if (((MngSysPolicyEntity) o).getId() != null)
                return false;
        } else if (!id.equals(((MngSysPolicyEntity) o).getId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
