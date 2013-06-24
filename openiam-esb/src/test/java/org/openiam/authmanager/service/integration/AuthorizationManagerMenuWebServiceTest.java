package org.openiam.authmanager.service.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.service.AuthorizationManagerMenuWebService;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class AuthorizationManagerMenuWebServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceTest.class);
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("authorizationManagerMenuServiceClient")
	protected AuthorizationManagerMenuWebService menuWebService;
	
	@Test
	public void testRootNotNull() {
		final MenuRequest request = new MenuRequest();
		request.setMenuRoot("IDM");
		request.setUserId("3000");
		final AuthorizationMenu menu = menuWebService.getMenuTreeForUserId(request);
		Assert.assertNotNull(menu);
		if(menu != null) {
			Assert.assertNotNull(menu.getFirstChild());
		}
	}
}
