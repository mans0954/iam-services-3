package org.openiam.authmanager.service;

import org.openiam.authmanager.common.model.AuthorizationMenu;

public interface AuthorizationManagerMenuService {

	public AuthorizationMenu getMenuTree(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTree(final String menuRoot, final String domain, final String login, final String managedSysId);
	
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String domain, final String login, final String managedSysId);
	
	public AuthorizationMenu getMenuTree(final String menuId);
}
