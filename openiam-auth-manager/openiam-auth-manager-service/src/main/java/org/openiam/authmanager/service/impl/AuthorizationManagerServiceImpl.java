package org.openiam.authmanager.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PreDestroy;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.common.xref.RoleUserXref;
import org.openiam.authmanager.dao.GroupDAO;
import org.openiam.authmanager.dao.GroupGroupXrefDAO;
import org.openiam.authmanager.dao.GroupUserXrefDAO;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.dao.ResourceGroupXrefDAO;
import org.openiam.authmanager.dao.ResourcePropDAO;
import org.openiam.authmanager.dao.ResourceResourceXrefDAO;
import org.openiam.authmanager.dao.ResourceRoleXrefDAO;
import org.openiam.authmanager.dao.ResourceUserXrefDAO;
import org.openiam.authmanager.dao.RoleDAO;
import org.openiam.authmanager.dao.RoleGroupXrefDAO;
import org.openiam.authmanager.dao.RoleRoleXrefDAO;
import org.openiam.authmanager.dao.RoleUserXrefDAO;
import org.openiam.authmanager.dao.UserDAO;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.idm.srvc.grp.ws.GroupResponse;
import org.openiam.idm.srvc.res.dto.ResourceProp;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * @author Lev Bornovalov
 * Use this class for checking Entitlements between Users, Groups, Roles, and Resources
 * This class uses JDBC Daos directly, in order to circumvent Hibernate.  In order to have a high-performing
 * partial-cache, custom JDBC Queries are required under high load (hence, the reason Hibernate is not used here)
 * This class is READ-ONLY!  No Database writes should be performed
 */
@Service("authorizationManagerService")
@ManagedResource(objectName="org.openiam.authorization.manager:name=authorizationManagerService")
public class AuthorizationManagerServiceImpl implements AuthorizationManagerService, InitializingBean, ApplicationContextAware, Sweepable/*, Runnable*/ {

	private ApplicationContext ctx;
	
	private static final Log log = LogFactory.getLog(AuthorizationManagerServiceImpl.class);
	
	@Value("${org.openiam.authorization.manager.login.hours.threshold}")
	private Long numOfLoggedInHoursThreshold;
	
	@Autowired
	@Qualifier("authManagerUserCache")
	private Ehcache userCache;
	
	@Autowired
	@Qualifier("userLoginCache")
	private Ehcache loginCache;
	
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
	private Map<String, AuthorizationRole> roleIdCache;
	private Map<String, AuthorizationGroup> groupNameCache;
	private Map<String, AuthorizationResource> resourceNameCache;
	private Map<String, AuthorizationRole> roleNameCache;
	private Integer userBitSet;
	
	@Autowired
	@Qualifier("jdbcGroupDAO")
	private GroupDAO groupDAO;
	
	@Autowired
	@Qualifier("jdbcResourceDAO")
	private ResourceDAO resourceDAO;
	
	@Autowired
	@Qualifier("jdbcRoleDAO")
	private RoleDAO roleDAO;
	
	@Autowired
	@Qualifier("jdbcUserDao")
	private UserDAO userDAO;
	
	@Autowired
	@Qualifier("jdbcGroupGroupXrefDao")
	private GroupGroupXrefDAO groupGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcGroupUserXrefDao")
	private GroupUserXrefDAO groupUserXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceGroupXrefDAO")
	private ResourceGroupXrefDAO resourceGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceResourceXrefDAO")
	private ResourceResourceXrefDAO resourceResourceXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceRoleXrefDAO")
	private ResourceRoleXrefDAO resourceRoleXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourceUserXrefDAO")
	private ResourceUserXrefDAO resourceUserXrefDAO;
	
	@Autowired
	@Qualifier("jdbcRoleGroupXrefDAO")
	private RoleGroupXrefDAO roleGroupXrefDAO;
	
	@Autowired
	@Qualifier("jdbcRoleRoleXrefDAO")
	private RoleRoleXrefDAO roleRoleXrefDAO;
	
	@Autowired
	@Qualifier("jdbcRoleUserXrefDAO")
	private RoleUserXrefDAO roleUserXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourcePropDAO")
	private ResourcePropDAO resourcePropDAO;
	
