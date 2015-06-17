package org.openiam.service.integration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.ws.AccessRightDataService;
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
	@Qualifier("organizationServiceClient")
	protected OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;
	
	@Autowired
	@Qualifier("accessRightServiceClient")
	protected AccessRightDataService accessRightServiceClient;

	@Test
	public void testDeleteWithRelationship() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()));
			
			if(parent != null) { /* should still work - relationship should be removed */
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			
			Assert.assertNull(getParentById(parent));
			Assert.assertNotNull(getChildById(child));
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
			Assert.assertNull(getChildById(child));
		} finally {
			
		}
	}
	
	@Test
	public void testDeleteWithInverseRelationship() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()));
			
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
			Assert.assertNull(getChildById(child));
			Assert.assertNotNull(getParentById(parent));
			
			if(parent != null) { /* should still work - relationship should be removed */
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			Assert.assertNull(getParentById(parent));
		} finally {
			
		}
	}
	
	@Test
	public void clusterTest() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddAndRemove(parent, child, null);
			
			final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
			doAddAndRemove(parent, child, rightIds);
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
	
	protected void doAddAndRemove(final Parent parent, final Child child, final Set<String> rightIds) {
		doAddChildToParent(parent, child, rightIds);
		doRemoveChildFromParent(parent, child, rightIds);
	}
	
	protected void doAddChildToParent(final Parent parent, final Child child, final Set<String> rightIds) {
		Response response = addChildToParent(parent, child, rightIds);
		refreshAuthorizationManager();
		refreshAuthorizationManager();
		Assert.assertTrue(response.isSuccess(), String.format("Could not add child to parent.  %s", response));
		Assert.assertTrue(isChildInParent(parent, child, rightIds), String.format("Child %s not in parent %s", child, parent));
		Assert.assertTrue(isChildInParent(parent, child, rightIds), String.format("Child %s not in parent %s", child, parent));
		Assert.assertTrue(parentHasChild(parent, child, rightIds), String.format("Parent does not have child", parent, child));
		Assert.assertTrue(parentHasChild(parent, child, rightIds), String.format("Parent does not have child", parent, child));
	}
	
	protected void doRemoveChildFromParent(final Parent parent, final Child child, final Set<String> rightIds) {
		Response response = removeChildFromParent(parent, child);
		refreshAuthorizationManager();
		refreshAuthorizationManager();
		Assert.assertTrue(response.isSuccess(), String.format("Could remove child from parent.  %s", response));
		Assert.assertFalse(isChildInParent(parent, child, rightIds), String.format("Child %s in parent %s", child, parent));
		Assert.assertFalse(isChildInParent(parent, child, rightIds), String.format("Child %s in parent %s", child, parent));
		Assert.assertFalse(parentHasChild(parent, child, rightIds), String.format("Parent has child", parent, child));
		Assert.assertFalse(parentHasChild(parent, child, rightIds), String.format("Parent has child", parent, child));
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
	
	protected abstract Parent getParentById(final Parent parent);
	protected abstract Child getChildById(final Child child);
	protected abstract Parent createParent();
	protected abstract Child createChild();
	protected abstract Response addChildToParent(final Parent parent, final Child child, final Set<String> rights);
	protected abstract Response removeChildFromParent(final Parent parent, final Child child);
	protected abstract Response deleteParent(final Parent parent);
	protected abstract Response deleteChild(final Child child);
	protected abstract boolean isChildInParent(final Parent parent, final Child child, final Set<String> rights);
	protected abstract boolean parentHasChild(final Parent parent, final Child child, final Set<String> rights);
}
