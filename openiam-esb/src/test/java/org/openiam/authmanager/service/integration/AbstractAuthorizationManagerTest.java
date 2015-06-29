package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public abstract class AbstractAuthorizationManagerTest extends AbstractServiceTest {

	protected static final Log log = LogFactory.getLog(AbstractAuthorizationManagerTest.class);
	
	final String[] urlpatterns = new String[] {
		"http://www.google.com/foo/bar.html",
		"http://www.google.com/openiam/selfservice",
		"http://www.google.com/openiam/selfservice.html",
		"http://www.facebook.com/foo/bar.html",
		"http://www.facebook.com/openiam/selfservice",
		"http://www.facebook.com/openiam/selfservice.html",
		"https://www.facebook.com/foo/bar.html",
		"https://www.facebook.com/openiam/selfservice",
		"https://www.facebook.com/openiam/selfservice.html",
		"https://www.google.com/foo/bar.html",
		"https://www.google.com/openiam/selfservice",
		"https://www.google.com/openiam/selfservice.html"
	};
	
	private User user = null;
	private Group group = null;
	private Role role = null;
	private Organization organization = null;
	private Resource resource = null;
	
	private void assertSuccess(final Response response) {
		Assert.assertTrue(response.isSuccess());
	}
	

	protected Set<String> getRightIdsNotIn(final Set<String> rightIds) {
		return accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage())
									   .stream()
									   .map(e -> e.getId())
									   .filter(e -> !rightIds.contains(e))
									   .collect(Collectors.toSet());
	}

	
	@BeforeClass
	public void _init() {
		user = super.createUser();
		group = super.createGroup();
		role = super.createRole();
		organization = super.createOrganization();
		resource = super.createResource();
		Assert.assertNotNull(user);
		Assert.assertNotNull(group);
		Assert.assertNotNull(role);
		Assert.assertNotNull(organization);
		Assert.assertNotNull(resource);
		
		if(loginAfterUserCreation()) {
			refreshAuthorizationManager();
		}
	}
	
	@AfterClass
	public void _destroy() {
		userServiceClient.removeUser(user.getId());
		groupServiceClient.deleteGroup(group.getId(), null);
		roleServiceClient.removeRole(role.getId(), null);
		organizationServiceClient.deleteOrganization(organization.getId());
		resourceDataService.deleteResource(resource.getId(), null);
	}
	
	private Set<String> getRightIds() {
		final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage());
		final Set<String> rightIds = rights.subList(0, rights.size() / 2).stream().map(e -> e.getId()).collect(Collectors.toSet());
		return rightIds;
	}
	
	private Set<String> getAllRightIds() {
		final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage());
		final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
		return rightIds;
	}

	@Test
	public void testUser2ResourceDirect() {
		doUser2ResourceAddition(user.getId(), resource.getId(), null);
	}
	
	@Test
	public void testUser2ResourceDirectWithRights() {
		doUser2ResourceAddition(user.getId(), resource.getId(), getRightIds());
	}
	
	private void doUser2ResourceAddition(final String userId, final String resourceId, final Set<String> rightIds) {
		Response response = resourceDataService.addUserToResource(resourceId, userId, getRequestorId(), rightIds);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(userId, resourceId, rightIds, true);
		response = resourceDataService.removeUserFromResource(resourceId, userId, getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(userId, resourceId, rightIds, false);
	}

	@Test
	public void testUser2RoleDirect() {
		doUser2RoleAddition(user.getId(), role.getId(), null);
	}
	/*
	@Test
	public void testUser2RoleDirectWithRihts() {
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds());
	}
	*/
	
	private void doUser2RoleAddition(final String userId, final String roleId, final Set<String> rightIds) {
		Response response = roleServiceClient.addUserToRole(roleId, userId, getRequestorId(), rightIds);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(userId, roleId, rightIds, true);
		response = roleServiceClient.removeUserFromRole(roleId, userId, getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(userId, roleId, rightIds, false);
	}
	
	@Test
	public void testUser2OrganizationDirect() {
		doUser2OrganizationAddition(user.getId(), organization.getId(), null);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRights() {
		doUser2OrganizationAddition(user.getId(), organization.getId(), getRightIds());
	}
	
	private Set<String> toArray(final String right) {
		return new HashSet<String>(Arrays.asList(new String[] {right}));
	}
	
	@Test
	public void testUser2RoleIndirect() {
		
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganization() {
		
		
	}
	
	@Test
	public void testUser2OrganizationIndirect() {
		final Set<Organization> organizationsToDelete = new HashSet<Organization>();
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		getAllRightIds().forEach(right -> {
			final Organization parent = super.createOrganization();
			Assert.assertNotNull(parent);
			organizationsToDelete.add(parent);
			doOrg2OrgAddition(parent.getId(), organization.getId(), toArray(right));
			refreshAuthorizationManager();
			checkUser2OrganizationMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2OrganizationMembership(user.getId(), parent.getId(), null, true);
		});
		organizationsToDelete.forEach(org -> {
			assertSuccess(organizationServiceClient.deleteOrganization(org.getId()));
		});
		refreshAuthorizationManager();
		organizationsToDelete.forEach(org -> {
			checkUser2OrganizationMembership(user.getId(), org.getId(), null, false);
		});
	}
	
	@Test
	public void testUser2OrganizationIndirectCompiled() {
		final Set<Organization> organizationsToDelete = new HashSet<Organization>();
		getAllRightIds().forEach(right -> {
			final Organization child = super.createOrganization();
			Assert.assertNotNull(child);
			organizationsToDelete.add(child);
			assertSuccess(organizationServiceClient.addUserToOrg(child.getId(), user.getId(), null));
			doOrg2OrgAddition(organization.getId(), child.getId(), toArray(right));
		});
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), getAllRightIds(), true);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		
		organizationsToDelete.forEach(org -> {
			assertSuccess(organizationServiceClient.deleteOrganization(org.getId()));
		});
		refreshAuthorizationManager();
		organizationsToDelete.forEach(org -> {
			checkUser2OrganizationMembership(user.getId(), org.getId(), null, false);
		});
	}
	
	
	private void doUser2OrganizationAddition(final String userId, final String organizationId, final Set<String> rightIds) {
		Response response = organizationServiceClient.addUserToOrg(organizationId, userId, rightIds);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(userId, organizationId, rightIds, true);
		response = organizationServiceClient.removeUserFromOrg(organizationId, userId);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(userId, organizationId, rightIds, false);
	}
	
	private void doOrg2OrgAddition(final String organizationId, final String childOrganizationId, final Set<String> rightIds) {
		final Response response = organizationServiceClient.addChildOrganization(organizationId, childOrganizationId, rightIds);
		assertSuccess(response);
	}
	
	@Test
	public void testUser2GroupDirect() {
		doUser2GroupAddition(user.getId(), group.getId(), null);
	}
	
	@Test
	public void testUser2GroupDirectWithRights() {
		doUser2GroupAddition(user.getId(), group.getId(), getRightIds());
	}
	
	private void doUser2GroupAddition(final String userId, final String groupId, final Set<String> rightIds) {
		Response response = groupServiceClient.addUserToGroup(groupId, userId, getRequestorId(), rightIds);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2GroupMembership(userId, groupId, rightIds, true);
		response = groupServiceClient.removeUserFromGroup(groupId, userId, getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2GroupMembership(userId, groupId, rightIds, false);
	}
	
	
	protected abstract void checkUser2ResourceEntitlement(final String userId, final String resourceId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2GroupMembership(final String userId, final String groupId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2RoleMembership(final String userId, final String roleId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2OrganizationMembership(final String userId, final String organizationId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUserURLEntitlements(final String userId, final String url);
	protected abstract boolean loginAfterUserCreation();
}
