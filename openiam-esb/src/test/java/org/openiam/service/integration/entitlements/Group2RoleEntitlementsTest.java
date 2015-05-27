package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;

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
	protected Response addChildToParent(Group parent, Role child, final Set<String> rights) {
		return roleServiceClient.addGroupToRole(child.getId(), parent.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Role child) {
		return roleServiceClient.removeGroupFromRole(child.getId(), parent.getId(), null);
	}

	@Override
	protected Response deleteParent(Group parent) {
		return groupServiceClient.deleteGroup(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Role child) {
		return roleServiceClient.removeRole(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Group parent, Role child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addRoleId(child.getId());
		final List<Group> groups = groupServiceClient.findBeansLocalize(searchBean, null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(groups)) ? groups.contains(parent) : false;
	}

	@Override
	protected boolean parentHasChild(Group parent, Role child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addGroupId(parent.getId());
		final List<Role> roles = roleServiceClient.findBeans(searchBean, null, 0, 100);
		return (CollectionUtils.isNotEmpty(roles)) ? roles.contains(child) : false;
	}

}
