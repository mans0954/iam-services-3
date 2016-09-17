package org.openiam.idm.srvc.membership.domain;

import org.openiam.idm.srvc.grp.domain.GroupEntity;

public interface GroupAwareMembershipXref extends AbstractMembershipAwareXref {

	public GroupEntity getGroup();
}
