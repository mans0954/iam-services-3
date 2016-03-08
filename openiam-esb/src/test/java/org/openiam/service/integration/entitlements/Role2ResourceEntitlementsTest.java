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
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class Role2ResourceEntitlementsTest extends AbstractEntitlementsTest<Role, Resource> {

	@Override
	protected Role createParent() {
		return super.createRole();
	}

	@Override
	protected Resource createChild() {
		return super.createResource();
	}

	@Override
	protected Response addChildToParent(final Role parent, final Resource child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return resourceDataService.addRoleToResource(child.getId(), parent.getId(), null, rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Role parent, Resource child, final String requestorId) {
		return resourceDataService.removeRoleToResource(child.getId(), parent.getId(), null);
	}

	@Override
	protected Response deleteParent(Role parent, final String requestorId) {
		return roleServiceClient.removeRole(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Resource child, final String requestorId) {
		return resourceDataService.deleteResource(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Role parent, Resource child, final Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addResourceId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, "3000", 0, Integer.MAX_VALUE);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child"), optional.isPresent());
			final Role e = optional.get();
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
	protected boolean parentHasChild(Role parent, Resource child, final Set<String> rights) {
		final ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addRoleId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Resource> dtos = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Resource> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Resource e = optional.get();
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
	protected Resource getChildById(Resource child) {
		return resourceDataService.getResource(child.getId(), getDefaultLanguage());
	}

	@Test
	public void foo() {}
}
