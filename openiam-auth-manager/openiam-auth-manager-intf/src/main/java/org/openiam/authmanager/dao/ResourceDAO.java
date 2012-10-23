package org.openiam.authmanager.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.url.AuthorizationDomain;

public interface ResourceDAO extends AbstractDAO<AuthorizationResource> {
	public Set<AuthorizationDomain> getAuthorizationDomains(final Map<String, AuthorizationResource> resourceMap);
	public List<AuthorizationMenu> getAuthorizationMenus();
}
