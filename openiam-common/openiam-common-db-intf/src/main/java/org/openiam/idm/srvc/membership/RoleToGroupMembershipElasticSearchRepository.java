package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToGroupMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleToGroupMembershipElasticSearchRepository extends MembershipElasticSearchRepository<RoleToGroupMembershipXrefEntity> {

	@Override
	public default Class<RoleToGroupMembershipXrefEntity> getDocumentClass() {
		return RoleToGroupMembershipXrefEntity.class;
	}
}
