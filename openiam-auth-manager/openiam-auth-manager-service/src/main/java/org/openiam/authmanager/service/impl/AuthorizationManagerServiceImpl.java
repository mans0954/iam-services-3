package org.openiam.authmanager.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.GroupAuthorizationRight;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.common.model.OrganizationAuthorizationRight;
import org.openiam.authmanager.common.model.ResourceAuthorizationRight;
import org.openiam.authmanager.common.model.RoleAuthorizationRight;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.OrgUserXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleUserXref;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.authmanager.model.AuthorizationManagerDataModel;
import org.openiam.authmanager.provider.AuthorizationManagerDataProvider;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.thread.Sweepable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * @author Lev Bornovalov
 * Use this class for checking Entitlements between Users, Groups, Roles, and Resources
 * This class uses JDBC Daos directly, in order to circumvent Hibernate.  In order to have a high-performing
 * partial-cache, custom JDBC Queries are required under high load (hence, the reason Hibernate is not used here)
 * This class is READ-ONLY!  No Database writes should be performed
 */
@Service("authorizationManagerService")
@ManagedResource(objectName="org.openiam.authorization.manager:name=authorizationManagerService")
public class AuthorizationManagerServiceImpl extends AbstractAuthorizationManagerService implements AuthorizationManagerService, InitializingBean, ApplicationContextAware, Sweepable, MessageListener<String> {

	private ApplicationContext ctx;
	
	private static final Log log = LogFactory.getLog(AuthorizationManagerServiceImpl.class);
	
	@Value("${org.openiam.authorization.manager.login.hours.threshold}")
	private Long numOfLoggedInHoursThreshold;
	
	@Autowired
	@Qualifier("authManagerUserCache")
	private Ehcache userCache;
	
    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;
    
    @Autowired
    @Qualifier("authManagerCompilationPool")
    private ThreadPoolTaskExecutor authManagerCompilationPool;
    
    @Autowired
    private HazelcastConfiguration hazelcastConfiguration;
	
	/*
	private boolean forceThreadShutdown = false;
	
	@Value("${org.openiam.authorization.manager.threadsweep}")
	private long sweepInterval;
	
	private ExecutorService service = new  ScheduledThreadPoolExecutor(1);
	*/
	
	/* used to prevent reads when a cache refresh takes place */
	//private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	private Map<String, AuthorizationGroup> groupIdCache;
	private Map<String, AuthorizationResource> resourceIdCache;
	private Map<String, AuthorizationOrganization> organizationIdCache;
	private Map<String, AuthorizationRole> roleIdCache;
	private Map<String, AuthorizationAccessRight> accessRightIdCache;
	private Map<Integer, AuthorizationAccessRight> accessRightBitCache;
	private AtomicInteger userBitSet;
	private Integer accessRightBitSet;
	
	@Autowired
	private MembershipDAO membershipDAO;
	
	@Autowired
	private org.openiam.idm.srvc.org.service.OrganizationDAO hibernateOrgDAO;
	
	@Autowired
	private org.openiam.idm.srvc.role.service.RoleDAO hibernateRoleDAO;
	
	@Autowired
	private org.openiam.idm.srvc.grp.service.GroupDAO hibernateGroupDAO;
	
	@Autowired
	private org.openiam.idm.srvc.res.service.ResourceDAO hibernateResourceDAO;
	
	@Autowired
	private org.openiam.idm.srvc.access.service.AccessRightDAO hibernateAccessRightDAO;
	
	@Autowired
	private org.openiam.idm.srvc.user.service.UserDAO hbmUserDAO;
	
	@Autowired
	private AuthorizationManagerDataProvider dataProvider;
	
	/**
	 * This function sweeps through the database, and compiles the new entitlement mappings
	 */
	@Override
    @ManagedOperation(description="sweep the Authorization Cache")
	@Transactional
	@Scheduled(fixedRateString="${org.openiam.authorization.manager.threadsweep}", initialDelayString="${org.openiam.authorization.manager.threadsweep}")
	public void sweep() {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Starting Authorization Manager Refresh.  ThreadId: %s.  Spring Context: %s:%s", Thread.currentThread().getId(), ctx.getId(), ctx.getDisplayName()));
		}
		final StopWatch sw = new StopWatch();
		sw.start();
		
