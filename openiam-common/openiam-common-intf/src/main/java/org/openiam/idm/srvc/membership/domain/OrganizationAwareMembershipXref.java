package org.openiam.idm.srvc.membership.domain;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;

public interface OrganizationAwareMembershipXref extends AbstractMembershipAwareXref {

	public OrganizationEntity getOrganization();
}
