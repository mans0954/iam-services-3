package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

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
	protected Response addChildToParent(final Group parent, final User child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return groupServiceClient.addUserToGroup(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Group parent, User child, final String requestorId) {
		return groupServiceClient.removeUserFromGroup(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Group parent, final String requestorId) {
		return groupServiceClient.deleteGroup(parent.getId());
	}

	@Override
	protected Response deleteChild(User child, final String requestorId) {
		return userServiceClient.removeUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Group parent, User child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addUserId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Group> dtos = groupServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Group> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Group e = optional.get();
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
	protected boolean parentHasChild(Group parent, User child, final Set<String> rights) {
		final UserSearchBean searchBean = new UserSearchBean();
		searchBean.addGroupId(parent.getId());
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
	protected Group getParentById(Group parent) {
		return groupServiceClient.getGroup(parent.getId());
	}

	@Override
	protected User getChildById(User child) {
		return userServiceClient.getUserWithDependent(child.getId(), false);
	}

	@Test
	public void foo() {}
}
