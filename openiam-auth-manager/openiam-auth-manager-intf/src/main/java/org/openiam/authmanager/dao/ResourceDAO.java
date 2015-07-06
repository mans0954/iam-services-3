package org.openiam.authmanager.dao;

import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationMenu;

public interface ResourceDAO {
	public List<AuthorizationMenu> getAuthorizationMenus();
}
