package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.AuthorizationManagerHessianClient;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class AuthorizationManagerHessianClientTest extends AbstractAuthorizationManagerTest {

	@Autowired
	@Qualifier("authorizationManagerHessianClient")
	private AuthorizationManagerHessianClient authClient;
	
	@Override
	protected void checkUserURLEntitlements(final String userId, final AuthorizationManagerLoginId loginId, final String url) {
		
	}
	
	@Override
	protected void confirmUserRoles(String userId,
			AuthorizationManagerLoginId loginId, Set<String> roleIds) {
		final Set<String> result = new HashSet<String>();
		if(userId != null) {
			result.addAll(Arrays.asList(authClient.getRoleIdsForUserWithId(userId)));
		} else {
			result.addAll(Arrays.asList(authClient.getRoleIdsForUserWithLogin(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId())));
		}
		
		Assert.assertEquals(CollectionUtils.size(roleIds), CollectionUtils.size(result),String.format("The number of DB roles and roles returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId));
		
		for(final String resourceId : roleIds) {
			Assert.assertTrue(result.contains(resourceId), String.format("Roles returned from Hessian did not contain '%s'", resourceId));
		}
		
		for(final String resourceId : result) {
			Assert.assertTrue(roleIds.contains(resourceId),String.format("Roles returned from Hessian did not contain '%s'", resourceId));
		}
	}

	@Override
	protected void confirmUserGroups(String userId,
			AuthorizationManagerLoginId loginId, Set<String> groupIds) {
		final Set<String> result = new HashSet<String>();
		if(userId != null) {
			result.addAll(Arrays.asList(authClient.getGroupIdsForUserWithId(userId)));
		} else {
			result.addAll(Arrays.asList(authClient.getGroupIdsForUserWithLogin(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId())));
		}
		
		Assert.assertEquals(CollectionUtils.size(groupIds), CollectionUtils.size(result),String.format("The number of DB groups and groups returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId));
		
		for(final String resourceId : groupIds) {
			Assert.assertTrue(result.contains(resourceId), String.format("Groups returned from WS did not contain '%s'", resourceId));
		}
		
		for(final String resourceId : result) {
			Assert.assertTrue(groupIds.contains(resourceId),String.format("Groups returned from DB did not contain '%s'", resourceId));
		}
	}

	@Override
	protected void confirmUserResources(String userId,
			AuthorizationManagerLoginId loginId, Set<String> resourceIds) {
		final Set<String> result = new HashSet<String>();
		if(userId != null) {
			result.addAll(Arrays.asList(authClient.getResourceIdsForUserWithId(userId)));
		} else {
			result.addAll(Arrays.asList(authClient.getResourceIdsForUserWithLogin(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId())));
		}
		
		Assert.assertEquals(CollectionUtils.size(resourceIds), CollectionUtils.size(result), String.format("The number of DB resoruces and resources returned from the WS are not equal for user '%s", (userId != null) ? userId : loginId));
		
		for(final String resourceId : resourceIds) {
			Assert.assertTrue(result.contains(resourceId),String.format("Resources returned from WS did not contain '%s'", resourceId));
		}
		
		for(final String resourceId : result) {
			Assert.assertTrue(resourceIds.contains(resourceId),String.format("Resources returned from DB did not contain '%s'", resourceId));
		}
	}

	@Override
	protected void checkUser2ResourceEntitlement(String userId,
			AuthorizationManagerLoginId loginId, String resourceId,
			String resourceName) {
		boolean result = false;
		if(resourceId != null) {
			if(userId != null) {
				result = authClient.isUserWithIdEntitledToResourceWithId(userId, resourceId);
			} else {
				result = authClient.isUserWithLoginEntitledToResourceWithId(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), resourceId);
			}
		} else {
			if(userId != null) {
				result = authClient.isUserWithIdEntitledToResourceWithName(userId, resourceName);
			} else {
				result = authClient.isUserWithLoginEntitledToResourceWithName(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), resourceName);
			}
		}
		String failMessage = String.format("User %s:%s is not entitled to resource.  %s", userId, loginId, resourceId);
		Assert.assertTrue(result,failMessage);
	}

	@Override
	protected void checkUser2GroupMembership(String userId,
			AuthorizationManagerLoginId loginId, String groupId,
			String groupName) {
		boolean result = false;
		if(groupId != null) {
			if(userId != null) {
				result = authClient.isUserWithIdMemberOfGroupWithId(userId, groupId);
			} else {
				result = authClient.isUserWithLoginMemberOfGroupWithId(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), groupId);
			}
		} else {
			if(userId != null) {
				result = authClient.isUserWithIdMemberOfGroupWithName(userId, groupName);
			} else {
				result = authClient.isUserWithLoginMemberOfGroupWithName(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), groupName);
			}
		}
		String failMessage = String.format("User not member of group.  %s", groupId);
		Assert.assertTrue(result,failMessage);
	}

	@Override
	protected void checkUser2RoleMembership(String userId,
			AuthorizationManagerLoginId loginId, String roleId, String roleName) {
		boolean result = false;
		if(roleId != null) {
			if(userId != null) {
				result = authClient.isUserWithIdMemberOfRoleWithId(userId, roleId);
			} else {
				result = authClient.isUserWithLoginMemberOfRoleWithId(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), roleId);
			}
		} else {
			if(userId != null) {
				result = authClient.isUserWithIdMemberOfRoleWithName(userId, roleName);
			} else {
				result = authClient.isUserWithLoginMemberOfRoleWithName(loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId(), roleName);
			}
		}
		String failMessage = String.format("User not member of role.  %s", roleName);
		Assert.assertTrue(result, failMessage);
	}
}
