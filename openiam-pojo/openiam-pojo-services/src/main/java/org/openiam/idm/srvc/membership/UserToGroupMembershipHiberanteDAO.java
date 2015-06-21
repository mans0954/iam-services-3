package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToGroupMembershipHiberanteDAO")
public class UserToGroupMembershipHiberanteDAO extends AbstractMembershipHibernateDAO<UserToGroupMembershipXrefEntity> {

	@Override
	protected Class<UserToGroupMembershipXrefEntity> getEntityClass() {
		return UserToGroupMembershipXrefEntity.class;
	}

}
