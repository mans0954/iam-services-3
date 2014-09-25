package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Group2UserEntitlementsTest extends AbstractEntitlementsTest<Group, User> {

	@Override
	protected Group createParent() {
		return super.createGroup();
	}

	@Override
	protected User createChild() {
		return super.createUser();
	}

	@Override
	protected Response addChildToParent(Group parent, User child) {
		return groupServiceClient.addUserToGroup(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Group parent, User child) {
		return groupServiceClient.removeUserFromGroup(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Group parent) {
		return groupServiceClient.deleteGroup(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(User child) {
		return userServiceClient.deleteUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Group parent, User child) {
		UserSearchBean searchBean = new UserSearchBean();
        searchBean.addGroupId(parent.getId());
        final List<User> users = userServiceClient.findBeans(searchBean, 0, 100);
        return (CollectionUtils.isNotEmpty(users)) ? users.contains(child) : false;
	}

	@Override
	protected boolean parentHasChild(Group parent, User child) {
		final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.addUserId(child.getId());
        final List<Group> groups = groupServiceClient.findBeansLocalize(searchBean, null, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(groups)) ? groups.contains(parent) : false;
	}

}