	/**
	 * This function sweeps through the database, and compiles the new entitlement mappings
	 */
	@ManagedOperation(description="sweep the Authorization Cache")
	public void sweep() {
		log.info(String.format("Starting Authorization Manager Refresh.  ThreadId: %s.  Spring Context: %s:%s", Thread.currentThread().getId(), ctx.getId(), ctx.getDisplayName()));
		final StopWatch sw = new StopWatch();
		sw.start();
		
		//Lock writeLock = null;
		try {
			/* create temporary bitSets */
			int tempGroupBitSet = 0;
			int tempRoleBitSet = 0;
			int tempResourceBitSet = 0;
			int tempUserBitSet = 0;
			
			/* create lastLogin Date for user dao */
			final long millisecLoginThreshold = new java.util.Date().getTime() - (numOfLoggedInHoursThreshold.longValue() * 60 * 60 * 1000);
			final Date loginThreshold = new Date(millisecLoginThreshold);
			
			log.debug("Fetching main objects from the database");
			log.debug(String.format("Login threashold date: %s.  Property was: %s", loginThreshold, numOfLoggedInHoursThreshold));
			final List<AuthorizationManagerLoginId> tempLoginIdList = userDAO.getLoginIdsForUsersLoggedInAfter(loginThreshold);
			final List<AuthorizationUser> tempUserList = userDAO.getAllUsersLoggedInAfter(loginThreshold);
			final List<AuthorizationRole> tempRoleList = roleDAO.getList();
			final List<AuthorizationResource> tempResourceList = resourceDAO.getList();
			final List<AuthorizationGroup> tempGroupList = groupDAO.getList();
			log.debug("Done fetching main objects");
			
			/* create Maps of the above objects for fast access.  Id->Object
			 * Set the bitSets of each object 
			 */
			final Map<String, AuthorizationUser> tempUserMap = new HashMap<String, AuthorizationUser>();
			for(final AuthorizationUser user : tempUserList) {
				tempUserMap.put(user.getId(), user);
				user.setBitSetIdx(tempUserBitSet++);
			}
			
			final Map<String, AuthorizationResource> tempResourceNameMap = new HashMap<String, AuthorizationResource>();
			final Map<String, AuthorizationResource> tempResourceIdMap = new HashMap<String, AuthorizationResource>();
			for(final AuthorizationResource resource : tempResourceList) {
				tempResourceIdMap.put(resource.getId(), resource);
				tempResourceNameMap.put(resource.getName(), resource);
				resource.setBitSetIdx(tempResourceBitSet++);
			}
			
			final Map<String, AuthorizationRole> tempRoleNameMap = new HashMap<String, AuthorizationRole>();
			final Map<String, AuthorizationRole> tempRoleIdMap = new HashMap<String, AuthorizationRole>();
			for(final AuthorizationRole role : tempRoleList) {
				tempRoleNameMap.put(role.getName(), role);
				tempRoleIdMap.put(role.getId(), role);
				role.setBitSetIdx(tempRoleBitSet++);
			}
			
			final Map<String, AuthorizationGroup> tempGroupNameMap = new HashMap<String, AuthorizationGroup>();
			final Map<String, AuthorizationGroup> tempGroupIdMap = new HashMap<String, AuthorizationGroup>();
			for(final AuthorizationGroup group : tempGroupList) {
				tempGroupNameMap.put(group.getName(), group);
				tempGroupIdMap.put(group.getId(), group);
				group.setBitSetIdx(tempGroupBitSet++);
			}
			
			/* set direct roles for Users */
			final List<RoleUserXref> tempRole2UserXrefList = roleUserXrefDAO.getList();
			for(final RoleUserXref xref : tempRole2UserXrefList) {
				final AuthorizationRole role = tempRoleIdMap.get(xref.getRoleId());
				final AuthorizationUser user = tempUserMap.get(xref.getUserId());
				if(user != null && role != null) {
					user.addRole(role);
				}
			}
			
			/* set direct Role<->Role mappings */
			final List<RoleRoleXref> tempRole2RoleXrefList = roleRoleXrefDAO.getList();
			for(final RoleRoleXref xref : tempRole2RoleXrefList) {
				final AuthorizationRole role = tempRoleIdMap.get(xref.getRoleId());
				final AuthorizationRole memberRole = tempRoleIdMap.get(xref.getMemberRoleId());
				if(role != null && memberRole != null) {
					//role.addChildRole(memberRole);
					memberRole.addParentRole(role);
				}
			}
			
			/* set direct group<->Role mappings */
			final List<RoleGroupXref> tempRole2GroupXrefList = roleGroupXrefDAO.getList();
			for(final RoleGroupXref xref : tempRole2GroupXrefList) {
				final AuthorizationRole role = tempRoleIdMap.get(xref.getRoleId());
				final AuthorizationGroup group = tempGroupIdMap.get(xref.getGroupId());
				if(role != null && group != null) {
					group.addRole(role);
				}
			}
			
			/* set direct Resource<->User mappings */
			final List<ResourceUserXref> tempResource2UserXrefList = resourceUserXrefDAO.getList();
			for(final ResourceUserXref xref : tempResource2UserXrefList) {
				final AuthorizationResource resource = tempResourceIdMap.get(xref.getResourceId());
				final AuthorizationUser user = tempUserMap.get(xref.getUserId());
				if(resource != null && user != null) {
					user.addResource(resource);
				}
			}
			
			/* set direct Resource<->Role mappings */
			final List<ResourceRoleXref> tempResource2RoleXrefList = resourceRoleXrefDAO.getList();
			for(final ResourceRoleXref xref : tempResource2RoleXrefList) {
				final AuthorizationResource resource = tempResourceIdMap.get(xref.getResourceId());
				final AuthorizationRole role = tempRoleIdMap.get(xref.getRoleId());
				if(resource != null && role != null) {
					role.addResource(resource);
				}
			}
			
			/* set direct Resource<->Resource mappings */
			final List<ResourceResourceXref> tempResource2ResourceXrefList = resourceResourceXrefDAO.getList();
			for(final ResourceResourceXref xref : tempResource2ResourceXrefList) {
				final AuthorizationResource resource = tempResourceIdMap.get(xref.getResourceId());
				final AuthorizationResource memberResource = tempResourceIdMap.get(xref.getMemberResourceId());
				if(resource != null && memberResource != null) {
					//resource.addChildResource(memberResource);
					memberResource.addParentResoruce(resource);
				}
			}
			
			/* set direct Resource<->Group mappings */
			final List<ResourceGroupXref> tempResource2GroupXrefList = resourceGroupXrefDAO.getList();
			for(final ResourceGroupXref xref : tempResource2GroupXrefList) {
				final AuthorizationResource resource = tempResourceIdMap.get(xref.getResourceId());
				final AuthorizationGroup group = tempGroupIdMap.get(xref.getGroupId());
				if(resource != null && group != null) {
					group.addResource(resource);
				}
			}
			
			/* set direct Group<->User mappings */
			final List<GroupUserXref> tempGroup2UserXrefList = groupUserXrefDAO.getList();
			for(final GroupUserXref xref : tempGroup2UserXrefList) {
				final AuthorizationGroup group = tempGroupIdMap.get(xref.getGroupId());
				final AuthorizationUser user = tempUserMap.get(xref.getUserId());
				if(user != null && group != null) {
					user.addGroup(group);
				}
			}
			
			/* set direct Group<->Group mappings */
			final List<GroupGroupXref> tempGroup2GroupXrefList = groupGroupXrefDAO.getList();
			for(final GroupGroupXref xref : tempGroup2GroupXrefList) {
				final AuthorizationGroup group = tempGroupIdMap.get(xref.getGroupId());
				final AuthorizationGroup memberGroup = tempGroupIdMap.get(xref.getMemberGroupId());
				if(group != null && memberGroup != null) {
					//group.addChildGroup(memberGroup);
					memberGroup.addParentGroup(group);
				}
			}
			
			/* create a map of LoginId cache Key ->LoginId */
			/* use case insensitive verions.  Log an error if case-insensitive duplicates occur */
			log.debug("Creating Login cache");
			final Set<String> duplicateLoginIdsCaseIgnore = new HashSet<String>();
			final Map<String, AuthorizationManagerLoginId> tempLoginIdMap = new HashMap<String, AuthorizationManagerLoginId>();
			for(final AuthorizationManagerLoginId loginId : tempLoginIdList) {
				final String loginIdKey = createLoginIdKey(loginId);
				if(!tempLoginIdMap.containsKey(loginIdKey)) {
					tempLoginIdMap.put(loginIdKey, loginId);
				} else {
					log.error(String.format("LoginId: '%s' contains two versions in the database when matching case-insensitively, ignoring...", loginIdKey));
					duplicateLoginIdsCaseIgnore.add(loginIdKey);
				}
			}
			
			/* remove duplicates */
			for(final String badLoginId : duplicateLoginIdsCaseIgnore) {
				tempLoginIdMap.remove(badLoginId);
			}
			log.debug("Done creating login cache");
			
			/* compile the entities */
			log.debug("Compiling resources...");
			for(final AuthorizationResource resource : tempResourceIdMap.values()) {
				resource.compile();
			}
			log.debug("Done compiling resources...");
			
			log.info("Compiling roles");
			for(final AuthorizationRole role : tempRoleIdMap.values()) {
				role.compile();
			}
			log.debug("Done compiling roles");
			
			log.debug("Compiling groups");
			for(final AuthorizationGroup group : tempGroupIdMap.values()) {
				group.compile();
			}
			log.debug("Done compiling groups");
			
			log.debug("Compiling users");
			final StopWatch userCompilationSW = new StopWatch();
			userCompilationSW.start();
			for(final AuthorizationUser user : tempUserMap.values()) {
				user.compile();
			}
			userCompilationSW.stop();
			log.debug(String.format("Done compiling users.  Done in: %s ms", userCompilationSW.getTotalTimeMillis()));
			
			synchronized (this) {
				/* CRITICAL SECTION - don't allow reads during write operation */
				//writeLock = readWriteLock.writeLock();
				
				/* find stale keys in User Cache */
				log.debug("In critical section - refreshing user cache");
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
				log.debug("Done refreshing user cache");
				
				/* find stale keys in Login Cache */
				log.debug("Refreshing login cache");
				final Set<String> loginIdKeysToRemove = new HashSet<String>();
				final List<?> loginIdCacheKeys = loginCache.getKeys();
				if(CollectionUtils.isNotEmpty(loginIdCacheKeys)) {
					for(final Object key : loginIdCacheKeys) {
						if(key instanceof String) {
							final String loginIdKey = (String)key;
							if(!tempLoginIdMap.containsKey(loginIdKey)) {
								loginIdKeysToRemove.add(loginIdKey);
							}
						}
					}
				}
				
				/* remove stale keys from Login Cache */
				for(final String loginIdKey : loginIdKeysToRemove) {
					loginCache.remove(loginIdKey);
				}
				
				/* put or override updated keys into Login Cache */
				for(final String loginIdKey : tempLoginIdMap.keySet()) {
					loginCache.put(new Element(loginIdKey, tempLoginIdMap.get(loginIdKey).getUserId()));
				}
				log.info("Done refreshing the login cache");
				
				
				userBitSet = tempUserBitSet;
				roleIdCache = tempRoleIdMap;
				groupIdCache = tempGroupIdMap;
				resourceIdCache = tempResourceIdMap;
				groupNameCache = tempGroupNameMap;
				roleNameCache = tempRoleNameMap;
				resourceNameCache = tempResourceNameMap;
				
				/* END CRITICAL SECTION */
			}
			log.info("Succeeded in refreshing the authorization manager");
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
			log.info(String.format("Refresh (or fail) took %s ms", sw.getTotalTimeMillis()));
		}
	}
	
