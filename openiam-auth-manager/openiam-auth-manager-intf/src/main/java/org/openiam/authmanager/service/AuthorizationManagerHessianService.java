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
	public boolean isUserWithIdEntitledToResourceWithId(final String userId, final String resourceId);
	
	/**
	 * @param userId - ID of the User
	 * @param resourceName - Name of the Resource
	 * @return true if the user has access to this Resource, false otherwise
	 */
	public boolean isUserWithIdEntitledToResourceWithName(final String userId, final String resourceName);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param resourceId - ID of the Resource
	 * @return true if the user has access to this Resource, false otherwise
	 */
	public boolean isUserWithLoginEntitledToResourceWithId(final String domain, final String login, final String managedSysId, final String resourceId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param resourceName - Name of the Resource
	 * @return true if the user has access to this Resource, false otherwise
	 */
	public boolean isUserWithLoginEntitledToResourceWithName(final String domain, final String login, final String managedSysId, final String resourceName);
	
	/**
	 * @param userId - ID of the User
	 * @param groupId - ID of the Group
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserWithIdMemberOfGroupWithId(final String userId, final String groupId);
	
	/**
	 * @param userId - ID of the User
	 * @param groupName - Name of the Group
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserWithIdMemberOfGroupWithName(final String userId, final String groupName);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param groupId - ID of the group
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserWithLoginMemberOfGroupWithId(final String domain, final String login, final String managedSysId, final String groupId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param groupName - Name of the group
	 * @return true if the User is part of the Group, false otherwise
	 */
	public boolean isUserWithLoginMemberOfGroupWithName(final String domain, final String login, final String managedSysId, final String groupName);
	
	/**
	 * @param userId - ID of the User
	 * @param roleId - ID of the Role
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserWithIdMemberOfRoleWithId(final String userId, final String roleId);
	
	/**
	 * @param userId - ID of the User
	 * @param roleName - Name of the Role
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserWithIdMemberOfRoleWithName(final String userId, final String roleName);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param roleId - ID of the Role
	 * @return true if the User is part of the Role, false otherwise
	 */
	public boolean isUserWithLoginMemberOfRoleWithId(final String domain, final String login, final String managedSysId, final String roleId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @param roleName - Name of the Role
	 * @return true if the User is part of the Role, false otherwise 
	 */
	public boolean isUserWithLoginMemberOfRoleWithName(final String domain, final String login, final String managedSysId, final String roleName);
	
	/**
	 * @param userId - ID of the User
	 * @return the Resource IDs that this User is entitled to
	 */
	public String[] getResourceIdsForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Resource IDs that this User is entitled to
	 */
	public String[] getResourceIdsForUserWithLogin(final String domain, final String login, final String managedSysId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Resource Names that this User is entitled to
	 */
	public String[] getResourceNamesForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Resource Names that this User is entitled to
	 */
	public String[] getResourceNamesForUserWithLogin(final String domain, final String login, final String managedSysId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Group IDs that this User is a member of
	 */
	public String[] getGroupIdsForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Group IDs that this User is a member of
	 */
	public String[] getGroupIdsForUserWithLogin(final String domain, final String login, final String managedSysId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Group Names that this User is a member of
	 */
	public String[] getGroupNamesForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Group Names that this User is a member of
	 */
	public String[] getGroupNamesForUserWithLogin(final String domain, final String login, final String managedSysId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Role IDs that this User is a member of
	 */
	public String[] getRoleIdsForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Role IDs that this User is a member of
	 */
	public String[] getRoleIdsForUserWithLogin(final String domain, final String login, final String managedSysId);
	
	/**
	 * @param userId - ID of the User
	 * @return the Role Names that this User is a member of
	 */
	public String[] getRoleNamesForUserWithId(final String userId);
	
	/**
	 * @param domain - domain of this login
	 * @param login - login used by the user
	 * @param managedSysId - managedSysId of this login
	 * @return the Role Names that this User is a member of
	 */
	public String[] getRoleNamesForUserWithLogin(final String domain, final String login, final String managedSysId);
}
