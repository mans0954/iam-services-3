package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToOrgMembershipElasticSearchRepository extends MembershipElasticSearchRepository<UserToOrganizationMembershipXrefEntity> {

	@Override
	public default Class<UserToOrganizationMembershipXrefEntity> getEntityClass() {
		return UserToOrganizationMembershipXrefEntity.class;
	}
}
