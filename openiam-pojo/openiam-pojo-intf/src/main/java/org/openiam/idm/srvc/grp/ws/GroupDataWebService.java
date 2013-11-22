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
     * This method creates a new group or update existed one. For example:
     * <p/>
     * <code>
     * Group grp = new Group();
     * grp.setGrpId(groupId);
     * grp.setGrpName("Test Group");
     * <p/>
     * grpManager.addGroup(grpValue);<br>
     * </code>
     *
     * @param group - the Group object, which should be created or updated
     * @return - a Response Object. If operation succeed then Response object contains the primary key of saved group
     * otherwise it contains error code.
     */
    @WebMethod
    public Response saveGroup(final @WebParam(name = "group", targetNamespace = "") Group group);

    /**
     * This method retrieves an existing group object. Dependent objects such as
     * users are not retrieved. Null is returned if the groupId is not found.
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return - a Group Object if it is found, otherwise null will be returned.
     */
    @WebMethod
    public Group getGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * This method removes group from openIAM database for a particular groupId.
     *
     * @param groupId The grpId to be removed.
     * @return - a Response Object which contains operation status.
     */
    @WebMethod
    public Response deleteGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * Gets the number of child groups that are direct members of this Group
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return Integer, total number of groups that are direct members of this Group
     */
    @WebMethod
    public int getNumOfChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns a paged List of child groups that are are direct members of this Group
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return a paged List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    public List<Group> getChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                      final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    								  final @WebParam(name = "from", targetNamespace = "") int from,
    								  final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Gets the number of groups that are direct parents of this Group
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of groups that are direct parents of this Group
     */
    @WebMethod
    public int getNumOfParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                    final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns a paged List of groups that are direct parents of this Group
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return a paged List of of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    public List<Group> getParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    								   final @WebParam(name = "from", targetNamespace = "") int from,
    								   final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Checks if a user belongs to a particular group or not. If a group has been marked as "Inherits from Parent", then
     * the system will check to see if the user belongs to one of the parent group objects.
     *
     * @param groupId
     * @param userId
     * @return   a Response Object which contains result of checking: true or false and operation status.
     */
    @WebMethod
    public Response isUserInGroup( @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   @WebParam(name = "userId", targetNamespace = "") String userId);

   
    /**
     * This method adds the user to a group .<br>
     *
     * @param userId UserID to be added to group.
     * @param groupId  GroupID to which user will be added.
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    public Response addUserToGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
                                   @WebParam(name = "userId", targetNamespace = "") String userId);


    /**
     * This method removes user from a group .<br>
     *
     * @param groupId  Group ID from where user would be removed .
     * @param userId User ID which is to be removed from group .
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    public Response removeUserFromGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
                                        @WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute - GroupAttribute object, which should be added
     * @return   a Response Object which contains an operation status and GroupAttribute ID.
     */
    @WebMethod
    public Response addAttribute(@WebParam(name = "attribute", targetNamespace = "") GroupAttribute attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId - GroupAttribute ID
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    public Response removeAttribute(@WebParam(name = "attributeId", targetNamespace = "") String attributeId);

    /**
     * Return a paged List of Groups based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    public List<Group> findBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    							 final @WebParam(name = "from", targetNamespace = "") int from,
    							 final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Returns total number of Groups based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of groups based on parameters, which are specified in GroupSearchBean object
     */
    @WebMethod
    public int countBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Groups directly entitled to the User specified by the userId
     * @param userId - the User ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @return a paged List of Groups directly entitled to the User specified by the userId
     */
    @WebMethod
    public List<Group> getGroupsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        @WebParam(name = "from") int from,
                                        @WebParam(name = "size") int size);
    /**
     * Gets the number of Groups directly entitled to this User specified by the userId
     * @param userId - the User ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this User specified by the userId
     */
    public int getNumOfGroupsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Groups directly entitled to the Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @return a paged List of Groups directly entitled to the Resource specified by the resourceId
     */
    @WebMethod
    public List<Group> getGroupsForResource(final @WebParam(name = "resourceId") String resourceId,
                                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    										final @WebParam(name = "from", targetNamespace = "") int from,
    										final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of Groups directly entitled to this Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this Resource specified by the resourceId
     */
    @WebMethod
    public int getNumOfGroupsforResource(final @WebParam(name = "resourceId") String resourceId,
                                         final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Groups directly entitled to the Role specified by the roleId
     * @param roleId - the Role ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @return a paged List of Groups directly entitled to the Role specified by the roleId
     */
    @WebMethod
    public List<Group> getGroupsForRole(final @WebParam(name = "roleId") String roleId,
                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    									final @WebParam(name = "from", targetNamespace = "") int from,
    									final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of Groups directly entitled to this Role specified by the roleId
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this Role specified by the roleId
     */
    @WebMethod
    public int getNumOfGroupsForRole(final @WebParam(name = "roleId") String roleId,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    /**
     * Makes Group specified by childGroupId a child of Group specified by groupId
     * @param groupId - the Group ID to which another group specified by childGroupId will be added
     * @param childGroupId - the Group ID which will be added to the group specified by groupId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    public Response addChildGroup(final @WebParam(name = "groupId") String groupId, 
    							  final @WebParam(name = "childGroupId") String childGroupId);


    /**
     * Remove Group specified by childGroupId from the membership list of Group specified by groupId
     * @param groupId - the Group ID from which another group specified by childGroupId will be deleted
     * @param childGroupId - the Group ID which will be deleted from the group specified by groupId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    public Response removeChildGroup(final @WebParam(name = "groupId") String groupId, 
    							 	 final @WebParam(name = "childGroupId") String childGroupId);

    /**
     * Checks if User specified by userId can be added to the Group specified by groupId as a member
     * @param userId - the User ID
     * @param groupId - the Group ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be added to this Croup
     */
    @WebMethod
	public Response canAddUserToGroup(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
									  final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * Checks if User specified by userId can be removed from the Group specified by groupId as a member
     * @param userId - the User ID
     * @param groupId - the Group ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be removed from this Group
     */
	@WebMethod
	public Response canRemoveUserFromGroup(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
										   final @WebParam(name = "groupId", targetNamespace = "") String groupId);
}