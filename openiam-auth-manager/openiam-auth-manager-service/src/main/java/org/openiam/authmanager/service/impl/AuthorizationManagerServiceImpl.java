package org.openiam.authmanager.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
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
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;
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
    
    @Autowired
    @Qualifier("authManagerCompilationPool")
    private ThreadPoolTaskExecutor authManagerCompilationPool;
	
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
	private Integer userBitSet;
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
	
	private Set<AuthorizationAccessRight> getAccessRight(final AbstractMembershipXrefEntity<?, ?> xref, 
														 final Map<String, AuthorizationAccessRight> rights) {
		Set<AuthorizationAccessRight> retVal = null;
		if(xref != null && CollectionUtils.isNotEmpty(xref.getRights())) {
			retVal = xref.getRights().stream().map(e -> rights.get(e.getId())).collect(Collectors.toSet());
		}
		return retVal;
	}
	
	private Set<AuthorizationAccessRight> getAccessRight(final Set<String> rightIds, 
			 final Map<String, AuthorizationAccessRight> rights) {
		Set<AuthorizationAccessRight> retVal = null;
		if(CollectionUtils.isNotEmpty(rightIds)) {
			retVal = rightIds.stream().map(e -> rights.get(e)).collect(Collectors.toSet());
		}
		return retVal;
	}
	
	private Map<String, Set<MembershipDTO>> getMembershipMapByEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}
	
	private Map<String, Set<MembershipDTO>> getMembershipMapByMemberEntityId(final List<MembershipDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipDTO::getMemberEntityId,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}
	
	private Map<String, Set<String>> getRightMap(final List<MembershipRightDTO> list) {
		return list.stream().collect(Collectors.groupingBy(MembershipRightDTO::getId,
				Collectors.mapping(MembershipRightDTO::getRightId, Collectors.toSet())));
	}
	
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
		
			if(log.isDebugEnabled()) {
				log.debug("Fetching main objects from the database");
				log.debug(String.format("Login threashold date: %s.  Property was: %s", loginThreshold, numOfLoggedInHoursThreshold));
			}
			
			final StopWatch swDB = new StopWatch();
			swDB.start();
			
			/* resources */
			final List<AuthorizationResource> hbmResourceList = membershipDAO.getResources();
			final Map<String, Set<MembershipDTO>> resource2ResourceMap = getMembershipMapByEntityId(membershipDAO.getResource2ResourceMembership());
			final Map<String, Set<String>> resource2ResourceRightMap = getRightMap(membershipDAO.getResource2ResourceRights());
			
			final Map<String, Set<MembershipDTO>> user2ResourceMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2ResourceMembership());
			final Map<String, Set<String>> user2ResourceRightMap = getRightMap(membershipDAO.getUser2ResourceRights());
			
			/* groups */
			final List<AuthorizationGroup> hbmGroupList = membershipDAO.getGroups();
			final Map<String, Set<MembershipDTO>> group2GroupMap = getMembershipMapByEntityId(membershipDAO.getGroup2GroupMembership());
			final Map<String, Set<String>> group2GroupRightMap = getRightMap(membershipDAO.getGroup2GroupRights());
			
			final Map<String, Set<MembershipDTO>> group2UserMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2GroupMembership());
			final Map<String, Set<String>> group2UserRightMap = getRightMap(membershipDAO.getUser2GroupRights());
			
			
			final Map<String, Set<MembershipDTO>> group2ResourceMap = getMembershipMapByEntityId(membershipDAO.getGroup2ResourceMembership());
			final Map<String, Set<String>> group2ResourceRightMap = getRightMap(membershipDAO.getGroup2ResourceRights());
			
			final List<AuthorizationRole> hbmRoleList = membershipDAO.getRoles();
			final Map<String, Set<MembershipDTO>> role2RoleMap = getMembershipMapByEntityId(membershipDAO.getRole2RoleMembership());
			final Map<String, Set<String>> role2RoleRightMap = getRightMap(membershipDAO.getRole2RoleRights());
			
			final Map<String, Set<MembershipDTO>> role2GroupMap = getMembershipMapByEntityId(membershipDAO.getRole2GroupMembership());
			final Map<String, Set<String>> role2GroupRightMap = getRightMap(membershipDAO.getRole2GroupRights());
			
			final Map<String, Set<MembershipDTO>> role2ResourceMap = getMembershipMapByEntityId(membershipDAO.getRole2ResourceMembership());
			final Map<String, Set<String>> role2ResourceRightMap = getRightMap(membershipDAO.getRole2ResourceRights());
			
			final Map<String, Set<MembershipDTO>> user2RoleMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2RoleMembership());
			final Map<String, Set<String>> user2RoleRightMap = getRightMap(membershipDAO.getUser2RoleRights());
			
			final List<AuthorizationOrganization> hbmOrganizationList = membershipDAO.getOrganizations();
			final Map<String, Set<MembershipDTO>> user2OrgMap = getMembershipMapByMemberEntityId(membershipDAO.getUser2OrgMembership());
			final Map<String, Set<String>> user2OrgRightMap = getRightMap(membershipDAO.getUser2OrgRights());
			
			final Map<String, Set<MembershipDTO>> role2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2RoleMembership());
			final Map<String, Set<String>> role2OrgRightMap = getRightMap(membershipDAO.getOrg2RoleRights());
			
			final Map<String, Set<MembershipDTO>> group2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2GroupMembership());
			final Map<String, Set<String>> group2OrgRightMap = getRightMap(membershipDAO.getOrg2GroupRights());
			
			final Map<String, Set<MembershipDTO>> resource2OrgMap = getMembershipMapByEntityId(membershipDAO.getOrg2ResourceMembership());
			final Map<String, Set<String>> resource2OrgRightMap = getRightMap(membershipDAO.getOrg2ResourceRights());
			
			final Map<String, Set<MembershipDTO>> org2Org2Map = getMembershipMapByEntityId(membershipDAO.getOrg2OrgMembership());
			final Map<String, Set<String>> org2OrgRightMap = getRightMap(membershipDAO.getOrg2OrgRights());
			
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.debug(String.format("Time to fetch relationships from the database: %s ms", swDB.getTime()));
			}
			swDB.reset();
			
			swDB.start();
			final List<AuthorizationUser> tempUserList = membershipDAO.getUsers(loginThreshold);
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.info(String.format("Time to fetch users from teh database: %s ms", swDB.getTime()));
			}
			swDB.reset();
			
			final Map<String, AuthorizationAccessRight> tempAccessRightMap = hibernateAccessRightDAO.findAll()
					  .stream()
					  .map(e -> new AuthorizationAccessRight(e, tempAccessRightBitSet.getAndIncrement()))
					  .collect(Collectors.toMap(AuthorizationAccessRight::getId, Function.identity()));
			
			swDB.start();
			final Map<Integer, AuthorizationAccessRight> tempAccessRightBitMap = new HashMap<Integer, AuthorizationAccessRight>();
			tempAccessRightMap.forEach((key, value) -> {
				tempAccessRightBitMap.put(Integer.valueOf(value.getBitIdx()), value);
			});
			
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
					if(CollectionUtils.isNotEmpty(user2ResourceMap.get(entity.getId()))) {
						user2ResourceMap.get(entity.getId()).forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getEntityId());
							if(resource != null) {
								final ResourceUserXref xref = new ResourceUserXref();
								xref.setUser(user);
								xref.setResource(resource);
								xref.setRights(getAccessRight(user2ResourceRightMap.get(e.getId()), tempAccessRightMap));
								user.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(group2UserMap.get(entity.getId()))) {
						group2UserMap.get(entity.getId()).forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getEntityId());
							if(group != null) {
								final GroupUserXref xref = new GroupUserXref();
								xref.setUser(user);
								xref.setGroup(group);
								xref.setRights(getAccessRight(group2UserRightMap.get(e.getId()), tempAccessRightMap));
								user.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(user2RoleMap.get(entity.getId()))) {
						user2RoleMap.get(entity.getId()).forEach(e -> {
							final AuthorizationRole role = tempRoleIdMap.get(e.getEntityId());
							if(role != null) {
								final RoleUserXref xref = new RoleUserXref();
								xref.setUser(user);
								xref.setRole(role);
								xref.setRights(getAccessRight(user2RoleRightMap.get(e.getId()), tempAccessRightMap));
								user.addRole(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(user2OrgMap.get(entity.getId()))) {
						user2OrgMap.get(entity.getId()).forEach(e -> {
							final AuthorizationOrganization organization = tempOrganizationIdMap.get(e.getEntityId());
							if(organization != null) {
								final OrgUserXref xref = new OrgUserXref();
								xref.setUser(user);
								xref.setOrganization(organization);
								xref.setRights(getAccessRight(user2OrgRightMap.get(e.getId()), tempAccessRightMap));
								user.addOrganization(xref);
							}
						});
					}
				}
			});
			
			hbmOrganizationList.forEach(entity -> {
				final AuthorizationOrganization organization = tempOrganizationIdMap.get(entity.getId());
				if(organization != null) {
					if(CollectionUtils.isNotEmpty(resource2OrgMap.get(entity.getId()))) {
						resource2OrgMap.get(entity.getId()).forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntityId());
							if(resource != null) {
								final OrgResourceXref xref = new OrgResourceXref();
								xref.setOrganization(organization);
								xref.setResource(resource);
								xref.setRights(getAccessRight(resource2OrgRightMap.get(e.getId()), tempAccessRightMap));
								organization.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(group2OrgMap.get(entity.getId()))) {
						group2OrgMap.get(entity.getId()).forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getMemberEntityId());
							if(group != null) {
								final OrgGroupXref xref = new OrgGroupXref();
								xref.setOrganization(organization);
								xref.setGroup(group);
								xref.setRights(getAccessRight(group2OrgRightMap.get(e.getId()), tempAccessRightMap));
								organization.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(role2OrgMap.get(entity.getId()))) {
						role2OrgMap.get(entity.getId()).forEach(e -> {
							final AuthorizationRole role = tempRoleIdMap.get(e.getMemberEntityId());
							if(role != null) {
								final OrgRoleXref xref = new OrgRoleXref();
								xref.setOrganization(organization);
								xref.setRole(role);
								xref.setRights(getAccessRight(role2OrgRightMap.get(e.getId()), tempAccessRightMap));
								organization.addRole(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(org2Org2Map.get(entity.getId()))) {
						org2Org2Map.get(entity.getId()).forEach(e -> {
							final AuthorizationOrganization child = tempOrganizationIdMap.get(e.getMemberEntityId());
							if(child != null) {
								final OrgOrgXref xref = new OrgOrgXref();
								xref.setOrganization(organization);
								xref.setMemberOrganization(child);
								xref.setRights(getAccessRight(org2OrgRightMap.get(e.getId()), tempAccessRightMap));
								child.addParentOrganization(xref);
							}
						});
					}
				}
			});
			
			hbmRoleList.forEach(entity -> {
				final AuthorizationRole role = tempRoleIdMap.get(entity.getId());
				if(role != null) {
					if(CollectionUtils.isNotEmpty(role2GroupMap.get(entity.getId()))) {
						role2GroupMap.get(entity.getId()).forEach(e -> {
							final AuthorizationGroup group = tempGroupIdMap.get(e.getMemberEntityId());
							if(group != null) {
								final RoleGroupXref xref = new RoleGroupXref();
								xref.setRole(role);
								xref.setGroup(group);
								xref.setRights(getAccessRight(role2GroupRightMap.get(e.getId()), tempAccessRightMap));
								role.addGroup(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(role2ResourceMap.get(entity.getId()))) {
						role2ResourceMap.get(entity.getId()).forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntityId());
							if(resource != null) {
								final ResourceRoleXref xref = new ResourceRoleXref();
								xref.setRole(role);
								xref.setResource(resource);
								xref.setRights(getAccessRight(role2ResourceRightMap.get(e.getId()), tempAccessRightMap));
								role.addResource(xref);
							}
						});
					}

					if(CollectionUtils.isNotEmpty(role2RoleMap.get(entity.getId()))) {
						role2RoleMap.get(entity.getId()).forEach(e -> {
							final AuthorizationRole child = tempRoleIdMap.get(e.getMemberEntityId());
							if(child != null) {
								final RoleRoleXref xref = new RoleRoleXref();
								xref.setRole(role);
								xref.setMemberRole(child);
								xref.setRights(getAccessRight(role2RoleRightMap.get(e.getId()), tempAccessRightMap));
								child.addParentRole(xref);
							}
						});
					}
				}
			});
			
			hbmGroupList.forEach(entity -> {
				final AuthorizationGroup group = tempGroupIdMap.get(entity.getId());
				if(group != null) {
					if(CollectionUtils.isNotEmpty(group2ResourceMap.get(entity.getId()))) {
						group2ResourceMap.get(entity.getId()).forEach(e -> {
							final AuthorizationResource resource = tempResourceIdMap.get(e.getMemberEntityId());
							if(resource != null) {
								final ResourceGroupXref xref = new ResourceGroupXref();
								xref.setGroup(group);
								xref.setResource(resource);
								xref.setRights(getAccessRight(group2ResourceRightMap.get(e.getId()), tempAccessRightMap));
								group.addResource(xref);
							}
						});
					}
					
					if(CollectionUtils.isNotEmpty(group2GroupMap.get(entity.getId()))) {
						group2GroupMap.get(entity.getId()).forEach(e -> {
							final AuthorizationGroup child = tempGroupIdMap.get(e.getMemberEntityId());
							if(child != null) {
								final GroupGroupXref xref = new GroupGroupXref();
								xref.setGroup(group);
								xref.setMemberGroup(child);
								xref.setRights(getAccessRight(group2GroupRightMap.get(e.getId()), tempAccessRightMap));
								child.addParentGroup(xref);
							}
						});
					}
				}
			});
			
			hbmResourceList.forEach(entity -> {
				final AuthorizationResource resource = tempResourceIdMap.get(entity.getId());
				if(resource != null) {
					if(CollectionUtils.isNotEmpty(resource2ResourceMap.get(entity.getId()))) {
						resource2ResourceMap.get(entity.getId()).forEach(e -> {
							final AuthorizationResource child = tempResourceIdMap.get(e.getMemberEntityId());
							if(child != null) {
								final ResourceResourceXref xref = new ResourceResourceXref();
								xref.setResource(resource);
								xref.setMemberResource(child);
								xref.setRights(getAccessRight(resource2ResourceRightMap.get(e.getId()), tempAccessRightMap));
								child.addParentResoruce(xref);
							}
						});
					}
				}
			});
			swDB.stop();
			if(log.isDebugEnabled()) {
				log.debug(String.format("Time to create xref objects: %s ms", swDB.getTime()));
			}
			swDB.reset();
			
			final int numOfRights = tempAccessRightMap.size();
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
				
				
				userBitSet = tempUserBitSet.get();
				roleIdCache = tempRoleIdMap;
				groupIdCache = tempGroupIdMap;
				resourceIdCache = tempResourceIdMap;
				organizationIdCache = tempOrganizationIdMap;
				accessRightIdCache = tempAccessRightMap;
				accessRightBitCache = tempAccessRightBitMap;
				
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
			final AuthorizationUser retVal = new AuthorizationUser(user);
			if(MapUtils.isNotEmpty(user.getGroups())) {
				user.getGroups().forEach((entityId, rights) -> {
					final AuthorizationGroup entity = groupIdCache.get(entityId);
					if(entity != null) {
						final GroupUserXref xref = new GroupUserXref();
						xref.setGroup(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addGroup(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getRoles())) {
				user.getRoles().forEach((entityId, rights) -> {
					final AuthorizationRole entity = roleIdCache.get(entityId);
					if(entity != null) {
						final RoleUserXref xref = new RoleUserXref();
						xref.setRole(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addRole(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getResources())) {
				user.getResources().forEach((entityId, rights) -> {
					final AuthorizationResource entity = resourceIdCache.get(entityId);
					if(entity != null) {
						final ResourceUserXref xref = new ResourceUserXref();
						xref.setResource(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
						retVal.addResource(xref);
					}
				});
			}
			
			if(MapUtils.isNotEmpty(user.getOrganizations())) {
				user.getOrganizations().forEach((entityId, rights) -> {
					final AuthorizationOrganization entity = organizationIdCache.get(entityId);
					if(entity != null) {
						final OrgUserXref xref = new OrgUserXref();
						xref.setOrganization(entity);
						xref.setUser(retVal);
						xref.setRights(getAccessRight(rights, accessRightIdCache));
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
			
			final int numOfRights = accessRightIdCache.size();
			retVal.compile(numOfRights, -1);
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
