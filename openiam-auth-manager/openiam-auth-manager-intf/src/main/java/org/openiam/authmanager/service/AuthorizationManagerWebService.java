package org.openiam.authmanager.service;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.ws.request.UserRequest;
import org.openiam.authmanager.ws.request.UserToGroupAccessRequest;
import org.openiam.authmanager.ws.request.UserToResourceAccessRequest;
import org.openiam.authmanager.ws.request.UserToRoleAccessRequest;
import org.openiam.authmanager.ws.response.AccessResponse;
import org.openiam.authmanager.ws.response.GroupsForUserResponse;
import org.openiam.authmanager.ws.response.ResourcesForUserResponse;
import org.openiam.authmanager.ws.response.RolesForUserResponse;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", name = "AuthorizationManagerWebService")
public interface AuthorizationManagerWebService {

	/**
	 * @param request 
	 * @return - answers the question "Is User A entitled to resource B, either directly or through recursive membership to other entities
	 */
	@WebMethod
	public AccessResponse isUserEntitledTo(
			@WebParam(name = "request", targetNamespace = "") final UserToResourceAccessRequest request
			);
	
	/**
	 * @param request
	 * @return answers the question "Is User A a member of group B, either directly or through recursive membership to other groups
	 */
	@WebMethod
	public AccessResponse isMemberOfGroup(
			@WebParam(name = "request", targetNamespace = "") final UserToGroupAccessRequest request
		);
	
	/**
	 * @param request
	 * @return answer the question "Is User A a member of role B, either directly or through recursive membership to other entities
	 */
	@WebMethod
	public AccessResponse isMemberOfRole(
			@WebParam(name = "request", targetNamespace = "") final UserToRoleAccessRequest request
		);
	
	/**
	 * @param request
	 * @return all resources for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public ResourcesForUserResponse getResourcesFor(
			@WebParam(name = "request", targetNamespace = "") final UserRequest request
			);
	
	/**
	 * @param request
	 * @return all groups for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public GroupsForUserResponse getGroupsFor(
			@WebParam(name = "request", targetNamespace = "") final UserRequest request
			);
	
	/**
	 * @param request
	 * @return all roles for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public RolesForUserResponse getRolesFor(
			@WebParam(name = "request", targetNamespace = "") final UserRequest request
			);
}
