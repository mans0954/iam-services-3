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
	
	
}
