package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class Role2RoleEntitlementsTest extends AbstractCircularEntitlementTest<Role> {

	@Override
	protected Role createParent() {
		return createRole();
	}

	@Override
	protected Role createChild() {
		return createRole();
	}

	@Override
	protected Response addChildToParent(Role parent, Role child, final Set<String> rights) {
		return roleServiceClient.addChildRole(parent.getId(), child.getId(), null, rights);
	}

	@Override
	protected Response removeChildFromParent(Role parent, Role child) {
		return roleServiceClient.removeChildRole(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Role parent) {
		return roleServiceClient.removeRole(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Role child) {
		return roleServiceClient.removeRole(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Role parent, Role child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addChildId(child.getId());
		//searchBean.setIncludeAccessRights(true);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, "3000", 0, 1000);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child role"), optional.isPresent());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(final Role parent, final Role child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addParentId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, "3000", 0, 1000);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent role"), optional.isPresent());
			final Role role = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(role.getAccessRightIds()));
			} else {
				Assert.assertEquals(role.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Test
	public void foo(){}
}
