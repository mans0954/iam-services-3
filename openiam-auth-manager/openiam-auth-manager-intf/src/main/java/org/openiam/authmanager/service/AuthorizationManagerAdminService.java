package org.openiam.authmanager.service;

import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.model.ResourceEntitlementToken;

public interface AuthorizationManagerAdminService {

	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId);
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId);
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId);
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);
}
