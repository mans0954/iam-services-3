package org.openiam.authmanager.service;


import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.LoginId;

public interface AuthorizationManagerService {

	public boolean isEntitled(final String userId, final AuthorizationResource resource);
	public boolean isEntitled(final LoginId loginId, final AuthorizationResource resource);
	public boolean isMemberOf(final String userId, final AuthorizationGroup group);
	public boolean isMemberOf(final LoginId loginId, final AuthorizationGroup group);
	public boolean isMemberOf(final String userId, final AuthorizationRole role);
	public boolean isMemberOf(final LoginId loginId, final AuthorizationRole role);
	public Set<AuthorizationResource> getResourcesFor(final String userId);
	public Set<AuthorizationResource> getResourcesFor(final LoginId loginId);
	public Set<AuthorizationGroup> getGroupsFor(final String userId);
	public Set<AuthorizationGroup> getGroupsFor(final LoginId loginId);
	public Set<AuthorizationRole> getRolesFor(final String userId);
	public Set<AuthorizationRole> getRolesFor(final LoginId loginId);
}
