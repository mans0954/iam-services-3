package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupToGroupMembershipElasticSearchRepository extends MembershipElasticSearchRepository<GroupToGroupMembershipXrefEntity> {

	@Override
	public default Class<GroupToGroupMembershipXrefEntity> getEntityClass() {
		return GroupToGroupMembershipXrefEntity.class;
	}
}
