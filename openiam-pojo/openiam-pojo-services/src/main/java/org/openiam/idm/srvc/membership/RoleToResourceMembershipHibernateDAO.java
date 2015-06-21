package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("roleToResourceMembershipHibernateDAO")
public class RoleToResourceMembershipHibernateDAO extends AbstractMembershipHibernateDAO<RoleToResourceMembershipXrefEntity> {

	@Override
	protected Class<RoleToResourceMembershipXrefEntity> getEntityClass() {
		return RoleToResourceMembershipXrefEntity.class;
	}

}
