package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("roleToResourceMembershipHibernateDAO")
public class RoleToResourceMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<RoleToResourceMembershipXrefEntity> 
	   implements RoleToResourceMembershipHibernateDAO {

	@Override
	protected Class<RoleToResourceMembershipXrefEntity> getEntityClass() {
		return RoleToResourceMembershipXrefEntity.class;
	}

}
