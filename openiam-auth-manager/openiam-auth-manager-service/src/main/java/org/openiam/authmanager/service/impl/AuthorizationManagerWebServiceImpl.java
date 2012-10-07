package org.openiam.authmanager.service.impl;

import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
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
import org.openiam.base.ws.ResponseStatus;
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
	public AccessResponse isUserEntitledTo( final UserToResourceAccessRequest request) {
		final AccessResponse response =  new AccessResponse(ResponseStatus.SUCCESS);
		try {
			checkNulls(request);
		
			final AuthorizationResource resource = request.getResource();
			final boolean result = (request.getUserId() != null) ? 
					authManagerService.isEntitled(request.getUserId(), resource) : 
					authManagerService.isEntitled(request.getLoginId(), resource);
			response.setResult(result);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public AccessResponse isMemberOfGroup(final UserToGroupAccessRequest request) {
		final AccessResponse response =  new AccessResponse(ResponseStatus.SUCCESS);
		try {
			checkNulls(request);
			final AuthorizationGroup group = request.getGroup();
			final boolean result = (request.getUserId() != null) ? 
					authManagerService.isMemberOf(request.getUserId(), group) : 
					authManagerService.isMemberOf(request.getLoginId(), group);
			response.setResult(result);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public AccessResponse isMemberOfRole(final UserToRoleAccessRequest request) {
		final AccessResponse response =  new AccessResponse(ResponseStatus.SUCCESS);
		try {
			checkNulls(request);
			
			final AuthorizationRole role = request.getRole();
			final boolean result = (request.getUserId() != null) ? 
									authManagerService.isMemberOf(request.getUserId(), role) : 
									authManagerService.isMemberOf(request.getLoginId(), role);
			response.setResult(result);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public ResourcesForUserResponse getResourcesFor(final UserRequest request) {
		final ResourcesForUserResponse response = new ResourcesForUserResponse(ResponseStatus.SUCCESS);
		try {
			checkUserIdNull(request);
			final Set<AuthorizationResource> authResources = (request.getUserId() != null) ? 
																authManagerService.getResourcesFor(request.getUserId()) : 
																authManagerService.getResourcesFor(request.getLoginId());
			response.setResources(authResources);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public GroupsForUserResponse getGroupsFor(final UserRequest request) {
		final GroupsForUserResponse response = new GroupsForUserResponse(ResponseStatus.SUCCESS);
		try {
			checkUserIdNull(request);
			final Set<AuthorizationGroup> authGroups = (request.getUserId() != null) ? 
														authManagerService.getGroupsFor(request.getUserId()) : 
														authManagerService.getGroupsFor(request.getLoginId());
			response.setGroups(authGroups);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public RolesForUserResponse getRolesFor(final UserRequest request) {
		final RolesForUserResponse response = new RolesForUserResponse(ResponseStatus.SUCCESS);
		try {
			checkUserIdNull(request);
			final Set<AuthorizationRole> authorizationRoles = (request.getUserId() != null) ? 
																authManagerService.getRolesFor(request.getUserId()) : 
																authManagerService.getRolesFor(request.getLoginId());
			response.setRoles(authorizationRoles);
		} catch(Throwable e) {
			response.setResponseStatus(ResponseStatus.FAILURE);
			response.setStatusMessage(e.getMessage());
		}
		return response;
	}
	
	private void checkNulls(final UserToResourceAccessRequest request) {
		if(request == null) {
			throw new AuthorizationManagerRuntimeException("No request");
		}
		
		if(request.getResource() == null) {
			throw new AuthorizationManagerRuntimeException("No Authorization Resource Specified");
		}
		
		final AuthorizationResource resource = request.getResource();
		if(StringUtils.isBlank(resource.getId()) && StringUtils.isBlank(resource.getName())) {
			throw new AuthorizationManagerRuntimeException("Authorization Resource has no ID or Name set");
		}
		checkUserIdNull(request);
	}
	
	private void checkNulls(final UserToGroupAccessRequest request) {
		if(request == null) {
			throw new AuthorizationManagerRuntimeException("No request");
		}
		
		if(request.getGroup() == null) {
			throw new AuthorizationManagerRuntimeException("No Authorization Group Specified");
		}
		
		final AuthorizationGroup authorizationGroup = request.getGroup();
		if(StringUtils.isBlank(authorizationGroup.getId()) && StringUtils.isBlank(authorizationGroup.getName())) {
			throw new AuthorizationManagerRuntimeException("Authorization Group has no ID or Name set");
		}
		checkUserIdNull(request);
	}
	
	private void checkNulls(final UserToRoleAccessRequest request) {
		if(request == null) {
			throw new AuthorizationManagerRuntimeException("No request");
		}
		
		if(request.getRole() == null) {
			throw new AuthorizationManagerRuntimeException("No Authorization Role Specified");
		}
		
		final AuthorizationRole role = request.getRole();
		if(StringUtils.isBlank(role.getId()) && StringUtils.isBlank(role.getName())) {
			throw new AuthorizationManagerRuntimeException("Authorization Role has no ID or Name set");
		}
		checkUserIdNull(request);
	}
	
	private void checkUserIdNull(final UserRequest request) {
		if(request == null) {
			throw new AuthorizationManagerRuntimeException("No User Information Specified");
		}
		
		if(StringUtils.isBlank(request.getUserId()) && request.getLoginId() == null) {
			throw new AuthorizationManagerRuntimeException("No User Id and Login Id Specified on the User Object");	
		}
	}
}
