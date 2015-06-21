package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("userToOrgMembershipHiberanteDAO")
public class UserToOrgMembershipHiberanteDAO extends AbstractMembershipHibernateDAO<UserToOrganizationMembershipXrefEntity> {

	@Override
	protected Class<UserToOrganizationMembershipXrefEntity> getEntityClass() {
		return UserToOrganizationMembershipXrefEntity.class;
	}

}
