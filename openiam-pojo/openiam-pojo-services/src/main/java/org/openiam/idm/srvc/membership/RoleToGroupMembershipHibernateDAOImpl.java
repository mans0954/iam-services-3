package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("roleToGroupMembershipHibernateDAO")
public class RoleToGroupMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<RoleToGroupMembershipXrefEntity> 
	   implements RoleToGroupMembershipHibernateDAO {

	@Override
	protected Class<RoleToGroupMembershipXrefEntity> getEntityClass() {
		return RoleToGroupMembershipXrefEntity.class;
	}

}
