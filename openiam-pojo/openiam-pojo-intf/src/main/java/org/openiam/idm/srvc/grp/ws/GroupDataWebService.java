package org.openiam.idm.srvc.grp.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.user.ws.UserListResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * <code>GroupDataWebService</code> provides a web service interface to manage groups as well
 * as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 *
 * @author Suneet Shah
 * @version 2.0
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/grp/service", name = "GroupDataWebService")
public interface GroupDataWebService {
   
	/**
     * This method creates a new group For example:
     * <p/>
     * <code>
     * Group grp = new Group();
     * grp.setGrpId(groupId);
     * grp.setGrpName("Test Group");
     * <p/>
     * grpManager.addGroup(grpValue);<br>
     * </code>
     *
     * @param Group
     * @return - Number of records created. 0 if add failed to add any records
     */

    @WebMethod
    public GroupResponse addGroup(final @WebParam(name = "group", targetNamespace = "") Group group);
    
    /**
     * This method updates and existing group in database. For example:
     * <p/>
     * <code>
     * grpManager.updateGroup(grpValue);<br>
     * </code>
     *
     * @param grp
     * @return - Number of records created. 0 if update failed to update any records
     */
    @WebMethod
    public GroupResponse updateGroup(final @WebParam(name = "group", targetNamespace = "") Group group);

    /**
     * This method retrieves an existing group object. Dependent objects such as
     * users are not retrieved. Null is returned if the groupId is not found.
     *
     * @param grpId
     */
    @WebMethod
    public GroupResponse getGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * This method removes group for a particular grpId. If the group has sub
     * groups they will be deleted as well. For example:
     * <p/>
     * <code>
     * grpManager.removeGroup(grpId);<br>
     * </code>
     *
     * @param grpId The grpId to be removed.
     * @return - Returns the number of records removed. 0 if no records were removed.
     */
    @WebMethod
    public Response deleteGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * Returns all the groups that are the immediate children of the parent
     * group. For example:
     * <p/>
     * <code>
     * List allGrp = grpManager.getChildGroups(parentGroupId, true);<br>
     * </code>
     *
     * @param parentGroupId
     * @param subGroups     -
     *                      true to retrieve the group hierarchy. false, to retrieve just
     *                      the current level of groups
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    public GroupListResponse getChildGroups(final @WebParam(name = "parentGroupId", targetNamespace = "") String parentGroupId);

    /**
     * Returns the parent Group object for the groupId that is passed in. If no
     * parent group is found, the system return null.
     *
     * @param parentGroupId
     * @param dependants    -
     *                      True indicates that dependant objects will be loaded as well.
     * @return
     */
    @WebMethod
    public GroupListResponse getParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId);

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
    @WebMethod
    public Response isUserInGroup(
            @WebParam(name = "groupId", targetNamespace = "")
            String groupId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * Returns List of Group objects that represent the groups this user belongs
     * to. For example:
     * <p/>
     * <code>
     * grpManager.getUserInGroups(userId);<br>
     * </code>
     *
     * @param userId
     * @return
     */
    @WebMethod
    public GroupListResponse getUserInGroups(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "from") String from,
            @WebParam(name = "size") String size);


    /**
     * Returns List of Groups that a user belongs to. The list of Groups is returns a flat list.  This operation will traverse the
     * group hierarchy from the bottom up to return the list of groups  a user belongs to.
     *
     * @param userId
     * @return
     */
    @WebMethod
    public GroupListResponse getUserInGroupsAsFlatList(final @WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * This method gets all users assigned to a particular group .<br>
     * For example:
     * <p/>
     * <code>
     * Set userList = grpManager.getUsersByGroup(groupId);<br>
     * </code>
     *
     * @param grpId The group to which users belong .
     * @return List of User object .
     */
    @WebMethod
    public UserListResponse getUsersByGroup(
    		final @WebParam(name = "groupId", targetNamespace = "") String groupId,
    		final @WebParam(name = "from") int from,
    		final @WebParam(name = "size") int size);

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
    @WebMethod
    public Response addUserToGroup(
            @WebParam(name = "groupId", targetNamespace = "")
            String groupId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);


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
    @WebMethod
    public Response removeUserFromGroup(
            @WebParam(name = "groupId", targetNamespace = "")
            String groupId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute
     */
    @WebMethod
    public Response addAttribute(
            @WebParam(name = "attribute", targetNamespace = "")
            GroupAttribute attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param userId
     * @param attributeId
     */
    @WebMethod
    public Response removeAttribute(
            @WebParam(name = "attribute", targetNamespace = "")
            GroupAttribute attribute);
}