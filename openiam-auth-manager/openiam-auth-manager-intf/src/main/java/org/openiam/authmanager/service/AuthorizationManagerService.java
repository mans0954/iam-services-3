package org.openiam.authmanager.service;


import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;

import java.util.List;
import java.util.Set;

public interface AuthorizationManagerService {

	public boolean isEntitled(final String userId, final String resourceId);
	public boolean isEntitled(final String userId, final AuthorizationResource resource);
	public boolean isEntitled(final AuthorizationManagerLoginId loginId, final AuthorizationResource resource);
	public boolean isMemberOf(final String userId, final AuthorizationGroup group);
	public boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationGroup group);
	public boolean isMemberOf(final String userId, final AuthorizationRole role);
	public boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationRole role);
	public Set<AuthorizationResource> getResourcesFor(final String userId);
	public Set<AuthorizationResource> getResourcesFor(final AuthorizationManagerLoginId loginId);
	public Set<AuthorizationGroup> getGroupsFor(final String userId);
	public Set<AuthorizationGroup> getGroupsFor(final AuthorizationManagerLoginId loginId);
	public Set<AuthorizationRole> getRolesFor(final String userId);
	public Set<AuthorizationRole> getRolesFor(final AuthorizationManagerLoginId loginId);
    public List<String> getUserIdsList();
	
	/*
	public List<AuthorizationUser> getUsersForRole(final String roleId);
	public List<AuthorizationUser> getUsersForGroup(final String groupId);
	*/
}
