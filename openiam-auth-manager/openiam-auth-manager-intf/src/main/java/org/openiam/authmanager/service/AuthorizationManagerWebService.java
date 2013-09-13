package org.openiam.authmanager.service;

import java.util.List;
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
import org.openiam.base.ws.Response;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", name = "AuthorizationManagerWebService")
public interface AuthorizationManagerWebService {

	@WebMethod
	public List<String> isUserEntitledToResources(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "resourceIdList", targetNamespace = "") final List<String> resourceIdList
			);
	
	@WebMethod
	public boolean isUserEntitledToResource(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId
			);
	
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
	
	/**
	 * This method should NOT be called, as the internal ESB mechanisms will do this.  However, just in case, it's here.
	 * This method will refresh the cache
	 */
	@WebMethod
	public void refreshCache();
}
