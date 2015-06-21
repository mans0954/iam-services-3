package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.grp.domain.GroupToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("groupToResourceMembershipHibernateDAO")
public class GroupToResourceMembershipHibernateDAO extends AbstractMembershipHibernateDAO<GroupToResourceMembershipXrefEntity> {

	@Override
	protected Class<GroupToResourceMembershipXrefEntity> getEntityClass() {
		return GroupToResourceMembershipXrefEntity.class;
	}

}
