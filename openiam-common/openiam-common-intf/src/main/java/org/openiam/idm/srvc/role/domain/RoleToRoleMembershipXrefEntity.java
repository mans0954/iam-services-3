package org.openiam.idm.srvc.role.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.RoleAwareMembershipXref;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role_to_role_membership")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
public class RoleToRoleMembershipXrefEntity extends AbstractMembershipXrefEntity<RoleEntity, RoleEntity> implements RoleAwareMembershipXref {

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false, nullable = false)
    //@ElasticsearchField(name = "entityId", bridge=@ElasticsearchFieldBridge(impl = RoleBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private RoleEntity entity;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MEMBER_ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false, nullable = false)
    //@ElasticsearchField(name = "memberEntityId", bridge=@ElasticsearchFieldBridge(impl = RoleBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private RoleEntity memberEntity;

    /* this is eager.  If you're loading the XREF - it's to get the rights */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_ROLE_MEMBERSHIP_RIGHTS",
            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<AccessRightEntity> rights;

    @Override
    @Transient
    public RoleEntity getRole() {
        return entity;
    }

    public RoleEntity getEntity() {
        return entity;
    }

    public void setEntity(RoleEntity entity) {
        this.entity = entity;
    }

    public RoleEntity getMemberEntity() {
        return memberEntity;
    }

    public void setMemberEntity(RoleEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    public Set<AccessRightEntity> getRights() {
        return rights;
    }

    public void setRights(Set<AccessRightEntity> rights) {
        this.rights = rights;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((entity == null) ? 0 : entity.hashCode());
        result = prime * result + ((memberEntity == null) ? 0 : memberEntity.hashCode());
        result = prime * result + ((rights == null) ? 0 : rights.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        RoleToRoleMembershipXrefEntity other = (RoleToRoleMembershipXrefEntity) obj;
        if (entity == null) {
            if (other.entity != null) return false;
        } else if (!entity.equals(other.entity)) return false;
        if (memberEntity == null) {
            if (other.memberEntity != null) return false;
        } else if (!memberEntity.equals(other.memberEntity)) return false;
        if (rights == null) {
            if (other.rights != null) return false;
        } else if (!rights.equals(other.rights)) return false;
        return true;
    }
}
