package org.openiam.idm.srvc.role.service;

import org.openiam.idm.searchbeans.MembershipRoleSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.List;

/**
 * Interface permitting the management of Roles and related objects such as
 * groups and users.
 *
 * @author Suneet Shah
 * @version 1
 */
public interface RoleDataService {
	
    public RoleEntity getRole(String roleId);
    public RoleEntity getRoleByName(final String roleName);
    
    public void saveRole(final RoleEntity role);
    
    public void removeRole(String roleId);

    public void savePolicy(RolePolicyEntity rPolicy);

    /**
     * Returns a single RolePolicy object based on the attributeId.
     *
     * @param rolePolicyId
     * @return
     */
    public RolePolicyEntity getRolePolicy(String rolePolicyId);

    /**
     * Removes a RolePolicy specified by the rPolicy parameter.
     *
     * @param rolePolicyId
     */
    public void removeRolePolicy(final String rolePolicyId);


    /** * Attribute Methods ****** */

    /**
     * Adds an attribute to the Role object.
     *
     * @param attribute
     */
    public void saveAttribute(RoleAttributeEntity attribute);

    /**
     * Removes a RoleAttribute specified by the attribute.
     *
     * @param roleAttributeId
     */
    public void removeAttribute(final String roleAttributeId);

    /** * Role-Group Methods ****** */

    /**
     * Returns an array of Role objects that are linked to a Group Returns null
     * if no roles are found.
     *
     * @param groupId
     * @return
     */
//   public List<RoleEntity> getRolesInGroup(String groupId, final int from, final int size);
//   public int getNumOfRolesForGroup(final String groupId);


    /**
     * This method adds particular roleId to a particular group.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addRoleToGroup(domainId, roleId, groupId);<br>
     * </code>
     *
     * @param groupId  The group for which the roleId is to be added .
     * @param roleId The roleId which is to be added to the group.
     */
    public void addGroupToRole(String roleId, String groupId);

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId
     * @param groupId
     */
    public void removeGroupFromRole(String roleId, String groupId);
    
    /**
     * Returns a list of UserRole objects
     *
     * @param userId
     * @return
     */
    public List<UserRoleEntity> getUserRolesForUser(final String userId, final int from, final int size);
    
    
//    public List<RoleEntity> getRolesForUser(final String userId, final int from, final int size);
//    public int getNumOfRolesForUser(final String userId);

    /**
     * Adds a user to a role using the UserRole object. Similar to addUserToRole, but allows you to update attributes likes start and end date.
     */
    void assocUserToRole(UserRoleEntity ur);

    void updateUserRoleAssoc(UserRoleEntity ur);
    /**
     * This method adds particular user directly to a role.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addUserToRole(domainId, roleId, userId);<br>
     * </code>
     *
     * @param roleId   The roleId to which the user will be associated.
     * @param userId   The userId to which the roleId is to be added .
     */
    public void addUserToRole(String roleId, String userId);

    /**
     * This method removes a particular user directly to a role.
     *
     * @param roleId
     * @param userId
     */
    public void removeUserFromRole(String roleId, String userId);

    /**
     * Return an array of users that are in a particular role
     *
     * @param roleId
     * @return
     */
    public List<UserEntity> getUsersInRole(final String roleId, final int from, final int size);

    /**
     * Returns an array of Role objects that indicate the Roles a user is
     * associated to.
     *
     * @param userId
     * @return
     */
    public List<RoleEntity> getUserRoles(final String userId, final int from, final int size);

    /**
     * Returns a list of roles that a user belongs to. Roles can be hierarchical and this operation traverses the tree to roles that are in the
     * hierarchy.
     *
     * @param userId
     * @return
     */
    public List<Role> getUserRolesAsFlatList(final String userId);
    
    public List<RoleEntity> findBeans(final RoleSearchBean searchBean, final int from, final int size);
    
    public int countBeans(final RoleSearchBean searchBean);
    
//    public List<RoleEntity> getRolesForResource(final String resourceId, final int from, final int size);
//    public int getNumOfRolesForResource(final String resourceId);
    
    public List<RoleEntity> getChildRoles(final MembershipRoleSearchBean searchBean, final int from, final int size);
    public int getNumOfChildRoles(final MembershipRoleSearchBean searchBean);
    public void addChildRole(final String roleId, final String childRoleId);
    public void removeChildRole(final String roleId, final String childRoleId);
    
    public List<RoleEntity> getParentRoles(final MembershipRoleSearchBean searchBean, final int from, final int size);
    public int getNumOfParentRoles(final MembershipRoleSearchBean searchBean);
    
    public UserRoleEntity getUserRole(final String userId, final String roleId);

    public List<RoleEntity> getEntitlementRoles(MembershipRoleSearchBean searchBean, int from, int size);
    public int getNumOfEntitlementRoles(MembershipRoleSearchBean searchBean);
}
