package org.openiam.authmanager.service;

import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface AuthorizationManagerAdminService {

	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId);
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId);
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId);
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);

    public Set<String> getOwnerIdsForResource(String resourceId);
    public HashMap<String, HashSet<String>> getOwnerIdsForResourceSet(Set<String> resourceIdSet);
}
