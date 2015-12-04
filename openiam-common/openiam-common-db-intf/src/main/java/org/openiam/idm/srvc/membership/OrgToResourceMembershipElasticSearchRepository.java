package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.ResourceToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgToResourceMembershipElasticSearchRepository extends MembershipElasticSearchRepository<ResourceToOrgMembershipXrefEntity> {

	@Override
	public default Class<ResourceToOrgMembershipXrefEntity> getEntityClass() {
		return ResourceToOrgMembershipXrefEntity.class;
	}
}
