package org.openiam.authmanager.service.integration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.activiti.engine.impl.cmd.AddCommentCmd;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
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
			final Login login = loginServiceClient.getPrimaryIdentity(user.getId()).getPrincipal();
			Assert.assertNotNull(login);
			final String password = (String)loginServiceClient.decryptPassword(user.getId(), login.getPassword()).getResponseValue();
			Assert.assertTrue(StringUtils.isNotBlank(password));
			
			final AuthenticationRequest authenticatedRequest = new AuthenticationRequest();
			authenticatedRequest.setPassword(password);
			authenticatedRequest.setPrincipal(login.getLogin());
			authenticatedRequest.setLanguageId(getDefaultLanguage().getId());
			try {
				authenticatedRequest.setNodeIP(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				
			}
			final AuthenticationResponse authenticationResponse = authServiceClient.login(authenticatedRequest);
			Assert.assertNotNull(authenticationResponse);
			Assert.assertEquals(authenticationResponse.getStatus(), ResponseStatus.SUCCESS);
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

	@Test
	public void testUser2RoleDirectWithRights() {
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds());
	}
	
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
		doUser2OrganizationAddition(user, organization, null);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRights() {
		doUser2OrganizationAddition(user, organization, getRightIds());
	}
	
	private Set<String> toArray(final String right) {
		return new HashSet<String>(Arrays.asList(new String[] {right}));
	}
	
	@Test
	public void testUser2ResourceIndirect() {
		final Set<Resource> entitiesToDelete = new HashSet<Resource>();
		assertSuccess(resourceDataService.addUserToResource(resource.getId(), user.getId(), getRequestorId(), null));
		getAllRightIds().forEach(right -> {
			final Resource parent = super.createResource();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doResource2ResourceAddition(parent, resource, toArray(right));
			refreshAuthorizationManager();
			checkUser2ResourceEntitlement(user.getId(), parent.getId(), toArray(right), true);
			checkUser2ResourceEntitlement(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(resourceDataService.deleteResource(e.getId(), getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2ResourceEntitlement(user.getId(), e.getId(), null, false);
		});
		assertSuccess(resourceDataService.removeUserFromResource(resource.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughGroup() {
		assertSuccess(groupServiceClient.addUserToGroup(group.getId(), user.getId(), getRequestorId(), null));
		assertSuccess(resourceDataService.addGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		
		assertSuccess(groupServiceClient.removeUserFromGroup(group.getId(), user.getId(), getRequestorId()));
		assertSuccess(resourceDataService.removeGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRole() {
		assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), null));
		assertSuccess(resourceDataService.addRoleToResource(resource.getId(), role.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		
		assertSuccess(roleServiceClient.removeUserFromRole(role.getId(), user.getId(), getRequestorId()));
		assertSuccess(resourceDataService.removeRoleToResource(resource.getId(), role.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleAndGroup() {
		assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), null));
		assertSuccess(roleServiceClient.addGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds()));
		assertSuccess(resourceDataService.addGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(roleServiceClient.removeUserFromRole(role.getId(), user.getId(), getRequestorId()));
		assertSuccess(roleServiceClient.removeGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		assertSuccess(resourceDataService.removeGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganization() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addResourceToOrganization(organization.getId(), resource.getId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		assertSuccess(organizationServiceClient.removeResourceFromOrganization(organization.getId(), resource.getId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRole() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addRoleToOrganization(organization.getId(), role.getId(), null));
		assertSuccess(resourceDataService.addRoleToResource(resource.getId(), role.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		assertSuccess(organizationServiceClient.removeRoleFromOrganization(organization.getId(), role.getId()));
		assertSuccess(resourceDataService.removeRoleToResource(resource.getId(), role.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
		
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndGroup() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addGroupToOrganization(organization.getId(), group.getId(), null));
		assertSuccess(resourceDataService.addGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		assertSuccess(organizationServiceClient.removeGroupFromOrganization(organization.getId(), group.getId()));
		assertSuccess(resourceDataService.removeGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addRoleToOrganization(organization.getId(), role.getId(), null));
		assertSuccess(roleServiceClient.addGroupToRole(role.getId(), group.getId(), getRequestorId(), null));
		assertSuccess(resourceDataService.addGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		assertSuccess(organizationServiceClient.removeRoleFromOrganization(organization.getId(), role.getId()));
		assertSuccess(roleServiceClient.removeGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		assertSuccess(resourceDataService.removeGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirect() {
		final Set<Group> entitiesToDelete = new HashSet<Group>();
		assertSuccess(groupServiceClient.addUserToGroup(group.getId(), user.getId(), getRequestorId(), null));
		getAllRightIds().forEach(right -> {
			final Group parent = super.createGroup();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doGroup2GroupAddition(parent, group, toArray(right));
			refreshAuthorizationManager();
			checkUser2GroupMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2GroupMembership(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(groupServiceClient.deleteGroup(e.getId(), getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2GroupMembership(user.getId(), e.getId(), null, false);
		});
		assertSuccess(groupServiceClient.removeUserFromGroup(group.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughRole() {
		assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), null));
		assertSuccess(roleServiceClient.addGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		
		assertSuccess(roleServiceClient.removeGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		
		assertSuccess(roleServiceClient.removeUserFromRole(role.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganization() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addGroupToOrganization(organization.getId(), group.getId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		assertSuccess(organizationServiceClient.removeGroupFromOrganization(organization.getId(), group.getId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationAndRole() {
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		assertSuccess(organizationServiceClient.addRoleToOrganization(organization.getId(), role.getId(), null));
		assertSuccess(roleServiceClient.addGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
		assertSuccess(organizationServiceClient.removeRoleFromOrganization(organization.getId(), role.getId()));
		assertSuccess(roleServiceClient.removeGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
	}
	
	@Test
	public void testUser2RoleIndirect() {
		final Set<Role> entitiesToDelete = new HashSet<Role>();
		assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), null));
		getAllRightIds().forEach(right -> {
			final Role parent = super.createRole();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doRole2RoleAddition(parent, role, toArray(right));
			refreshAuthorizationManager();
			checkUser2RoleMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2RoleMembership(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(roleServiceClient.removeRole(e.getId(), getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2RoleMembership(user.getId(), e.getId(), null, false);
		});
		assertSuccess(roleServiceClient.removeUserFromRole(role.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
	}
	
	public void testUser2RoleIndirectCompiled() {
		final Set<Role> entitiesToDelete = new HashSet<Role>();
		getAllRightIds().forEach(right -> {
			final Role child = super.createRole();
			Assert.assertNotNull(child);
			entitiesToDelete.add(child);
			assertSuccess(roleServiceClient.addUserToRole(child.getId(), user.getId(), getRequestorId(), null));
			doRole2RoleAddition(role, child, toArray(right));
		});
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), getAllRightIds(), true);
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		
		entitiesToDelete.forEach(e -> {
			assertSuccess(roleServiceClient.removeUserFromRole(e.getId(), user.getId(), getRequestorId()));
			assertSuccess(roleServiceClient.removeRole(e.getId(), getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2RoleMembership(user.getId(), e.getId(), null, false);
		});
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganization() {
		Response response = organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null);
		assertSuccess(response);
		response = organizationServiceClient.addRoleToOrganization(organization.getId(), role.getId(), getRightIds());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), getRightIds(), true);
		
		response = organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId());
		assertSuccess(response);
		response = organizationServiceClient.removeRoleFromOrganization(organization.getId(), role.getId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, false);
	}
	
	@Test
	public void testUser2OrganizationIndirect() {
		final Set<Organization> organizationsToDelete = new HashSet<Organization>();
		assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
		getAllRightIds().forEach(right -> {
			final Organization parent = super.createOrganization();
			Assert.assertNotNull(parent);
			organizationsToDelete.add(parent);
			doOrg2OrgAddition(parent, organization, toArray(right));
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
			doOrg2OrgAddition(organization, child, toArray(right));
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
	
	@Test
	public void testGetResourcesForUser() {
		Resource entity = null;
		try {
			entity = super.createResource();
			assertSuccess(resourceDataService.addChildResource(entity.getId(), resource.getId(), getRequestorId(), getRightIds()));
			assertSuccess(resourceDataService.addUserToResource(resource.getId(), user.getId(), getRequestorId(), null));
			refreshAuthorizationManager();
			checkUser2ResourceCollection(user.getId(), resource.getId(), null, true);
			checkUser2ResourceCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(resourceDataService.deleteResource(entity.getId(), getRequestorId()));
				assertSuccess(resourceDataService.removeUserFromResource(resource.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetGroupsForUser() {
		Group entity = null;
		try {
			entity = super.createGroup();
			assertSuccess(groupServiceClient.addChildGroup(entity.getId(), group.getId(), getRequestorId(), getRightIds()));
			assertSuccess(groupServiceClient.addUserToGroup(group.getId(), user.getId(), getRequestorId(), null));
			refreshAuthorizationManager();
			checkUser2GroupCollection(user.getId(), group.getId(), null, true);
			checkUser2GroupCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(groupServiceClient.deleteGroup(entity.getId(), getRequestorId()));
				assertSuccess(groupServiceClient.removeUserFromGroup(group.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetRolesForUser() {
		Role entity = null;
		try {
			entity = super.createRole();
			assertSuccess(roleServiceClient.addChildRole(entity.getId(), role.getId(), getRequestorId(), getRightIds()));
			assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), null));
			refreshAuthorizationManager();
			checkUser2RoleCollection(user.getId(), role.getId(), null, true);
			checkUser2RoleCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(roleServiceClient.removeRole(entity.getId(), getRequestorId()));
				assertSuccess(roleServiceClient.removeUserFromRole(role.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetOrgsForUser() {
		Organization entity = null;
		try {
			entity = super.createOrganization();
			assertSuccess(organizationServiceClient.addChildOrganization(entity.getId(), organization.getId(), getRightIds()));
			assertSuccess(organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), null));
			refreshAuthorizationManager();
			checkUser2OrgCollection(user.getId(), organization.getId(), null, true);
			checkUser2OrgCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(entity.getId()));
				assertSuccess(organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	
	private void doUser2OrganizationAddition(final User user, final Organization organization, final Set<String> rightIds) {
		Response response = organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), rightIds);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), rightIds, true);
		response = organizationServiceClient.removeUserFromOrg(organization.getId(), user.getId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), rightIds, false);
	}
	
	private void doResource2ResourceAddition(final Resource resource, final Resource child, final Set<String> rightIds) {
		final Response response = resourceDataService.addChildResource(resource.getId(), child.getId(), getRequestorId(), rightIds);
		assertSuccess(response);
	}
	
	private void doGroup2GroupAddition(final Group group, final Group child, final Set<String> rightIds) {
		final Response response = groupServiceClient.addChildGroup(group.getId(), child.getId(), getRequestorId(), rightIds);
		assertSuccess(response);
	}
	
	private void doRole2RoleAddition(final Role role, final Role child, final Set<String> rightIds) {
		final Response response = roleServiceClient.addChildRole(role.getId(), child.getId(), getRequestorId(), rightIds);
		assertSuccess(response);
	}
	
	private void doOrg2OrgAddition(final Organization organization, final Organization child, final Set<String> rightIds) {
		final Response response = organizationServiceClient.addChildOrganization(organization.getId(), child.getId(), rightIds);
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
	
	protected abstract void checkUser2OrgCollection(final String userId, final String organizationId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2RoleCollection(final String userId, final String roleId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2GroupCollection(final String userId, final String groupId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2ResourceCollection(final String userId, final String resourceId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2ResourceEntitlement(final String userId, final String resourceId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2GroupMembership(final String userId, final String groupId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2RoleMembership(final String userId, final String roleId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUser2OrganizationMembership(final String userId, final String organizationId, final Set<String> rightIds, final boolean isAddition);
	protected abstract void checkUserURLEntitlements(final String userId, final String url);
	protected abstract boolean loginAfterUserCreation();
}
