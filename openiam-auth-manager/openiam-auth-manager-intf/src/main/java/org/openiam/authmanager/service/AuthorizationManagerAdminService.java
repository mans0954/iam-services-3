package org.openiam.authmanager.service;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.ResourceEntitlementToken;
import org.openiam.authmanager.model.UserEntitlementsMatrix;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface AuthorizationManagerAdminService {

	public ResourceEntitlementToken getNonCachedEntitlementsForUser(final String userId, final Date date);
	public ResourceEntitlementToken getNonCachedEntitlementsForGroup(final String groupId, final Date date);
	public ResourceEntitlementToken getNonCachedEntitlementsForRole(final String roleId, final Date date);
	public ResourceEntitlementToken getNonCachedEntitlementsForOrganization(final String organizationId, final Date date);
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId, final Date date);

    public Set<String> getOwnerIdsForResource(final String resourceId, final Date date);
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(final Set<String> resourceIdSet, final Date date);
	public Set<String> getUserIdsEntitledForResource(final String resourceId, final Date date);
	public HashMap<String, SetStringResponse> getUserIdsEntitledForResourceSet(final Set<String> resourceIdSet, final Date date);

	public Set<String> getOwnerIdsForGroup(final String groupId, final Date date);

    public HashMap<String,SetStringResponse> getOwnerIdsForGroupSet(final Set<String> groupIdSet, final Date date);
}
