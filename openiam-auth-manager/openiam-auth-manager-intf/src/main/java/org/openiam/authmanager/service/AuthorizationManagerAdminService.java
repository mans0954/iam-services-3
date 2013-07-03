package org.openiam.authmanager.service;

import org.openiam.authmanager.model.EntitlementsMatrix;
import org.openiam.authmanager.model.ResourceEntitlementToken;

public interface AuthorizationManagerAdminService {

	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId);
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId);
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId);
	public EntitlementsMatrix getEntitlementsMatrix(final String entityId, final String entityType);
}
