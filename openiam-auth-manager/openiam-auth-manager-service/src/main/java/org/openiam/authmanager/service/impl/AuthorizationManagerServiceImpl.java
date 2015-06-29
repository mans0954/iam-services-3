package org.openiam.authmanager.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.OrgGroupXref;
import org.openiam.authmanager.common.xref.OrgOrgXref;
import org.openiam.authmanager.common.xref.OrgResourceXref;
import org.openiam.authmanager.common.xref.OrgRoleXref;
import org.openiam.authmanager.common.xref.OrgUserXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.common.xref.RoleUserXref;
import org.openiam.authmanager.dao.UserDAO;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
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
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;
	
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
	private Integer userBitSet;
	private Integer accessRightBitSet;
	
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
	
	private Set<AuthorizationAccessRight> getAccessRight(final AbstractMembershipXrefEntity<?, ?> xref, 
														 final Map<String, AuthorizationAccessRight> rights) {
		Set<AuthorizationAccessRight> retVal = null;
		if(xref != null && CollectionUtils.isNotEmpty(xref.getRights())) {
			retVal = xref.getRights().stream().map(e -> rights.get(e.getId())).collect(Collectors.toSet());
		}
		return retVal;
	}
	
	/**
	 * This function sweeps through the database, and compiles the new entitlement mappings
	 */
	@Override
    @ManagedOperation(description="sweep the Authorization Cache")
	@Transactional
	public void sweep() {
		log.info(String.format("Starting Authorization Manager Refresh.  ThreadId: %s.  Spring Context: %s:%s", Thread.currentThread().getId(), ctx.getId(), ctx.getDisplayName()));
		final StopWatch sw = new StopWatch();
		sw.start();
		
		//Lock writeLock = null;
		try {
			/* create temporary bitSets */
			
			/* used AtomicInteger b/c of ability to declare it as final */
			final AtomicInteger tempGroupBitSet = new AtomicInteger(0);
			final AtomicInteger tempRoleBitSet = new AtomicInteger(0);
			final AtomicInteger tempResourceBitSet = new AtomicInteger(0);
			final AtomicInteger tempUserBitSet = new AtomicInteger(0);
			final AtomicInteger tempOrgBitSet = new AtomicInteger(0);
			final AtomicInteger tempAccessRightBitSet = new AtomicInteger(1);
			
			/* create lastLogin Date for user dao */
			final long millisecLoginThreshold = new java.util.Date().getTime() - (numOfLoggedInHoursThreshold.longValue() * 60 * 60 * 1000);
			final Date loginThreshold = new Date(millisecLoginThreshold);
			
			log.debug("Fetching main objects from the database");
			log.debug(String.format("Login threashold date: %s.  Property was: %s", loginThreshold, numOfLoggedInHoursThreshold));
			
			final List<ResourceEntity> hbmResourceList = hibernateResourceDAO.findAll();
			final List<GroupEntity> hbmGroupList = hibernateGroupDAO.findAll();
			final List<RoleEntity> hbmRoleList = hibernateRoleDAO.findAll();
			final List<OrganizationEntity> hbmOrganizationList = hibernateOrgDAO.findAll();
			final List<UserEntity> tempUserList = hbmUserDAO.getAllUsersLoggedInAfter(loginThreshold);
			
			final Map<String, AuthorizationAccessRight> tempAccessRightMap = hibernateAccessRightDAO.findAll()
					  .stream()
					  .map(e -> new AuthorizationAccessRight(e, tempAccessRightBitSet.getAndIncrement()))
					  .collect(Collectors.toMap(AuthorizationAccessRight::getId, Function.identity()));
			
			final Map<String, AuthorizationOrganization> tempOrganizationIdMap = hbmOrganizationList
					.stream()
					.map(e -> new AuthorizationOrganization(e, tempOrgBitSet.getAndIncrement()))
					.collect(Collectors.toMap(AuthorizationOrganization::getId, Function.identity()));
			
			final Map<String, AuthorizationRole> tempRoleIdMap = hbmRoleList
					.stream()
					.map(e -> new AuthorizationRole(e, tempRoleBitSet.getAndIncrement()))
					.collect(Collectors.toMap(AuthorizationRole::getId, Function.identity()));
			
			final Map<String, AuthorizationResource> tempResourceIdMap = hbmResourceList
					.stream()
					.map(e -> new AuthorizationResource(e, tempResourceBitSet.getAndIncrement()))
					.collect(Collectors.toMap(AuthorizationResource::getId, Function.identity()));
			
			final Map<String, AuthorizationGroup> tempGroupIdMap = hbmGroupList
					.stream()
					.map(e -> new AuthorizationGroup(e, tempGroupBitSet.getAndIncrement()))
					.collect(Collectors.toMap(AuthorizationGroup::getId, Function.identity()));
			
			final Map<String, AuthorizationUser> tempUserMap = tempUserList
					.stream()
					.map(e -> new AuthorizationUser(e))
					.collect(Collectors.toMap(AuthorizationUser::getId, Function.identity()));
			
			log.debug("Done fetching main objects");
			
			tempUserList.forEach(entity -> {
				final AuthorizationUser user = tempUserMap.get(entity.getId());
				if(user != null) {
					if(CollectionUtils.isNotEmpty(entity.getResources())) {
						entity.getResources().forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getEntity().getId());
							if(resource != null) {
								final ResourceUserXref xref = new ResourceUserXref();
								xref.setUser(user);
								xref.setResource(resource);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								user.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getGroups())) {
						entity.getGroups().forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getEntity().getId());
							if(group != null) {
								final GroupUserXref xref = new GroupUserXref();
								xref.setUser(user);
								xref.setGroup(group);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								user.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getRoles())) {
						entity.getRoles().forEach(e -> {
							final AuthorizationRole role = tempRoleIdMap.get(e.getEntity().getId());
							if(role != null) {
								final RoleUserXref xref = new RoleUserXref();
								xref.setUser(user);
								xref.setRole(role);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								user.addRole(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getAffiliations())) {
						entity.getAffiliations().forEach(e -> {
							final AuthorizationOrganization organization = tempOrganizationIdMap.get(e.getEntity().getId());
							if(organization != null) {
								final OrgUserXref xref = new OrgUserXref();
								xref.setUser(user);
								xref.setOrganization(organization);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								user.addOrganization(xref);
							}
						});
					}
				}
			});
			
			hbmOrganizationList.forEach(entity -> {
				final AuthorizationOrganization organization = tempOrganizationIdMap.get(entity.getId());
				if(organization != null) {
					if(CollectionUtils.isNotEmpty(entity.getResources())) {
						entity.getResources().forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntity().getId());
							if(resource != null) {
								final OrgResourceXref xref = new OrgResourceXref();
								xref.setOrganization(organization);
								xref.setResource(resource);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								organization.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getGroups())) {
						entity.getGroups().forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getMemberEntity().getId());
							if(group != null) {
								final OrgGroupXref xref = new OrgGroupXref();
								xref.setOrganization(organization);
								xref.setGroup(group);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								organization.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getRoles())) {
						entity.getRoles().forEach(e -> {
							final AuthorizationRole role = tempRoleIdMap.get(e.getMemberEntity().getId());
							if(role != null) {
								final OrgRoleXref xref = new OrgRoleXref();
								xref.setOrganization(organization);
								xref.setRole(role);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								organization.addRole(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getChildOrganizations())) {
						entity.getChildOrganizations().forEach(e -> {
							final AuthorizationOrganization child = tempOrganizationIdMap.get(e.getMemberEntity().getId());
							if(child != null) {
								final OrgOrgXref xref = new OrgOrgXref();
								xref.setOrganization(organization);
								xref.setMemberOrganization(child);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								child.addParentOrganization(xref);
							}
						});
					}
				}
			});
			
			hbmRoleList.forEach(entity -> {
				final AuthorizationRole role = tempRoleIdMap.get(entity.getId());
				if(role != null) {
					if(CollectionUtils.isNotEmpty(entity.getGroups())) {
						entity.getGroups().forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getMemberEntity().getId());
							if(group != null) {
								final RoleGroupXref xref = new RoleGroupXref();
								xref.setRole(role);
								xref.setGroup(group);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								role.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getResources())) {
						entity.getResources().forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntity().getId());
							if(resource != null) {
								final ResourceRoleXref xref = new ResourceRoleXref();
								xref.setRole(role);
								xref.setResource(resource);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								role.addResource(xref);
							}
						});
					}

					if(CollectionUtils.isNotEmpty(entity.getChildRoles())) {
						entity.getChildRoles().forEach(e -> {
							final AuthorizationRole child = tempRoleIdMap.get(e.getMemberEntity().getId());
							if(child != null) {
								final RoleRoleXref xref = new RoleRoleXref();
								xref.setRole(role);
								xref.setMemberRole(child);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								child.addParentRole(xref);
							}
						});
					}
				}
			});
			
			hbmGroupList.forEach(entity -> {
				final AuthorizationGroup group = tempGroupIdMap.get(entity.getId());
				if(group != null) {
					if(CollectionUtils.isNotEmpty(entity.getResources())) {
						entity.getResources().forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntity().getId());
							if(resource != null) {
								final ResourceGroupXref xref = new ResourceGroupXref();
								xref.setGroup(group);
								xref.setResource(resource);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								group.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(entity.getChildGroups())) {
						entity.getChildGroups().forEach(e -> {
							final AuthorizationGroup child = tempGroupIdMap.get(e.getMemberEntity().getId());
							if(child != null) {
								final GroupGroupXref xref = new GroupGroupXref();
								xref.setGroup(group);
								xref.setMemberGroup(child);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								child.addParentGroup(xref);
							}
						});
					}
				}
			});
			
			hbmResourceList.forEach(entity -> {
				final AuthorizationResource resource = tempResourceIdMap.get(entity.getId());
				if(resource != null) {
					if(CollectionUtils.isNotEmpty(entity.getChildResources())) {
						entity.getChildResources().forEach(e -> {
							final AuthorizationResource child = tempResourceIdMap.get(e.getMemberEntity().getId());
							if(child != null) {
								final ResourceResourceXref xref = new ResourceResourceXref();
								xref.setResource(resource);
								xref.setMemberResource(child);
								xref.setRights(getAccessRight(e, tempAccessRightMap));
								child.addParentResoruce(xref);
							}
						});
					}
				}
			});
					
			
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
				
				
				userBitSet = tempUserBitSet.get();
				roleIdCache = tempRoleIdMap;
				groupIdCache = tempGroupIdMap;
				resourceIdCache = tempResourceIdMap;
				organizationIdCache = tempOrganizationIdMap;
				accessRightIdCache = tempAccessRightMap;
				
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
			final UserEntity user = hbmUserDAO.findById(userId);
			retVal = process(user);
		}
		return retVal;
	}
	
	private AuthorizationUser process(final UserEntity user) {
		if(user != null) {
			final AuthorizationUser retVal = new AuthorizationUser(user);
			if(CollectionUtils.isNotEmpty(user.getGroups())) {
				user.getGroups().forEach(e -> {
					final AuthorizationGroup entity = groupIdCache.get(e.getEntity().getId());
					if(entity != null) {
						final GroupUserXref xref = new GroupUserXref();
						xref.setGroup(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(e, accessRightIdCache));
						retVal.addGroup(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(user.getRoles())) {
				user.getRoles().forEach(e -> {
					final AuthorizationRole entity = roleIdCache.get(e.getEntity().getId());
					if(entity != null) {
						final RoleUserXref xref = new RoleUserXref();
						xref.setRole(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(e, accessRightIdCache));
						retVal.addRole(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(user.getResources())) {
				user.getResources().forEach(e -> {
					final AuthorizationResource entity = resourceIdCache.get(e.getEntity().getId());
					if(entity != null) {
						final ResourceUserXref xref = new ResourceUserXref();
						xref.setResource(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(e, accessRightIdCache));
						retVal.addResource(xref);
					}
				});
			}
			
			if(CollectionUtils.isNotEmpty(user.getAffiliations())) {
				user.getAffiliations().forEach(e -> {
					final AuthorizationOrganization entity = organizationIdCache.get(e.getEntity().getId());
					if(entity != null) {
						final OrgUserXref xref = new OrgUserXref();
						xref.setOrganization(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(e, accessRightIdCache));
						retVal.addOrganization(xref);
					}
				});
			}
			
			//NEW:  public resources are really public
			/*
			if(CollectionUtils.isNotEmpty(publicResources)) {
				for(final AuthorizationResource resource : publicResources) {
					retVal.addResource(resource);
				}
			}
			*/
			
			retVal.compile();
			retVal.setBitSetIdx(userBitSet++);
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
		if(user != null && group != null) {
			return user.isMemberOf(group);
		} else {
			return false;
		}
	}
	

	@Override
	public boolean isMemberOfGroup(String userId, String groupId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationGroup group = groupIdCache.get(groupId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		if(user != null && group != null && right != null) {
			return user.isMemberOf(group, right);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationRole role = roleIdCache.get(roleId);
		if(user != null && role != null) {
			return user.isMemberOf(role);
		} else {
			return false;
		}
	}
	

	@Override
	public boolean isMemberOfRole(String userId, String roleId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationRole role = roleIdCache.get(roleId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		if(user != null && role != null && right != null) {
			return user.isMemberOf(role, right);
		} else {
			return false;
		}
	}

	@Override
	public Set<AuthorizationResource> getResourcesForUser(final String userId) {
		return getResorucesFor(fetchUser(userId));
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
					final AuthorizationResource copy = resource.shallowCopy();
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<AuthorizationGroup> getGroupsForUser(final String userId) {
		return getGroupsFor(fetchUser(userId));
	}
	

	@Override
	public Set<AuthorizationOrganization> getOrganizationsForUser(String userId) {
		return getOrganizationsFor(fetchUser(userId));
	}

	private Set<AuthorizationOrganization> getOrganizationsFor(final AuthorizationUser user) {
		Set<AuthorizationOrganization> retVal = new HashSet<AuthorizationOrganization>();
		if(user != null) {
			final Set<Integer> bitSet = user.getLinearOrganizations();
			final Map<Integer, AuthorizationOrganization> bitSet2OrgCache = new HashMap<Integer, AuthorizationOrganization>();
			for(final AuthorizationOrganization org : organizationIdCache.values()) {
				bitSet2OrgCache.put(org.getBitSetIdx(), org);
			}
			
			for(final Integer bit : bitSet) {
				if(bitSet2OrgCache.containsKey(bit)) {
					final AuthorizationOrganization org = bitSet2OrgCache.get(bit);
					final AuthorizationOrganization copy = org.shallowCopy();
					retVal.add(copy);
				}
			}
		}
		return retVal;
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
					final AuthorizationGroup copy = group.shallowCopy();
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}

	@Override
	public Set<AuthorizationRole> getRolesForUser(final String userId) {
		return getRolesFor(fetchUser(userId));
	}

    @Override
    public List<String> getUserIdsList(){
        return hbmUserDAO.getAllIds();
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
					final AuthorizationRole copy = role.shallowCopy();
					retVal.add(copy);
				}
			}
		}
		return retVal;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
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
		if(user != null && resource != null) {
			return user.isEntitledTo(resource);
		} else {
			return false;
		}
	}

	@Override
	public boolean isEntitled(String userId, String resourceId, String rightId) {
		final AuthorizationResource resource = resourceIdCache.get(resourceId);
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		if(user != null && resource != null && right != null) {
			return user.isEntitledTo(resource, right);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationOrganization organization = organizationIdCache.get(organizationId);
		if(user != null && organization != null) {
			return user.isMemberOf(organization);
		} else {
			return false;
		}
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId, String rightId) {
		final AuthorizationUser user = fetchUser(userId);
		final AuthorizationOrganization organization = organizationIdCache.get(organizationId);
		final AuthorizationAccessRight right = accessRightIdCache.get(rightId);
		if(user != null && organization != null && right != null) {
			return user.isMemberOf(organization, right);
		} else {
			return false;
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
