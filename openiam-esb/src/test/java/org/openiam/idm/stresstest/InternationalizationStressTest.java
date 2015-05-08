package org.openiam.idm.stresstest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml", 
"classpath:test-esb-integration.xml"})
public class InternationalizationStressTest extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("groupServiceClient")
	private GroupDataWebService groupServiceFactory;
	
	@Autowired
	@Qualifier("roleServiceClient")
	private RoleDataWebService roleServiceClient;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<String> userIds = null;
	private List<String> groupIds = null;
	private List<String> roleIds = null;
	
	private AtomicInteger roleInt = new AtomicInteger();
	private AtomicInteger groupInt = new AtomicInteger();
	private AtomicInteger atomicUserInt = new AtomicInteger();
	
	@BeforeClass
	public void before() {
		userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", null, String.class);
		groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM GRP", null, String.class);
		roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM ROLE", null, String.class);
	}
	
	@Test(threadPoolSize = 100, invocationCount = 127130)
	public void groupStressTest() {
		final int groupIdx = groupInt.incrementAndGet() % groupIds.size();
		final int roleIdx = roleInt.incrementAndGet() % roleIds.size();
		final String groupId = groupIds.get(groupIdx);
		final String roleId = roleIds.get(roleIdx);
		final StopWatch sw = new StopWatch();
		sw.start();
			
		int userIdx = atomicUserInt.incrementAndGet() % userIds.size();
		final String userId = userIds.get(userIdx);
			
		//System.out.println(String.format("User ID: %s", userId));
		final Group group = groupServiceFactory.getGroup(groupId, userId);
		final Role role = roleServiceClient.getRole(roleId, userId);
		sw.stop();
		//Assert.assertNotNull(group);
		System.out.println(String.format("%s ms.  UID: %s", sw.getTime(), userId));
		//Assert.assertTrue(sw.getTime() <= 1000);
	}
}
