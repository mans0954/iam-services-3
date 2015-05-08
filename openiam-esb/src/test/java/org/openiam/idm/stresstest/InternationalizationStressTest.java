package org.openiam.idm.stresstest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
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
	
	private AtomicInteger atomicInt = new AtomicInteger();
	private AtomicInteger atomicUserInt = new AtomicInteger();
	
	@BeforeClass
	public void before() {
		userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USER_ROLE WHERE ROLE_ID='HP_ADMIN_ROLE_ID'", null, String.class);
		groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM openiam.GRP", null, String.class);
	}
	
	@Test(threadPoolSize = 20, invocationCount = 127130)
	public void groupStressTest() {
		final int nextInt = atomicInt.incrementAndGet();
		if(nextInt < groupIds.size() - 1) {
			final String id = groupIds.get(nextInt);
			final StopWatch sw = new StopWatch();
			sw.start();
			
			int userIdx = atomicUserInt.incrementAndGet();
			if(userIdx >= userIds.size()) {
				userIdx = 0;
				atomicUserInt.set(0);
			}
			
			final Group group = groupServiceFactory.getGroup(id, userIds.get(userIdx));
			sw.stop();
			Assert.assertNotNull(group);
			System.out.println(String.format("%s:%s", nextInt, sw.getTime()));
			//Assert.assertTrue(sw.getTime() <= 1000);
		} else {
			atomicInt.set(0);
		}
	}
}
