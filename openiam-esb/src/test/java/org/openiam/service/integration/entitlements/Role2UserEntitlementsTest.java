package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Role2UserEntitlementsTest extends AbstractEntitlementsTest<Role, User> {

	@Override
	protected Role createParent() {
		return super.createRole();
	}

	@Override
	protected User createChild() {
		return super.createUser();
	}

	@Override
	protected Response addChildToParent(Role parent, User child) {
		return roleServiceClient.addUserToRole(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Role parent, User child) {
		return roleServiceClient.removeUserFromRole(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Role parent) {
		return roleServiceClient.removeRole(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(User child) {
		return userServiceClient.deleteUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Role parent, User child) {
		final UserSearchBean searchBean = new UserSearchBean();
        searchBean.addRoleId(parent.getId());
        final List<User> users = userServiceClient.findBeans(searchBean, 0, 100);
        return (CollectionUtils.isNotEmpty(users)) ? users.contains(child) : false;
	}

	@Override
	protected boolean parentHasChild(Role parent, User child) {
		final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.addUserId(child.getId());
        final List<Role> roles = roleServiceClient.findBeans(searchBean, null, 0, 100);
        return (CollectionUtils.isNotEmpty(roles)) ? roles.contains(parent) : false;
	}

}
