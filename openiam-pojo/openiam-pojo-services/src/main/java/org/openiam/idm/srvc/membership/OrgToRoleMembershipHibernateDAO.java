package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.RoleToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToRoleMembershipHibernateDAO")
public class OrgToRoleMembershipHibernateDAO extends AbstractMembershipHibernateDAO<RoleToOrgMembershipXrefEntity> {

	@Override
	protected Class<RoleToOrgMembershipXrefEntity> getEntityClass() {
		return RoleToOrgMembershipXrefEntity.class;
	}

}
