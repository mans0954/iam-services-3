package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleToResourceMembershipElasticSearchRepository extends MembershipElasticSearchRepository<RoleToResourceMembershipXrefEntity> {

	@Override
	public default Class<RoleToResourceMembershipXrefEntity> getDocumentClass() {
		return RoleToResourceMembershipXrefEntity.class;
	}
	
}
