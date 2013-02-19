package org.openiam.authmanager.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;

public interface ResourceDAO extends AbstractDAO<AuthorizationResource> {
	public List<AuthorizationMenu> getAuthorizationMenus();
	public AuthorizationMenu getAuthorizationMenu(final String menuId);
}
