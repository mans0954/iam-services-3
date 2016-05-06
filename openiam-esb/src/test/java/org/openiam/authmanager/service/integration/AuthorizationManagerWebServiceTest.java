package org.openiam.authmanager.service.integration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.GroupAuthorizationRight;
import org.openiam.authmanager.common.model.OrganizationAuthorizationRight;
import org.openiam.authmanager.common.model.ResourceAuthorizationRight;
import org.openiam.authmanager.common.model.RoleAuthorizationRight;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
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
