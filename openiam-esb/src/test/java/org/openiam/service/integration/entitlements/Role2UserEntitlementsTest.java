package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

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
	protected Response addChildToParent(Role parent, User child, final Set<String> rights) {
		return roleServiceClient.addUserToRole(parent.getId(), child.getId(), null, rights);
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
		return userServiceClient.removeUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Role parent, User child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addUserId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, "3000", 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Role e = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			} else {
				Assert.assertEquals(rights, e.getAccessRightIds());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Role parent, User child, final Set<String> rights) {
		final UserSearchBean searchBean = new UserSearchBean();
		searchBean.addRoleId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<User> dtos = userServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<User> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final User e = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			} else {
				Assert.assertEquals(rights, e.getAccessRightIds());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Role getParentById(Role parent) {
		return roleServiceClient.getRoleLocalized(parent.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected User getChildById(User child) {
		return userServiceClient.getUserWithDependent(child.getId(), "3000", false);
	}

	@Test
	public void foo() {}
}
