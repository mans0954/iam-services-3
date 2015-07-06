package org.openiam.authmanager.service.integration;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.AuthorizationManagerHessianClient;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * should be run with 4.0.0/util/1.create_test_data.sql
 * @author lbornova
 *
 */
public class StressTestAuthorizationManagerHessianClient extends AbstractServiceTest {

	private static final Log log = LogFactory.getLog(StressTestAuthorizationManagerHessianClient.class);
	
	@Autowired
	@Qualifier("authorizationManagerHessianClient")
	private AuthorizationManagerHessianClient authClient;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	private static final int NUM_OF_USERS = 50000;
	private static final int NUM_OF_GROUPS = 100000;
	private static final int NUM_OF_ROLES = 100000;
	private static final int NUM_OF_COMPANIES = 10000;
	private static final int NUM_OF_RESOURCES = 10000;
	
	@BeforeClass
	public void init() {
		
	}
	
	@AfterClass
	public void destroy() {
		
	}
	
	@Test(threadPoolSize=50, invocationCount=NUM_OF_USERS / 50)
	public void stressTest() {
		for(int i = 0; i < NUM_OF_USERS; i++) {
			for(int j = 0; j < NUM_OF_RESOURCES; j++) {
				final StopWatch sw = new StopWatch();
				sw.start();
				authClient.isUserEntitledToResource(String.format("STRESS_%s_", i), String.format("STRESS_%s_", j));
				sw.stop();
				Assert.assertTrue(sw.getTime() < 1000);
			}
			
			for(int j = 0; j < NUM_OF_ROLES; j++) {
				final StopWatch sw = new StopWatch();
				sw.start();
				authClient.isUserMemberOfRole(String.format("STRESS_%s_", i), String.format("STRESS_%s_", j));
				sw.stop();
				Assert.assertTrue(sw.getTime() < 1000);
			}
			
			for(int j = 0; j < NUM_OF_GROUPS; j++) {
				final StopWatch sw = new StopWatch();
				sw.start();
				authClient.isUserMemberOfGroup(String.format("STRESS_%s_", i), String.format("STRESS_%s_", j));
				sw.stop();
				Assert.assertTrue(sw.getTime() < 1000);
			}
			
			for(int j = 0; j < NUM_OF_COMPANIES; j++) {
				final StopWatch sw = new StopWatch();
				sw.start();
				authClient.isUserMemberOfOrganization(String.format("STRESS_%s_", i), String.format("STRESS_%s_", j));
				sw.stop();
				Assert.assertTrue(sw.getTime() < 1000);
			}
		}
	}
}
