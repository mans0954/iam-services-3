package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("roleToRoleMembershipHibernateDAO")
public class RoleToRoleMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<RoleToRoleMembershipXrefEntity> 
	   implements RoleToRoleMembershipHibernateDAO {

	@Override
	protected Class<RoleToRoleMembershipXrefEntity> getEntityClass() {
		return RoleToRoleMembershipXrefEntity.class;
	}

}
