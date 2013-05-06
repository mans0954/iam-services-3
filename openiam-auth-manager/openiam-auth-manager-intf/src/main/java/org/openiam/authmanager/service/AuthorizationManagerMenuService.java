package org.openiam.authmanager.service;

import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.ws.request.MenuEntitlementsRequest;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

public interface AuthorizationManagerMenuService {

	public AuthorizationMenu getMenuTree(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTree(final String menuRoot, final String domain, final String login, final String managedSysId);
	
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String domain, final String login, final String managedSysId);
	
	public AuthorizationMenu getMenuTree(final String menuId);
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType);
	
	public void processTreeUpdate(final List<ResourceEntity> toSave, final List<ResourceEntity> toUpdate, final List<ResourceEntity> toDelete);
	
	public void entitle(final MenuEntitlementsRequest menuEntitlementsRequest);
}
