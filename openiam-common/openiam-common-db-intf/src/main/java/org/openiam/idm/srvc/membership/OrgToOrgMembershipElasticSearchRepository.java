package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgToOrgMembershipElasticSearchRepository extends MembershipElasticSearchRepository<OrgToOrgMembershipXrefEntity> {

	@Override
	public default Class<OrgToOrgMembershipXrefEntity> getEntityClass() {
		return OrgToOrgMembershipXrefEntity.class;
	}
}
