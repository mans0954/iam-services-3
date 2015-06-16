package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Resource2UserEntitlementsTest extends AbstractEntitlementsTest<Resource, User> {

	@Override
	protected Resource createParent() {
		return super.createResource();
	}

	@Override
	protected User createChild() {
		return super.createUser();
	}

	@Override
	protected Response addChildToParent(Resource parent, User child) {
		return resourceDataService.addUserToResource(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Resource parent, User child) {
		return resourceDataService.removeUserFromResource(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Resource parent) {
		return resourceDataService.deleteResource(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(User child) {
		return userServiceClient.removeUser(child.getId());
	}

	@Override
	protected boolean isChildInParent(Resource parent, User child) {
		UserSearchBean searchBean = new UserSearchBean();
        searchBean.addResourceId(parent.getId());
        final List<User> users = userServiceClient.findBeans(searchBean, 0, 100);
        return (CollectionUtils.isNotEmpty(users)) ? users.contains(child) : false;
	}

	@Override
	protected boolean parentHasChild(Resource parent, User child) {
		final ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addUserId(child.getId());
		final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(resources)) ? resources.contains(parent) : false;
	}

}
