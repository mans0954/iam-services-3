package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToResourceMembershipHiberanteDAO")
public class UserToResourceMembershipHiberanteDAO extends AbstractMembershipHibernateDAO<UserToResourceMembershipXrefEntity> {

	@Override
	protected Class<UserToResourceMembershipXrefEntity> getEntityClass() {
		return UserToResourceMembershipXrefEntity.class;
	}

}
