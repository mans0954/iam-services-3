package org.openiam.service.integration.entitlements;

import org.openiam.base.ws.Response;
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
		return userServiceClient.deleteUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Organization parent, User child) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean parentHasChild(Organization parent, User child) {
		// TODO Auto-generated method stub
		return false;
	}

}
