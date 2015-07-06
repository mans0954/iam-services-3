package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToOrgMembershipHiberanteDAO")
public class UserToOrgMembershipHiberanteDAOImpl 
	   extends AbstractMembershipHibernateDAO<UserToOrganizationMembershipXrefEntity>
	   implements UserToOrgMembershipHiberanteDAO {

	@Override
	protected Class<UserToOrganizationMembershipXrefEntity> getEntityClass() {
		return UserToOrganizationMembershipXrefEntity.class;
	}

}
