package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleToRoleMembershipElasticSearchRepository extends MembershipElasticSearchRepository<RoleToRoleMembershipXrefEntity> {

	@Override
	public default Class<RoleToRoleMembershipXrefEntity> getDocumentClass() {
		return RoleToRoleMembershipXrefEntity.class;
	}
}
