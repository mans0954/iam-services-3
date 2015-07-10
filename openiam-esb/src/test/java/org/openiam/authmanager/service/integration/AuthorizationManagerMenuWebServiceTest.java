package org.openiam.authmanager.service.integration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.service.AuthorizationManagerAdminWebService;
import org.openiam.authmanager.service.AuthorizationManagerMenuWebService;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.openiam.authmanager.ws.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
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
		AuthorizationMenu root = generateMenuRoot();
		try {
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
			assertEquals(dbRoot, root, 5);
			
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
			if(root != null && root.getId() != null) {
				menuWebService.deleteMenuTree(root.getId());
			}
		}
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
		resourceTypeSearchBean.setKey("MENU_ITEM");
		return resourceDataService.findResourceTypes(resourceTypeSearchBean, 0, 1, null).get(0);
	}
	
	private AuthorizationMenu generateMenuRoot() {
		final Resource root = new Resource();
		root.setResourceType(getMenuResourceType());
		root.setDisplayNameMap(getLanguageMap());
		root.setIsPublic(true);
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
		menu.setIsPublic(true);
		menu.setName(getRandomName());
		menu.setUrl(getRandomName());
		menu.setUrlParams(getRandomName());
		menu.setDisplayNameMap(getLanguageMap());
		menu.setDisplayOrder(order);
		return menu;
	}
}
