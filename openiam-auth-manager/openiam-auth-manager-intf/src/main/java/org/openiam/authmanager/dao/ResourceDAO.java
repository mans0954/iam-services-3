package org.openiam.authmanager.dao;

import java.util.List;

import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;

public interface ResourceDAO {
	public List<AuthorizationMenu> getAuthorizationMenus();
}
