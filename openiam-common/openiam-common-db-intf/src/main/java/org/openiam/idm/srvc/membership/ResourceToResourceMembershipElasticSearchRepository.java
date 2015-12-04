package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceToResourceMembershipElasticSearchRepository extends MembershipElasticSearchRepository<ResourceToResourceMembershipXrefEntity> {

	@Override
	public default Class<ResourceToResourceMembershipXrefEntity> getEntityClass() {
		return ResourceToResourceMembershipXrefEntity.class;
	}
}
