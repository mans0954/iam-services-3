package org.openiam.authmanager.service.integration;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AuthorizationManagerWebServiceTest extends AbstractAuthorizationManagerTest {

	private static final Log log = LogFactory.getLog(AuthorizationManagerWebServiceTest.class);
	
	@BeforeClass
	public void _init() {
		super._init();
	}
	
	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	private AuthorizationManagerWebService authorizationManagerServiceClient;

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
	protected void checkUserURLEntitlements(String userId, String url) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Test
	public void testUser2OrganizationIndirectCompiled() {
		super.testUser2OrganizationIndirectCompiled();
	}

	@Override
	protected boolean loginAfterUserCreation() {
		return false;
	}
}
