package org.openiam.service.integration.stresstest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import twitter4j.UserList;

public class AuthorizationAndAuthenticationStressTest extends AbstractServiceTest {

	private static final int NUM_OF_RESOURCES = 10000;
	private static final int NUM_OF_GROUPS = 10000;
	private static final int NUM_OF_ROLES = 1000;
	private static final int NUM_OF_USERS = 1500000;
	
	private final List<Resource> resourceList = new LinkedList<Resource>();
	private final List<Group> groupList = new LinkedList<Group>();
	private final List<Role> roleList = new LinkedList<Role>();
	private final List<User> userList = new LinkedList<User>();
	
	private final List<Long> entitlementResponseTimes = new LinkedList<Long>();
	
	private enum EXECUTOION_STATE {BOUNDED, UNBOUNDED};
	
	private static final EXECUTOION_STATE state = EXECUTOION_STATE.UNBOUNDED;
	
	@BeforeClass
	public void init() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		for(int i = 0; i < NUM_OF_RESOURCES; i++) {
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					final Resource resource = createResource();
					if(resource != null) {
						synchronized(resourceList) {
							resourceList.add(resource);
						}
					}
				}
			});
		}
		for(int i = 0; i < NUM_OF_GROUPS; i++) {
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					final Group group = createGroup();
					if(group != null) {
						synchronized(groupList) {
							groupList.add(group);
						}
					}
				}
			});
		}
		for(int i = 0; i < NUM_OF_ROLES; i++) {
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					final Role role = createRole();
					if(role != null) {
						synchronized(roleList) {
							roleList.add(role);
						}
					}
				}
			});
		}
		
		pool.shutdown();
		try {
			pool.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		  
		}
		
		pool = Executors.newFixedThreadPool(50);
		for(int i = 0; i < NUM_OF_USERS; i++) {
			pool.submit(new UserThread(i));
		}
		pool.shutdown();
		try {
			pool.awaitTermination(10, TimeUnit.HOURS);
		} catch (InterruptedException e) {
		  
		}
		
		roleList.removeIf(e -> e == null);
		groupList.removeIf(e -> e == null);
		resourceList.removeIf(e -> e == null);
		userList.removeIf(e -> e == null);
		
		pool = Executors.newFixedThreadPool(50);
		for(int i = 0; i < roleList.size(); i++) {
			final Role role = roleList.get(i);
			for(int j = 0; j < 10; j++) {
				final Group group = groupList.get(RandomUtils.nextInt(0, groupList.size()));
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						assertSuccess(roleServiceClient.addGroupToRole(role.getId(), group.getId(), getRequestorId(), getAllRightIds(), null, null));
					}
				});
			}
			
			for(int j = 0; j < 25; j++) {
				final Resource resource = resourceList.get(RandomUtils.nextInt(0, resourceList.size()));
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						assertSuccess(resourceDataService.addRoleToResource(resource.getId(), role.getId(), getRequestorId(), getAllRightIds(), null, null));
					}
				});
			}
		}
		pool.shutdown();
		try {
			pool.awaitTermination(100, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		  
		}
		
		pool = Executors.newFixedThreadPool(50);
		for(int i = 0; i < userList.size(); i++) {
			final User user = userList.get(i);
			for(int j = 0; j < 5; j++) {
				final Role role = roleList.get(RandomUtils.nextInt(0, NUM_OF_ROLES));
				pool.submit(new Runnable() {
				
					@Override
					public void run() {
						assertSuccess(roleServiceClient.addUserToRole(role.getId(), user.getId(), getRequestorId(), getAllRightIds(), null, null));
					}
				});
			}
		}
		pool.shutdown();
		try {
			pool.awaitTermination(2, TimeUnit.HOURS);
		} catch (InterruptedException e) {
		  
		}
	}
	
	@Test
	public void testAuthorization() {
		final Set<String> allRightIds = getAllRightIds();
		final AtomicInteger idx = new AtomicInteger(0);
		
		final int numOfRequestsPerSecond = 30;
		
		final ExecutorService pool = Executors.newFixedThreadPool(100);
		userList.forEach(user -> {
			resourceList.forEach(entity -> {
				final Runnable r = new ResourceAuthorizationTask(user, entity, allRightIds, idx.getAndIncrement());
				if(state == EXECUTOION_STATE.BOUNDED) {
					pool.submit(r);
				} else {
					new Thread(r).start();
					sleep(1000 / numOfRequestsPerSecond);
				}
			});
			groupList.forEach(entity -> {
				final Runnable r = new GroupAuthorizationTask(user, entity, allRightIds, idx.getAndIncrement());
				if(state == EXECUTOION_STATE.BOUNDED) {
					pool.submit(r);
				} else {
					new Thread(r).start();
					sleep(1000 / numOfRequestsPerSecond);
				}
			});
			roleList.forEach(entity -> {
				final Runnable r = new RoleAuthorizationTask(user, entity, allRightIds, idx.getAndIncrement());
				if(state == EXECUTOION_STATE.BOUNDED) {
					pool.submit(r);
				} else {
					new Thread(r).start();
					sleep(1000 / numOfRequestsPerSecond);
				}
			});
		});
		
		pool.shutdown();
		try {
			pool.awaitTermination(2, TimeUnit.HOURS);
		} catch (InterruptedException e) {
		  
		}
		
		final double avg = entitlementResponseTimes.stream().mapToLong(e -> e.longValue()).average().getAsDouble();
		logger.info(String.format("%s ms per authorization call", avg / 4.0));
	}
	
	@Test
	public void testAuthentication() {
		
		final int numOfRequestsPerSecond = 4;
		
		final ExecutorService pool = Executors.newFixedThreadPool(100);
		final AtomicInteger idx = new AtomicInteger(0);
		userList.forEach(user -> {
			final Runnable r = new LoginThread(user, idx.getAndIncrement());
			if(state == EXECUTOION_STATE.BOUNDED) {
				pool.submit(r);
			} else {
				new Thread(r).start();
				sleep(1000 / numOfRequestsPerSecond);
			}
		});
		
		pool.shutdown();
		try {
			pool.awaitTermination(2, TimeUnit.HOURS);
		} catch (InterruptedException e) {
		  
		}
	}
	
	private class ResourceAuthorizationTask implements Runnable {
		
		private User user;
		private Resource entity;
		private Set<String> allRightIds;
		private int idx;
		
		private ResourceAuthorizationTask(final User user, final Resource entity, final Set<String> allRightIds, final int idx) {
			this.user = user;
			this.entity = entity;
			this.allRightIds = allRightIds;
			this.idx = idx;
		}

		@Override
		public void run() {
			final StopWatch sw = new StopWatch();
			sw.start();
			authorizationManagerServiceClient.isUserEntitledToResource(user.getId(), entity.getId());
			allRightIds.forEach(right -> {
				authorizationManagerServiceClient.isUserEntitledToResourceWithRight(user.getId(), entity.getId(), right);
			});
			sw.stop();
			entitlementResponseTimes.add(sw.getTime());
			logger.info(String.format("%s ms for %s checks.  %s.  IDX: %s", sw.getTime(), allRightIds.size() + 1, this.getClass(), idx));
		}
		
	}
	
	private class GroupAuthorizationTask implements Runnable {
		
		private User user;
		private Group entity;
		private Set<String> allRightIds;
		private int idx;
		
		private GroupAuthorizationTask(final User user, final Group entity, final Set<String> allRightIds, final int idx) {
			this.user = user;
			this.entity = entity;
			this.allRightIds = allRightIds;
			this.idx = idx;
		}

		@Override
		public void run() {
			final StopWatch sw = new StopWatch();
			sw.start();
			authorizationManagerServiceClient.isMemberOfGroup(user.getId(), entity.getId());
			allRightIds.forEach(right -> {
				authorizationManagerServiceClient.isMemberOfGroupWithRight(user.getId(), entity.getId(), right);
			});
			sw.stop();
			entitlementResponseTimes.add(sw.getTime());
			logger.info(String.format("%s ms for %s checks.  %s.  IDX: %s", sw.getTime(), allRightIds.size() + 1, this.getClass(), idx));
		}
		
	}
	
	private class RoleAuthorizationTask implements Runnable {
		
		private User user;
		private Role entity;
		private Set<String> allRightIds;
		private int idx;
		
		private RoleAuthorizationTask(final User user, final Role entity, final Set<String> allRightIds, final int idx) {
			this.user = user;
			this.entity = entity;
			this.allRightIds = allRightIds;
			this.idx = idx;
		}

		@Override
		public void run() {
			final StopWatch sw = new StopWatch();
			sw.start();
			authorizationManagerServiceClient.isMemberOfRole(user.getId(), entity.getId());
			allRightIds.forEach(right -> {
				authorizationManagerServiceClient.isMemberOfRoleWithRight(user.getId(), entity.getId(), right);
			});
			sw.stop();
			entitlementResponseTimes.add(sw.getTime());
			logger.info(String.format("%s ms for %s checks.  %s.  IDX: %s", sw.getTime(), allRightIds.size() + 1, this.getClass(), idx));
		}
		
	}
	
	private class LoginThread implements Runnable {
		
		private User user;
		private int idx;
		
		LoginThread(final User user, final int idx) {
			this.user = user;
			this.idx = idx;
		}

		@Override
		public void run() {
			final StopWatch sw = new StopWatch();
			sw.start();
			login(user.getId());
			sw.stop();
			logger.info(String.format("%s ms for Login.  IDX: %s", sw.getTime(), idx));
		}
		
		
	}
	
	private class UserThread implements Runnable {
		
		private int i;
		
		UserThread(final int i) {
			this.i = i;
		}

		@Override
		public void run() {
			final User user = createUser();
			if(i % 10 == 0) {
				login(user.getId());
			}
			synchronized(userList) {
				userList.add(user);
			}
		}
		
	}
	
}
