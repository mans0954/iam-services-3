package org.openiam.authmanager.service;


import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;

import java.util.List;
import java.util.Set;

public interface AuthorizationManagerService {

	boolean isEntitled(final String userId, final String resourceId);
	boolean isEntitled(final String userId, final AuthorizationResource resource);
	boolean isEntitled(final AuthorizationManagerLoginId loginId, final AuthorizationResource resource);
	boolean isMemberOf(final String userId, final AuthorizationGroup group);
	boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationGroup group);
	boolean isMemberOf(final String userId, final AuthorizationRole role);
	boolean isMemberOf(final AuthorizationManagerLoginId loginId, final AuthorizationRole role);
	Set<AuthorizationResource> getResourcesFor(final String userId);
	Set<AuthorizationResource> getResourcesFor(final AuthorizationManagerLoginId loginId);
	Set<AuthorizationGroup> getGroupsFor(final String userId);
	Set<AuthorizationGroup> getGroupsFor(final AuthorizationManagerLoginId loginId);
	Set<AuthorizationRole> getRolesFor(final String userId);
	Set<AuthorizationRole> getRolesFor(final AuthorizationManagerLoginId loginId);
    List<String> getUserIdsList();
	
	/*
	public List<AuthorizationUser> getUsersForRole(final String roleId);
	public List<AuthorizationUser> getUsersForGroup(final String groupId);
	*/
}
