package org.openiam.authentication.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.service.AuthenticationWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class LoginStresstest extends AbstractTestNGSpringContextTests {
	
	private static final Log log = LogFactory.getLog(LoginStresstest.class);

	@Autowired
	@Qualifier("authServiceClient")
	private AuthenticationWebService authServiceClient;
	
	/*
	@Test
	public void testStress() throws InterruptedException {
		for(int i = 0; i < 100000; i++) {
			final AuthenticationRequest authenticatedRequest = new AuthenticationRequest();
			authenticatedRequest.setClientIP("127.0.0.1");
			authenticatedRequest.setDomainId("USR_SEC_DOMAIN");
			authenticatedRequest.setPassword("passwd00");
			authenticatedRequest.setPrincipal("admin");
			try {
				authenticatedRequest.setNodeIP(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
			
			}
			authServiceClient.login(authenticatedRequest);
			log.info("Running iteration: " + i);
			Thread.sleep(1000L);
		}
	}
	*/
}
