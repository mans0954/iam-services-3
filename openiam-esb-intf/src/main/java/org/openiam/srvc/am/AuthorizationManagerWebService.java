package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Set;


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
	public Set<ResourceAuthorizationRight> getResourcesForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all groups for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<GroupAuthorizationRight> getGroupsForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all roles for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<RoleAuthorizationRight> getRolesForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * @param userId
	 * @return all organizations for the requested user.  No distinction is made between direct or hierarchial membership
	 */
	@WebMethod
	public Set<OrganizationAuthorizationRight> getOrganizationsForUser(
			@WebParam(name = "userId", targetNamespace = "") final String userId
			);
	
	/**
	 * This method should NOT be called, as the internal ESB mechanisms will do this.  However, just in case, it's here.
	 * This method will refresh the cache
	 */
	@WebMethod
	public void refreshCache();
}
