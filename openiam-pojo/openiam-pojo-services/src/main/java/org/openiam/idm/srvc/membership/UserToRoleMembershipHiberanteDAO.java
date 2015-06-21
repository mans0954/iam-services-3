package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToRoleMembershipHiberanteDAO")
public class UserToRoleMembershipHiberanteDAO extends AbstractMembershipHibernateDAO<UserToRoleMembershipXrefEntity> {

	@Override
	protected Class<UserToRoleMembershipXrefEntity> getEntityClass() {
		return UserToRoleMembershipXrefEntity.class;
	}

}
