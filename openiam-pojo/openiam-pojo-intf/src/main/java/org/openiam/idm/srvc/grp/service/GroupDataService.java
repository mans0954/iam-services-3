package org.openiam.idm.srvc.grp.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

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
	
	public int getNumOfChildGroups(final String groupId);
	public List<GroupEntity> getChildGroups(final String groupId, final int from, final int size);
	
	public int getNumOfParentGroups(final String groupId);
	public List<GroupEntity> getParentGroups(final String groupId, final int from, final int size);
	
    public GroupEntity getGroup(String grpId);
    
    public List<Group> getCompiledGroupsForUser(final String userId);
    
    public List<GroupEntity> getGroupsForResource(final String resourceId, final int from, final int size);
    public int getNumOfGroupsForResource(final String resourceId);

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
     * Returns List of Groups that a user belongs to. Since groups can be hierarchical, its possible that additional groups will be found
     * in the subGroups property.  This operation will traverse the group hierarchy from the bottom up to return the list of groups
     * a user belongs to.  For example:
     * <p/>
     * <code>
     * grpManager.getUserInGroups(userId);<br>
     * </code>
     *
     * @param userId
     * @return
     */
    public List<GroupEntity> getGroupsForUser(final String userId, final int from, final int size);

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
     * @param grpId  Group from where user would be removed .
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
     * @param userId
     * @param attributeId
     */
    public void removeAttribute(final String attributeId);

    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param search
     * @return
     */
    public List<GroupEntity> findBeans(final GroupEntity entity, final int from, final int size);
    
    public int countBeans(final GroupEntity entity);
    
    public List<GroupEntity> getGroupsForRole(final String roleId, final int from, final int size);
    public int getNumOfGroupsForRole(final String roleId);
    
    public void addChildGroup(final String groupId, final String childGroupId);
    public void removeChildGroup(final String groupId, final String childGroupId);

}