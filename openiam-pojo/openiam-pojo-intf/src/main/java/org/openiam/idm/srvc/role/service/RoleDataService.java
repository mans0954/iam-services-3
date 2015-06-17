package org.openiam.idm.srvc.role.service;

import org.openiam.base.TreeObjectId;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

import java.util.List;
import java.util.Set;

/**
 * Interface permitting the management of Roles and related objects such as
 * groups and users.
 *
 * @author Suneet Shah
 * @version 1
 */
public interface RoleDataService {

	@Deprecated
    RoleEntity getRole(String roleId);
	
	@Deprecated
    RoleEntity getRole(String roleId, String requesterId);
	
	@Deprecated
    RoleEntity getRoleByName(final String roleName, String requesterId);
    

    Role getRoleDtoByName(final String roleName, String requesterId);

    RoleEntity getRoleLocalized(final String roleId, final String requestorId, final LanguageEntity language);
    
    void saveRole(final RoleEntity role, final String requestorId) throws BasicDataServiceException;

    void addRequiredAttributes(RoleEntity role);
    
    void removeRole(String roleId);


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
    @Deprecated
    List<RoleEntity> getRolesInGroup(String groupId, String requesterId, final int from, final int size);
    
    @Deprecated
    int getNumOfRolesForGroup(final String groupId, String requesterId);


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
    void addGroupToRole(String roleId, String groupId);
    
    void validateGroup2RoleAddition(String roleId, String groupId) throws BasicDataServiceException;

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId
     * @param groupId
     */
    void removeGroupFromRole(String roleId, String groupId);


    @Deprecated
    List<RoleEntity> getRolesForUser(final String userId, String requesterId, final int from, final int size);

    List<Role> getRolesDtoForUser(final String userId, String requesterId, final int from, final int size);

    
    @Deprecated
    int getNumOfRolesForUser(final String userId, String requesterId);

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
    void addUserToRole(String roleId, String userId);

    /**
     * This method removes a particular user directly to a role.
     *
     * @param roleId
     * @param userId
     */
    void removeUserFromRole(String roleId, String userId);

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
    List<RoleEntity> getUserRoles(final String userId, String requesterId, final int from, final int size);

    /**
     * Returns a list of roles that a user belongs to. Roles can be hierarchical and this operation traverses the tree to roles that are in the
     * hierarchy.
     *
     * @param userId
     * @return
     */
    List<Role> getUserRolesAsFlatList(final String userId);
    
    List<RoleEntity> findBeans(final RoleSearchBean searchBean, final String requesterId, final int from, final int size);
    
    int countBeans(final RoleSearchBean searchBean, final String requesterId);

    List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue);
    
    @Deprecated
    List<RoleEntity> getRolesForResource(final String resourceId, final String requesterId, final int from, final int size);
    
    @Deprecated
    int getNumOfRolesForResource(final String resourceId, final String requesterId);
    
    @Deprecated
    List<RoleEntity> getChildRoles(final String roleId, final String requesterId, final int from, final int size);
    @Deprecated
    int getNumOfChildRoles(final String roleId, final String requesterId);
    void addChildRole(final String roleId, final String childRoleId, final Set<String> rights);
    void removeChildRole(final String roleId, final String childRoleId);
    
    @Deprecated
    List<RoleEntity> getParentRoles(final String roleId, final String requesterId, final int from, final int size);
    
    @Deprecated
    int getNumOfParentRoles(final String roleId, final String requesterId);

    void validateRole2RoleAddition(final String parentId, final String memberId, final Set<String> rights) throws BasicDataServiceException;
    
    Role getRoleDTO(final String roleId);

    List<RoleAttribute> getRoleAttributes(final String roleId);

    void addAttribute(RoleAttributeEntity attribute);
    void updateAttribute(RoleAttributeEntity attribute);


    List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, final String requesterId);

    void rebuildRoleHierarchyCache();
    
    boolean hasChildEntities(String roleId);
}
