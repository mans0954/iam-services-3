package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("roleToRoleMembershipHibernateDAO")
public class RoleToRoleMembershipHibernateDAO extends AbstractMembershipHibernateDAO<RoleToRoleMembershipXrefEntity> {

	@Override
	protected Class<RoleToRoleMembershipXrefEntity> getEntityClass() {
		return RoleToRoleMembershipXrefEntity.class;
	}

}