	private String createLoginIdKey(final String domainId, final String login, final String managedSysId) {
		return createLoginIdKey(new AuthorizationManagerLoginId(domainId, login, managedSysId));
	}
	
	private String createLoginIdKey(final AuthorizationManagerLoginId loginId) {
		clean(loginId);
		return String.format("%s:%s:%s", loginId.getDomain(), loginId.getLogin(), loginId.getManagedSysId());
	}
	
	private void clean(final AuthorizationManagerLoginId loginId) {
		loginId.setDomain(StringUtils.trimToNull(StringUtils.lowerCase(loginId.getDomain())));
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
		final List<Object> keysToRemove = new LinkedList<Object>();
		for(final Object key : userCache.getKeys()) {
			keysToRemove.add((String)loginCache.get(key).getValue());
		}
		for(final Object key : keysToRemove) {
			loginCache.remove(key);
		}
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
		sb.append("Login Cache:").append(lineSeparator);
		for(final Object key : loginCache.getKeys()) {
			sb.append(key).append("=>").append(loginCache.get(key).getValue()).append(lineSeparator);
		}
		
		sb.append(lineSeparator);
		sb.append(lineSeparator);
		sb.append("Done...");
		return sb.toString();
	}
	
	private AuthorizationUser fetchUser(final String userId) {
		AuthorizationUser retVal = getCachedUser(userId);
		if(retVal == null) {
			final InternalAuthroizationUser internalUser = userDAO.getFullUser(userId);
			retVal = process(internalUser);
		}
		return retVal;
	}
	
