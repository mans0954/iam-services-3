package org.openiam.authmanager.service;

/**
 * @author Lev Bornovalov
 * The Hessian interface that remote clients can call.
 * Methods are purposefully not overloaded, as that would degrade performance
 */
public interface AuthorizationManagerHessianService {

	/**
	 * @param userId - ID of the User
	 * @param resourceId - ID of the Resource
	 * @return true if the User has access to this Resource, false otherwise
	 */
	public boolean isUserEntitledToResource(final String userId, final String resourceId);
	
	/**
	 * @param userId - ID of the User
	 * @param resourceId - ID of the Resource
	 * @param rightId - ID of the access right
	 * @return true if the User has access to this Resource, false otherwise
	 */
	public boolean isUserEntitledToResourceWithRight(final String userId, final String resourceId, final String rightId);
	
	/**
	 * @param userId - ID of the User
	 * @param groupId - ID of the Group
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserMemberOfGroup(final String userId, final String groupId);
	
	/**
	 * @param userId - ID of the User
	 * @param groupId - ID of the Group
	 * @param rightId - ID of the access right
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserMemberOfGroupWithRight(final String userId, final String groupId, final String rightId);
	
	/**
	 * @param userId - ID of the User
	 * @param roleId - ID of the Role
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserMemberOfRole(final String userId, final String roleId);
	
	
	/**
	 * @param userId - ID of the User
	 * @param roleId - ID of the Role
	 * @param rightId - ID of the access right
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserMemberOfRoleWithRight(final String userId, final String roleId, final String rightId);
	
	/**
	 * @param userId - ID of the User
	 * @param organizationId - ID of the Role
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserMemberOfOrganization(final String userId, final String organizationId);
	
	
	/**
	 * @param userId - ID of the User
	 * @param organizationId - ID of the Role
	 * @param rightId - ID of the access right
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserMemberOfOrganizationWithRight(final String userId, final String organizationId, final String rightId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Resource IDs that this User is entitled to
	 */
	public String[] getResourceIdsForUserWithId(final String userId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Resource Names that this User is entitled to
	 */
	public String[] getResourceNamesForUserWithId(final String userId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Group IDs that this User is a member of
	 */
	public String[] getGroupIdsForUserWithId(final String userId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Group Names that this User is a member of
	 */
	public String[] getGroupNamesForUserWithId(final String userId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Role IDs that this User is a member of
	 */
	public String[] getRoleIdsForUserWithId(final String userId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Role Names that this User is a member of
	 */
	public String[] getRoleNamesForUserWithId(final String userId);
}
