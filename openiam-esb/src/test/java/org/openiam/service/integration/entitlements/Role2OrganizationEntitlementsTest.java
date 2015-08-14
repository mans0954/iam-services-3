package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Role2OrganizationEntitlementsTest extends AbstractEntitlementsTest<Organization, Role> {

	@Override
	protected Organization getParentById(Organization parent) {
		return organizationServiceClient.getOrganizationLocalized(parent.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected Role getChildById(Role child) {
		return roleServiceClient.getRoleLocalized(child.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected Role createChild() {
		return super.createRole();
	}

	@Override
	protected Response addChildToParent(final Organization parent, final Role child, final Set<String> rights) {
		return organizationServiceClient.addRoleToOrganization(parent.getId(), child.getId(), rights);
	}

	@Override
	protected Response removeChildFromParent(final Organization parent, final Role child) {
		return organizationServiceClient.removeRoleFromOrganization(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(Role child) {
		return roleServiceClient.removeRole(child.getId(), "3000");
	}

	@Override
	protected boolean isChildInParent(Organization parent, Role child,
			Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addRoleId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Organization> dtos = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Organization> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child"), optional.isPresent());
			final Organization dto = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(dto.getAccessRightIds()));
			} else {
				Assert.assertEquals(dto.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Organization parent, Role child,
			Set<String> rights) {
		final RoleSearchBean searchBean = new RoleSearchBean();
		searchBean.addOrganizationId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Role> dtos = roleServiceClient.findBeans(searchBean, "3000", 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Role> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
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

	@Test
	public void foo(){}
}