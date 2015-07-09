package org.openiam.authmanager.service;


import java.util.List;
import java.util.Set;

import org.openiam.authmanager.common.model.GroupAuthorizationRight;
import org.openiam.authmanager.common.model.OrganizationAuthorizationRight;
import org.openiam.authmanager.common.model.ResourceAuthorizationRight;
import org.openiam.authmanager.common.model.RoleAuthorizationRight;

public interface AuthorizationManagerService {

	public boolean isEntitled(final String userId, final String resourceId);
	public boolean isEntitled(final String userId, final String resourceId, final String rightId);
	public boolean isMemberOfGroup(final String userId, final String groupId);
	public boolean isMemberOfGroup(final String userId, final String groupId, final String rightId);
	public boolean isMemberOfRole(final String userId, final String roleId);
	public boolean isMemberOfRole(final String userId, final String roleId, final String rightId);
	
	public boolean isMemberOfOrganization(final String userId, final String organizationId);
	public boolean isMemberOfOrganization(final String userId, final String organizationId, final String rightId);
	public Set<ResourceAuthorizationRight> getResourcesForUser(final String userId);
	public Set<GroupAuthorizationRight> getGroupsForUser(final String userId);
	public Set<RoleAuthorizationRight> getRolesForUser(final String userId);
	public Set<OrganizationAuthorizationRight> getOrganizationsForUser(final String userId);
    public List<String> getUserIdsList();
	
	/*
	public List<AuthorizationUser> getUsersForRole(final String roleId);
	public List<AuthorizationUser> getUsersForGroup(final String groupId);
	*/
}
