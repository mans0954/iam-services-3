package org.openiam.idm.srvc.grp.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

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
     * @param group
     * @return - Number of records created. 0 if add failed to add any records
     */

    @WebMethod
    public Response saveGroup(final @WebParam(name = "group", targetNamespace = "") Group group);

    /**
     * This method retrieves an existing group object. Dependent objects such as
     * users are not retrieved. Null is returned if the groupId is not found.
     *
     * @param groupId
     */
    @WebMethod
    public Group getGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * This method removes group for a particular grpId. If the group has sub
     * groups they will be deleted as well. For example:
     * <p/>
     * <code>
     * grpManager.removeGroup(grpId);<br>
     * </code>
     *
     * @param groupId The grpId to be removed.
     * @return - Returns the number of records removed. 0 if no records were removed.
     */
    @WebMethod
    public Response deleteGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId);
    
    
    @WebMethod
    public int getNumOfChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns all the groups that are the immediate children of the parent
     * group. For example:
     * <p/>
     * <code>
     * List allGrp = grpManager.getChildGroups(parentGroupId, true);<br>
     * </code>
     *
     * @param searchBean
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    public List<Group> getChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                      final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    								  final @WebParam(name = "from", targetNamespace = "") int from,
    								  final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public int getNumOfParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                    final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    
    /**
     * Returns the parent Group object for the groupId that is passed in. If no
     * parent group is found, the system return null.
     *
     * @param searchBean
     * @return
     */
    @WebMethod
    public List<Group> getParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    								   final @WebParam(name = "from", targetNamespace = "") int from,
    								   final @WebParam(name = "size", targetNamespace = "") int size);

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
    public Response isUserInGroup( @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   @WebParam(name = "userId", targetNamespace = "") String userId);

   
    /**
     * This method adds the user to a group .<br>
     * For example:
     * <p/>
     * <code>
     * grpManager.addUserToGroup(groupId,userId);<br>
     * </code>
     *
     * @param userId User to be added to group.
     * @param groupId  Group to which user will be added .
     */
    @WebMethod
    public Response addUserToGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   @WebParam(name = "userId", targetNamespace = "") String userId);


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
    @WebMethod
    public Response removeUserFromGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
                                        @WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute
     */
    @WebMethod
    public Response addAttribute(@WebParam(name = "attribute", targetNamespace = "") GroupAttribute attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId
     */
    @WebMethod
    public Response removeAttribute(@WebParam(name = "attributeId", targetNamespace = "") String attributeId);
    
    
    @WebMethod
    public List<Group> findBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    							 final @WebParam(name = "from", targetNamespace = "") int from,
    							 final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public int countBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<Group> getGroupsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        @WebParam(name = "from") int from,
                                        @WebParam(name = "size") int size);

    public int getNumOfGroupsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<Group> getGroupsForResource(final @WebParam(name = "resourceId") String resourceId,
                                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    										final @WebParam(name = "from", targetNamespace = "") int from,
    										final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public int getNumOfGroupsforResource(final @WebParam(name = "resourceId") String resourceId,
                                         final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<Group> getGroupsForRole(final @WebParam(name = "roleId") String roleId,
                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    									final @WebParam(name = "from", targetNamespace = "") int from,
    									final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public int getNumOfGroupsForRole(final @WebParam(name = "roleId") String roleId,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    
    @WebMethod
    public Response addChildGroup(final @WebParam(name = "groupId") String groupId, 
    							  final @WebParam(name = "childGroupId") String childGroupId);
    
    
    @WebMethod
    public Response removeChildGroup(final @WebParam(name = "groupId") String groupId, 
    							 	 final @WebParam(name = "childGroupId") String childGroupId);
    
    
    @WebMethod
	public Response canAddUserToGroup(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
									  final @WebParam(name = "groupId", targetNamespace = "") String groupId);
	
	@WebMethod
	public Response canRemoveUserFromGroup(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
										   final @WebParam(name = "groupId", targetNamespace = "") String groupId);
}