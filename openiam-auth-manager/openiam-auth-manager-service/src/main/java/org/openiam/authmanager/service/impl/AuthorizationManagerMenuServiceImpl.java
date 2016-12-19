package org.openiam.authmanager.service.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.am.srvc.dto.jdbc.AuthorizationResource;
import org.openiam.am.srvc.dto.jdbc.xref.ResourceResourceXref;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.authmanager.dao.ResourcePropDAO;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.model.MenuEntitlementType;
import org.openiam.model.ResourceEntitlementToken;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.membership.MembershipDTO;
import org.openiam.util.AuthorizationConstants;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service("authorizationManagerMenuService")
//@ManagedResource(objectName="org.openiam.authorization.manager:name=authorizationManagerMenuService")
public class AuthorizationManagerMenuServiceImpl extends AbstractBaseService implements AuthorizationManagerMenuService, InitializingBean, ApplicationContextAware/*, Runnable*/ {

	private ApplicationContext ctx;
	
	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuServiceImpl.class);
	
	private static final Comparator<AuthorizationMenu> menuOrderComparator = new AuthorizationMenuOrderComparator();
	
	private Map<String, AuthorizationMenu> menuCache;
	private Map<String, AuthorizationMenu> menuNameCache;
	private Map<String, AuthorizationMenu> urlCache;
	private Map<String, AuthorizationMenu> urlIdCache;
	
	@Autowired
	@Qualifier("jdbcResourcePropDAO")
	private ResourcePropDAO resourcePropDAO;
	
	@Autowired
	@Qualifier("jdbcResourceDAO")
	private org.openiam.authmanager.dao.ResourceDAO resourceDAO;

    @Autowired
    private UserDAO userDAOHibernate;
    @Autowired
    private GroupDAO groupDAOHibernate;
    @Autowired
    private RoleDAO roleDAOHibernate;
    @Autowired
    private org.openiam.idm.srvc.res.service.ResourceDAO resourceDAOHibernate;
    @Autowired
    private OrganizationDAO organizationDAO;

	@Autowired
	private AuthorizationManagerService authManager;
	
	@Autowired
	private LanguageMappingDAO languageMappingDAO;
	
	@Autowired
	private MembershipDAO membershipDAO;
	
	@Autowired
	private AuthorizationManagerAdminService authManagerAdminService;
	
	 @Autowired
	 @Qualifier("transactionTemplate")
	 private TransactionTemplate transactionTemplate;
	 
	 @Autowired
	 private AccessRightDAO accessRightDAO;

	@Autowired
	private ResourceService resourceService;
	 
	 @Autowired

	/*
	private boolean forceThreadShutdown = false;
	
	@Value("${org.openiam.authorization.manager.threadsweep}")
	private long sweepInterval;
	
	*/
	
	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	@ManagedOperation(description="Print Menu Tree for a User")
	public String printMenuTree(final String rootName, final String userId) {
		final StringBuilder sb = new StringBuilder();
		try {
			if(rootName != null) {
				final AuthorizationMenu root = getMenuTree(rootName, userId);
				if(root == null) {
					sb.append(String.format("No menu with root '%s'", rootName));
				} else {
					sb.append(root.toString(0));
				}
			}
		} catch(Throwable e) {
			log.error("Exception", e);
			sb.append(e.getMessage());
		}
		
		return sb.toString();
	}
	
	@ManagedOperation(description="Print Menu Tree")
	public String printMenuTree(final String rootName) {
		final StringBuilder sb = new StringBuilder();
		if(rootName != null) {
			final AuthorizationMenu root = menuCache.get(rootName);
			if(root == null) {
				sb.append(String.format("No menu with root '%s'", rootName));
			} else {
				sb.append(root.toString(0));
			}
		}
		
		return sb.toString();
	}
	
	@Override
	@Transactional(readOnly = true)
	public AuthorizationMenu getMenuTree(final String menuId) {
		return getAllMenuTress().get(menuId);
	}
	
	private void doEntitlementsCheck(final AuthorizationMenu menu, final ResourceEntitlementToken token) {
		if(menu != null && token != null) {
			if(token.isDirect(menu.getId())) {
				menu.addEntitlementType(MenuEntitlementType.EXPLICIT);
			}
			
			if(token.isIndirect(menu.getId())) {
				menu.addEntitlementType(MenuEntitlementType.IMPLICIT);
			}
			
			doEntitlementsCheck(menu.getFirstChild(), token);
			
			AuthorizationMenu next = menu.getNextSibling();
			while(next != null) {
				doEntitlementsCheck(next, token);
				next = next.getNextSibling();
			}
		}
	}
	@Transactional(readOnly = true)
	private final Map<String, AuthorizationMenu> getAllMenuTress() {
		final List<AuthorizationMenu> tempMenuList = resourceDAO.getAuthorizationMenus();		
		final List<ResourceProp> tempResourcePropertyList = resourcePropDAO.getList();
		
		final Map<String, List<ResourceProp>> tempResourcePropMap = new HashMap<String, List<ResourceProp>>();
		for(final ResourceProp prop : tempResourcePropertyList) {
			if(!tempResourcePropMap.containsKey(prop.getResourceId())) {
				tempResourcePropMap.put(prop.getResourceId(), new LinkedList<ResourceProp>());
			}
			tempResourcePropMap.get(prop.getResourceId()).add(prop);
		}
		
		final Set<String> idSet = new HashSet<>();
		if(CollectionUtils.isNotEmpty(tempMenuList)) {
			for(final AuthorizationMenu menu : tempMenuList) {
				idSet.add(menu.getId());
			}
		}
		final List<LanguageMappingEntity> languageMappings = languageMappingDAO.getByReferenceIdsAndType(idSet, "ResourceEntity.displayNameMap");
		final Map<String, List<LanguageMappingEntity>> languageMappingMap = new HashMap<String, List<LanguageMappingEntity>>();
		if(CollectionUtils.isNotEmpty(languageMappings)) {
			for(final LanguageMappingEntity mapping : languageMappings) {
				if(!languageMappingMap.containsKey(mapping.getReferenceId())) {
					languageMappingMap.put(mapping.getReferenceId(), new LinkedList<LanguageMappingEntity>());
				}
				languageMappingMap.get(mapping.getReferenceId()).add(mapping);
			}
		}
		
		final Map<String, AuthorizationMenu> tempMenuMap = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuList) {
			tempMenuMap.put(menu.getId(), menu);
			menu.afterPropertiesSet(tempResourcePropMap.get(menu.getId()), languageMappingMap.get(menu.getId()));
		}
		final Map<String, AuthorizationMenu> tempMenuTreeMap = createMenuTrees(tempMenuMap);
		return tempMenuTreeMap;
	}
	
	private String getURLCacheId(final String id, final String url) {
		return String.format("%s:%s", id, url);
	}
	
	@Override
	@Transactional
	@Scheduled(fixedRateString="${org.openiam.authorization.manager.threadsweep}", initialDelayString="${org.openiam.authorization.manager.threadsweep}")
    //@ManagedOperation(description="sweep the Menu Cache")
	public void sweep() {
		final StopWatch sw = new StopWatch();
		sw.start();
		final Map<String, AuthorizationMenu> tempMenuTreeMap = getAllMenuTress();
		
		final Map<String, AuthorizationMenu> tempMenuNameMap = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuTreeMap.values()) {
			tempMenuNameMap.put(menu.getName(), menu);
		}
		
		final Map<String, AuthorizationMenu> tempURLIDCache = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuTreeMap.values()) {
			tempURLIDCache.put(getURLCacheId(menu.getId(), menu.getUrl()), menu);
		}
		
		final Map<String, AuthorizationMenu> tempUrlCache = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuTreeMap.values()) {
			tempUrlCache.put(menu.getUrl(), menu);
		}
		
		synchronized(this) {
			menuCache = tempMenuTreeMap;
			menuNameCache = tempMenuNameMap;
			urlIdCache = tempURLIDCache;
			urlCache = tempUrlCache;
		}
		sw.stop();
		if(log.isDebugEnabled()) {
			log.debug(String.format("Done creating menu trees. Took: %s ms", sw.getTime()));
		}
	}
	
	private Map<String, AuthorizationMenu> createMenuTrees(final Map<String, AuthorizationMenu> menuMap) {
		
		final List<MembershipDTO> resource2ResourceMap = membershipDAO.getResource2ResourceMembership(new Date());
		
		
		final Map<String, String> childResource2ParentResourceMap = new HashMap<String, String>();
		final Map<String, Set<String>> parentResource2ChildResourceMap = new HashMap<String, Set<String>>();
		resource2ResourceMap.forEach(resource -> {
			final String resourceId = resource.getEntityId();
			final String memberResourceId = resource.getMemberEntityId();
		
			if(!parentResource2ChildResourceMap.containsKey(resourceId)) {
				parentResource2ChildResourceMap.put(resourceId, new HashSet<String>());
			}
			childResource2ParentResourceMap.put(memberResourceId, resourceId);
			parentResource2ChildResourceMap.get(resourceId).add(memberResourceId);
		});
		
		/* create a HashMap structure that mimicks a tree */
		final Map<AuthorizationMenu, TreeSet<AuthorizationMenu>> menu2ChildMap = new HashMap<AuthorizationMenu, TreeSet<AuthorizationMenu>>();
		for(final AuthorizationMenu menu : menuMap.values()) {
			final TreeSet<AuthorizationMenu> menuChildren = new TreeSet<AuthorizationMenu>(menuOrderComparator);
			final Set<String> childResourceIds = parentResource2ChildResourceMap.get(menu.getId());
			
			/* set the children and parent Menu */
			if(CollectionUtils.isNotEmpty(childResourceIds)) {
				for(final String childResourceId : childResourceIds) {
					final AuthorizationMenu childMenu = menuMap.get(childResourceId);
					if(childMenu != null) {
						
						/* add child to the TreeSet */
						menuChildren.add(childMenu);
						
						/* set parent Menu */
						childMenu.setParent(menu);
					}
				}
			}
			
			if(menuChildren.size() > 0) {
				
				/* set the first child reference */
				menu.setFirstChild(menuChildren.first());
				
				/* set the 'next' pointer on each child */
				AuthorizationMenu prev = null;
				for(final Iterator<AuthorizationMenu> it = menuChildren.iterator(); it.hasNext();) {
					final AuthorizationMenu next = it.next();
					if(prev != null) {
						prev.setNextSibling(next);
					}
					prev = next;
				}
			}
			
			menu2ChildMap.put(menu, menuChildren);
		}
		
		/* at this point, all references point to the correct object.  Simply find the roots now */
		final Map<String, AuthorizationMenu> menuTreeMap = new HashMap<String, AuthorizationMenu>();
		for(final String menuId : menuMap.keySet()) {
			final AuthorizationMenu menu = menuMap.get(menuId);
			menuTreeMap.put(menu.getId(), menu);
		}
		
		return menuTreeMap;
	}
	
	@Override
	public AuthorizationMenu getMenuTree(final String menuRoot, final String userId) {
		return getMenu(menuCache.get(menuRoot), userId, true);
	}

	@Override
    public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId) {
		return getMenu(menuNameCache.get(menuRoot), userId, true);
	}
	
	private AuthorizationMenu getMenu(final AuthorizationMenu menu, final String userId, final boolean isRoot) {
		AuthorizationMenu retVal = null;
		if(menu != null) {
			//make copy
			AuthorizationMenu parent = menu.copy();
			final AuthorizationMenu child = getMenu(menu.getFirstChild(), userId, false);
			if(child != null) {
				parent.setFirstChild(child);
				child.setParent(parent);
			}
			
			final AuthorizationMenu next = getMenu(menu.getNextSibling(), userId, false);
			if(next != null) {
				parent.setNextSibling(next);
			}
			
			retVal = parent;
			//end copy
			
			if(isRoot) {
				if(!hasAccess(retVal, userId)) {
					retVal = null;
				} else {
					removeUnauthorizedMenus(retVal, userId, true);
				}
			}
		}
		return retVal;
	}
	
	private List<AuthorizationMenu> getAuthorizedSiblings(final AuthorizationMenu menu, final String userId) {
		final List<AuthorizationMenu> nextMenus = new LinkedList<>();
		if(menu != null) {
			AuthorizationMenu nextSibling = menu.getNextSibling();
			while(nextSibling != null) {
				if(hasAccess(nextSibling, userId)) {
					removeUnauthorizedMenus(nextSibling, userId, false);
					nextMenus.add(nextSibling);
				}
				nextSibling = nextSibling.getNextSibling();
			}
		}
		return nextMenus;
	}
	
	private AuthorizationMenu getFirstAuthorizedChild(final AuthorizationMenu menu, final String userId) {
		AuthorizationMenu authorizedChild = null;
		if(menu != null) {
			AuthorizationMenu child = menu.getFirstChild();
			while(child != null) {
				if(hasAccess(child, userId)) {
					authorizedChild = child;
					break;
				}
				child = child.getNextSibling();
			}
		}
		return authorizedChild;
	}
	
	private void removeUnauthorizedMenus(final AuthorizationMenu menu, final String userId, final boolean checkNext) {
		final AuthorizationMenu child = getFirstAuthorizedChild(menu, userId);
		if(child != null) {
			menu.setFirstChild(child);
			removeUnauthorizedMenus(menu.getFirstChild(), userId, true);
		} else {
			menu.setFirstChild(null);
		}
		/*
		if(menu.getFirstChild() != null) {
			if(!hasAccess(menu.getFirstChild(), userId, loginId)) {
				menu.setFirstChild(null);
			} else {
				removeUnauthorizedMenus(menu.getFirstChild(), userId, loginId, true);
			}
		}
		*/
		
		if(checkNext) {
			final List<AuthorizationMenu> nextMenus = getAuthorizedSiblings(menu, userId);
			
			if(CollectionUtils.isNotEmpty(nextMenus)) {
				AuthorizationMenu previous = null;
				for(final AuthorizationMenu next : nextMenus) {
					next.setNextSibling(null);
					if(previous != null) {
						previous.setNextSibling(next);
					}
					previous = next;
				}
				menu.setNextSibling(nextMenus.get(0));
			} else {
				menu.setNextSibling(null);
			}
		}
	}
	
	private boolean hasAccess(final AuthorizationMenu menu, final String userId) {
		final StopWatch sw = new StopWatch();
		sw.start();
		boolean retVal = false;
		if(menu.getIsPublic()) {
			retVal = true;
		} else {
			retVal = authManager.isEntitled(userId, menu.getId());
		}
		sw.stop();
		if(log.isInfoEnabled()) {
			//log.info(String.format("hasAccess took %s ms", sw.getTime()));
		}
		return retVal;
	}

	private static class AuthorizationMenuOrderComparator implements Comparator<AuthorizationMenu> {

		@Override
		public int compare(final AuthorizationMenu o1, final AuthorizationMenu o2) {
			if(o1 == null || o2 == null) {
				throw new IllegalArgumentException("Authorization menus can't be null");
			}
			
			if(o1.getDisplayOrder() == null && o2.getDisplayOrder() == null) {
				return o1.getName().compareTo(o2.getName());
			} else if(o1.getDisplayOrder() != null && o2.getDisplayOrder() == null) {
				return 1;
			} else if(o1.getDisplayOrder() == null && o2.getDisplayOrder() != null) {
				return -1;
			/* having this return '0' will override a particular menu - guard against this */
			} else if(o1.getDisplayOrder().compareTo(o2.getDisplayOrder()) != 0) {
				return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
			/* display orders are equal - just place at end of DS */
			} else {
				return -1;
			}
		}
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
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				sweep();
				return null;
			}
		});
	}

	@Override
	@Transactional
	public void processTreeUpdate(List<ResourceEntity> toSave, List<ResourceEntity> toUpdate, List<ResourceEntity> toDelete) {
		if(CollectionUtils.isNotEmpty(toSave)) {
			resourceDAOHibernate.save(toSave);
		}
		
		if(CollectionUtils.isNotEmpty(toUpdate)) {
			resourceDAOHibernate.save(toUpdate);
		}
		
		if(CollectionUtils.isNotEmpty(toDelete)) {
			for(final ResourceEntity resource : toDelete) {
				resourceDAOHibernate.delete(resource);
			}
		}
	}

    @Override
    @Transactional
    public void entitle(MenuEntitlementsRequest menuEntitlementsRequest)  throws BasicDataServiceException {
		if(menuEntitlementsRequest == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Request is null");
		}

        final String principalType = menuEntitlementsRequest.getPrincipalType();
        final String principalId = menuEntitlementsRequest.getPrincipalId();
		if(StringUtils.isBlank(principalType) || StringUtils.isBlank(principalId)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "PrincipalId or principalType is null");
		}

        if(StringUtils.equalsIgnoreCase("user", principalType)) {
        	final UserEntity userEntity = userDAOHibernate.findById(principalId);
            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getDisentitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getDisentitled());
                for(final ResourceEntity resourceEntity : resourceEntities) {
                	resourceEntity.removeUser(userEntity);
                }

            }

            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getNewlyEntitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getNewlyEntitled());
                for(final ResourceEntity resourceEntity : resourceEntities) {
                	resourceEntity.addUser(userEntity, null, null);
                }

            }
        } else if(StringUtils.equalsIgnoreCase("group", principalType)) {
        	final GroupEntity groupEntity = groupDAOHibernate.findById(principalId);

            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getDisentitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getDisentitled());
                for(final ResourceEntity resourceEntity : resourceEntities) {
                	groupEntity.removeResource(resourceEntity);
                }
            }

            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getNewlyEntitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getNewlyEntitled());
                for(final ResourceEntity resource : resourceEntities) {
                    groupEntity.addResource(resource, null, null, null);
                }

            }
        } else if(StringUtils.equalsIgnoreCase("role", principalType)) {
            final RoleEntity role = roleDAOHibernate.findById(principalId);

            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getDisentitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getDisentitled());
                for(final ResourceEntity resourceEntity : resourceEntities) {
                	role.removeResource(resourceEntity);
                }

            }

            if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getNewlyEntitled())) {
            	final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getNewlyEntitled());
                for(final ResourceEntity resource : resourceEntities) {
                	role.addResource(resource, null, null, null);
                }
            }
        } else if(StringUtils.equalsIgnoreCase("organization", principalType)) {
        	final OrganizationEntity organization = organizationDAO.findById(principalId);
        	
        	 if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getDisentitled())) {
        		 final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getDisentitled());
                 for(final ResourceEntity resourceEntity : resourceEntities) {
                	 organization.removeResource(resourceEntity);
                 }
        	 }
        	
        	 if(CollectionUtils.isNotEmpty(menuEntitlementsRequest.getNewlyEntitled())) {
                 final List<ResourceEntity> resourceEntities = resourceDAOHibernate.findByIds(menuEntitlementsRequest.getNewlyEntitled());
                 for(final ResourceEntity resource : resourceEntities) {
                	 organization.addResource(resource, null, null, null);
                 }
        	 }
        }
    }

    @Override
    public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult) {
        boolean retVal = defaultResult;
        final AuthorizationMenu menu = StringUtils.isNotBlank(menuId) ? urlIdCache.get(getURLCacheId(menuId, url)) : urlCache.get(url);
        if (menu != null) {
            if (menu.getIsPublic()) {
                retVal = true;
            } else {
                retVal = authManager.isEntitled(userId, menu.getId());
            }
        }

        return retVal;
    }
	@Transactional(readOnly = true)
	public AuthorizationMenu getMenuTreeForUserId(final String menuId, final String menuName, final String userId, final Language language){
		final StopWatch sw = new StopWatch();
		sw.start();
		AuthorizationMenu retVal = null;
		if(StringUtils.isNotEmpty(userId)) {
			if(StringUtils.isNotEmpty(menuId)) {
				retVal = this.getMenuTree(menuId, userId);
			} else {
				retVal = this.getMenuTreeByName(menuName, userId);
			}
		}
		sw.stop();
		if(log.isInfoEnabled()) {
			log.info(String.format("getMenuTreeForUserId: {menuId: %s, menuName: %s, userId: %s}, time: %s ms", menuId, menuName, userId, sw.getTime()));
		}
		localize(retVal, language);
		return retVal;
	}
	@Transactional(readOnly = true)
	public AuthorizationMenu getMenuTree(final String menuId, final Language language){
		final AuthorizationMenu menu = this.getMenuTree(menuId);
		localize(menu, language);
		return menu;
	}
	@Transactional(readOnly = true)
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType, final Language language){
		final AuthorizationMenu menu = getMenuTree(menuId);
		ResourceEntitlementToken token = null;
		final Date now = new Date();
		if(menu != null) {
			if(StringUtils.equalsIgnoreCase("user", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForUser(principalId, now);
			} else if(StringUtils.equalsIgnoreCase("group", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForGroup(principalId, now);
			} else if(StringUtils.equalsIgnoreCase("role", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForRole(principalId, now);
			} else if(StringUtils.equalsIgnoreCase("organization", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForOrganization(principalId, now);
			}
		}

		doEntitlementsCheck(menu, token);

		localize(menu, language);
		return menu;
	}
	@Transactional
	public void deleteMenuTree(final String menuId)  throws AuthorizationMenuException{
		final ResourceEntity resource = resourceService.findResourceById(menuId);

		if(resource == null) {
			throw new AuthorizationMenuException(ResponseCode.MENU_DOES_NOT_EXIST, menuId);
		}

		if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
			throw new AuthorizationMenuException(ResponseCode.HANGING_CHILDREN, resource.getName());
		}

		/*
		if(CollectionUtils.isNotEmpty(resource.getEntitlements())) {
			throw new AuthorizationMenuException(MenuError.HANGING_ENTITLEMENTS, resource.getName());
		}
		*/
		resourceService.deleteResource(menuId);
	}
	@Transactional
	public void saveMenuTree(final AuthorizationMenu root) throws AuthorizationMenuException{
		setParents(null, root);
		final AuthorizationMenu currentRoot = this.getMenuTree(root.getId());

		final List<AuthorizationMenu> changedMenus = new LinkedList<>();
		final List<AuthorizationMenu> newMenus = new LinkedList<>();
		final List<AuthorizationMenu> deletedMenus = new LinkedList<>();

		//final List<ResourceResourceXref> newXrefs = new LinkedList<>();
		final Map<String, String> newResourceName2ParentIdMap = new HashMap<>();
		final List<ResourceResourceXref> deletedXrefs = new LinkedList<>();
		if(currentRoot != null) {
			/* put existing menus in a map */
			final List<AuthorizationMenu> currentMenus = getMenus(currentRoot);
			final Map<String, AuthorizationMenu> currentMenuMap = new HashMap<>();
			for(final AuthorizationMenu menu : currentMenus) {
				currentMenuMap.put(menu.getId(), menu);
			}

			/* put incoming menus in a map */
			final List<AuthorizationMenu> incomingMenus = getMenus(root);
			final Map<String, AuthorizationMenu> incomingMenuMap = new HashMap<>();
			for(final AuthorizationMenu menu : incomingMenus) {
				if(StringUtils.isNotBlank(menu.getId())) {
					incomingMenuMap.put(menu.getId(), menu);
				}
			}

			/* find new entries */
			for(final AuthorizationMenu menu : incomingMenus) {
				if(StringUtils.isEmpty(menu.getId())) {
					newMenus.add(menu);
					if(menu.getParent() != null) {
						newResourceName2ParentIdMap.put(menu.getName(), menu.getParent().getId());
					}
				}
			}

			/* find deleted entries */
			for(final String currentId : currentMenuMap.keySet()) {
				if(!incomingMenuMap.containsKey(currentId)) {
					final AuthorizationMenu currentMenu = currentMenuMap.get(currentId);
					deletedMenus.add(currentMenu);
					if(currentMenu.getParent() != null) {
						final ResourceResourceXref xref = new ResourceResourceXref();
						xref.setResource(new AuthorizationResource(currentMenu.getParent()));
						xref.setMemberResource(new AuthorizationResource(currentMenu));
						deletedXrefs.add(xref);
					}
				}
			}

			/* find changed resources */
			for(final AuthorizationMenu menu : incomingMenus) {
				if(StringUtils.isNotEmpty(menu.getId())) {
					if(!menu.equals(currentMenuMap.get(menu.getId()))) {
						changedMenus.add(menu);
					}
				}
			}

			final List<ResourceEntity> resourcesToCreate = new LinkedList<>();
			final List<ResourceEntity> resourcesToUpdate = new LinkedList<>();
			final List<ResourceEntity> resourcesToDelete = new LinkedList<>();

			/* mark menus for deletion */
			if(CollectionUtils.isNotEmpty(deletedMenus)) {
				final List<ResourceEntity> deletedResourceList = new LinkedList<>();
				for(final AuthorizationMenu menu : deletedMenus) {
					final ResourceEntity resource = resourceService.findResourceById(menu.getId());
					if(resource != null) {
						deletedResourceList.add(resource);
					}
				}
				resourcesToDelete.addAll(deletedResourceList);
			}

			/* return error if Resource has Collections on it */
			if(CollectionUtils.isNotEmpty(resourcesToDelete)) {
				for(final ResourceEntity resource : resourcesToDelete) {
					if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
						throw new AuthorizationMenuException(ResponseCode.HANGING_CHILDREN, resource.getName());
					}

					/*
					if(CollectionUtils.isNotEmpty(resource.getEntitlements())) {
						throw new AuthorizationMenuException(MenuError.HANGING_ENTITLEMENTS, resource.getName());
					}
					*/

				}
			}

			/* Find Menus to udpate */
			if(CollectionUtils.isNotEmpty(changedMenus)) {
				final Map<String, AuthorizationMenu> changedMenuMap = new HashMap<>();
				for(final AuthorizationMenu menu : changedMenus) {
					changedMenuMap.put(menu.getId(), menu);
				}
				final List<ResourceEntity> resourceList = resourceService.findResourcesByIds(changedMenuMap.keySet());
				for(final ResourceEntity resource : resourceList) {
					final AuthorizationMenu menu = changedMenuMap.get(resource.getId());

					final ResourceEntity existingResource = resourceService.findResourceByName(menu.getName());
					/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
					if(existingResource != null && !existingResource.getId().equals(resource.getId())) {
						throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
					}

					merge(resource, menu);
				}
				resourcesToUpdate.addAll(resourceList);
			}

			/* find Menus to create */
			if(CollectionUtils.isNotEmpty(newMenus)) {
				final List<ResourceEntity> newResourceList = new LinkedList<>();
				for(final AuthorizationMenu menu : newMenus) {
					final ResourceEntity resource = createResource(menu);
					newResourceList.add(resource);

					final ResourceEntity existingResource = resourceService.findResourceByName(resource.getName());
					/* check that, if the user changed the name of the menu, it doesn't conflict with another resource with the same name */
					if(existingResource != null) {
						throw new AuthorizationMenuException(ResponseCode.NAME_TAKEN, resource.getName());
					}
				}
				resourcesToCreate.addAll(newResourceList);
			}

			/* create the maps */
			final Map<String, ResourceEntity> resourcesToUpdateMap = new HashMap<>();
			final Map<String, ResourceEntity> resourceToCreateMap = new HashMap<>();
			final Map<String, ResourceEntity> resourceToDeleteMap = new HashMap<>();
			for(final ResourceEntity resource : resourcesToUpdate) {
				resourcesToUpdateMap.put(resource.getId(), resource);
			}
			for(final ResourceEntity resource : resourcesToCreate) {
				resourceToCreateMap.put(resource.getId(), resource);
			}
			for(final ResourceEntity resource : resourcesToDelete) {
				resourceToDeleteMap.put(resource.getId(), resource);
			}

			/* set new xrefs, if any */
			if(CollectionUtils.isNotEmpty(resourcesToCreate)) {
				for(final ResourceEntity resource : resourcesToCreate) {
					final String parentId = newResourceName2ParentIdMap.get(resource.getName());
					if(!resourcesToUpdateMap.containsKey(parentId)) {
						final ResourceEntity parent = resourceService.findResourceById(parentId);
						resourcesToUpdateMap.put(parentId, parent);
					}
					final ResourceEntity parent = resourcesToUpdateMap.get(parentId);
					parent.addChildResource(resource, null, null, null);
				}
			}

			/* remove old xrefs, if any */
			if(CollectionUtils.isNotEmpty(deletedXrefs)) {
				for(final ResourceResourceXref xref : deletedXrefs) {
					if(!resourcesToUpdateMap.containsKey(xref.getResource().getId())) {
						final ResourceEntity resource = resourceService.findResourceById(xref.getResource().getId());
						resourcesToUpdateMap.put(resource.getId(), resource);
					}
					final ResourceEntity resource = resourcesToUpdateMap.get(xref.getResource().getId());
					final ResourceEntity toDelete = resourceToDeleteMap.get(xref.getMemberResource().getId());
					resource.removeChildResource(toDelete);
				}
			}
			this.getProxyService().processTreeUpdate(resourcesToCreate, resourcesToUpdate, resourcesToDelete);
		}
	}

	private ResourceEntity createResource(final AuthorizationMenu menu) {
		final ResourceEntity resource = new ResourceEntity();
		resource.setURL(menu.getUrl());
		resource.setName(menu.getName());
		resource.setDisplayOrder(menu.getDisplayOrder());
		if(StringUtils.isNotBlank(menu.getRisk())){
			resource.setRisk(ResourceRisk.valueOf(menu.getRisk()));
		}
		resource.setIsPublic(menu.getIsPublic());
		resource.setResourceType(resourceService.findResourceTypeById(AuthorizationConstants.MENU_ITEM_RESOURCE_TYPE));
		resource.setDisplayNameMap(convert(menu.getDisplayNameMap()));
		/*
		final ResourcePropEntity displayNameProp = new ResourcePropEntity();
		displayNameProp.setResource(resource);
		displayNameProp.setName(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
		displayNameProp.setPropValue(menu.getDisplayName());
		resource.addResourceProperty(displayNameProp);
		*/

		final ResourcePropEntity iconProp = new ResourcePropEntity();
		iconProp.setResource(resource);
		iconProp.setName(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
		iconProp.setValue(menu.getIcon());
		resource.addResourceProperty(iconProp);

		final ResourcePropEntity visibleProp = new ResourcePropEntity();
		visibleProp.setResource(resource);
		visibleProp.setName(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);
		visibleProp.setValue(Boolean.valueOf(menu.getIsVisible()).toString());
		resource.addResourceProperty(visibleProp);

		return resource;
	}

	private void merge(final ResourceEntity resource, final AuthorizationMenu menu) {
		resource.setURL(menu.getUrl());
		resource.setName(menu.getName());
		resource.setDisplayOrder(menu.getDisplayOrder());
		resource.setRisk(ResourceRisk.getByValue(menu.getRisk()));
		resource.setIsPublic(menu.getIsPublic());
		resource.setDisplayNameMap(convert(menu.getDisplayNameMap()));

		//ResourcePropEntity displayNameProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
		ResourcePropEntity iconProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
		ResourcePropEntity visibleProp = resource.getResourceProperty(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);

		if(visibleProp != null) {
			visibleProp.setValue(Boolean.valueOf(menu.getIsVisible()).toString());
		} else {
			visibleProp = new ResourcePropEntity();
			visibleProp.setResource(resource);
			visibleProp.setName(AuthorizationConstants.MENU_ITEM_IS_VISIBLE);
			visibleProp.setValue(Boolean.valueOf(menu.getIsVisible()).toString());
			resource.addResourceProperty(visibleProp);
		}

		/*
		if(displayNameProp != null) {
			displayNameProp.setPropValue(menu.getDisplayName());
		} else {
			displayNameProp = new ResourcePropEntity();
			displayNameProp.setResource(resource);
			displayNameProp.setName(AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY);
			displayNameProp.setPropValue(menu.getDisplayName());
			resource.addResourceProperty(displayNameProp);
		}
		*/

		if(iconProp != null) {
			iconProp.setValue(menu.getIcon());
		} else {
			iconProp = new ResourcePropEntity();
			iconProp.setResource(resource);
			iconProp.setName(AuthorizationConstants.MENU_ITEM_ICON_PROPERTY);
			iconProp.setValue(menu.getIcon());
			resource.addResourceProperty(iconProp);
		}
	}

	private List<AuthorizationMenu> getMenus(final AuthorizationMenu menu) {
		final List<AuthorizationMenu> authMenus = new LinkedList<AuthorizationMenu>();
		if(menu != null) {
			authMenus.addAll(getMenus(menu.getFirstChild()));
			authMenus.addAll(getMenus(menu.getNextSibling()));
			authMenus.add(menu);
		}
		return authMenus;
	}

	private void setParents(final AuthorizationMenu parent, final AuthorizationMenu menu) {
		if(menu != null) {
			menu.setParent(parent);

			if(menu.getFirstChild() != null) {
				setParents(menu, menu.getFirstChild());
			}

			if(menu.getNextSibling() != null) {
				setParents(parent, menu.getNextSibling());
			}
		}
	}
	private Map<String, LanguageMappingEntity> convert(final Map<String, LanguageMapping> transientMap) {
		final Map<String, LanguageMappingEntity> convertedMap = new HashMap<>();
		if(MapUtils.isNotEmpty(transientMap)) {
			for(final String key : transientMap.keySet()) {
				final LanguageMapping mapping = transientMap.get(key);
				final LanguageMappingEntity entity = new LanguageMappingEntity();
				entity.setId(mapping.getId());
				entity.setLanguageId(mapping.getLanguageId());
				entity.setReferenceId(mapping.getReferenceId());
				entity.setReferenceType(mapping.getReferenceType());
				entity.setValue(mapping.getValue());
				convertedMap.put(key, entity);
			}
		}
		return convertedMap;
	}

	private void localize(final AuthorizationMenu menu, final Language language) {
		if(menu != null && language != null) {
			menu.localize(language);

			AuthorizationMenu sibling = menu.getNextSibling();
			while(sibling != null) {
				localize(sibling, language);
				sibling = sibling.getNextSibling();
			}

			localize(menu.getFirstChild(), language);
		}
	}

	private AuthorizationManagerMenuService getProxyService() {
		AuthorizationManagerMenuService service = (AuthorizationManagerMenuService) SpringContextProvider.getBean("authorizationManagerMenuService");
		return service;
	}
}
