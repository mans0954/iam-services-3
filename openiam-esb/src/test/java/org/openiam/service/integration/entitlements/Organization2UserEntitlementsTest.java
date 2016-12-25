package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Organization2UserEntitlementsTest extends AbstractEntitlementsTest<Organization, User> {

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected User createChild() {
		return super.createUser();
	}

	@Override
	protected Response addChildToParent(Organization parent, User child, final String requestorId, final Set<String> rights, Date startDate, final Date endDate) {
		return organizationServiceClient.addUserToOrg(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Organization parent, User child, final String requestorId) {
		return organizationServiceClient.removeUserFromOrg(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent, final String requestorId) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(User child, final String requestorId) {
		return userServiceClient.removeUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Organization parent, User child, final Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addUserId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		searchBean.setLanguage(getDefaultLanguage());
		final List<Organization> dtos = organizationServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Organization> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Organization e = optional.get();
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
	protected boolean parentHasChild(Organization parent, User child, final Set<String> rights) {
		final UserSearchBean searchBean = new UserSearchBean();
		searchBean.addOrganizationId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<User> dtos = userServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<User> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			if(optional.isPresent()) {
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
		} else {
			return false;
		}
	}

	@Override
	protected Organization getParentById(Organization parent) {
		return organizationServiceClient.getOrganization(parent.getId());
	}

	@Override
	protected User getChildById(User child) {
		return userServiceClient.getUserWithDependent(child.getId(), false);
	}
	
	@Test
	public void foo() {}

}
