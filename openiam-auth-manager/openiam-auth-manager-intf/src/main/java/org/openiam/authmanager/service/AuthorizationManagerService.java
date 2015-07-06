package org.openiam.authmanager.service;


import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;

import java.util.List;
import java.util.Set;

public interface AuthorizationManagerService {

	public boolean isEntitled(final String userId, final String resourceId);
	public boolean isEntitled(final String userId, final String resourceId, final String rightId);
	public boolean isMemberOfGroup(final String userId, final String groupId);
	public boolean isMemberOfGroup(final String userId, final String groupId, final String rightId);
	public boolean isMemberOfRole(final String userId, final String roleId);
	public boolean isMemberOfRole(final String userId, final String roleId, final String rightId);
	
	public boolean isMemberOfOrganization(final String userId, final String organizationId);
	public boolean isMemberOfOrganization(final String userId, final String organizationId, final String rightId);
	public Set<AuthorizationResource> getResourcesForUser(final String userId);
	public Set<AuthorizationGroup> getGroupsForUser(final String userId);
	public Set<AuthorizationRole> getRolesForUser(final String userId);
	public Set<AuthorizationOrganization> getOrganizationsForUser(final String userId);
    public List<String> getUserIdsList();
	
	/*
	public List<AuthorizationUser> getUsersForRole(final String roleId);
	public List<AuthorizationUser> getUsersForGroup(final String groupId);
	*/
}
