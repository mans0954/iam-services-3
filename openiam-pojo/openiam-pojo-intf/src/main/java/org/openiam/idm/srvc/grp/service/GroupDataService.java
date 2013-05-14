package org.openiam.idm.srvc.grp.service;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MembershipGroupSearchBean;
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

	public void saveGroup(final GroupEntity group);
	
	public void deleteGroup(final String groupId);
	
	public int getNumOfChildGroups(final MembershipGroupSearchBean searchBean);
	public List<GroupEntity> getChildGroups(final MembershipGroupSearchBean searchBean, final int from, final int size);
	
	public int getNumOfParentGroups(final MembershipGroupSearchBean searchBean);
	public List<GroupEntity> getParentGroups(final MembershipGroupSearchBean searchBean, final int from, final int size);
	
    public GroupEntity getGroup(String grpId);
    public GroupEntity getGroupByName(final String groupName);
    
    public List<Group> getCompiledGroupsForUser(final String userId);
    
//    public List<GroupEntity> getGroupsForResource(final String resourceId, final int from, final int size);
//    public int getNumOfGroupsForResource(final String resourceId);

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

    
//    public List<GroupEntity> getGroupsForUser(final String userId, final int from, final int size);
//    public int getNumOfGroupsForUser(final String userId);

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

    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param searchBean
     * @return
     */
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final int from, final int size);
    
    public int countBeans(final GroupSearchBean searchBean);
    
//    public List<GroupEntity> getGroupsForRole(final String roleId, final int from, final int size);
//    public int getNumOfGroupsForRole(final String roleId);

    public List<GroupEntity> getEntitlementGroups(MembershipGroupSearchBean searchBean, int from, int size);
    public int getNumOfEntitlementGroups(MembershipGroupSearchBean searchBean);


    public void addChildGroup(final String groupId, final String childGroupId);
    public void removeChildGroup(final String groupId, final String childGroupId);
    
    public UserGroupEntity getRecord(final String userId, final String groupId);

}