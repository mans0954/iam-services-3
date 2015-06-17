package org.openiam.authmanager.service;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.ws.request.MenuEntitlementsRequest;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import java.util.List;

public interface AuthorizationManagerMenuService {

	AuthorizationMenu getMenuTree(final String menuRoot, final String userId);
	AuthorizationMenu getMenuTree(final String menuRoot, final String login, final String managedSysId);
	
	AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId);
	AuthorizationMenu getMenuTreeByName(final String menuRoot, final String login, final String managedSysId);
	
	AuthorizationMenu getMenuTree(final String menuId);
	AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType);
	
	void processTreeUpdate(final List<ResourceEntity> toSave, final List<ResourceEntity> toUpdate, final List<ResourceEntity> toDelete);
	
	void entitle(final MenuEntitlementsRequest menuEntitlementsRequest);
	
	boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult);
}
