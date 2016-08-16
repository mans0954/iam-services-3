package org.openiam.authmanager.service.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AuthorizationManagerWebServiceTest extends AbstractAdminAuthorizationManagerTest {

	private static final Log log = LogFactory.getLog(AuthorizationManagerWebServiceTest.class);
	
	@BeforeClass
	public void _init() {
		super._init();
	}

	@Override
	protected void checkUserURLEntitlements(String userId, String url) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean loginAfterUserCreation() {
		return true;
	}
	
	@Test public void foo() {}
}
