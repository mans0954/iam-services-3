package org.openiam.authmanager.service;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface AuthorizationManagerAdminService {

	ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId);
	ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId);
	ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId);
	UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);

    Set<String> getOwnerIdsForResource(String resourceId);
    HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(Set<String> resourceIdSet);
	Set<String> getUserIdsEntitledForResource(String resourceId);
	HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(Set<String> resourceIdSet);

	Set<String> getOwnerIdsForGroup(String groupId);

    HashMap<String,SetStringResponse> getOwnerIdsForGroupSet(Set<String> groupIdSet);
}
