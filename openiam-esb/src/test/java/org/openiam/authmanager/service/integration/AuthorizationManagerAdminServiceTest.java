package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.model.UserEntitlementsMatrix;
import org.openiam.srvc.am.AuthorizationManagerAdminWebService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationManagerAdminServiceTest extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("authManagerAdminClient")
    private AuthorizationManagerAdminWebService authMangerAdminClient;

	private static final Log log = LogFactory.getLog(AuthorizationManagerAdminServiceTest.class);

	
	@Test
	public void testUserEntitlementsMatrixNoRange() {
		testUserEntitlementsMatrix(null, null);
	}
	
	@Test
	public void testUserEntitlementsMatrixWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testUserEntitlementsMatrix(now, tomorrow);
	}
	
	@Test
	public void testUserEntitlementsMatrixStartDate() {
		testUserEntitlementsMatrix(new Date(), null);
	}
	
	@Test
	public void testUserEntitlementsMatrixEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testUserEntitlementsMatrix(null, tomorrow);
	}
	
	private void testUserEntitlementsMatrix(final Date startDate, final Date endDate) {
		User user = null;
		Organization organization = null;
		Role role = null;
		Group group = null;
		Resource resource = null;

		Role childRole = null;
		Group childGroup = null;
		Resource childResource = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			group = super.createGroup();
			resource = super.createResource();

			childRole = super.createRole();
			childGroup = super.createGroup();
			childResource = super.createResource();
			
			final Set<String> rightIds = getRightIds();
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = null;

			final String childRoleId = childRole.getId();
			final String childGroupId = childGroup.getId();
			final String childResourceId = childResource.getId();
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, getRequestorId(), rightIds, startDate, endDate));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, getRequestorId(), rightIds, startDate, endDate));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, getRequestorId(), rightIds, startDate, endDate));
			assertSuccess(organizationServiceClient.addResourceToOrganization(organizationId, resourceId, getRequestorId(), rightIds, startDate, endDate));
			
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, rightIds, startDate, endDate));
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, rightIds, startDate, endDate));
			
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, rightIds, startDate, endDate));
			
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds, startDate, endDate));
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds, startDate, endDate));
			assertSuccess(resourceDataService.addUserToResource(resourceId, userId, requesterId, rightIds, startDate, endDate));

			assertSuccess(resourceDataService.addChildResource(resourceId, childResourceId, requesterId, rightIds, startDate, endDate));
			assertSuccess(groupServiceClient.addChildGroup(groupId, childGroupId, requesterId, rightIds, startDate, endDate));
			assertSuccess(roleServiceClient.addChildRole(roleId, childRoleId, rightIds, startDate, endDate));

			final UserEntitlementsMatrix matrix = authMangerAdminClient.getUserEntitlementsMatrix(user.getId(), getMiddleDate(startDate, endDate));
			Assert.assertNotNull(matrix);
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleMap()));

			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getDirectGroupIds()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getDirectRoleIds()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getDirectResourceIds()));

			Assert.assertTrue(matrix.getDirectGroupIds().containsKey(groupId));
			Assert.assertTrue(matrix.getDirectRoleIds().containsKey(roleId));
			Assert.assertTrue(matrix.getDirectResourceIds().containsKey(resourceId));

			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getDirectGroupIds().get(groupId)));
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getDirectRoleIds().get(roleId)));
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getDirectResourceIds().get(resourceId)));

			Assert.assertEquals(matrix.getDirectGroupIds().get(groupId), rightIds);
			Assert.assertEquals(matrix.getDirectRoleIds().get(roleId), rightIds);
			Assert.assertEquals(matrix.getDirectResourceIds().get(resourceId), rightIds);

			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupToResourceMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToResourceMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToRoleMap()));

			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToResourceMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToRoleMap()));
			
			//
			Assert.assertTrue(matrix.getGroupToResourceMap().get(groupId).containsKey(resourceId));
			
			Assert.assertTrue(matrix.getRoleToGroupMap().get(roleId).containsKey(groupId));
			Assert.assertTrue(matrix.getRoleToResourceMap().get(roleId).containsKey(resourceId));
			
			Assert.assertTrue(matrix.getResourceToGroupMap().get(resourceId).containsKey(groupId));
			Assert.assertTrue(matrix.getResourceToRoleMap().get(resourceId).containsKey(roleId));


			Assert.assertTrue(matrix.getResourceToResourceMap().get(resourceId).containsKey(childResourceId));
			Assert.assertTrue(matrix.getGroupToGroupMap().get(groupId).containsKey(childGroupId));
			Assert.assertTrue(matrix.getRoleToRoleMap().get(roleId).containsKey(childRoleId));

		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceDirectNoRange() {
		testGetOwnerIdsForResourceDirect(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceDirectWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceDirect(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceDirectStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceDirect(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceDirectEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceDirect(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceDirect(final Date startDate, final Date endDate) {
		User user = null;
		Organization organization = null;
		Role role = null;
		Group group = null;
		Resource resource = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			group = super.createGroup();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addUserToResource(resourceId, userId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupNoRange() {
		testGetOwnerIdsForResourceIndirectViaGroup(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroup(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaGroup(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroup(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaGroup(final Date startDate, final Date endDate) {
		User user = null;
		Organization organization = null;
		Role role = null;
		Group group = null;
		Resource resource = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			group = super.createGroup();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds, startDate, endDate));
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleNoRange() {
		testGetOwnerIdsForResourceIndirectViaRole(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaRole(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaRole(final Date startDate, final Date endDate) {
		User user = null;
		Organization organization = null;
		Role role = null;
		Group group = null;
		Resource resource = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			group = super.createGroup();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds, startDate, endDate));
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaOrgNoRange() {
		testGetOwnerIdsForResourceIndirectViaOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaOrg(final Date startDate, final Date endDate) {
		User user = null;
		Organization organization = null;
		Role role = null;
		Group group = null;
		Resource resource = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			group = super.createGroup();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = getRequestorId();
			
			assertSuccess(organizationServiceClient.addResourceToOrganization(organizationId, resourceId, requesterId, rightIds, startDate, endDate));
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, requesterId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentResourceNoRange() {
		testGetOwnerIdsForResourceIndirectViaParentResource(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentResourceWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentResource(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentResourceStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaParentResource(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentResourceEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentResource(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaParentResource(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Resource child = null;
		try {
			user = super.createUser();
			child = super.createResource();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addChildResource(resource.getId(), child.getId(), requesterId, rightIds, startDate, endDate));
			assertSuccess(resourceDataService.addUserToResource(child.getId(), userId, requesterId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(resourceDataService.deleteResource(child.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentGroupNoRange() {
		testGetOwnerIdsForResourceIndirectViaParentGroup(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentGroupWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentGroup(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentGroupStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaParentGroup(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentGroupEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentGroup(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaParentGroup(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Group child = null;
		Group parent = null;
		try {
			user = super.createUser();
			child = super.createGroup();
			parent = super.createGroup();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addGroupToResource(resource.getId(), parent.getId(), requesterId, rightIds, startDate, endDate));
			assertSuccess(groupServiceClient.addChildGroup(parent.getId(), child.getId(), requesterId, null, startDate, endDate));
			assertSuccess(groupServiceClient.addUserToGroup(child.getId(), userId, requesterId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(groupServiceClient.deleteGroup(child.getId(), null));
			}
			if(parent != null) {
				assertSuccess(groupServiceClient.deleteGroup(parent.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentRoleNoRange() {
		testGetOwnerIdsForResourceIndirectViaParentRole(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentRoleWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentRoleStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaParentRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentRoleEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentRole(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaParentRole(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Role child = null;
		Role parent = null;
		try {
			user = super.createUser();
			child = super.createRole();
			parent = super.createRole();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			
			assertSuccess(resourceDataService.addRoleToResource(resource.getId(), parent.getId(), requesterId, rightIds, startDate, endDate));
			assertSuccess(roleServiceClient.addChildRole(parent.getId(), child.getId(), null, startDate, endDate));
			assertSuccess(roleServiceClient.addUserToRole(child.getId(), userId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(roleServiceClient.removeRole(child.getId()));
			}
			if(parent != null) {
				assertSuccess(roleServiceClient.removeRole(parent.getId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentOrgNoRange() {
		testGetOwnerIdsForResourceIndirectViaParentOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaParentOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaParentOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaParentOrg(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Organization child = null;
		Organization parent = null;
		try {
			user = super.createUser();
			child = super.createOrganization();
			parent = super.createOrganization();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			
			assertSuccess(organizationServiceClient.addResourceToOrganization(parent.getId(), resource.getId(), getRequestorId(), rightIds, startDate, endDate));
			assertSuccess(organizationServiceClient.addChildOrganization(parent.getId(), child.getId(), getRequestorId(), null, startDate, endDate));
			assertSuccess(organizationServiceClient.addUserToOrg(child.getId(), userId, getRequestorId(), null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(child.getId(), getRequestorId()));
			}
			if(parent != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(parent.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndRoleNoRange() {
		testGetOwnerIdsForResourceIndirectViaGroupAndRole(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndRoleWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroupAndRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndRoleStartDate() {
		final Date now = new Date();
		final Date tomorrow = null;
		testGetOwnerIdsForResourceIndirectViaGroupAndRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndRoleEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroupAndRole(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaGroupAndRole(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Group group = null;
		Role role = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			role = super.createRole();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			final String groupId = group.getId();
			final String roleId = role.getId();
			final String resourceId = resource.getId();
			
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, null, startDate, endDate));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, null, startDate, endDate));
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndOrgNoRange() {
		testGetOwnerIdsForResourceIndirectViaGroupAndOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroupAndOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroupAndOrg(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaGroupAndOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaGroupAndOrg(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Group group = null;
		Organization organization = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			organization = super.createOrganization();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			final String groupId = group.getId();
			final String organizationId = organization.getId();
			final String resourceId = resource.getId();
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, requesterId, null, startDate, endDate));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, requesterId, null, startDate, endDate));
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleAndOrgNoRange() {
		testGetOwnerIdsForResourceIndirectViaRoleAndOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleAndOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaRoleAndOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleAndOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaRoleAndOrg(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleAndOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForResourceIndirectViaRoleAndOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForResourceIndirectViaRoleAndOrg(final Date startDate, final Date endDate) {
		User user = null;
		Resource resource = null;
		Role role = null;
		Organization organization = null;
		try {
			user = super.createUser();
			role = super.createRole();
			organization = super.createOrganization();
			resource = super.createResource();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			final String roleId = role.getId();
			final String organizationId = organization.getId();
			final String resourceId = resource.getId();
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, requesterId, null, startDate, endDate));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, requesterId, null, startDate, endDate));
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsDirectNoRange() {
		testGetOwnerIdsForGroupsDirect(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsDirectWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsDirect(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsDirectStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsDirect(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsDirectEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsDirect(null, tomorrow);
	}
	
	private void testGetOwnerIdsForGroupsDirect(final Date startDate, final Date endDate) {
		User user = null;
		Group group = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String groupId = group.getId();
			final String requesterId = null;
			
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsIndirectViaGroupNoRange() {
		testGetOwnerIdsForGroupsIndirectViaGroup(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsIndirectViaGroupWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsIndirectViaGroup(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsIndirectViaGroupStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsIndirectViaGroup(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsIndirectViaGroupEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsIndirectViaGroup(null, tomorrow);
	}
	
	private void testGetOwnerIdsForGroupsIndirectViaGroup(final Date startDate, final Date endDate) {
		User user = null;
		Group parent = null;
		Group child = null;
		try {
			user = super.createUser();
			parent = super.createGroup();
			child = super.createGroup();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String requesterId = null;
			
			assertSuccess(groupServiceClient.addChildGroup(parent.getId(), child.getId(), requesterId, rightIds, startDate, endDate));
			assertSuccess(groupServiceClient.addUserToGroup(child.getId(), userId, requesterId, null, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(parent.getId(), getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(parent != null) {
				assertSuccess(groupServiceClient.deleteGroup(parent.getId(), null));
			}
			if(child != null) {
				assertSuccess(groupServiceClient.deleteGroup(child.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleNoRange() {
		testGetOwnerIdsForGroupsViaRole(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRole(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRole(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRole(null, tomorrow);
	}
	
	private void testGetOwnerIdsForGroupsViaRole(final Date startDate, final Date endDate) {
		User user = null;
		Group group = null;
		Role role = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			role = super.createRole();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String groupId = group.getId();
			final String requesterId = null;
			final String roleId = role.getId();
			
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, null, startDate, endDate));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaOrgNoRange() {
		testGetOwnerIdsForGroupsViaOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaOrg(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForGroupsViaOrg(final Date startDate, final Date endDate) {
		User user = null;
		Group group = null;
		Organization organization = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			organization = super.createOrganization();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String groupId = group.getId();
			final String requesterId = null;
			final String organizationId = organization.getId();
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, requesterId, null, startDate, endDate));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, requesterId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleAndOrgNoRange() {
		testGetOwnerIdsForGroupsViaRoleAndOrg(null, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleAndOrgWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRoleAndOrg(now, tomorrow);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleAndOrgStartDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRoleAndOrg(now, null);
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleAndOrgEndDate() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetOwnerIdsForGroupsViaRoleAndOrg(null, tomorrow);
	}
	
	private void testGetOwnerIdsForGroupsViaRoleAndOrg(final Date startDate, final Date endDate) {
		User user = null;
		Group group = null;
		Role role = null;
		Organization organization = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			role = super.createRole();
			organization = super.createOrganization();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String groupId = group.getId();
			final String requesterId = null;
			final String roleId = role.getId();
			final String organizationId = organization.getId();
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, requesterId, null, startDate, endDate));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, requesterId, null, startDate, endDate));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, rightIds, startDate, endDate));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId, getMiddleDate(startDate, endDate));
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId()));
			}
		}
	}
}
