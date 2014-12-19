package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;

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
	protected Response addChildToParent(Organization parent, User child) {
		return organizationServiceClient.addUserToOrg(parent.getId(), child.getId());
	}

	@Override
	protected Response removeChildFromParent(Organization parent, User child) {
		return organizationServiceClient.removeUserFromOrg(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(User child) {
		return userServiceClient.removeUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Organization parent, User child) {
		final List<Organization> organizations = organizationServiceClient.getOrganizationsForUserLocalized(child.getId(), null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(organizations)) ? organizations.contains(parent) : false;
	}

	@Override
	protected boolean parentHasChild(Organization parent, User child) {
		final UserSearchBean searchBean = new UserSearchBean();
		searchBean.addOrganizationId(parent.getId());
		final List<User> users = userServiceClient.findBeans(searchBean, 0, 100);
		return (CollectionUtils.isNotEmpty(users)) ? users.contains(child) : false;
	}

}
