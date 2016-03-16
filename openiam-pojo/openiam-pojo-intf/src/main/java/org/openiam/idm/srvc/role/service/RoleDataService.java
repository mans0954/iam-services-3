package org.openiam.idm.srvc.role.service;

import org.openiam.base.TreeObjectId;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

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
    public RoleEntity getRole(String roleId, String requesterId);
    public RoleEntity getRoleByName(final String roleName, String requesterId);

    public Role getRoleDtoByName(final String roleName, String requesterId);

    public RoleEntity getRoleLocalized(final String roleId, final String requestorId, final LanguageEntity language);

    public Role getRoleDtoLocalized(final String roleId, final String requesterId, final LanguageEntity language);
    
    public void saveRole(final RoleEntity role, final String requestorId) throws BasicDataServiceException;

    public void addRequiredAttributes(RoleEntity role);
    
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
    //public void saveAttribute(RoleAttributeEntity attribute);

    /**
     * Removes a RoleAttribute specified by the attribute.
     *
     * @param roleAttributeId
     */
    //public void removeAttribute(final String roleAttributeId);

    /** * Role-Group Methods ****** */

    /**
     * Returns an array of Role objects that are linked to a Group Returns null
     * if no roles are found.
     *
     * @param groupId
     * @return
     */
     public List<RoleEntity> getRolesInGroup(String groupId, String requesterId, final int from, final int size);

     public List<Role> getRolesDtoInGroup(final String groupId, final String requesterId, int from, int size);

     public int getNumOfRolesForGroup(final String groupId, String requesterId);


    /**
     * This method adds particular roleId to a particular group.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addRoleToGroup(roleId, groupId);<br>
     * </code>
     *
     * @param groupId  The group for which the roleId is to be added .
     * @param roleId The roleId which is to be added to the group.
     */
    public void addGroupToRole(String roleId, String groupId);
    
    public void validateGroup2RoleAddition(String roleId, String groupId) throws BasicDataServiceException;

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId
     * @param groupId
     */
    public void removeGroupFromRole(String roleId, String groupId);


    public List<RoleEntity> getRolesForUser(final String userId, String requesterId, final int from, final int size);

    public List<Role> getRolesDtoForUser(final String userId, String requesterId, final int from, final int size);

    public int getNumOfRolesForUser(final String userId, String requesterId);

    /**
     * Adds a user to a role using the UserRole object. Similar to addUserToRole, but allows you to update attributes likes start and end date.
     */
    void assocUserToRole(String userId, String roleId);

    /**
     * This method adds particular user directly to a role.<br>
     * For example:
     * <p/>
     * <code>
     * roleService.addUserToRole(roleId, userId);<br>
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

//    /**
//     * Return an array of users that are in a particular role
//     *
//     * @param roleId
//     * @return
//     */
//    public List<UserEntity> getUsersInRole(final String roleId, final String requesterId, final int from, final int size);

    /**
     * Returns an array of Role objects that indicate the Roles a user is
     * associated to.
     *
     * @param userId
     * @return
     */
    public List<RoleEntity> getUserRoles(final String userId, String requesterId, final int from, final int size);

    /**
     * Returns a list of roles that a user belongs to. Roles can be hierarchical and this operation traverses the tree to roles that are in the
     * hierarchy.
     *
     * @param userId
     * @return
     */
    public List<Role> getUserRolesAsFlatList(final String userId);
    
    public List<RoleEntity> findBeans(final RoleSearchBean searchBean, final String requesterId, final int from, final int size);

    public List<Role> findBeansDto(RoleSearchBean searchBean, final String requesterId, int from, int size);
    
    public int countBeans(final RoleSearchBean searchBean, final String requesterId);

    public List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue);

    public List<Role> findRolesDtoByAttributeValue(String attrName, String attrValue);

    public List<Role> findRolesDtoByAttributeValue(String attrName, String attrValue,boolean deepCopy);

    public List<RoleEntity> getRolesForResource(final String resourceId, final String requesterId, final int from, final int size);

    public List<Role> getRolesDtoForResource(final String resourceId, final String requesterId, final int from, final int size);

    public int getNumOfRolesForResource(final String resourceId, final String requesterId);
    
    public List<RoleEntity> getChildRoles(final String roleId, final String requesterId, final int from, final int size);

    public List<Role> getChildRolesDto(final String id, final String requesterId, int from, int size);

    public int getNumOfChildRoles(final String roleId, final String requesterId);
    public void addChildRole(final String roleId, final String childRoleId);
    public void removeChildRole(final String roleId, final String childRoleId);
    
    public List<RoleEntity> getParentRoles(final String roleId, final String requesterId, final int from, final int size);

    public List<Role> getParentRolesDto(final String id, final String requesterId, int from, int size);

    public int getNumOfParentRoles(final String roleId, final String requesterId);

    public void validateRole2RoleAddition(final String parentId, final String memberId) throws BasicDataServiceException;
    
    public Role getRoleDTO(final String roleId);

    public List<RoleAttribute> getRoleAttributes(final String roleId);

    public void addAttribute(RoleAttributeEntity attribute);
    public void updateAttribute(RoleAttributeEntity attribute);


    List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, final String requesterId);

    void rebuildRoleHierarchyCache();
}
