package org.openiam.authmanager.service.integration;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.AuthorizationManagerHessianClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationManagerHessianClientTest extends AbstractAuthorizationManagerTest {

	@Autowired
	@Qualifier("authorizationManagerHessianClient")
	private AuthorizationManagerHessianClient authClient;
	
	@Override
	protected void checkUserURLEntitlements(final String userId, final String url) {
		
	}

	@Override
	protected void checkUser2ResourceEntitlement(String userId,
			String resourceId, Set<String> rightIds, boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authClient.isUserEntitledToResourceWithRight(userId, resourceId, rightId), 
							String.format("User %s should be entitled to resource %s with right %s", userId, resourceId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authClient.isUserEntitledToResourceWithRight(userId, resourceId, rightId), 
							String.format("User %s should NOT be entitled to resource %s with right %s", userId, resourceId, rightId));
				});
			} else {
				Assert.assertTrue(authClient.isUserEntitledToResource(userId, resourceId), 
						String.format("User %s should have been entitled to resource %s", userId, resourceId));
			}
		} else {
			Assert.assertFalse(authClient.isUserEntitledToResource(userId, resourceId), 
					String.format("User %s should NOT have been entitled to resource %s", userId, resourceId));
		}
	}

	@Override
	protected void checkUser2GroupMembership(String userId, String groupId,
			Set<String> rightIds, boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authClient.isUserMemberOfGroupWithRight(userId, groupId, rightId), 
							String.format("User %s should have been a member of group %s with right %s", userId, groupId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authClient.isUserMemberOfGroupWithRight(userId, groupId, rightId), 
							String.format("User %s should NOT have been a member of group %s with right %s", userId, groupId, rightId));
				});
			} else {
				Assert.assertTrue(authClient.isUserMemberOfGroup(userId, groupId), 
						String.format("User %s should have been a member of group %s", userId, groupId));
			}
		} else {
			Assert.assertFalse(authClient.isUserMemberOfGroup(userId, groupId), 
					String.format("User %s should NOT have been a member of group %s", userId, groupId));
		}
	}

	@Override
	protected void checkUser2RoleMembership(String userId, String roleId,
			Set<String> rightIds, boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authClient.isUserMemberOfRoleWithRight(userId, roleId, rightId), 
							String.format("User %s should have been a member of role %s with right %s", userId, roleId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authClient.isUserMemberOfRoleWithRight(userId, roleId, rightId), 
							String.format("User %s should NOT have been a member of role %s with right %s", userId, roleId, rightId));
				});
			} else {
				Assert.assertTrue(authClient.isUserMemberOfRole(userId, roleId), 
						String.format("User %s should have been a member of role %s", userId, roleId));
			}
		} else {
			Assert.assertFalse(authClient.isUserMemberOfRole(userId, roleId), 
					String.format("User %s should NOT have been a member of role %s", userId, roleId));
		}
	}

	@Override
	protected void checkUser2OrganizationMembership(String userId,
			String organizationId, Set<String> rightIds, boolean isAddition) {
		if(isAddition) {
			if(CollectionUtils.isNotEmpty(rightIds)) {
				rightIds.forEach(rightId -> {
					Assert.assertTrue(authClient.isUserMemberOfOrganizationWithRight(userId, organizationId, rightId), 
							String.format("User %s should have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
				
				getRightIdsNotIn(rightIds).forEach(rightId -> {
					Assert.assertFalse(authClient.isUserMemberOfOrganizationWithRight(userId, organizationId, rightId), 
							String.format("User %s should NOT have been a member of organization %s with right %s", userId, organizationId, rightId));
				});
			} else {
				Assert.assertTrue(authClient.isUserMemberOfOrganization(userId, organizationId), 
						String.format("User %s should have been a member of organization %s", userId, organizationId));
			}
		} else {
			Assert.assertFalse(authClient.isUserMemberOfOrganization(userId, organizationId), 
					String.format("User %s should NOT have been a member of organization %s", userId, organizationId));
		}
	}

	@Override
	protected boolean loginAfterUserCreation() {
		return false;
	}
	
	@Test
	public void foo() {}

	@Override
	protected void checkUser2OrgCollection(String userId,
			String organizationId, Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void checkUser2RoleCollection(String userId, String roleId,
			Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkUser2GroupCollection(String userId, String groupId,
			Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkUser2ResourceCollection(String userId,
			String resourceId, Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}
}
