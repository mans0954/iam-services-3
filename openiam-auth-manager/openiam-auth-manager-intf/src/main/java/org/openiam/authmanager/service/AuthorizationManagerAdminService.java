package org.openiam.authmanager.service;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

import java.util.HashMap;
import java.util.Set;

public interface AuthorizationManagerAdminService {

	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId);
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId);
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId);
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);

    public Set<String> getOwnerIdsForResource(String resourceId);
    public HashMap<String, Set<String>> getOwnerIdsForResourceSet(Set<String> resourceIdSet);
	public Set<String> getUserIdsEntitledForResource(String resourceId);
	public HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(Set<String> resourceIdSet);

	public Set<String> getOwnerIdsForGroup(String groupId);
    public HashMap<String, Set<String>> getOwnerIdsForGroupSet(Set<String> groupIdSet);

	public Set<String> getOwnerIdsForRole(String roleId);
	public HashMap<String, Set<String>> getOwnerIdsForRoleSet(Set<String> roleIdSet);
}
