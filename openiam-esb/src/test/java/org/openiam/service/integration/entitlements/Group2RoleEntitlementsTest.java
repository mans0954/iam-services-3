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
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Group2RoleEntitlementsTest extends AbstractEntitlementsTest<Group, Role> {

	@Override
	protected Group createParent() {
		return super.createGroup();
	}

	@Override
	protected Role createChild() {
		return super.createRole();
	}

	@Override
	protected Response addChildToParent(final Group parent, final Role child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return roleServiceClient.addGroupToRole(child.getId(), parent.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Role child, final String requestorId) {
		return roleServiceClient.removeGroupFromRole(child.getId(), parent.getId());
	}

	@Override
	protected Response deleteParent(Group parent, final String requestorId) {
		return groupServiceClient.deleteGroup(parent.getId());
	}

	@Override
	protected Response deleteChild(Role child, final String requestorId) {
		return roleServiceClient.removeRole(child.getId());
	}

	@Override
	protected boolean isChildInParent(Group parent, Role child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addGroupId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
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
	protected boolean parentHasChild(Group parent, Role child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addRoleId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		searchBean.setLanguage(getDefaultLanguage());
		final List<Group> dtos = groupServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Group> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child"), optional.isPresent());
			final Group e = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			} else {
				Assert.assertEquals(e.getAccessRightIds(), rights);
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
	protected Role getChildById(Role child) {
		return roleServiceClient.getRoleLocalized(child.getId(), getDefaultLanguage());
	}

	@Test
	public void foo() {}
}
