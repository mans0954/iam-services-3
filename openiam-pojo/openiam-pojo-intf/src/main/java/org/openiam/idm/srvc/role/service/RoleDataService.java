package org.openiam.idm.srvc.role.service;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.*;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

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
    
    public void saveRole(final RoleEntity role);
    
    public void removeRole(String roleId);

    public void savePolicy(RolePolicyEntity rPolicy);

    /**
     * Returns a single RolePolicy object based on the attributeId.
     *
     * @param attrId
     * @return
     */
    public RolePolicyEntity getRolePolicy(String rolePolicyId);

    /**
     * Removes a RolePolicy specified by the rPolicy parameter.
     *
     * @param attr
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
     * @param attr
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
   public List<RoleEntity> getRolesInGroup(String groupId, final int from, final int size);


    /**
     * This method adds particular roleId to a particular group.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addRoleToGroup(domainId, roleId, groupId);<br>
     * </code>
     *
     * @param grpId  The group for which the roleId is to be added .
     * @param roleId The roleId which is to be added to the group.
     */
    public void addGroupToRole(String roleId, String groupId);

    /**
     * Removes the association between a single group and role.
     *
     * @param domainId
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


    /**
     * This method adds particular user directly to a role.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addUserToRole(domainId, roleId, userId);<br>
     * </code>
     *
     * @param domainId
     * @param roleId   The roleId to which the user will be associated.
     * @param userId   The userId to which the roleId is to be added .
     */
    public void addUserToRole(String roleId, String userId);

    /**
     * This method removes a particular user directly to a role.
     *
     * @param domainId
     * @param roleId
     * @param userId
     */
    public void removeUserFromRole(String roleId, String userId);

    /**
     * Return an array of users that are in a particular role
     *
     * @param domainId
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
    
    public List<RoleEntity> findBeans(final RoleEntity example, final int from, final int size);
    
    public int countBeans(final RoleEntity example);
}
