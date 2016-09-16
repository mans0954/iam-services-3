package org.openiam.authmanager.service;

import java.util.List;

import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.thread.Sweepable;

public interface AuthorizationManagerMenuService extends Sweepable {

	public AuthorizationMenu getMenuTreeForUserId(final String menuId, final String menuName, final String userId, final Language language);
	public AuthorizationMenu getMenuTree(final String menuId, final Language language);
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType, final Language language);
	public void deleteMenuTree(final String menuId) throws AuthorizationMenuException;
	public void saveMenuTree(final AuthorizationMenu root) throws AuthorizationMenuException;
	public void entitle(final MenuEntitlementsRequest menuEntitlementsRequest) throws BasicDataServiceException;
	public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult);
	public void processTreeUpdate(final List<ResourceEntity> toSave, final List<ResourceEntity> toUpdate, final List<ResourceEntity> toDelete);

	public AuthorizationMenu getMenuTree(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTreeByName(final String menuRoot, final String userId);
	public AuthorizationMenu getMenuTree(final String menuId);
}
