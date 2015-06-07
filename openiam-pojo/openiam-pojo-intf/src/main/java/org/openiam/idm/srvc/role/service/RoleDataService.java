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
    public RoleEntity getRole(String roleId);
	
	@Deprecated
    public RoleEntity getRole(String roleId, String requesterId);
	
	@Deprecated
    public RoleEntity getRoleByName(final String roleName, String requesterId);
    

    public Role getRoleDtoByName(final String roleName, String requesterId);

    public RoleEntity getRoleLocalized(final String roleId, final String requestorId, final LanguageEntity language);
    
    public void saveRole(final RoleEntity role, final String requestorId) throws BasicDataServiceException;

    public void addRequiredAttributes(RoleEntity role);
    
    public void removeRole(String roleId);


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
    public void addGroupToRole(String roleId, String groupId, Set<String> rightIds);
    
    public void validateGroup2RoleAddition(String roleId, String groupId) throws BasicDataServiceException;

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId
     * @param groupId
     */
    public void removeGroupFromRole(String roleId, String groupId);

    public List<Role> getRolesDtoForUser(final String userId, String requesterId, final int from, final int size);

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
    public void addUserToRole(String roleId, String userId, Set<String> rightIds);

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
     * Returns a list of roles that a user belongs to. Roles can be hierarchical and this operation traverses the tree to roles that are in the
     * hierarchy.
     *
     * @param userId
     * @return
     */
    public List<Role> getUserRolesAsFlatList(final String userId);
    
    public List<RoleEntity> findBeans(final RoleSearchBean searchBean, final String requesterId, final int from, final int size);
    
    public int countBeans(final RoleSearchBean searchBean, final String requesterId);

    public List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue);
    
    public void addChildRole(final String roleId, final String childRoleId, final Set<String> rights);
    public void removeChildRole(final String roleId, final String childRoleId);
    
    public void validateRole2RoleAddition(final String parentId, final String memberId, final Set<String> rights) throws BasicDataServiceException;
    
    public Role getRoleDTO(final String roleId);

    public List<RoleAttribute> getRoleAttributes(final String roleId);

    public void addAttribute(RoleAttributeEntity attribute);
    public void updateAttribute(RoleAttributeEntity attribute);


    List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, final String requesterId);

    void rebuildRoleHierarchyCache();
    
    public boolean hasChildEntities(String roleId);
}
