package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.grp.domain.GroupToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupToResourceMembershipElasticSearchRepository extends MembershipElasticSearchRepository<GroupToResourceMembershipXrefEntity> {

	@Override
	public default Class<GroupToResourceMembershipXrefEntity> getEntityClass() {
		return GroupToResourceMembershipXrefEntity.class;
	}
}