	private AuthorizationUser fetchUser(final AuthorizationManagerLoginId loginId) {
		AuthorizationUser retVal = getCachedUser(loginId);
		if(retVal == null) {
			final InternalAuthroizationUser internalUser = userDAO.getFullUser(loginId);
			retVal = process(internalUser);
		}
		return retVal;
	}
	
	private AuthorizationUser process(final InternalAuthroizationUser internalUser) {
		AuthorizationUser retVal = null;
		if(internalUser != null) {
			retVal = new AuthorizationUser();
			retVal.setId(internalUser.getUserId());
			if(CollectionUtils.isNotEmpty(internalUser.getGroupIds())) {
				for(final String groupId : internalUser.getGroupIds()) {
					retVal.addGroup(groupIdCache.get(groupId));
				}
			}
			
			if(CollectionUtils.isNotEmpty(internalUser.getRoleIds())) {
				for(final String roleId : internalUser.getRoleIds()) {
					retVal.addRole(roleIdCache.get(roleId));
				}
			}
			
			if(CollectionUtils.isNotEmpty(internalUser.getResourceIds())) {
				for(final String resourceId : internalUser.getResourceIds()) {
					retVal.addResource(resourceIdCache.get(resourceId));
				}
			}
			retVal.compile();
			retVal.setBitSetIdx(userBitSet++);
			userCache.put(new Element(retVal.getId(), retVal));
			
			if(CollectionUtils.isNotEmpty(internalUser.getLoginIds())) {
				final Set<String> duplicateLoginIds = new HashSet<String>();
				for(final AuthorizationManagerLoginId loginId : internalUser.getLoginIds()) {
					final String loginIdKey = createLoginIdKey(loginId);
					if(loginCache.get(loginIdKey) == null) {
						loginCache.put(new Element(createLoginIdKey(loginId), internalUser.getUserId()));
					} else {
						duplicateLoginIds.add(loginIdKey);
						log.error(String.format("Login ID: '%s' has a case-insensitive duplicate, ignoring...", loginIdKey));
					}
				}
				
				for(final String loginIdKey : duplicateLoginIds) {
					loginCache.remove(loginIdKey);
				}
			}
		}
		return retVal;
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
	
	private AuthorizationUser getCachedUser(final AuthorizationManagerLoginId loginId) {
		AuthorizationUser retVal = null;
		final Element cachedLoginId = loginCache.get(createLoginIdKey(loginId));
		if(cachedLoginId != null) {
			final Object cachedLoginValue = cachedLoginId.getValue();
			if(cachedLoginValue != null) {
				final String userId = (String)cachedLoginValue;
				retVal = getCachedUser(userId);
			}
		}
		return retVal;
	}

	@Override
	public boolean isEntitled(final String userId, final AuthorizationResource resource) {
		return isEntitled(fetchUser(userId), resource);
	}

	@Override
	public boolean isEntitled(final AuthorizationManagerLoginId loginId, final AuthorizationResource resource) {
		return isEntitled(fetchUser(loginId), resource);
	}
	
	private boolean isEntitled(final AuthorizationUser user, final AuthorizationResource resource) {
		boolean retVal = false;
		if(user != null && resource != null) {
			AuthorizationResource toCheck = null;
			if(resource.getId() != null) {
				toCheck = resourceIdCache.get(resource.getId());
			} else if(resource.getName() != null) {
				toCheck = resourceNameCache.get(resource.getName());
			}
			retVal = user.isEntitledTo(toCheck);
		}
		return retVal;
	}

	@Override
	public boolean isMemberOf(final String userId, final AuthorizationGroup group) {
		return isMemberOf(fetchUser(userId), group);
	}

	@Override
	public boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationGroup group) {
		return isMemberOf(fetchUser(loginId), group);
	}
	
