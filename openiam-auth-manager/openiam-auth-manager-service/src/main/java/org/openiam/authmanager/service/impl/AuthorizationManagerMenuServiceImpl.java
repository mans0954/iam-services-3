package org.openiam.authmanager.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.dao.ResourcePropDAO;
import org.openiam.authmanager.dao.ResourceResourceXrefDAO;
import org.openiam.authmanager.model.MenuEntitlementType;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
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

@Service("authorizationManagerMenuService")
@ManagedResource(objectName="org.openiam.authorization.manager:name=authorizationManagerMenuService")
public class AuthorizationManagerMenuServiceImpl implements AuthorizationManagerMenuService, InitializingBean, ApplicationContextAware, Runnable {

	private ApplicationContext ctx;
	
	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuServiceImpl.class);
	
	private static final Comparator<AuthorizationMenu> menuOrderComparator = new AuthorizationMenuOrderComparator();
	
	private Map<String, AuthorizationMenu> menuCache;
	private Map<String, AuthorizationMenu> menuNameCache;
	
	@Autowired
	@Qualifier("jdbcResourceResourceXrefDAO")
	private ResourceResourceXrefDAO resourceResourceXrefDAO;
	
	@Autowired
	@Qualifier("jdbcResourcePropDAO")
	private ResourcePropDAO resourcePropDAO;
	
	@Autowired
	@Qualifier("jdbcResourceDAO")
	private ResourceDAO resourceDAO;
	
	@Autowired
	private AuthorizationManagerService authManager;
	
	@Autowired
	private AuthorizationManagerAdminService authManagerAdminService;
	
	private boolean forceThreadShutdown = false;
	
	@Value("${org.openiam.authorization.manager.threadsweep}")
	private long sweepInterval;
	
	private ExecutorService service = new  ScheduledThreadPoolExecutor(1);
	
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
	public AuthorizationMenu getMenuTree(final String menuId) {
		return getAllMenuTress().get(menuId);
	}
	
	@Override
	public AuthorizationMenu getNonCachedMenuTree(String menuId, String principalId, String principalType) {
		final AuthorizationMenu menu = getMenuTree(menuId);
		ResourceEntitlementToken token = null;
		if(menu != null) {
			if(StringUtils.equalsIgnoreCase("user", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForUser(principalId);
			} else if(StringUtils.equalsIgnoreCase("group", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForGroup(principalId);
			} else if(StringUtils.equalsIgnoreCase("role", principalType)) {
				token = authManagerAdminService.getNonCachedEntitlementsForRole(principalId);
			}
		}
		
		doEntitlementsCheck(menu, token);
		
		return menu;
	}
	
	private void doEntitlementsCheck(final AuthorizationMenu menu, final ResourceEntitlementToken token) {
		if(menu != null && token != null) {
			if(token.isResourceDirect(menu.getId())) {
				menu.addEntitlementType(MenuEntitlementType.EXPLICIT);
			}
			
			if(token.isResourceIndirect(menu.getId())) {
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
	
	private final Map<String, AuthorizationMenu> getAllMenuTress() {
		final List<ResourceProp> tempResourcePropertyList = resourcePropDAO.getList();
		final List<AuthorizationMenu> tempMenuList = resourceDAO.getAuthorizationMenus();
		
		final Map<String, List<ResourceProp>> tempResourcePropMap = new HashMap<String, List<ResourceProp>>();
		for(final ResourceProp prop : tempResourcePropertyList) {
			if(!tempResourcePropMap.containsKey(prop.getResourceId())) {
				tempResourcePropMap.put(prop.getResourceId(), new LinkedList<ResourceProp>());
			}
			tempResourcePropMap.get(prop.getResourceId()).add(prop);
		}
		
		final Map<String, AuthorizationMenu> tempMenuMap = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuList) {
			tempMenuMap.put(menu.getId(), menu);
			menu.afterPropertiesSet(tempResourcePropMap.get(menu.getId()));
		}
		final Map<String, AuthorizationMenu> tempMenuTreeMap = createMenuTrees(tempMenuMap);
		return tempMenuTreeMap;
	}
	
	@ManagedOperation(description="sweep the Menu Cache")
	public void sweep() {
		final StopWatch sw = new StopWatch();
		sw.start();
		final Map<String, AuthorizationMenu> tempMenuTreeMap = getAllMenuTress();
		
		final Map<String, AuthorizationMenu> tempMenuNameMap = new HashMap<String, AuthorizationMenu>();
		for(final AuthorizationMenu menu : tempMenuTreeMap.values()) {
			tempMenuNameMap.put(menu.getName(), menu);
		}
		synchronized(this) {
			menuCache = tempMenuTreeMap;
			menuNameCache = tempMenuNameMap;
		}
		sw.stop();
		log.debug(String.format("Done creating menu trees. Took: %s ms", sw.getTime()));
	}
	
	private Map<String, AuthorizationMenu> createMenuTrees(final Map<String, AuthorizationMenu> menuMap) {
		final List<ResourceResourceXref> xrefList = resourceResourceXrefDAO.getList();
		
		final Map<String, String> childResource2ParentResourceMap = new HashMap<String, String>();
		final Map<String, Set<String>> parentResource2ChildResourceMap = new HashMap<String, Set<String>>();
		for(final ResourceResourceXref xref : xrefList) {
			final String resourceId = xref.getResourceId();
			final String memberResourceId = xref.getMemberResourceId();
			
			if(!parentResource2ChildResourceMap.containsKey(resourceId)) {
				parentResource2ChildResourceMap.put(resourceId, new HashSet<String>());
			}
			childResource2ParentResourceMap.put(memberResourceId, resourceId);
			parentResource2ChildResourceMap.get(resourceId).add(memberResourceId);
		}
		
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
		return getMenu(menuCache.get(menuRoot), userId, null);
	}

	@Override
	public AuthorizationMenu getMenuTree(final String menuRoot, final String domain, final String login, final String managedSysId) {
		return getMenu(menuCache.get(menuRoot), null, new AuthorizationManagerLoginId(domain, login, managedSysId));
	}
	
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId) {
		return getMenu(menuNameCache.get(menuRoot), userId, null);
	}
	
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String domain, final String login, final String managedSysId) {
		return getMenu(menuNameCache.get(menuRoot), null, new AuthorizationManagerLoginId(domain, login, managedSysId));
	}
	
	private AuthorizationMenu getMenu(final AuthorizationMenu menu, final String userId, final AuthorizationManagerLoginId loginId) {
		AuthorizationMenu retVal = null;
		if(menu != null && hasAccess(menu, userId, loginId)) {
			final AuthorizationMenu copy = menu.copy();
			final List<AuthorizationMenu> children = getSiblings(menu.getFirstChild(), userId, loginId);
			final List<AuthorizationMenu> siblings = getSiblings(menu.getNextSibling(), userId, loginId);
			
			if(CollectionUtils.isNotEmpty(children)) {
				copy.setFirstChild(children.get(0));
				for(final AuthorizationMenu child : children) {
					child.setParent(copy);
				}
			}
			if(CollectionUtils.isNotEmpty(siblings)) {
				copy.setNextSibling(siblings.get(0));
			}
			setNextSiblings(children);
			setNextSiblings(siblings);
			retVal = copy;
		}
		return retVal;
	}
	
	private void setNextSiblings(final List<AuthorizationMenu> menuList) {
		if(CollectionUtils.isNotEmpty(menuList)) {
			AuthorizationMenu prev = null;
			for(final Iterator<AuthorizationMenu> it = menuList.iterator(); it.hasNext();) {
				final AuthorizationMenu next = it.next();
				if(prev != null) {
					prev.setNextSibling(next);
				}
				prev = next;
			}
		}
	}
	
	private List<AuthorizationMenu> getSiblings(final AuthorizationMenu menu, final String userId, final AuthorizationManagerLoginId loginId) {
		final List<AuthorizationMenu> siblings = new LinkedList<AuthorizationMenu>();
		if(menu != null) {
			AuthorizationMenu sibling = menu;
			while(sibling != null) {
				final AuthorizationMenu siblingCopy = getMenu(sibling, userId, loginId);
				if(siblingCopy != null) {
					siblings.add(siblingCopy);
				}
				sibling = sibling.getNextSibling();
			}
		}
		return siblings;
	}
	
	private boolean hasAccess(final AuthorizationMenu menu, final String userId, final AuthorizationManagerLoginId loginId) {
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setId(menu.getId());
		boolean retVal = false;
		if(menu.getIsPublic()) {
			retVal = true;
		} else {
			retVal = (userId != null) ? (authManager.isEntitled(userId, resource)) : authManager.isEntitled(loginId, resource);
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
	
	@PreDestroy
	public void destroy() {
		/* This Runnable only stops running after a server shutdown.  When doing a "redeploy" (i.e. not stopping JBOSS),
		 * This Runnable keeps running.  Setting this flat to true will force the Runnable to exit.
		 */
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

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
		//service.submit(this);
	}
}
