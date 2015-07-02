package org.openiam.authmanager.service.impl;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.exception.AuthorizationManagerRuntimeException;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.authmanager.ws.request.UserRequest;
import org.openiam.authmanager.ws.request.UserToGroupAccessRequest;
import org.openiam.authmanager.ws.request.UserToResourceAccessRequest;
import org.openiam.authmanager.ws.request.UserToRoleAccessRequest;
import org.openiam.authmanager.ws.response.AccessResponse;
import org.openiam.authmanager.ws.response.GroupsForUserResponse;
import org.openiam.authmanager.ws.response.ResourcesForUserResponse;
import org.openiam.authmanager.ws.response.RolesForUserResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.authmanager.service.AuthorizationManagerWebService", 
			targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", 
			portName = "AuthorizationManagerWebServicePort",
			serviceName = "AuthorizationManagerWebService")
@Service("authorizationManagerWebService")
public class AuthorizationManagerWebServiceImpl implements AuthorizationManagerWebService {

	@Autowired
	private AuthorizationManagerService authManagerService;
	
	@Override
	public boolean isUserEntitledToResource(String userId, String resourceId) {
		return authManagerService.isEntitled(userId, resourceId);
	}

	@Override
	public boolean isMemberOfGroup(final String userId, final String groupId) {
		return authManagerService.isMemberOfGroup(userId, groupId);
	}

	@Override
	public boolean isMemberOfRole(final String userId, final String roleId) {
		return authManagerService.isMemberOfRole(userId, roleId);
	}

	@Override
	public Set<AuthorizationResource> getResourcesForUser(final String userId) {
		return authManagerService.getResourcesForUser(userId);
	}

	@Override
	public Set<AuthorizationGroup> getGroupsForUser(final String userId) {
		return authManagerService.getGroupsForUser(userId);
	}

	@Override
	public Set<AuthorizationRole> getRolesForUser(final String userId) {
		return authManagerService.getRolesForUser(userId);
	}
	

	@Override
	public void refreshCache() {
		((Sweepable)authManagerService).sweep();
	}

	@Override
	public boolean isMemberOfOrganization(String userId, String organizationId) {
		return authManagerService.isMemberOfOrganization(userId, organizationId);
	}

	@Override
	public boolean isUserEntitledToResourceWithRight(String userId,
			String resourceId, String rightId) {
		return authManagerService.isEntitled(userId, resourceId, rightId);
	}

	@Override
	public boolean isMemberOfGroupWithRight(String userId, String groupId,
			String rightId) {
		return authManagerService.isMemberOfGroup(userId, groupId, rightId);
	}

	@Override
	public boolean isMemberOfRoleWithRight(String userId, String roleId,
			String rightId) {
		return authManagerService.isMemberOfRole(userId, roleId, rightId);
	}

	@Override
	public boolean isMemberOfOrganizationWithRight(String userId,
			String organizationId, String rightId) {
		return authManagerService.isMemberOfOrganization(userId, organizationId, rightId);
	}

	@Override
	public Set<AuthorizationOrganization> getOrganizationsForUser(String userId) {
		return authManagerService.getOrganizationsForUser(userId);
	}
}
