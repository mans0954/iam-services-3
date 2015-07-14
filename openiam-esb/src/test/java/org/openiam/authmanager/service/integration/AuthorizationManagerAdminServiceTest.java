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
}
