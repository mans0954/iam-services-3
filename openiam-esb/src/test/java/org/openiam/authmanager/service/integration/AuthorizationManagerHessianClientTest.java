package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.AuthorizationManagerHessianClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(enabled=false)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkUser2GroupMembership(String userId, String groupId,
			Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkUser2RoleMembership(String userId, String roleId,
			Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void checkUser2OrganizationMembership(String userId,
			String organizationId, Set<String> rightIds, boolean isAddition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean loginAfterUserCreation() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
