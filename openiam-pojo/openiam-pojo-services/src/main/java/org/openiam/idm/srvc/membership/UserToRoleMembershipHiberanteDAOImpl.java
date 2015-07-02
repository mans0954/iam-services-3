package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToRoleMembershipHiberanteDAO")
public class UserToRoleMembershipHiberanteDAOImpl 
	   extends AbstractMembershipHibernateDAO<UserToRoleMembershipXrefEntity>
	   implements UserToRoleMembershipHiberanteDAO{

	@Override
	protected Class<UserToRoleMembershipXrefEntity> getEntityClass() {
		return UserToRoleMembershipXrefEntity.class;
	}

}
