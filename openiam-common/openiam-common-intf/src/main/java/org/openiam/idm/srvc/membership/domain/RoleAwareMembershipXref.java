package org.openiam.idm.srvc.membership.domain;

import org.openiam.idm.srvc.role.domain.RoleEntity;

public interface RoleAwareMembershipXref extends AbstractMembershipAwareXref {

	public RoleEntity getRole();
}
