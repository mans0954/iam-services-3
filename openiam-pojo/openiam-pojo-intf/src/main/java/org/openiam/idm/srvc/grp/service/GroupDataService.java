package org.openiam.idm.srvc.grp.service;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;

import java.util.List;

/**
 * <code>GroupDataService</code> provides a service to manage groups as well
 * as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 *
 * @author Suneet Shah
 * @version 2.0
 */

public interface GroupDataService {
    public GroupEntity getGroup(final String grpId);
    public GroupEntity getGroup(final String grpId, final String requesterId);
    public GroupEntity getGroupByName(final String groupName, final String requesterId);
    public List<Group> getCompiledGroupsForUser(final String userId);
    public UserGroupEntity getRecord(final String userId, final String groupId, final String requesterId);
    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param searchBean
     * @return
     */
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, String requesterId, final int from, final int size);
    public List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size);
    public List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size);
    public List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size);
    public List<GroupEntity> getGroupsForUser(final String userId, String requesterId, final int from, final int size);
    public List<GroupEntity> getGroupsForRole(final String roleId, String requesterId, final int from, final int size);

    public int getNumOfGroupsForRole(final String roleId, final String requesterId);
    public int getNumOfGroupsForUser(final String userId, final String requesterId);
    public int getNumOfGroupsForResource(final String resourceId, final String requesterId);
    public int getNumOfChildGroups(final String groupId, final String requesterId);
    public int getNumOfParentGroups(final String groupId, final String requesterId);
    public int countBeans(final GroupSearchBean searchBean, final String requesterId);


	public void saveGroup(final GroupEntity group);
	
	public void deleteGroup(final String groupId);

    /**
     * Returns true or false depending on whether a user belongs to a particular
     * group or not. If a group has been marked as "Inherits from Parent", then
     * the system will check to see if the user belongs to one of the parent
     * group objects.
     *
     * @param groupId
     * @param userId
     * @return
     */
    public boolean isUserInCompiledGroupList(String groupId, String userId);

    /**
     * This method adds the user to a group .<br>
     * For example:
     * <p/>
     * <code>
     * grpManager.addUserToGroup(groupId,userId);<br>
     * </code>
     *
     * @param userId User to be added to group.
     * @param grpId  Group to which user will be added .
     */
    public void addUserToGroup(String grpId, String userId);

    /**
     * This method removes user from a group .<br>
     * For example:
     * <p/>
     * <code>
     * grpManager.removeUserGroup(groupId,userId);<br>
     * </code>
     *
     * @param groupId  Group from where user would be removed .
     * @param userId User which is to be removed from group .
     */
    public void removeUserFromGroup(String groupId, String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute
     */
    public void saveAttribute(GroupAttributeEntity attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId
     */
    public void removeAttribute(final String attributeId);

    public void addChildGroup(final String groupId, final String childGroupId);
    public void removeChildGroup(final String groupId, final String childGroupId);

}