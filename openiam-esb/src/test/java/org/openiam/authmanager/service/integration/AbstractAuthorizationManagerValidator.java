package org.openiam.authmanager.service.integration;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.base.ws.Response;
import org.testng.Assert;

public abstract class AbstractAuthorizationManagerValidator extends AbstractAuthorizationManagerTest {

	@Override
	protected Response doAddChildOrganization(final String organizationId, final String childOrganizationId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return organizationServiceClient.addChildOrganization(organizationId, childOrganizationId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doAddResourceToOrganization(final String organizationId, final String resourceId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return organizationServiceClient.addResourceToOrganization(organizationId, resourceId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveResourceFromOrganization(final String organizationid, final String resourceId, final String requestorId) {
		return organizationServiceClient.removeResourceFromOrganization(organizationid, resourceId);
	}
	
	@Override
	protected Response doAddRoleToOrganization(final String organizationId, final String roleId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return organizationServiceClient.addRoleToOrganization(organizationId, roleId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveRoleFromOrganization(final String organizationId, final String roleId, final String requestorId) {
		return organizationServiceClient.removeRoleFromOrganization(organizationId, roleId);
	}
	
	@Override
	protected Response doAddGroupToOrganization(final String organizationId, final String groupId, final String requestorId, final Set<String> rightIds, final Date startDate, final Date endDate) {
		return organizationServiceClient.addGroupToOrganization(organizationId, groupId, rightIds, startDate, endDate);
	}
	
	@Override
	protected Response doRemoveGroupFromOrganization(final String organizationId, final String groupId, final String requestorId) {
		return organizationServiceClient.removeGroupFromOrganization(organizationId, groupId);
	}
	
	@Override
	protected void checkUser2ResourceEntitlement(final String userId,
			final String resourceId, final Set<String> rightIds, final boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authorizationManagerServiceClient.isUserEntitledToResourceWithRight(userId, resourceId, rightId), 
							String.format("User %s should be entitled to resource %s with right %s", userId, resourceId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authorizationManagerServiceClient.isUserEntitledToResourceWithRight(userId, resourceId, rightId), 
							String.format("User %s should NOT be entitled to resource %s with right %s", userId, resourceId, rightId));
				});
			} else {
				Assert.assertTrue(authorizationManagerServiceClient.isUserEntitledToResource(userId, resourceId), 
						String.format("User %s should have been entitled to resource %s", userId, resourceId));
			}
		} else {
			Assert.assertFalse(authorizationManagerServiceClient.isUserEntitledToResource(userId, resourceId), 
					String.format("User %s should NOT have been entitled to resource %s", userId, resourceId));
		}
	}
	
	@Override
	protected void checkUser2GroupMembership(final String userId, final String groupId,
			final Set<String> rightIds, final boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authorizationManagerServiceClient.isMemberOfGroupWithRight(userId, groupId, rightId), 
							String.format("User %s should have been a member of group %s with right %s", userId, groupId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authorizationManagerServiceClient.isMemberOfGroupWithRight(userId, groupId, rightId), 
							String.format("User %s should NOT have been a member of group %s with right %s", userId, groupId, rightId));
				});
			} else {
				Assert.assertTrue(authorizationManagerServiceClient.isMemberOfGroup(userId, groupId), 
						String.format("User %s should have been a member of group %s", userId, groupId));
			}
		} else {
			Assert.assertFalse(authorizationManagerServiceClient.isMemberOfGroup(userId, groupId), 
					String.format("User %s should NOT have been a member of group %s", userId, groupId));
		}
	}

	@Override
	protected void checkUser2RoleMembership(final String userId, final String roleId,
			final Set<String> rightIds, final boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authorizationManagerServiceClient.isMemberOfRoleWithRight(userId, roleId, rightId), 
							String.format("User %s should have been a member of role %s with right %s", userId, roleId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authorizationManagerServiceClient.isMemberOfRoleWithRight(userId, roleId, rightId), 
							String.format("User %s should NOT have been a member of role %s with right %s", userId, roleId, rightId));
				});
			} else {
				Assert.assertTrue(authorizationManagerServiceClient.isMemberOfRole(userId, roleId), 
						String.format("User %s should have been a member of role %s", userId, roleId));
			}
		} else {
			Assert.assertFalse(authorizationManagerServiceClient.isMemberOfRole(userId, roleId), 
					String.format("User %s should NOT have been a member of role %s", userId, roleId));
		}
	}

	@Override
	protected void checkUser2OrganizationMembership(final String userId,
			final String organizationId, final Set<String> rightIds, final boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authorizationManagerServiceClient.isMemberOfOrganizationWithRight(userId, organizationId, rightId), 
							String.format("User %s should have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authorizationManagerServiceClient.isMemberOfOrganizationWithRight(userId, organizationId, rightId), 
							String.format("User %s should NOT have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
			} else {
				Assert.assertTrue(authorizationManagerServiceClient.isMemberOfOrganization(userId, organizationId), 
						String.format("User %s should have been a member of organization %s", userId, organizationId));
			}
		} else {
			Assert.assertFalse(authorizationManagerServiceClient.isMemberOfOrganization(userId, organizationId), 
					String.format("User %s should NOT have been a member of organization %s", userId, organizationId));
		}
	}

	@Override
	protected void checkUser2OrgCollection(final String userId, final String organizationId, final Set<String> rightIds, final boolean isAddition) {
		final Set<OrganizationAuthorizationRight> entities = authorizationManagerServiceClient.getOrganizationsForUser(userId);
		if(isAddition) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(entities), String.format("No matching organizations found for user %s", userId));
			final Optional<OrganizationAuthorizationRight> optional = entities.stream().filter(e -> e.getEntity().getId().equals(organizationId)).findFirst();
			Assert.assertTrue(optional.isPresent(), String.format("Organization %s not found", organizationId));
			final OrganizationAuthorizationRight right = optional.get();
			if(CollectionUtils.isNotEmpty(rightIds)) {
				Assert.assertTrue(CollectionUtils.isNotEmpty(right.getRights()));
				rightIds.forEach(rightId -> {
					final Set<String> ids = right.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet());
					Assert.assertTrue(ids.contains(rightId), 
							String.format("User %s should have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(right.getRights().stream().filter(e -> e.getId().equals(right)).findFirst().isPresent(), 
							String.format("User %s should NOT have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
			} else {
				Assert.assertTrue(CollectionUtils.isEmpty(right.getRights()), 
						String.format("User %s should have been a member of organization %s with no rights", userId, organizationId));
			}
		} else {
			Assert.assertTrue(CollectionUtils.isEmpty(entities) || !entities.stream().filter(e -> e.getEntity().getId().equals(organizationId)).findAny().isPresent(), 
					String.format("User %s should NOT have been a member of organization %s", userId, organizationId));
		}
	}

	@Override
	protected void checkUser2RoleCollection(final String userId, final String roleId, final Set<String> rightIds, final boolean isAddition) {
		final Set<RoleAuthorizationRight> entities = authorizationManagerServiceClient.getRolesForUser(userId);
		if(isAddition) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(entities), String.format("No matching roles found for user %s", userId));
			final Optional<RoleAuthorizationRight> optional = entities.stream().filter(e -> e.getEntity().getId().equals(roleId)).findFirst();
			Assert.assertTrue(optional.isPresent(), String.format("Role %s not found", roleId));
			final RoleAuthorizationRight right = optional.get();
			if(CollectionUtils.isNotEmpty(rightIds)) {
				Assert.assertTrue(CollectionUtils.isNotEmpty(right.getRights()));
				rightIds.forEach(rightId -> {
					final Set<String> ids = right.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet());
					Assert.assertTrue(ids.contains(rightId), 
							String.format("User %s should have been a member of roles %s with right %s", userId, roleId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(right.getRights().stream().filter(e -> e.getId().equals(right)).findFirst().isPresent(), 
							String.format("User %s should NOT have been a member of roles %s with right %s", userId, roleId, rightId));
				});
			} else {
				Assert.assertTrue(CollectionUtils.isEmpty(right.getRights()), 
						String.format("User %s should have been a member of roles %s with no rights", userId, roleId));
			}
		} else {
			Assert.assertTrue(CollectionUtils.isEmpty(entities) || !entities.stream().filter(e -> e.getEntity().getId().equals(roleId)).findAny().isPresent(), 
					String.format("User %s should NOT have been a member of roles %s", userId, roleId));
		}
	}

	@Override
	protected void checkUser2GroupCollection(final String userId, final String groupId, final Set<String> rightIds, final boolean isAddition) {
		final Set<GroupAuthorizationRight> entities = authorizationManagerServiceClient.getGroupsForUser(userId);
		if(isAddition) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(entities), String.format("No matching groups found for user %s", userId));
			final Optional<GroupAuthorizationRight> optional = entities.stream().filter(e -> e.getEntity().getId().equals(groupId)).findFirst();
			Assert.assertTrue(optional.isPresent(), String.format("Group %s not found", groupId));
			final GroupAuthorizationRight right = optional.get();
			if(CollectionUtils.isNotEmpty(rightIds)) {
				Assert.assertTrue(CollectionUtils.isNotEmpty(right.getRights()));
				rightIds.forEach(rightId -> {
					final Set<String> ids = right.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet());
					Assert.assertTrue(ids.contains(rightId), 
							String.format("User %s should have been a member of groups %s with right %s", userId, groupId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(right.getRights().stream().filter(e -> e.getId().equals(right)).findFirst().isPresent(), 
							String.format("User %s should NOT have been a member of groups %s with right %s", userId, groupId, rightId));
				});
			} else {
				Assert.assertTrue(CollectionUtils.isEmpty(right.getRights()), 
						String.format("User %s should have been a member of groups %s with no rights", userId, groupId));
			}
		} else {
			Assert.assertTrue(CollectionUtils.isEmpty(entities) || !entities.stream().filter(e -> e.getEntity().getId().equals(groupId)).findAny().isPresent(), 
					String.format("User %s should NOT have been a member of groups %s", userId, groupId));
		}
	}

	@Override
	protected void checkUser2ResourceCollection(final String userId, final String resourceId, final Set<String> rightIds, final boolean isAddition) {
		final Set<ResourceAuthorizationRight> entities = authorizationManagerServiceClient.getResourcesForUser(userId);
		if(isAddition) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(entities), String.format("No matching resources found for user %s", userId));
			final Optional<ResourceAuthorizationRight> optional = entities.stream().filter(e -> e.getEntity().getId().equals(resourceId)).findFirst();
			Assert.assertTrue(optional.isPresent(), String.format("Resource %s not found", resourceId));
			final ResourceAuthorizationRight right = optional.get();
			if(CollectionUtils.isNotEmpty(rightIds)) {
				Assert.assertTrue(CollectionUtils.isNotEmpty(right.getRights()));
				rightIds.forEach(rightId -> {
					final Set<String> ids = right.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet());
					Assert.assertTrue(ids.contains(rightId), 
							String.format("User %s should have been entitled to resource %s with right %s", userId, resourceId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(right.getRights().stream().filter(e -> e.getId().equals(right)).findFirst().isPresent(), 
							String.format("User %s should NOT have been entitled to resource %s with right %s", userId, resourceId, rightId));
				});
			} else {
				Assert.assertTrue(CollectionUtils.isEmpty(right.getRights()), 
						String.format("User %s should have been entitled to resource %s with no rights", userId, resourceId));
			}
		} else {
			Assert.assertTrue(CollectionUtils.isEmpty(entities) || !entities.stream().filter(e -> e.getEntity().getId().equals(resourceId)).findAny().isPresent(), 
					String.format("User %s should NOT have been entitled to resource %s", userId, resourceId));
		}
	}
}
