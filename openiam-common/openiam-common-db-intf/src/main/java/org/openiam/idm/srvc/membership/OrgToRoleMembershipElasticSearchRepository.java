package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.RoleToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgToRoleMembershipElasticSearchRepository extends MembershipElasticSearchRepository<RoleToOrgMembershipXrefEntity> {

	@Override
	public default Class<RoleToOrgMembershipXrefEntity> getEntityClass() {
		return RoleToOrgMembershipXrefEntity.class;
	}
}
