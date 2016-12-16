package org.openiam.authmanager.service.integration;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.model.MenuEntitlementType;
import org.openiam.srvc.am.AuthorizationManagerAdminWebService;
import org.openiam.srvc.am.AuthorizationManagerMenuWebService;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.KeyDTO;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationManagerMenuWebServiceTest extends AbstractServiceTest {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceTest.class);
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("authorizationManagerMenuServiceClient")
	protected AuthorizationManagerMenuWebService menuWebService;
	
	@Autowired
	@Qualifier("authManagerAdminClient")
	private AuthorizationManagerAdminWebService authManagerAdminClient;
	
	private Set<String> collect(final AuthorizationMenu menu) {
		final Set<String> set = new HashSet<String>();
		if(menu.getFirstChild() != null) {
			set.addAll(collect(menu.getFirstChild()));
		} else if(menu.getNextSibling() != null) {
			set.addAll(collect(menu.getNextSibling()));
		}
		return set;
	}
	
	private AuthorizationMenu getNSibling(final AuthorizationMenu menu, final int n) {
		final AuthorizationMenu firstChild = menu.getFirstChild();
		AuthorizationMenu sibling = firstChild;
		for(int i = 0; i < n; i++) {
			sibling = sibling.getNextSibling();
		}
		return sibling;
	}
	
	public void assertAccess(final String principalId, final String principalType, final User user) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = createAndAssert(5);
		AuthorizationMenu root = tuple.getKey();
		AuthorizationMenu dbRoot = tuple.getValue();
		
		final AuthorizationMenu toEntitle = getNSibling(dbRoot, 3);
		
		final MenuEntitlementsRequest request = new MenuEntitlementsRequest();
		request.setPrincipalId(principalId);
		request.setPrincipalType(principalType);
		request.setNewlyEntitled(new LinkedList<String>(Arrays.asList(new String [] {toEntitle.getId()})));
		Assert.assertTrue(menuWebService.entitle(request).isSuccess());
		refreshAuthorizationManager();
		AuthorizationMenu authorizedRoot = menuWebService.getNonCachedMenuTree(root.getId(), principalId, principalType, getDefaultLanguage());
		Assert.assertTrue(authorizedRoot.getEntitlementTypeList() != null && authorizedRoot.getEntitlementTypeList().contains(MenuEntitlementType.IMPLICIT));
		Assert.assertTrue(getNSibling(authorizedRoot, 3).getEntitlementTypeList() != null && getNSibling(authorizedRoot, 3).getEntitlementTypeList().contains(MenuEntitlementType.EXPLICIT));
		
		if(!StringUtils.equalsIgnoreCase("user", principalType)) {
			authorizedRoot = menuWebService.getNonCachedMenuTree(root.getId(), user.getId(), "user", getDefaultLanguage());
			Assert.assertTrue(authorizedRoot.getEntitlementTypeList() != null && authorizedRoot.getEntitlementTypeList().contains(MenuEntitlementType.IMPLICIT));
			Assert.assertTrue(getNSibling(authorizedRoot, 3).getEntitlementTypeList() != null && getNSibling(authorizedRoot, 3).getEntitlementTypeList().contains(MenuEntitlementType.IMPLICIT));
		}
	}
	
	public void assertAccessIndirect(final KeyDTO principalToEntitle, final KeyDTO targetPrincipal) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = createAndAssert(5);
		AuthorizationMenu root = tuple.getKey();
		AuthorizationMenu dbRoot = tuple.getValue();
		
		final AuthorizationMenu toEntitle = getNSibling(dbRoot, 3);
		
		final MenuEntitlementsRequest request = new MenuEntitlementsRequest();
		request.setPrincipalId(principalToEntitle.getId());
		request.setPrincipalType(principalToEntitle.getClass().getSimpleName().toLowerCase());
		request.setNewlyEntitled(new LinkedList<String>(Arrays.asList(new String [] {toEntitle.getId()})));
		Assert.assertTrue(menuWebService.entitle(request).isSuccess());
		refreshAuthorizationManager();
		AuthorizationMenu authorizedRoot = menuWebService.getNonCachedMenuTree(root.getId(), targetPrincipal.getId(), targetPrincipal.getClass().getSimpleName().toLowerCase(), getDefaultLanguage());
		Assert.assertTrue(authorizedRoot.getEntitlementTypeList() != null && authorizedRoot.getEntitlementTypeList().contains(MenuEntitlementType.IMPLICIT));
		Assert.assertTrue(getNSibling(authorizedRoot, 3).getEntitlementTypeList() != null && getNSibling(authorizedRoot, 3).getEntitlementTypeList().contains(MenuEntitlementType.IMPLICIT));
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserDirect() {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		try {
			user = super.createUser();
			assertAccess(user.getId(), "user", user);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaOrganizationNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaOrganization(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaOrganizationWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetNonCachedMenuTreeForUserIndirectViaOrganization(now, tomorrow);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaOrganization(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Organization organization = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
			assertAccess(organization.getId(), "organization", user);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(organization != null) {
				organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInOrganizationNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaChildInOrganization(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInOrganizationWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testGetNonCachedMenuTreeForUserIndirectViaChildInOrganization(now, tomorrow);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaChildInOrganization(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Organization organization = null;
		Organization child = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			child = super.createOrganization();
			organizationServiceClient.addUserToOrg(child.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
			organizationServiceClient.addChildOrganization(organization.getId(), child.getId(), getRequestorId(), null, startDate, endDate);
			assertAccessIndirect(organization, child);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(child != null) {
				organizationServiceClient.deleteOrganization(child.getId(), getRequestorId());
			}
			if(organization != null) {
				organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaRoleInOrganizationNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaRoleInOrganization(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaRoleInOrganizationWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaRoleInOrganization(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaRoleInOrganization(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Organization organization = null;
		Role role = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			role = super.createRole();
			organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
			organizationServiceClient.addRoleToOrganization(organization.getId(), role.getId(), getRequestorId(), null, startDate, endDate);
			assertAccessIndirect(role, organization);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(role != null) {
				roleServiceClient.removeRole(role.getId());
			}
			if(organization != null) {
				organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupInOrganizationNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaGroupInOrganization(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupInOrganizationWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaGroupInOrganization(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaGroupInOrganization(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Organization organization = null;
		Group group = null;
		try {
			user = super.createUser();
			organization = super.createOrganization();
			group = super.createGroup();
			organizationServiceClient.addUserToOrg(organization.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
			organizationServiceClient.addGroupToOrganization(organization.getId(), group.getId(), getRequestorId(), null, startDate, endDate);
			assertAccessIndirect(group, organization);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(group != null) {
				groupServiceClient.deleteGroup(group.getId(), null);
			}
			if(organization != null) {
				organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInRoleNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaChildInRole(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaChildInRole(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaChildInRole(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Role role = null;
		Role child = null;
		try {
			user = super.createUser();
			role = super.createRole();
			child = super.createRole();
			roleServiceClient.addUserToRole(child.getId(), user.getId(), null, startDate, endDate);
			roleServiceClient.addChildRole(role.getId(), child.getId(), null, startDate, endDate);
			assertAccessIndirect(role, child);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(role != null) {
				roleServiceClient.removeRole(role.getId());
			}
			if(child != null) {
				roleServiceClient.removeRole(child.getId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupInRoleNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaGroupInRole(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupInRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaGroupInRole(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaGroupInRole(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Role role = null;
		Group group = null;
		try {
			user = super.createUser();
			role = super.createRole();
			group = super.createGroup();
			roleServiceClient.addUserToRole(role.getId(), user.getId(), null, startDate, endDate);
			roleServiceClient.addGroupToRole(role.getId(), group.getId(), null, startDate, endDate);
			assertAccessIndirect(group, role);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(group != null) {
				groupServiceClient.deleteGroup(group.getId(), null);
			}
			if(role != null) {
				roleServiceClient.removeRole(role.getId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInGroupNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaChildInGroup(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaChildInGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaChildInGroup(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaChildInGroup(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Group group = null;
		Group child = null;
		try {
			user = super.createUser();
			group = super.createGroup();
			child = super.createGroup();
			groupServiceClient.addUserToGroup(group.getId(), user.getId(), null, null, startDate, endDate);
			groupServiceClient.addChildGroup(group.getId(), child.getId(), null, null, startDate, endDate);
			assertAccessIndirect(group, child);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(group != null) {
				groupServiceClient.deleteGroup(group.getId(), null);
			}
			if(child != null) {
				groupServiceClient.deleteGroup(child.getId(), null);
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaRoleNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaRole(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaRoleWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaRole(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaRole(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Role entity = null;
		try {
			user = super.createUser();
			entity = super.createRole();
			roleServiceClient.addUserToRole(entity.getId(), user.getId(), null, startDate, endDate);
			assertAccess(entity.getId(), "role", user);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(entity != null) {
				roleServiceClient.removeRole(entity.getId());
			}
		}
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupNoRange() {
		testGetNonCachedMenuTreeForUserIndirectViaGroup(null, null);
	}
	
	@Test
	public void testGetNonCachedMenuTreeForUserIndirectViaGroupWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testGetNonCachedMenuTreeForUserIndirectViaGroup(startDate, endDate);
	}
	
	private void testGetNonCachedMenuTreeForUserIndirectViaGroup(final Date startDate, final Date endDate) {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		User user = null;
		Group entity = null;
		try {
			user = super.createUser();
			entity = super.createGroup();
			groupServiceClient.addUserToGroup(entity.getId(), user.getId(), getRequestorId(), null, startDate, endDate);
			assertAccess(entity.getId(), "group", user);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
			if(user != null) {
				userServiceClient.removeUser(user.getId());
			}
			if(entity != null) {
				roleServiceClient.removeRole(entity.getId());
			}
		}
	}
	
	@Test
	public void testGetMenuTreeForUserId() {
		
	}
	
	@Test
	public void testIsUserAuthenticatedToMenuWithURL() {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		try {
			tuple = createAndAssert(5);
			AuthorizationMenu root = tuple.getKey();
			AuthorizationMenu dbRoot = tuple.getValue();
			
			final MenuEntitlementsRequest request = new MenuEntitlementsRequest();
			request.setNewlyEntitled(new LinkedList<String>(collect(dbRoot)));
			request.setPrincipalId(getRequestorId());
			request.setPrincipalType("user");
			Assert.assertTrue(menuWebService.entitle(request).isSuccess());
			menuWebService.sweep();
			refreshAuthorizationManager();
			Assert.assertTrue(menuWebService.isUserAuthenticatedToMenuWithURL(getRequestorId(), root.getUrl(), root.getId(), false));
			Assert.assertTrue(menuWebService.isUserAuthenticatedToMenuWithURL(getRequestorId(), root.getUrl(), null, false));
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
		}
	}
	
	@Test
	public void testRootNotNull() {
		final MenuRequest request = new MenuRequest();
		request.setMenuRoot("IDM");
		request.setUserId("3000");
		final AuthorizationMenu menu = menuWebService.getMenuTreeForUserId(request, null);
		Assert.assertNotNull(menu);
		if(menu != null) {
			Assert.assertNotNull(menu.getFirstChild());
		}
	}
	
	@Test
	public void testMenuTreeCreation() {
		Tuple<AuthorizationMenu, AuthorizationMenu> tuple = null;
		try {
			tuple = createAndAssert(5);
			AuthorizationMenu root = tuple.getKey();
			AuthorizationMenu dbRoot = tuple.getValue();
			
			/* tests delete */
			root.getFirstChild().getNextSibling().setNextSibling(root.getFirstChild().getNextSibling().getNextSibling().getNextSibling());
			Assert.assertTrue(menuWebService.saveMenuTree(root).isSuccess());
			dbRoot = menuWebService.getMenuTree(root.getId(), getDefaultLanguage());
			assertEquals(dbRoot, root, 4);
			
			/* tests re-settting head */
			root.setFirstChild(root.getFirstChild().getNextSibling());
			Assert.assertTrue(menuWebService.saveMenuTree(root).isSuccess());
			dbRoot = menuWebService.getMenuTree(root.getId(), getDefaultLanguage());
			assertEquals(dbRoot, root, 3);
		} finally {
			if(tuple != null && tuple.getKey() != null && tuple.getKey().getId() != null) {
				menuWebService.deleteMenuTree(tuple.getKey().getId());
			}
		}
	}
	
	private Tuple<AuthorizationMenu, AuthorizationMenu> createAndAssert(final int numOfChildren) {
		AuthorizationMenu root = generateMenuRoot();
		AuthorizationMenu prev = null;
		for(int i = 0; i < 5; i++) {
			if(prev == null) {
				prev = generateMenu(i);
				root.setFirstChild(prev);
				Assert.assertTrue(menuWebService.saveMenuTree(root).isSuccess());
				root = menuWebService.getMenuTree(root.getId(), getDefaultLanguage());
				prev = root.getFirstChild();
			} else {
				AuthorizationMenu next = generateMenu(i);
				prev.setNextSibling(next);
				Assert.assertTrue(menuWebService.saveMenuTree(root).isSuccess());
				root = menuWebService.getMenuTree(root.getId(), getDefaultLanguage());
				prev = findMenu(root, next);
			}
		}
		AuthorizationMenu dbRoot = menuWebService.getMenuTree(root.getId(), getDefaultLanguage());
		assertEquals(dbRoot, root, numOfChildren);
		return new Tuple<AuthorizationMenu, AuthorizationMenu>(root, dbRoot);
	}
	
	private void assertEquals(final AuthorizationMenu dbRoot, final AuthorizationMenu root, final int count) {
		Assert.assertNotNull(dbRoot);
		
		assertEquals(dbRoot, root);
		AuthorizationMenu dbMenu = dbRoot.getFirstChild();
		AuthorizationMenu menu = root.getFirstChild();
		for(int i = 0; i < count; i++) {
			assertEquals(dbMenu, menu);
			dbMenu = dbMenu.getNextSibling();
			menu = menu.getNextSibling();
		}
	}
	
	private void assertEquals(final AuthorizationMenu dbMenu, final AuthorizationMenu menu) {
		Assert.assertEquals(dbMenu.getName(), menu.getName());
		Assert.assertEquals(dbMenu.getIsPublic(), menu.getIsPublic());
		Assert.assertEquals(dbMenu.getIsVisible(), menu.getIsVisible());
		Assert.assertEquals(dbMenu.getRisk(), menu.getRisk());
		Assert.assertEquals(dbMenu.getUrl(), menu.getUrl());
		Assert.assertEquals(dbMenu.getUrlParams(), menu.getUrlParams());
		Assert.assertEquals(dbMenu.getDisplayOrder(), menu.getDisplayOrder());
	}
	
	private AuthorizationMenu findMenu(final AuthorizationMenu dbRoot, final AuthorizationMenu target) {
		if(dbRoot.getName().equals(target.getName())) {
			return dbRoot;
		} else if(dbRoot.getFirstChild() != null) {
			return findMenu(dbRoot.getFirstChild(), target);
		} else if(dbRoot.getNextSibling() != null) {
			return findMenu(dbRoot.getNextSibling(), target);
		} else {
			return null;
		}
	}
	
	private ResourceType getMenuResourceType() {
		final ResourceTypeSearchBean resourceTypeSearchBean = new ResourceTypeSearchBean();
		resourceTypeSearchBean.addKey("MENU_ITEM");
		return resourceDataService.findResourceTypes(resourceTypeSearchBean, 0, 1, null).get(0);
	}
	
	private AuthorizationMenu generateMenuRoot() {
		final Resource root = new Resource();
		root.setResourceType(getMenuResourceType());
		root.setDisplayNameMap(getLanguageMap());
		root.setIsPublic(false);
		root.setName(getRandomName());
		root.setURL(getRandomName());
		final Response response = resourceDataService.saveResource(root, getRequestorId());
		final AuthorizationMenu menu = menuWebService.getMenuTree((String)response.getResponseValue(), getDefaultLanguage());
		Assert.assertNotNull(menu);
		return menu;
	}
	
	private Map<String, LanguageMapping> getLanguageMap() {
		final Map<String, LanguageMapping> map = new HashMap<String, LanguageMapping>();
		
		final Language language = getDefaultLanguage(); 
		final LanguageMapping mapping = new LanguageMapping();
		mapping.setLanguageId(language.getId());
		mapping.setValue(getRandomName());
		map.put(language.getId(), mapping);
		return map;
	}
	
	private AuthorizationMenu generateMenu(final int order) {
		final AuthorizationMenu menu = new AuthorizationMenu();
		menu.setDisplayName(getRandomName());
		menu.setDisplayOrder(0);
		menu.setIsPublic(false);
		menu.setName(getRandomName());
		menu.setUrl(getRandomName());
		menu.setUrlParams(getRandomName());
		menu.setDisplayNameMap(getLanguageMap());
		menu.setDisplayOrder(order);
		return menu;
	}
}
