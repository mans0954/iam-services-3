package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
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
	
	protected Group group = null;
	protected Role role = null;
	protected Organization organization = null;
	protected Resource resource = null;
	protected Resource publicResource = null;
	
	@BeforeClass
	public void _init() {
		/* setup the public resource, and ensure that it's actually public after saving */
		publicResource = super.createResource();
		publicResource.setIsPublic(true);
		assertSuccess(resourceDataService.saveResource(publicResource, null));
		publicResource = resourceDataService.getResource(publicResource.getId(), getDefaultLanguage());
		Assert.assertNotNull(publicResource);
		Assert.assertTrue(publicResource.getIsPublic());
		refreshAuthorizationManager(); /* new reosurce created - make sure it's cached */
		
		
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
			login(user.getId());
			refreshAuthorizationManager();
		}
	}
	
	@AfterClass
	public void _destroy() {
		resourceDataService.deleteResource(publicResource.getId(), null);
		if(user != null) {
			userServiceClient.removeUser(user.getId());
		}
		if(group != null) {
			groupServiceClient.deleteGroup(group.getId());
		}
		if(role != null) {
			roleServiceClient.removeRole(role.getId());
		}
		if(organization != null) {
			organizationServiceClient.deleteOrganization(organization.getId());
		}
		if(resource != null) {
			resourceDataService.deleteResource(resource.getId(), null);
		}
	}
	
	@Test
	public void assertUserEntitledToPublicResource() {
		checkUser2ResourceEntitlement(user.getId(), publicResource.getId(), new HashSet<String>(), true);
		Assert.assertTrue(authorizationManagerServiceClient.getResourcesForUser(user.getId()).stream().filter(e -> e.getEntity().getId().equals(publicResource.getId())).count() > 0);
	}
	
	@Test
	public void testUser2ResourceDirectNoRange() {
		doUser2ResourceAddition(user.getId(), resource.getId(), null, null, null);
	}
	
	@Test
	public void testUser2ResourceDirectWithRightsWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2ResourceAddition(user.getId(), resource.getId(), getRightIds(), startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceDirectWithRightsStartDate() {
		final Date startDate = new Date();
		doUser2ResourceAddition(user.getId(), resource.getId(), getRightIds(), startDate, null);
	}
	
	@Test
	public void testUser2ResourceDirectWithRightsEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2ResourceAddition(user.getId(), resource.getId(), getRightIds(), null, endDate);
	}
	
	private void doUser2ResourceAddition(final String userId, final String resourceId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		Response response = doAddUserToResource(resourceId, userId, getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(userId, resourceId, rightIds, true);
		response = doRemoveUserFromResource(resourceId, userId, getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(userId, resourceId, rightIds, false);
	}

	@Test
	public void testUser2RoleDirect() {
		doUser2RoleAddition(user.getId(), role.getId(), null, null, null);
	}

	@Test
	public void testUser2RoleDirectWithRights() {
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds(), null, null);
	}
	
	@Test
	public void testUser2RoleDirectWithRightsWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds(), startDate, endDate);
	}
	
	@Test
	public void testUser2RoleDirectWithRightsStartDate() {
		final Date startDate = new Date();
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds(), startDate, null);
	}
	
	@Test
	public void testUser2RoleDirectWithRightsEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2RoleAddition(user.getId(), role.getId(), getRightIds(), null, endDate);
	}
	
	private void doUser2RoleAddition(final String userId, final String roleId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		Response response = doAddUserToRole(roleId, userId, getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(userId, roleId, rightIds, true);
		response = doRemoveUserFromRole(roleId, userId, getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(userId, roleId, rightIds, false);
	}
	
	@Test
	public void testUser2OrganizationDirect() {
		doUser2OrganizationAddition(user, organization, null, null, null);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRights() {
		doUser2OrganizationAddition(user, organization, getRightIds(), null, null);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRightsWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2OrganizationAddition(user, organization, getRightIds(), startDate, endDate);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRightsStartDate() {
		final Date startDate = new Date();
		doUser2OrganizationAddition(user, organization, getRightIds(), startDate, null);
	}
	
	@Test
	public void testUser2OrganizationDirectWithRightsEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2OrganizationAddition(user, organization, getRightIds(), startDate, endDate);
	}
	
	private Set<String> toArray(final String right) {
		return new HashSet<String>(Arrays.asList(new String[] {right}));
	}
	
	@Test
	public void testUser2ResourceIndirectNoRange() {
		testUser2ResourceIndirect(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirect(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirect(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirect(null, endDate);
	}
	
	private void testUser2ResourceIndirect(final Date startDate, final Date endDate) {
		final Set<Resource> entitiesToDelete = new HashSet<Resource>();
		assertSuccess(doAddUserToResource(resource.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		getAllRightIds().forEach(right -> {
			final Resource parent = super.createResource();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doResource2ResourceAddition(parent, resource, toArray(right), startDate, endDate);
			refreshAuthorizationManager();
			checkUser2ResourceEntitlement(user.getId(), parent.getId(), toArray(right), true);
			checkUser2ResourceEntitlement(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(doDeleteResource(e, getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2ResourceEntitlement(user.getId(), e.getId(), null, false);
		});
		assertSuccess(doRemoveUserFromResource(resource.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughGroupNoRange() {
		testUser2ResourceIndirectThroughGroup(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughGroup(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughGroupStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughGroup(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughGroupEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughGroup(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughGroup(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToGroup(group.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		
		assertSuccess(doRemoveUserFromGroup(group.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleNoRange() {
		testUser2ResourceIndirectThroughRole(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughRole(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughRole(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughRole(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughRole(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToRole(role.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddRoleToResource(resource.getId(), role.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		
		assertSuccess(doRemoveUserFromRole(role.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveRoleToResource(resource.getId(), role.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleAndGroupNoRange() {
		testUser2ResourceIndirectThroughRoleAndGroup(null, null);
	}

	@Test
	public void testUser2ResourceIndirectThroughRoleAndGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughRoleAndGroup(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleAndGroupStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughRoleAndGroup(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughRoleAndGroupEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughRoleAndGroup(null, endDate);
	}
	
	
	private void testUser2ResourceIndirectThroughRoleAndGroup(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToRole(role.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		assertSuccess(doAddGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(doRemoveUserFromRole(role.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationNoRange() {
		testUser2ResourceIndirectThroughOrganization(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganization(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughOrganization(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganization(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughOrganization(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddResourceToOrganization(organization.getId(), resource.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveResourceFromOrganization(organization.getId(), resource.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleNoRange() {
		testUser2ResourceIndirectThroughOrganizationAndRole(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndRole(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughOrganizationAndRole(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndRole(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughOrganizationAndRole(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddRoleToOrganization(organization.getId(), role.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddRoleToResource(resource.getId(), role.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveRoleFromOrganization(organization.getId(), role.getId(), getRequestorId()));
		assertSuccess(doRemoveRoleToResource(resource.getId(), role.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
		
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndGroupNoRange() {
		testUser2ResourceIndirectThroughOrganizationAndGroup(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndGroup(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndGroupStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughOrganizationAndGroup(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndGroupEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndGroup(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughOrganizationAndGroup(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToOrganization(organization.getId(), group.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupFromOrganization(organization.getId(), group.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroupNoRange() {
		testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup(null, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup(startDate, endDate);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroupStartDate() {
		final Date startDate = new Date();
		testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup(startDate, null);
	}
	
	@Test
	public void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroupEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup(null, endDate);
	}
	
	private void testUser2ResourceIndirectThroughOrganizationAndRoleAndGroup(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddRoleToOrganization(organization.getId(), role.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToRole(role.getId(), group.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToResource(resource.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), getRightIds(), true);
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveRoleFromOrganization(organization.getId(), role.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupToResource(resource.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2ResourceEntitlement(user.getId(), resource.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectNoRange() {
		testUser2GroupIndirect(null, null);
	}
	
	@Test
	public void testUser2GroupIndirectWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirect(startDate, endDate);
	}
	
	@Test
	public void testUser2GroupIndirectStartDate() {
		final Date startDate = new Date();
		testUser2GroupIndirect(startDate, null);
	}
	
	@Test
	public void testUser2GroupIndirectEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirect(null, endDate);
	}
	
	private void testUser2GroupIndirect(final Date startDate, final Date endDate) {
		final Set<Group> entitiesToDelete = new HashSet<Group>();
		assertSuccess(doAddUserToGroup(group.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		getAllRightIds().forEach(right -> {
			final Group parent = super.createGroup();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doGroup2GroupAddition(parent, group, toArray(right), startDate, endDate);
			refreshAuthorizationManager();
			checkUser2GroupMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2GroupMembership(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(doDeleteGroup(e, getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2GroupMembership(user.getId(), e.getId(), null, false);
		});
		assertSuccess(doRemoveUserFromGroup(group.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughRoleNoRange() {
		testUser2GroupIndirectThroughRole(null, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughRole(startDate, endDate);
	}
	
	@Test
	public void testUser2GroupIndirectThroughRoleStartDate() {
		final Date startDate = new Date();
		testUser2GroupIndirectThroughRole(startDate, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughRoleEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughRole(null, endDate);
	}
	
	private void testUser2GroupIndirectThroughRole(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToRole(role.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		
		assertSuccess(doRemoveGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		
		assertSuccess(doRemoveUserFromRole(role.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationNoRange() {
		testUser2GroupIndirectThroughOrganization(null, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughOrganization(startDate, endDate);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationStartDate() {
		final Date startDate = new Date();
		testUser2GroupIndirectThroughOrganization(startDate, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughOrganization(null, endDate);
	}
	
	private void testUser2GroupIndirectThroughOrganization(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToOrganization(organization.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		assertSuccess(doRemoveGroupFromOrganization(organization.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, false);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationAndRoleNoRange() {
		testUser2GroupIndirectThroughOrganizationAndRole(null, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationAndRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughOrganizationAndRole(startDate, endDate);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationAndRoleStartDate() {
		final Date startDate = new Date();
		testUser2GroupIndirectThroughOrganizationAndRole(startDate, null);
	}
	
	@Test
	public void testUser2GroupIndirectThroughOrganizationAndRoleEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2GroupIndirectThroughOrganizationAndRole(null, endDate);
	}
	
	private void testUser2GroupIndirectThroughOrganizationAndRole(final Date startDate, final Date endDate) {
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddRoleToOrganization(organization.getId(), role.getId(), getRequestorId(), null, startDate, endDate));
		assertSuccess(doAddGroupToRole(role.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, true);
		checkUser2GroupMembership(user.getId(), group.getId(), getRightIds(), true);
		
		assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
		assertSuccess(doRemoveRoleFromOrganization(organization.getId(), role.getId(), getRequestorId()));
		assertSuccess(doRemoveGroupFromRole(role.getId(), group.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2GroupMembership(user.getId(), group.getId(), null, false);
	}
	
	@Test
	public void testUser2RoleIndirectNoRange() {
		testUser2RoleIndirect(null, null);
	}
	
	@Test
	public void testUser2RoleIndirectWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirect(startDate, endDate);
	}
	
	@Test
	public void testUser2RoleIndirectStartDate() {
		final Date startDate = new Date();
		testUser2RoleIndirect(startDate, null);
	}
	
	@Test
	public void testUser2RoleIndirectEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirect(null, endDate);
	}
	
	private void testUser2RoleIndirect(final Date startDate, final Date endDate) {
		final Set<Role> entitiesToDelete = new HashSet<Role>();
		assertSuccess(doAddUserToRole(role.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		getAllRightIds().forEach(right -> {
			final Role parent = super.createRole();
			Assert.assertNotNull(parent);
			entitiesToDelete.add(parent);
			doRole2RoleAddition(parent, role, toArray(right), startDate, endDate);
			refreshAuthorizationManager();
			checkUser2RoleMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2RoleMembership(user.getId(), parent.getId(), null, true);
		});
		entitiesToDelete.forEach(e -> {
			assertSuccess(doRemoveRole(e, getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2RoleMembership(user.getId(), e.getId(), null, false);
		});
		assertSuccess(doRemoveUserFromRole(role.getId(), user.getId(), getRequestorId()));
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
	}
	
	@Test
	public void testUser2RoleIndirectCompiledNoRange() {
		testUser2RoleIndirectCompiled(null, null);
	}
	
	@Test
	public void testUser2RoleIndirectCompiledWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirectCompiled(startDate, endDate);
	}
	
	@Test
	public void testUser2RoleIndirectCompiledStartDate() {
		final Date startDate = new Date();
		testUser2RoleIndirectCompiled(startDate, null);
	}
	
	@Test
	public void testUser2RoleIndirectCompiledEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirectCompiled(null, endDate);
	}
	
	private void testUser2RoleIndirectCompiled(final Date startDate, final Date endDate) {
		final Set<Role> entitiesToDelete = new HashSet<Role>();
		getAllRightIds().forEach(right -> {
			final Role child = super.createRole();
			Assert.assertNotNull(child);
			entitiesToDelete.add(child);
			assertSuccess(doAddUserToRole(child.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			doRole2RoleAddition(role, child, toArray(right), startDate, endDate);
		});
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), getAllRightIds(), true);
		checkUser2RoleMembership(user.getId(), role.getId(), null, true);
		
		entitiesToDelete.forEach(e -> {
			assertSuccess(doRemoveUserFromRole(e.getId(), user.getId(), getRequestorId()));
			assertSuccess(doRemoveRole(e, getRequestorId()));
		});
		refreshAuthorizationManager();
		entitiesToDelete.forEach(e -> {
			checkUser2RoleMembership(user.getId(), e.getId(), null, false);
		});
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganizationNoRange() {
		testUser2RoleIndirectThroughOrganization(null, null);
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganizationWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirectThroughOrganization(startDate, endDate);
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganizationStartDate() {
		final Date startDate = new Date();
		testUser2RoleIndirectThroughOrganization(startDate, null);
	}
	
	@Test
	public void testUser2RoleIndirectThroughOrganizationEnDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2RoleIndirectThroughOrganization(null, endDate);
	}
	
	private void testUser2RoleIndirectThroughOrganization(final Date startDate, final Date endDate) {
		Response response = doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
		assertSuccess(response);
		response = doAddRoleToOrganization(organization.getId(), role.getId(), getRequestorId(), getRightIds(), startDate, endDate);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), getRightIds(), true);
		
		response = doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId());
		assertSuccess(response);
		response = doRemoveRoleFromOrganization(organization.getId(), role.getId(), getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2RoleMembership(user.getId(), role.getId(), null, false);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, false);
	}
	
	@Test
	public void testUser2OrganizationIndirectNoRange() {
		testUser2OrganizationIndirect(null, null);
	}
	
	@Test
	public void testUser2OrganizationIndirectWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2OrganizationIndirect(startDate, endDate);
	}
	
	@Test
	public void testUser2OrganizationIndirectStartDate() {
		final Date startDate = new Date();
		testUser2OrganizationIndirect(startDate, null);
	}
	
	@Test
	public void testUser2OrganizationIndirectEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2OrganizationIndirect(null, endDate);
	}
	
	private void testUser2OrganizationIndirect(final Date startDate, final Date endDate) {
		final Set<Organization> organizationsToDelete = new HashSet<Organization>();
		assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
		getAllRightIds().forEach(right -> {
			final Organization parent = super.createOrganization();
			Assert.assertNotNull(parent);
			organizationsToDelete.add(parent);
			doOrg2OrgAddition(parent, organization, toArray(right), startDate, endDate);
			refreshAuthorizationManager();
			checkUser2OrganizationMembership(user.getId(), parent.getId(), toArray(right), true);
			checkUser2OrganizationMembership(user.getId(), parent.getId(), null, true);
		});
		organizationsToDelete.forEach(org -> {
			assertSuccess(doRemoveOrganization(org, getRequestorId()));
		});
		refreshAuthorizationManager();
		organizationsToDelete.forEach(org -> {
			checkUser2OrganizationMembership(user.getId(), org.getId(), null, false);
		});
	}
	
	@Test
	public void testUser2OrganizationIndirectCompiledNoRange() {
		testUser2OrganizationIndirectCompiled(null, null);
	}
	
	@Test
	public void testUser2OrganizationIndirectCompiledWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2OrganizationIndirectCompiled(startDate, endDate);
	}
	
	@Test
	public void testUser2OrganizationIndirectCompiledStartDate() {
		final Date startDate = new Date();
		testUser2OrganizationIndirectCompiled(startDate, null);
	}
	
	@Test
	public void testUser2OrganizationIndirectCompiledEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testUser2OrganizationIndirectCompiled(null, endDate);
	}
	
	private void testUser2OrganizationIndirectCompiled(final Date startDate, final Date endDate) {
		final Set<Organization> organizationsToDelete = new HashSet<Organization>();
		getAllRightIds().forEach(right -> {
			final Organization child = super.createOrganization();
			Assert.assertNotNull(child);
			organizationsToDelete.add(child);
			assertSuccess(doAddUserToOrg(child.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			doOrg2OrgAddition(organization, child, toArray(right), startDate, endDate);
		});
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), getAllRightIds(), true);
		checkUser2OrganizationMembership(user.getId(), organization.getId(), null, true);
		
		organizationsToDelete.forEach(org -> {
			assertSuccess(doRemoveOrganization(org, getRequestorId()));
		});
		refreshAuthorizationManager();
		organizationsToDelete.forEach(org -> {
			checkUser2OrganizationMembership(user.getId(), org.getId(), null, false);
		});
	}
	
	@Test
	public void testGetResourcesForUserNoRange() {
		testGetResourcesForUser(null, null);
	}
	
	@Test
	public void testGetResourcesForUserWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetResourcesForUser(startDate, endDate);
	}
	
	@Test
	public void testGetResourcesForUserStartDate() {
		final Date startDate = new Date();
		testGetResourcesForUser(startDate, null);
	}
	
	@Test
	public void testGetResourcesForUserEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetResourcesForUser(null, endDate);
	}
	
	public void testGetResourcesForUser(final Date startDate, final Date endDate) {
		Resource entity = null;
		try {
			entity = super.createResource();
			assertSuccess(doAddChildResource(entity.getId(), resource.getId(), getRequestorId(), getRightIds(), startDate, endDate));
			assertSuccess(doAddUserToResource(resource.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			refreshAuthorizationManager();
			checkUser2ResourceCollection(user.getId(), resource.getId(), null, true);
			checkUser2ResourceCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(doDeleteResource(entity, getRequestorId()));
				assertSuccess(doRemoveUserFromResource(resource.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetGroupsForUserNoRange() {
		testGetGroupsForUser(null, null);
	}
	
	@Test
	public void testGetGroupsForUserWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetGroupsForUser(startDate, endDate);
	}
	
	@Test
	public void testGetGroupsForUserStartDate() {
		final Date startDate = new Date();
		testGetGroupsForUser(startDate, null);
	}
	
	@Test
	public void testGetGroupsForUserEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetGroupsForUser(null, endDate);
	}
	
	private void testGetGroupsForUser(final Date startDate, final Date endDate) {
		Group entity = null;
		try {
			entity = super.createGroup();
			assertSuccess(doAddChildGroup(entity.getId(), group.getId(), getRequestorId(), getRightIds(), startDate, endDate));
			assertSuccess(doAddUserToGroup(group.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			refreshAuthorizationManager();
			checkUser2GroupCollection(user.getId(), group.getId(), null, true);
			checkUser2GroupCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(doDeleteGroup(entity, getRequestorId()));
				assertSuccess(doRemoveUserFromGroup(group.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetRolesForUserNoRange() {
		testGetRolesForUser(null, null);
	}
	
	@Test
	public void testGetRolesForUserWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetRolesForUser(startDate, endDate);
	}
	
	@Test
	public void testGetRolesForUserStartDate() {
		final Date startDate = new Date();
		testGetRolesForUser(startDate, null);
	}
	
	@Test
	public void testGetRolesForUserEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetRolesForUser(null, endDate);
	}
	
	private void testGetRolesForUser(final Date startDate, final Date endDate) {
		Role entity = null;
		try {
			entity = super.createRole();
			assertSuccess(doAddChildRole(entity.getId(), role.getId(), getRequestorId(), getRightIds(), startDate, endDate));
			assertSuccess(doAddUserToRole(role.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			refreshAuthorizationManager();
			checkUser2RoleCollection(user.getId(), role.getId(), null, true);
			checkUser2RoleCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(doRemoveRole(entity, getRequestorId()));
				assertSuccess(doRemoveUserFromRole(role.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	@Test
	public void testGetOrgsForUserNoRange() {
		testGetOrgsForUser(null, null);
	}
	
	@Test
	public void testGetOrgsForUserWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetOrgsForUser(startDate, endDate);
	}
	
	@Test
	public void testGetOrgsForUserStartDate() {
		final Date startDate = new Date();
		testGetOrgsForUser(startDate, null);
	}
	
	@Test
	public void testGetOrgsForUserEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetOrgsForUser(null, endDate);
	}
	
	private void testGetOrgsForUser(final Date startDate, final Date endDate) {
		Organization entity = null;
		try {
			entity = super.createOrganization();
			assertSuccess(doAddChildOrganization(entity.getId(), organization.getId(), getRequestorId(), getRightIds(), startDate, endDate));
			assertSuccess(doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate));
			refreshAuthorizationManager();
			checkUser2OrgCollection(user.getId(), organization.getId(), null, true);
			checkUser2OrgCollection(user.getId(), entity.getId(), getRightIds(), true);
		} finally {
			if(entity != null) {
				assertSuccess(doRemoveOrganization(entity, getRequestorId()));
				assertSuccess(doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId()));
				refreshAuthorizationManager();
			}
		}
	}
	
	
	private void doUser2OrganizationAddition(final User user, final Organization organization, final Set<String> rightIds, final Date startDate, final Date endDate) {
		Response response = doAddUserToOrg(organization.getId(), user.getId(), getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), rightIds, true);
		response = doRemoveUserFromOrg(organization.getId(), user.getId(), getRequestorId());
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2OrganizationMembership(user.getId(), organization.getId(), rightIds, false);
	}
	
	private void doResource2ResourceAddition(final Resource resource, final Resource child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		final Response response = doAddChildResource(resource.getId(), child.getId(), getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
	}
	
	private void doGroup2GroupAddition(final Group group, final Group child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		final Response response = doAddChildGroup(group.getId(), child.getId(), getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
	}
	
	private void doRole2RoleAddition(final Role role, final Role child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		final Response response = doAddChildRole(role.getId(), child.getId(), getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
	}
	
	private void doOrg2OrgAddition(final Organization organization, final Organization child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		final Response response = doAddChildOrganization(organization.getId(), child.getId(), getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
	}
	
	@Test
	public void testUser2GroupDirect() {
		doUser2GroupAddition(user.getId(), group.getId(), null, null, null);
	}
	
	@Test
	public void testUser2GroupDirectWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2GroupAddition(user.getId(), group.getId(), null, startDate, endDate);
	}
	
	@Test
	public void testUser2GroupDirectStartDate() {
		final Date startDate = new Date();
		doUser2GroupAddition(user.getId(), group.getId(), null, startDate, null);
	}
	
	@Test
	public void testUser2GroupDirectEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2GroupAddition(user.getId(), group.getId(), null, null, endDate);
	}
	
	@Test
	public void testUser2GroupDirectWithRights() {
		doUser2GroupAddition(user.getId(), group.getId(), getRightIds(), null, null);
	}
	
	@Test
	public void testUser2GroupDirectWithRightsWithRights() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2GroupAddition(user.getId(), group.getId(), getRightIds(), startDate, endDate);
	}
	
	@Test
	public void testUser2GroupDirectWithRightsStartDate() {
		final Date startDate = new Date();
		doUser2GroupAddition(user.getId(), group.getId(), getRightIds(), startDate, null);
	}
	
	@Test
	public void testUser2GroupDirectWithRightsEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		doUser2GroupAddition(user.getId(), group.getId(), getRightIds(), null, endDate);
	}
	
	private void doUser2GroupAddition(final String userId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		Response response = doAddUserToGroup(groupId, userId, getRequestorId(), rightIds, startDate, endDate);
		assertSuccess(response);
		refreshAuthorizationManager();
		checkUser2GroupMembership(userId, groupId, rightIds, true);
		response = doRemoveUserFromGroup(groupId, userId, getRequestorId());
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
	
	protected abstract Response doAddUserToResource(final String resourceId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveUserFromResource(final String resourceId, final String userId, final String requestorId);
	protected abstract Response doDeleteResource(final Resource resource, final String requestorId);
	protected abstract Response doAddGroupToResource(final String resourceId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doAddRoleToResource(final String resourceId, final String roleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doAddUserToRole(final String roleId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doAddChildRole(final String roleId, final String childRoleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveUserFromRole(final String roleId, final String userId, final String requestorId);
	protected abstract Response doAddUserToGroup(final String groupId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveUserFromGroup(final String groupId, final String userId, final String requestorId);
	protected abstract Response doAddChildGroup(final String groupId, final String childGroupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doAddChildOrganization(final String organizationId, final String childOrganizationId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doAddChildResource(final String resourceId, final String childResourceId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doDeleteGroup(final Group group, final String requestorId);
	protected abstract Response doAddGroupToRole(final String roleId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveGroupFromRole(final String roleId, final String groupId, final String requestorId);
	protected abstract Response doAddUserToOrg(final String organizationId, final String userId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveUserFromOrg(final String organizationId, final String userId, final String requestorId);
	protected abstract Response doAddResourceToOrganization(final String organizationId, final String resourceId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveResourceFromOrganization(final String organizationid, final String resourceId, final String requestorId);
	protected abstract Response doRemoveRoleToResource(final String resourceId, final String roleId, final String requestorId);
	protected abstract Response doAddRoleToOrganization(final String organizationId, final String roleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveGroupToResource(final String resourceId, final String groupId, final String requestorId);
	protected abstract Response doRemoveRoleFromOrganization(final String organizationId, final String roleId, final String requestorId);
	protected abstract Response doRemoveRole(final Role role, final String requestorId);
	protected abstract Response doRemoveOrganization(final Organization organization, final String requestorId);
	protected abstract Response doAddGroupToOrganization(final String organizationId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate);
	protected abstract Response doRemoveGroupFromOrganization(final String organizationId, final String groupId, final String requestorId);
}
