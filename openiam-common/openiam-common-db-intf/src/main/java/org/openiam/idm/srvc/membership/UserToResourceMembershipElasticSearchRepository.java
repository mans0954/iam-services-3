package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToResourceMembershipElasticSearchRepository extends MembershipElasticSearchRepository<UserToResourceMembershipXrefEntity> {

	@Override
	public default Class<UserToResourceMembershipXrefEntity> getDocumentClass() {
		return UserToResourceMembershipXrefEntity.class;
	}
}
