package org.openiam.idm.srvc.membership.domain;

import org.openiam.idm.srvc.res.domain.ResourceEntity;

public interface ResourceAwareMembershipXref extends AbstractMembershipAwareXref {

	public ResourceEntity getResource();
}
