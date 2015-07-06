package org.openiam.authmanager.service.integration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	private List<String> userIds;
	private List<String> groupIds;
	private List<String> roleIds;
	private List<String> orgIds;
	private List<String> resourceIds;
	
	private final AtomicInteger userInt = new AtomicInteger();
	private final AtomicInteger groupInt = new AtomicInteger();
	private final AtomicInteger roleInt = new AtomicInteger();
	private final AtomicInteger orgInt = new AtomicInteger();
	private final AtomicInteger resourceInt = new AtomicInteger();
	
	@BeforeClass
	public void init() {
		userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", String.class);
		groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM GRP", String.class);
		roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM ROLE", String.class);
		orgIds = jdbcTemplate.queryForList("SELECT COMPANY_ID FROM COMPANY", String.class);
		resourceIds = jdbcTemplate.queryForList("SELECT RESOURCE_ID FROM RES", String.class);
	}
	
	@AfterClass
	public void destroy() {
		
	}
	
	@Test(threadPoolSize=50, invocationCount=1000)
	public void stressTest() {
		final String userId = userIds.get(userInt.incrementAndGet() % userIds.size());
		final String groupId = groupIds.get(groupInt.incrementAndGet() % groupIds.size());
		final String roleId = roleIds.get(roleInt.incrementAndGet() % roleIds.size());
		final String orgId = orgIds.get(orgInt.incrementAndGet() % orgIds.size());
		final String resourceId = resourceIds.get(resourceInt.incrementAndGet() % resourceIds.size());
		
		final StopWatch sw = new StopWatch();
		sw.start();
		authClient.isUserEntitledToResource(userId, resourceId);
		authClient.isUserMemberOfGroup(userId, groupId);
		authClient.isUserMemberOfRole(userId, roleId);
		authClient.isUserMemberOfOrganization(userId, orgId);
		sw.stop();
		
		System.out.println(sw.getTime());
	}
}
