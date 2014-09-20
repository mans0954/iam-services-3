package org.openiam.service.integration;

import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationTypeDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractEntitlementsTest<Parent extends KeyDTO, Child extends KeyDTO> extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("groupServiceClient")
	protected GroupDataWebService groupServiceClient;
	
	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleServiceClient;
	

	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;
	
	@Autowired
	@Qualifier("organizationServiceClient")
	protected OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;

	@Test
	public void clusterTest() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			response = addChildToParent(parent, child);
			refreshAuthorizationManager();
			Assert.assertTrue(response.isSuccess(), String.format("Could not add child to parent.  %s", response));
			Assert.assertTrue(isChildInParent(parent, child), String.format("Child %s not in parent %s", child, parent));
			Assert.assertTrue(isChildInParent(parent, child), String.format("Child %s not in parent %s", child, parent));
			Assert.assertTrue(parentHasChild(parent, child), String.format("Parent does not have child", parent, child));
			Assert.assertTrue(parentHasChild(parent, child), String.format("Parent does not have child", parent, child));
			response = removeChildFromParent(parent, child);
			refreshAuthorizationManager();
			Assert.assertTrue(response.isSuccess(), String.format("Could remove child from parent.  %s", response));
			Assert.assertFalse(isChildInParent(parent, child), String.format("Child %s in parent %s", child, parent));
			Assert.assertFalse(isChildInParent(parent, child), String.format("Child %s in parent %s", child, parent));
			Assert.assertFalse(parentHasChild(parent, child), String.format("Parent has child", parent, child));
			Assert.assertFalse(parentHasChild(parent, child), String.format("Parent has child", parent, child));
		} finally {
			if(parent != null) {
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
		}
	}
	
	protected Group createGroup() {
		Group group = new Group();
		group.setName(getRandomName());
		final Response wsResponse = groupServiceClient.saveGroup(group, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", group, wsResponse));
		group = groupServiceClient.getGroup((String)wsResponse.getResponseValue(), null);
		return group;
	}
	
	protected Organization createOrganization() {
		Organization organization = new Organization();
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1, null).get(0).getId());
		organization.setName(getRandomName());
		final Response wsResponse = organizationServiceClient.saveOrganization(organization, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", organization, wsResponse));
		organization = organizationServiceClient.getOrganizationLocalized((String)wsResponse.getResponseValue(), null, getDefaultLanguage());
		return organization;
	}
	
	protected User createUser() {
		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setLogin(getRandomName());
		user.setPassword(getRandomName());
		user.setNotifyUserViaEmail(false);
		final UserResponse userResponse = userServiceClient.saveUserInfo(user, null);
		Assert.assertTrue(userResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", user, userResponse));
		return userServiceClient.getUserWithDependent(userResponse.getUser().getId(), null, true);
	}
	
	protected Resource createResource() {
		Resource resource = new Resource();
		final ResourceTypeSearchBean resourceTypeSearchBean = new ResourceTypeSearchBean();
		resourceTypeSearchBean.setSupportsHierarchy(true);
		resource.setResourceType(resourceDataService.findResourceTypes(resourceTypeSearchBean, 0, 1, null).get(0));
		resource.setName(getRandomName());
		final Response wsResponse = resourceDataService.saveResource(resource, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", resource, wsResponse));
		resource = resourceDataService.getResource((String)wsResponse.getResponseValue(), getDefaultLanguage());
		return resource;
	}
	
	protected Role createRole() {
		Role role = new Role();
		role.setName(getRandomName());
		final Response wsResponse = roleServiceClient.saveRole(role, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", role, wsResponse));
		role = roleServiceClient.getRoleLocalized((String)wsResponse.getResponseValue(), null, getDefaultLanguage());
		return role;
	}
	
	protected abstract Parent createParent();
	protected abstract Child createChild();
	protected abstract Response addChildToParent(final Parent parent, final Child child);
	protected abstract Response removeChildFromParent(final Parent parent, final Child child);
	protected abstract Response deleteParent(final Parent parent);
	protected abstract Response deleteChild(final Child child);
	protected abstract boolean isChildInParent(final Parent parent, final Child child);
	protected abstract boolean parentHasChild(final Parent parent, final Child child);
}
