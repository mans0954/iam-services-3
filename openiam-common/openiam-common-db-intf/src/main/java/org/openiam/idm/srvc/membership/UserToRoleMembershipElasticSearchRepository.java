package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToRoleMembershipElasticSearchRepository extends MembershipElasticSearchRepository<UserToRoleMembershipXrefEntity> {

	@Override
	public default Class<UserToRoleMembershipXrefEntity> getEntityClass() {
		return UserToRoleMembershipXrefEntity.class;
	}
}
