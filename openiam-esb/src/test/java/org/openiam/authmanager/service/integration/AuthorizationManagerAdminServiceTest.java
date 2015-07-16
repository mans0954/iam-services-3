package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminWebService;
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
	public void testUserEntitlementsMatrix() {
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
			
			final Set<String> rightIds = getRightIds();
			final String userId = user.getId();
			final String organizationId = organization.getId();
			final String roleId = role.getId();
			final String groupId = group.getId();
			final String resourceId = resource.getId();
			final String requesterId = null;
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, rightIds));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, rightIds));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, rightIds));
			assertSuccess(organizationServiceClient.addResourceToOrganization(organizationId, resourceId, rightIds));
			
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, requesterId, rightIds));
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, requesterId, rightIds));
			
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, rightIds));
			
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds));
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds));
			assertSuccess(resourceDataService.addUserToResource(resourceId, userId, requesterId, rightIds));
			
			final UserEntitlementsMatrix matrix = authMangerAdminClient.getUserEntitlementsMatrix(user.getId());
			Assert.assertNotNull(matrix);
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getOrgMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupIds()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleIds()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceIds()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getOrganizationIds()));
			
			Assert.assertTrue(matrix.getGroupIds().containsKey(groupId));
			Assert.assertTrue(matrix.getRoleIds().containsKey(roleId));
			Assert.assertTrue(matrix.getResourceIds().containsKey(resourceId));
			Assert.assertTrue(matrix.getOrganizationIds().containsKey(organizationId));
			
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getGroupIds().get(groupId)));
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getRoleIds().get(roleId)));
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getResourceIds().get(resourceId)));
			Assert.assertTrue(CollectionUtils.isNotEmpty(matrix.getOrganizationIds().get(organizationId)));
			
			Assert.assertEquals(matrix.getGroupIds().get(groupId), rightIds);
			Assert.assertEquals(matrix.getRoleIds().get(roleId), rightIds);
			Assert.assertEquals(matrix.getResourceIds().get(resourceId), rightIds);
			Assert.assertEquals(matrix.getOrganizationIds().get(organizationId), rightIds);
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupToOrgMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupToRoleMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getGroupToResourceMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToOrgMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getRoleToResourceMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToOrgMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getResourceToRoleMap()));
			
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getOrgToGroupMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getOrgToResourceMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(matrix.getOrgToRoleMap()));
			
			//
			Assert.assertTrue(matrix.getGroupToOrgMap().get(groupId).containsKey(organizationId));
			Assert.assertTrue(matrix.getGroupToRoleMap().get(groupId).containsKey(roleId));
			Assert.assertTrue(matrix.getGroupToResourceMap().get(groupId).containsKey(resourceId));
			
			Assert.assertTrue(matrix.getRoleToGroupMap().get(roleId).containsKey(groupId));
			Assert.assertTrue(matrix.getRoleToOrgMap().get(roleId).containsKey(organizationId));
			Assert.assertTrue(matrix.getRoleToResourceMap().get(roleId).containsKey(resourceId));
			
			Assert.assertTrue(matrix.getResourceToGroupMap().get(resourceId).containsKey(groupId));
			Assert.assertTrue(matrix.getResourceToOrgMap().get(resourceId).containsKey(organizationId));
			Assert.assertTrue(matrix.getResourceToRoleMap().get(resourceId).containsKey(roleId));
			
			Assert.assertTrue(matrix.getOrgToGroupMap().get(organizationId).containsKey(groupId));
			Assert.assertTrue(matrix.getOrgToResourceMap().get(organizationId).containsKey(resourceId));
			Assert.assertTrue(matrix.getOrgToRoleMap().get(organizationId).containsKey(roleId));
			
			
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
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
	public void testGetOwnerIdsForResourceDirect() {
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
			
			assertSuccess(resourceDataService.addUserToResource(resourceId, userId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
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
	public void testGetOwnerIdsForResourceIndirectViaGroup() {
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
			
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds));
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
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
	public void testGetOwnerIdsForResourceIndirectViaRole() {
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
			
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds));
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
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
	public void testGetOwnerIdsForResourceIndirectViaOrg() {
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
			
			assertSuccess(organizationServiceClient.addResourceToOrganization(organizationId, resourceId, rightIds));
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resourceId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
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
	public void testGetOwnerIdsForResourceIndirectViaParentResource() {
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
			
			assertSuccess(resourceDataService.addChildResource(resource.getId(), child.getId(), requesterId, rightIds));
			assertSuccess(resourceDataService.addUserToResource(child.getId(), userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
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
	public void testGetOwnerIdsForResourceIndirectViaParentGroup() {
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
			
			assertSuccess(resourceDataService.addGroupToResource(resource.getId(), parent.getId(), requesterId, rightIds));
			assertSuccess(groupServiceClient.addChildGroup(parent.getId(), child.getId(), requesterId, null));
			assertSuccess(groupServiceClient.addUserToGroup(child.getId(), userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
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
	public void testGetOwnerIdsForResourceIndirectViaParentRole() {
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
			
			assertSuccess(resourceDataService.addRoleToResource(resource.getId(), parent.getId(), requesterId, rightIds));
			assertSuccess(roleServiceClient.addChildRole(parent.getId(), child.getId(), requesterId, null));
			assertSuccess(roleServiceClient.addUserToRole(child.getId(), userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(roleServiceClient.removeRole(child.getId(), null));
			}
			if(parent != null) {
				assertSuccess(roleServiceClient.removeRole(parent.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaParentOrg() {
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
			
			assertSuccess(organizationServiceClient.addResourceToOrganization(parent.getId(), resource.getId(), rightIds));
			assertSuccess(organizationServiceClient.addChildOrganization(parent.getId(), child.getId(), null));
			assertSuccess(organizationServiceClient.addUserToOrg(child.getId(), userId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(child != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(child.getId()));
			}
			if(parent != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(parent.getId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndRole() {
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
			
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, requesterId, null));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, requesterId, null));
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
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
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaGroupAndOrg() {
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
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, null));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, null));
			assertSuccess(resourceDataService.addGroupToResource(resourceId, groupId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForResourceIndirectViaRoleAndOrg() {
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
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, null));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, null));
			assertSuccess(resourceDataService.addRoleToResource(resourceId, roleId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForResource(resource.getId());
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(role != null) {
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
			if(resource != null) {
				assertSuccess(resourceDataService.deleteResource(resource.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsDirect() {
		User user = null;
		Group group = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			
			final Set<String> rightIds = new HashSet<String>(Arrays.asList(new String[] {"ADMIN"}));
			final String userId = user.getId();
			final String groupId = group.getId();
			final String requesterId = null;
			
			assertSuccess(groupServiceClient.addUserToGroup(groupId, userId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId);
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
	public void testGetOwnerIdsForGroupsIndirectViaGroup() {
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
			
			assertSuccess(groupServiceClient.addChildGroup(parent.getId(), child.getId(), requesterId, rightIds));
			assertSuccess(groupServiceClient.addUserToGroup(child.getId(), userId, requesterId, null));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(parent.getId());
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
	public void testGetOwnerIdsForGroupsViaRole() {
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
			
			assertSuccess(roleServiceClient.addUserToRole(roleId, userId, requesterId, null));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId);
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
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaOrg() {
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
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, null));
			assertSuccess(organizationServiceClient.addGroupToOrganization(organizationId, groupId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(userIds));
			Assert.assertTrue(userIds.contains(userId));
		} finally {
			if(user != null) {
				assertSuccess(userServiceClient.removeUser(user.getId()));
			}
			if(group != null) {
				assertSuccess(groupServiceClient.deleteGroup(group.getId(), null));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
		}
	}
	
	@Test
	public void testGetOwnerIdsForGroupsViaRoleAndOrg() {
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
			
			assertSuccess(organizationServiceClient.addUserToOrg(organizationId, userId, null));
			assertSuccess(organizationServiceClient.addRoleToOrganization(organizationId, roleId, null));
			assertSuccess(roleServiceClient.addGroupToRole(roleId, groupId, requesterId, rightIds));
			
			final Set<String> userIds = authMangerAdminClient.getOwnerIdsForGroup(groupId);
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
				assertSuccess(roleServiceClient.removeRole(role.getId(), null));
			}
			if(organization != null) {
				assertSuccess(organizationServiceClient.deleteOrganization(organization.getId()));
			}
		}
	}
}