		//Lock writeLock = null;
		try {
			/* create temporary bitSets */
			
			
			/* create lastLogin Date for user dao */
			final long millisecLoginThreshold = new java.util.Date().getTime() - (numOfLoggedInHoursThreshold.longValue() * 60 * 60 * 1000);
			final Date loginThreshold = new Date(millisecLoginThreshold);
		
			if(log.isDebugEnabled()) {
				log.debug("Fetching main objects from the database");
				log.debug(String.format("Login threashold date: %s.  Property was: %s", loginThreshold, numOfLoggedInHoursThreshold));
			}
			
			final StopWatch swDB = new StopWatch();
			swDB.start();
			final AuthorizationManagerDataModel model = dataProvider.getModel();
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.debug(String.format("Time to fetch relationships from the database: %s ms", swDB.getTime()));
			}
			swDB.reset();
			
			swDB.start();
			final List<AuthorizationUser> tempUserList = membershipDAO.getUsers(loginThreshold);
			

			final Map<String, AuthorizationUser> tempUserMap = tempUserList
					.stream()
					.map(e -> new AuthorizationUser(e))
					.collect(Collectors.toMap(AuthorizationUser::getId, Function.identity()));
		
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.info(String.format("Time to fetch users from teh database: %s ms", swDB.getTime()));
			}
			swDB.reset();	
			log.debug("Done fetching main objects");
			
			tempUserList.forEach(entity -> {
				final AuthorizationUser user = tempUserMap.get(entity.getId());
				populateUser(user, model);
			});
			
			final int numOfRights = model.getTempAccessRightMap().size();
			swDB.start();
			if(log.isDebugEnabled()) {
				log.debug("Compiling users");
			}
			
			final int numOfCompilationTasks = tempUserMap.size();
			final CountDownLatch latch = new CountDownLatch(numOfCompilationTasks);
			for(final AuthorizationUser user : tempUserMap.values()) {
				authManagerCompilationPool.submit(new CompilationTask(user, latch, numOfRights));
			}
			latch.await();
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.debug(String.format("Done compiling users.  Done in: %s ms", swDB.getTime()));
			}
			
			synchronized (this) {
				/* CRITICAL SECTION - don't allow reads during write operation */
				//writeLock = readWriteLock.writeLock();
				
				/* find stale keys in User Cache */
				if(log.isDebugEnabled()) {
					log.debug("In critical section - refreshing user cache");
				}
				final Set<String> userKeysToRemove = new HashSet<String>();
				final List<?> userCacheKeys = userCache.getKeys();
				if(CollectionUtils.isNotEmpty(userCacheKeys)) {
					for(final Object key : userCacheKeys) {
						/* keyed by the numeric userId - no need for lower/upper case */
						if(key instanceof String) {
							final String userId = (String)key;
							if(!tempUserMap.containsKey(userId)) {
								userKeysToRemove.add(userId);
							}
						}
					}
				}
				
				/* remove stale keys from User Cache */
				for(final String userId : userKeysToRemove) {
					userCache.remove(userId);
				}
				
				/* put or override updated keys into User Cache */
				for(final String userId : tempUserMap.keySet()) {
					userCache.put(new Element(userId, tempUserMap.get(userId)));
				}
				if(log.isDebugEnabled()) {
					log.debug("Done refreshing user cache");
				}
				
				
				userBitSet = model.getTempUserBitSet();
				roleIdCache = model.getTempRoleIdMap();
				groupIdCache = model.getTempGroupIdMap();
				resourceIdCache = model.getTempResourceIdMap();
				organizationIdCache = model.getTempOrganizationIdMap();
				accessRightIdCache = model.getTempAccessRightMap();
				accessRightBitCache = model.getTempAccessRightBitMap();
				
				/* END CRITICAL SECTION */
			}
			if(log.isDebugEnabled()) {
				log.debug("Succeeded in refreshing the authorization manager");
			}
		} catch(Throwable e) {
			log.error("Error refreshing Authorization Manager", e);
		} finally {
			/*
			try {
				if(writeLock != null) {
					writeLock.unlock();
				}
			} catch(Throwable e) {
				log.error("FATAL!!!  Can't retrieve write lock during authorization manager refresh", e);
			}
			*/
			sw.stop();
			if(log.isDebugEnabled()) {
				log.debug(String.format("Refresh (or fail) took %s ms", sw.getTime()));
			}
		}
	}
	
	private String createLoginIdKey(final String login, final String managedSysId) {
		return createLoginIdKey(new AuthorizationManagerLoginId(login, managedSysId));
	}
	
	private String createLoginIdKey(final AuthorizationManagerLoginId loginId) {
		clean(loginId);
		return String.format("%s:%s", loginId.getLogin(), loginId.getManagedSysId());
	}
	
	private void clean(final AuthorizationManagerLoginId loginId) {
		loginId.setLogin(StringUtils.trimToNull(StringUtils.lowerCase(loginId.getLogin())));
		loginId.setManagedSysId(StringUtils.trimToNull(StringUtils.lowerCase(loginId.getManagedSysId())));
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	@ManagedOperation
	public String fetchUserJMX(final String userId) {
		final AuthorizationUser user = fetchUser(userId);
		return (user != null) ? user.toString() : null;
	}
	
	@ManagedOperation
	public void purgeUser(final String userId) {
		userCache.remove(userId);
	}
	
	@ManagedOperation
	public String getCachedUserContents() {
		final String lineSeparator = System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();
		
		sb.append("User Cache:").append(lineSeparator);
		for(final Object key : userCache.getKeys()) {
			sb.append(key).append("=>").append(userCache.get(key).getValue()).append(lineSeparator);
		}
		
		sb.append(lineSeparator);
		sb.append(lineSeparator);
		sb.append("Done...");
		return sb.toString();
	}
	
	private AuthorizationUser fetchUser(final String userId) {
		AuthorizationUser retVal = getCachedUser(userId);
		if(retVal == null) {
			final InternalAuthroizationUser user = membershipDAO.getUser(userId);
			retVal = process(user);
		}
		return retVal;
	}
	
	private AuthorizationUser process(final InternalAuthroizationUser user) {
		if(user != null) {
			final AuthorizationUser retVal = super.process(user, groupIdCache, roleIdCache, resourceIdCache, organizationIdCache, accessRightIdCache, userBitSet);
			userCache.put(new Element(retVal.getId(), retVal));
			return retVal;
		} else {
			return null;
		}
	}
	
	private AuthorizationUser getCachedUser(final String userId) {
		AuthorizationUser retVal = null;
		final Element cachedUser = userCache.get(userId);
		if(cachedUser != null) {
			final Object cachedValue = cachedUser.getValue();
			if(cachedValue != null) {
				retVal = (AuthorizationUser)cachedValue;
			}
		}
		return retVal;
	}
	
	@Override
	public boolean isMemberOfGroup(final String userId, final String groupId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationGroup group = groupIdCache.get(groupId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && group != null) {
			return user.isMemberOf(group, numOfRights);
		} else {
			return false;
		}
	}
	

	@Override
	public boolean isMemberOfGroup(String userId, String groupId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationGroup group = groupIdCache.get(groupId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && group != null && right != null) {
			return user.isMemberOf(group, right, numOfRights);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationRole role = roleIdCache.get(roleId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && role != null) {
			return user.isMemberOf(role, numOfRights);
		} else {
			return false;
		}
	}
	

	@Override
	public boolean isMemberOfRole(String userId, String roleId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationRole role = roleIdCache.get(roleId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && role != null && right != null) {
			return user.isMemberOf(role, right, numOfRights);
		} else {
			return false;
		}
	}

	@Override
	public Set<ResourceAuthorizationRight> getResourcesForUser(final String userId) {
		return getResorucesFor(fetchUser(userId));
	}

	private Set<ResourceAuthorizationRight> getResorucesFor(final AuthorizationUser user) {
		final int numOfRights = accessRightIdCache.size();
		final Set<ResourceAuthorizationRight> retVal = new HashSet<ResourceAuthorizationRight>();
		if(user != null) {
			final List<Integer> bitList = user.getLinearResources();
			final Map<Integer, AuthorizationResource> bitsetMap = new HashMap<Integer, AuthorizationResource>();
			for(final AuthorizationResource entity : resourceIdCache.values()) {
				bitsetMap.put(entity.getBitSetIdx(), entity);
			}
			
			ResourceAuthorizationRight currentEntity = null;
			for(int i = 0; i < bitList.size(); i++) {
				final Integer bit = bitList.get(i);
				final Integer entityBit = AuthorizationUser.getEntityBit(bit.intValue(), numOfRights);
				if(entityBit != null && bitsetMap.containsKey(entityBit)) {
					currentEntity = new ResourceAuthorizationRight(bitsetMap.get(entityBit).shallowCopy());
					retVal.add(currentEntity);
				} else {
					if(currentEntity != null) {
						final int rightBit = AuthorizationUser.getRightBit(bit.intValue(), currentEntity.getEntity(), numOfRights);
						final AuthorizationAccessRight right = accessRightBitCache.get(Integer.valueOf(rightBit));
						if(right != null) {
							currentEntity.addRight(right);
						}
					}
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<GroupAuthorizationRight> getGroupsForUser(final String userId) {
		return getGroupsFor(fetchUser(userId));
	}
	

	@Override
	public Set<OrganizationAuthorizationRight> getOrganizationsForUser(String userId) {
		return getOrganizationsFor(fetchUser(userId));
	}

	private Set<OrganizationAuthorizationRight> getOrganizationsFor(final AuthorizationUser user) {
		final int numOfRights = accessRightIdCache.size();
		final Set<OrganizationAuthorizationRight> retVal = new HashSet<OrganizationAuthorizationRight>();
		if(user != null) {
			final List<Integer> bitList = user.getLinearOrganizations();
			final Map<Integer, AuthorizationOrganization> bitsetMap = new HashMap<Integer, AuthorizationOrganization>();
			for(final AuthorizationOrganization entity : organizationIdCache.values()) {
				bitsetMap.put(entity.getBitSetIdx(), entity);
			}
			
			OrganizationAuthorizationRight currentEntity = null;
			for(int i = 0; i < bitList.size(); i++) {
				final Integer bit = bitList.get(i);
				final Integer entityBit = AuthorizationUser.getEntityBit(bit.intValue(), numOfRights);
				if(entityBit != null && bitsetMap.containsKey(entityBit)) {
					currentEntity = new OrganizationAuthorizationRight(bitsetMap.get(entityBit).shallowCopy());
					retVal.add(currentEntity);
				} else {
					if(currentEntity != null) {
						final int rightBit = AuthorizationUser.getRightBit(bit.intValue(), currentEntity.getEntity(), numOfRights);
						final AuthorizationAccessRight right = accessRightBitCache.get(Integer.valueOf(rightBit));
						if(right != null) {
							currentEntity.addRight(right);
						}
					}
				}
			}
		}
		return retVal;
	}
	
	private Set<GroupAuthorizationRight> getGroupsFor(final AuthorizationUser user) {
		final int numOfRights = accessRightIdCache.size();
		final Set<GroupAuthorizationRight> retVal = new HashSet<GroupAuthorizationRight>();
		if(user != null) {
			final List<Integer> bitList = user.getLinearGroups();
			final Map<Integer, AuthorizationGroup> bitsetMap = new HashMap<Integer, AuthorizationGroup>();
			for(final AuthorizationGroup entity : groupIdCache.values()) {
				bitsetMap.put(entity.getBitSetIdx(), entity);
			}
			
			GroupAuthorizationRight currentEntity = null;
			for(int i = 0; i < bitList.size(); i++) {
				final Integer bit = bitList.get(i);
				final Integer entityBit = AuthorizationUser.getEntityBit(bit.intValue(), numOfRights);
				if(entityBit != null && bitsetMap.containsKey(entityBit)) {
					currentEntity = new GroupAuthorizationRight(bitsetMap.get(entityBit).shallowCopy());
					retVal.add(currentEntity);
				} else {
					if(currentEntity != null) {
						final int rightBit = AuthorizationUser.getRightBit(bit.intValue(), currentEntity.getEntity(), numOfRights);
						final AuthorizationAccessRight right = accessRightBitCache.get(Integer.valueOf(rightBit));
						if(right != null) {
							currentEntity.addRight(right);
						}
					}
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<RoleAuthorizationRight> getRolesForUser(final String userId) {
		return getRolesFor(fetchUser(userId));
	}

    @Override
    public List<String> getUserIdsList(){
        return hbmUserDAO.getAllIds();
    }
	
	private Set<RoleAuthorizationRight> getRolesFor(final AuthorizationUser user) {
		final int numOfRights = accessRightIdCache.size();
		final Set<RoleAuthorizationRight> retVal = new HashSet<RoleAuthorizationRight>();
		if(user != null) {
			final List<Integer> bitList = user.getLinearRoles();
			final Map<Integer, AuthorizationRole> bitsetMap = new HashMap<Integer, AuthorizationRole>();
			for(final AuthorizationRole entity : roleIdCache.values()) {
				bitsetMap.put(entity.getBitSetIdx(), entity);
			}
			
			RoleAuthorizationRight currentEntity = null;
			for(int i = 0; i < bitList.size(); i++) {
				final Integer bit = bitList.get(i);
				final Integer entityBit = AuthorizationUser.getEntityBit(bit.intValue(), numOfRights);
				if(entityBit != null && bitsetMap.containsKey(entityBit)) {
					currentEntity = new RoleAuthorizationRight(bitsetMap.get(entityBit).shallowCopy());
					retVal.add(currentEntity);
				} else {
					if(currentEntity != null) {
						final int rightBit = AuthorizationUser.getRightBit(bit.intValue(), currentEntity.getEntity(), numOfRights);
						final AuthorizationAccessRight right = accessRightBitCache.get(Integer.valueOf(rightBit));
						if(right != null) {
							currentEntity.addRight(right);
						}
					}
				}
			}
		}
		return retVal;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		onMessage(null);
		hazelcastConfiguration.getTopic("authManagerTopic").addMessageListener(this);
	}
	

	@Override
	public void onMessage(Message<String> message) {
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				sweep();
				return null;
			}
		});
	}

	@Override
	public boolean isEntitled(String userId, String resourceId) {
		final AuthorizationResource resource = resourceIdCache.get(resourceId);
		final AuthorizationUser user = fetchUser(userId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && resource != null) {
			return user.isEntitledTo(resource, numOfRights);
		} else {
			return false;
		}
	}

	@Override
	public boolean isEntitled(String userId, String resourceId, String rightId) {
		final AuthorizationResource resource = resourceIdCache.get(resourceId);
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && resource != null && right != null) {
			return user.isEntitledTo(resource, right, numOfRights);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationOrganization organization = organizationIdCache.get(organizationId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && organization != null) {
			return user.isMemberOf(organization, numOfRights);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationOrganization organization = organizationIdCache.get(organizationId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		final int numOfRights = accessRightIdCache.size();
		if(user != null && organization != null && right != null) {
			return user.isMemberOf(organization, right, numOfRights);
		} else {
			return false;
		}
	}
	
	private class CompilationTask implements Callable<Void> {
		
		private AuthorizationUser entity;
		private CountDownLatch latch;
		private int numOfRights;
		
		private CompilationTask() {}
		
		
		CompilationTask(final AuthorizationUser entity, final CountDownLatch latch, final int numOfRights) {
			this.entity = entity;
			this.latch = latch;
			this.numOfRights = numOfRights;
		}

		@Override
		public Void call() throws Exception {
			this.entity.compile(numOfRights, (int)latch.getCount());
			this.latch.countDown();
			return null;
		}
		
	}

	/*
	@Override
	public List<AuthorizationUser> getUsersForRole(String roleId) {
		final AuthorizationRole role = new AuthorizationRole();
		role.setId(roleId);
		final List<AuthorizationUser> users = new LinkedList<AuthorizationUser>();
		final List<Object> keys = userCache.getKeys();
		if(keys != null && StringUtils.isNotBlank(roleId)) {
			for(final Object key : keys) {
				final Element element = userCache.get(key);
				if(element != null) {
					final AuthorizationUser user = (AuthorizationUser)element.getValue();
					if(user != null) {
						if(user.isMemberOf(role)) {
							users.add(user);
						}
					}
				}
			}
		}
		return users;
	}

	@Override
	public List<AuthorizationUser> getUsersForGroup(String groupId) {
		final AuthorizationGroup group = new AuthorizationGroup();
		group.setId(groupId);
		final List<AuthorizationUser> users = new LinkedList<AuthorizationUser>();
		final List<Object> keys = userCache.getKeys();
		if(keys != null && StringUtils.isNotBlank(groupId)) {
			for(final Object key : keys) {
				final Element element = userCache.get(key);
				if(element != null) {
					final AuthorizationUser user = (AuthorizationUser)element.getValue();
					if(user != null) {
						if(user.isMemberOf(group)) {
							users.add(user);
						}
					}
				}
			}
		}
		return users;
	}
	*/
}
