package org.openiam.authmanager.service;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/service", name = "AuthorizationManagerWebService")
public interface AuthorizationManagerWebService {

	/**
	 * @param userId
	 * @param resourceId
	 * @return is the user entitled to this resource?
	 */
	@WebMethod
	public boolean isUserEntitledToResource(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId
			);
	
	/**
	 * @param userId
	 * @param groupId
	 * @return is the user a member of this group?
	 */
	@WebMethod
	public boolean isMemberOfGroup(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "groupId", targetNamespace = "") final String groupId
	);
	
	/**
	 * @param userId
	 * @param roleId
	 * @return is the user a member of this role?
	 */
	@WebMethod
	public boolean isMemberOfRole(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "roleId", targetNamespace = "") final String roleId
		);
	
	/**
	 * @param userId
	 * @param organizationId
	 * @return is the user a member of this organization?
	 */
	@WebMethod
	public boolean isMemberOfOrganization(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "organizationId", targetNamespace = "") final String organizationId
		);
	
	/**
	 * @param userId
	 * @param resourceId
	 * @param rightId
	 * @return is the user entitled to this resource with the given right?
	 */
	@WebMethod
	public boolean isUserEntitledToResourceWithRight(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "rightId", targetNamespace = "") final String rightId
			);
	
	/**
	 * @param userId
	 * @param groupId
	 * @param rightId
	 * @return is the user a member of this group with the given right?
	 */
	@WebMethod
	public boolean isMemberOfGroupWithRight(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "groupId", targetNamespace = "") final String groupId,
			@WebParam(name = "rightId", targetNamespace = "") final String rightId
	);
	
	/**
	 * @param userId
	 * @param roleId
	 * @param rightId
	 * @return is the user a member of this role with the given right?
	 */
	@WebMethod
	public boolean isMemberOfRoleWithRight(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "roleId", targetNamespace = "") final String roleId,
			@WebParam(name = "rightId", targetNamespace = "") final String rightId
		);
	
	/**
	 * @param userId
	 * @param organizationId
	 * @param rightId
	 * @return is the user a member of this organization with the given right?
	 */
	@WebMethod
	public boolean isMemberOfOrganizationWithRight(
			@WebParam(name = "userId", targetNamespace = "") final String userId,
			@WebParam(name = "organizationId", targetNamespace = "") final String organizationId,
			@WebParam(name = "rightId", targetNamespace = "") final String rightId
		);
	
	/**
	 * @param userId
	 * @return all resources for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<AuthorizationResource> getResourcesForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all groups for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<AuthorizationGroup> getGroupsForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all roles for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<AuthorizationRole> getRolesForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all organizations for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<AuthorizationOrganization> getOrganizationsForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * This method should NOT be called, as the internal ESB mechanisms will do this.  However, just in case, it's here.
	 * This method will refresh the cache
	 */
	@WebMethod
	public void refreshCache();
}
