package org.openiam.authmanager.service;

import java.util.List;

import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.thread.Sweepable;

public interface AuthorizationManagerMenuService extends Sweepable {

	public AuthorizationMenu getMenuTree(final String menuRoot, final String userId);
	
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId);
	
	public AuthorizationMenu getMenuTree(final String menuId);
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType);
	
	public void processTreeUpdate(final List<ResourceEntity> toSave, final List<ResourceEntity> toUpdate, final List<ResourceEntity> toDelete);
	
	public void entitle(final MenuEntitlementsRequest menuEntitlementsRequest);
	
	public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult);
}
