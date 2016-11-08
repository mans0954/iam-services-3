package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToGroupMembershipElasticSearchRepository extends MembershipElasticSearchRepository<UserToGroupMembershipXrefEntity> {

	@Override
	public default Class<UserToGroupMembershipXrefEntity> getDocumentClass() {
		return UserToGroupMembershipXrefEntity.class;
	}
}