	private boolean isMemberOf(final AuthorizationUser user, final AuthorizationGroup group) {
		boolean retVal = false;
		if(user != null && group != null) {
			AuthorizationGroup toCheck = null;
			if(group.getId() != null) {
				toCheck = groupIdCache.get(group.getId());
			} else if(group.getName() != null) {
				toCheck = groupNameCache.get(group.getName());
			}
			retVal = user.isMemberOf(toCheck);
		}
		return retVal;
	}

	@Override
	public boolean isMemberOf(final String userId, final AuthorizationRole role) {
		return isMemberOf(fetchUser(userId), role);
	}

	@Override
	public boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationRole role) {
		return isMemberOf(fetchUser(loginId), role);
	}
	
	private boolean isMemberOf(final AuthorizationUser user, final AuthorizationRole role) {
		boolean retVal = false;
		if(user != null && role != null) {
			AuthorizationRole toCheck = null;
			if(role.getId() != null) {
				toCheck = roleIdCache.get(role.getId());
			} else if(role.getName() != null) {
				toCheck = roleNameCache.get(role.getName());
			}
			retVal = user.isMemberOf(toCheck);
		}
		return retVal;
	}

	@Override
	public Set<AuthorizationResource> getResourcesFor(final String userId) {
		return getResorucesFor(fetchUser(userId));
	}

	@Override
	public Set<AuthorizationResource> getResourcesFor(final AuthorizationManagerLoginId loginId) {
		return getResorucesFor(fetchUser(loginId));
	}
	
	private Set<AuthorizationResource> getResorucesFor(final AuthorizationUser user) {
		Set<AuthorizationResource> retVal = new HashSet<AuthorizationResource>();
		if(user != null) {
			final Set<Integer> bitSet = user.getLinearResources();
			final Map<Integer, AuthorizationResource> bitSet2RoleCache = new HashMap<Integer, AuthorizationResource>();
			for(final AuthorizationResource resource : resourceIdCache.values()) {
				bitSet2RoleCache.put(resource.getBitSetIdx(), resource);
			}
			
			for(final Integer bit : bitSet) {
				if(bitSet2RoleCache.containsKey(bit)) {
					final AuthorizationResource resource = bitSet2RoleCache.get(bit);
					final AuthorizationResource copy = new AuthorizationResource();
					copy.setId(resource.getId());
					copy.setName(resource.getName());
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<AuthorizationGroup> getGroupsFor(final String userId) {
		return getGroupsFor(fetchUser(userId));
	}

	@Override
	public Set<AuthorizationGroup> getGroupsFor(final AuthorizationManagerLoginId loginId) {
		return getGroupsFor(fetchUser(loginId));
	}
	
	private Set<AuthorizationGroup> getGroupsFor(final AuthorizationUser user) {
		Set<AuthorizationGroup> retVal = new HashSet<AuthorizationGroup>();
		if(user != null) {
			final Set<Integer> bitSet = user.getLinearGroups();
			final Map<Integer, AuthorizationGroup> bitSet2RoleCache = new HashMap<Integer, AuthorizationGroup>();
			for(final AuthorizationGroup group : groupIdCache.values()) {
				bitSet2RoleCache.put(group.getBitSetIdx(), group);
			}
			
			for(final Integer bit : bitSet) {
				if(bitSet2RoleCache.containsKey(bit)) {
					final AuthorizationGroup group = bitSet2RoleCache.get(bit);
					final AuthorizationGroup copy = new AuthorizationGroup();
					copy.setId(group.getId());
					copy.setName(group.getName());
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<AuthorizationRole> getRolesFor(final String userId) {
		return getRolesFor(fetchUser(userId));
	}

	@Override
	public Set<AuthorizationRole> getRolesFor(final AuthorizationManagerLoginId loginId) {
		return getRolesFor(fetchUser(loginId));
	}
	
	private Set<AuthorizationRole> getRolesFor(final AuthorizationUser user) {
		Set<AuthorizationRole> retVal = new HashSet<AuthorizationRole>();
		if(user != null) {
			final Set<Integer> bitSet = user.getLinearRoles();
			final Map<Integer, AuthorizationRole> bitSet2RoleCache = new HashMap<Integer, AuthorizationRole>();
			for(final AuthorizationRole role : roleIdCache.values()) {
				bitSet2RoleCache.put(role.getBitSetIdx(), role);
			}
			
			for(final Integer bit : bitSet) {
				if(bitSet2RoleCache.containsKey(bit)) {
					final AuthorizationRole role = bitSet2RoleCache.get(bit);
					final AuthorizationRole copy = new AuthorizationRole();
					copy.setId(role.getId());
					copy.setName(role.getName());
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}
	
	/*
	@PreDestroy
	public void destroy() {
		
		forceThreadShutdown = true;
	}
	
	@Override
	public void run() {
		while(true && !forceThreadShutdown) {
			try {
				sweep();
				Thread.sleep(sweepInterval);
			} catch(Throwable e) {
				try {
					Thread.sleep(sweepInterval);
				} catch(Throwable e2) {
					
				}
				log.error("Error while executing thread", e);
			}
		}
	}
	*/

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
		//service.submit(this);
	}
}
