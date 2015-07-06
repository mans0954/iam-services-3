package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToResourceMembershipHiberanteDAO")
public class UserToResourceMembershipHiberanteDAOImpl 
	   extends AbstractMembershipHibernateDAO<UserToResourceMembershipXrefEntity>
	   implements UserToResourceMembershipHiberanteDAO {

	@Override
	protected Class<UserToResourceMembershipXrefEntity> getEntityClass() {
		return UserToResourceMembershipXrefEntity.class;
	}

}
