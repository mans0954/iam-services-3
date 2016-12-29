package org.openiam.idm.srvc.membership.domain;

import java.util.Set;

import org.openiam.idm.srvc.access.domain.AccessRightEntity;

public interface AbstractMembershipAwareXref {

	public Set<AccessRightEntity> getRights();
}
