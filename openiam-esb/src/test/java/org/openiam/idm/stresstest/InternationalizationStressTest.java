package org.openiam.idm.stresstest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.srvc.am.GroupDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.srvc.am.RoleDataWebService;
import org.openiam.srvc.user.UserDataWebService;
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
	@Qualifier("userServiceClient")
    private UserDataWebService userServiceClient;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<String> userIds = null;
	private List<String> groupIds = null;
	private List<String> roleIds = null;
	private List<String> userIdsInHpAdmin = null;
	
	private AtomicInteger roleInt = new AtomicInteger();
	private AtomicInteger groupInt = new AtomicInteger();
	private AtomicInteger atomicUserInt = new AtomicInteger();
	
	@BeforeClass
	public void before() {
		userIds = jdbcTemplate.queryForList("SELECT USER_ID FROM USERS", null, String.class);
		groupIds = jdbcTemplate.queryForList("SELECT GRP_ID FROM GRP", null, String.class);
		roleIds = jdbcTemplate.queryForList("SELECT ROLE_ID FROM ROLE", null, String.class);
		userIdsInHpAdmin = jdbcTemplate.queryForList("SELECT USER_ID FROM USER_ROLE WHERE ROLE_ID ='HP_ADMIN_ROLE_ID'", null, String.class);
	}
	
	@Test(threadPoolSize = 100, invocationCount = 127130)
	public void testUserAttributeStressTest() {
		final int userIdx = atomicUserInt.incrementAndGet() % userIds.size();
		final String userId = userIds.get(userIdx);
		
		final Language language = new Language();
		language.setId("1");
		final StopWatch sw = new StopWatch();
		sw.start();
		userServiceClient.getUserAttributesInternationalized(userId, language);
		sw.stop();
		//if(sw.getTime() > 20000) {
			System.out.println(String.format("IDX: %s, %s ms.  UID: %s", userIdx, sw.getTime(), userId));
		//}
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
		final Group group = groupServiceFactory.getGroup(groupId);
		final Role role = roleServiceClient.getRole(roleId);
		sw.stop();
		//Assert.assertNotNull(group);
		System.out.println(String.format("%s ms.  UID: %s", sw.getTime(), userId));
		//Assert.assertTrue(sw.getTime() <= 1000);
	}
}
