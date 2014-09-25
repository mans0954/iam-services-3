package org.openiam.authmanager.service.integration;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.AuthorizationManagerHessianClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(enabled=false)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class StressTestAuthorizationManagerHessianClient extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(StressTestAuthorizationManagerHessianClient.class);
	
	@Autowired
	@Qualifier("authorizationManagerHessianClient")
	private AuthorizationManagerHessianClient authClient;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Test
	public void testResources() throws InterruptedException {
		final List<Thread> threadList = new LinkedList<Thread>();
		final List<Map<String, Object>> resourceMap = jdbcTemplate.queryForList("SELECT RESOURCE_ID, USER_ID FROM RESOURCE_USER LIMIT 10000");
		for(final Map<String, Object> map : resourceMap) {
			threadList.add(new ResourceThread((String)map.get("USER_ID"), (String)map.get("RESOURCE_ID"), true));
		}
		
		final List<Map<String, Object>> groupMap = jdbcTemplate.queryForList("SELECT GRP_ID, USER_ID FROM USER_GRP LIMIT 10000");
		for(final Map<String, Object> map : groupMap) {
			threadList.add(new GroupThread((String)map.get("USER_ID"), (String)map.get("GRP_ID"), true));
		}
		
		final List<Map<String, Object>> roleMap = jdbcTemplate.queryForList("SELECT ROLE_ID, USER_ID FROM USER_ROLE LIMIT 10000");
		for(final Map<String, Object> map : roleMap) {
			threadList.add(new RoleThread((String)map.get("USER_ID"), (String)map.get("ROLE_ID"), true));
		}
		
		Collections.shuffle(threadList);
		for(final Thread t : threadList) {
			t.start();
			Thread.sleep(250L);
		}
	}
	
	private class GroupThread extends Thread {
		private String userId;
		private String groupId;
		private boolean hasAccess;
		
		public GroupThread(final String userId, final String groupId, boolean hasAccess) {
			this.userId = userId;
			this.groupId = groupId;
			this.hasAccess = hasAccess;
		}
		
		@Override
		public void run() {
			final boolean result = authClient.isUserWithIdMemberOfGroupWithId(userId, groupId);
			Assert.assertEquals(result, hasAccess);
			if(result != hasAccess) {
				log.error(String.format("Failed:  userId: %s,  groupid: %s, result: %s", userId, groupId, result));
			}
		}
	}
	
	private class ResourceThread extends Thread {
		
		private String userId;
		private String resourceId;
		private boolean hasAccess;
		
		public ResourceThread(final String userId, final String resourceId, boolean hasAccess) {
			this.userId = userId;
			this.resourceId = resourceId;
			this.hasAccess = hasAccess;
		}
		
		@Override
		public void run() {
			final boolean result = authClient.isUserWithIdEntitledToResourceWithId(userId, resourceId);
			Assert.assertEquals(result, hasAccess);
			if(result != hasAccess) {
				log.error(String.format("Failed:  userId: %s,  resourceId: %s, result: %s", userId, resourceId, result));
			}
		}
	}
	
	private class RoleThread extends Thread {
		
		private String userId;
		private String id;
		private boolean hasAccess;
		
		public RoleThread(final String userId, final String id, boolean hasAccess) {
			this.userId = userId;
			this.id = id;
			this.hasAccess = hasAccess;
		}
		
		@Override
		public void run() {
			final boolean result = authClient.isUserWithIdMemberOfRoleWithId(userId, id);
			Assert.assertEquals(result, hasAccess);
			if(result != hasAccess) {
				log.error(String.format("Failed:  userId: %s,  roleId: %s, result: %s", userId, id, result));
			}
		}
	}
}
