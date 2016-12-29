package org.openiam.idm.srvc.membership.domain;

import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;

import javax.persistence.MappedSuperclass;
import java.util.Set;

@MappedSuperclass
public abstract class AbstractMembershipXrefEntity<Parent extends KeyEntity, Child extends KeyEntity> extends KeyEntity {

    public abstract Set<AccessRightEntity> getRights();

    public abstract Parent getEntity();

    public abstract Child getMemberEntity();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractMembershipXrefEntity other = (AbstractMembershipXrefEntity) obj;
        return true;
    }


}
