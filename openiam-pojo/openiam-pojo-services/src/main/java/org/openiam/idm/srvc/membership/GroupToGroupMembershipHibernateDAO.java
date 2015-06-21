package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("groupToGroupMembershipHibernateDAO")
public class GroupToGroupMembershipHibernateDAO extends AbstractMembershipHibernateDAO<GroupToGroupMembershipXrefEntity> {

	@Override
	protected Class<GroupToGroupMembershipXrefEntity> getEntityClass() {
		return GroupToGroupMembershipXrefEntity.class;
	}

}
