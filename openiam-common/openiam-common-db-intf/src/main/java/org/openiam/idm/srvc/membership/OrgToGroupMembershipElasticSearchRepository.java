package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgToGroupMembershipElasticSearchRepository extends MembershipElasticSearchRepository<GroupToOrgMembershipXrefEntity> {

	@Override
	public default Class<GroupToOrgMembershipXrefEntity> getDocumentClass() {
		return GroupToOrgMembershipXrefEntity.class;
	}
}
