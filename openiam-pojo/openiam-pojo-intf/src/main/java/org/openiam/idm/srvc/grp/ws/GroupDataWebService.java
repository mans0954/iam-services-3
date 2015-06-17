package org.openiam.idm.srvc.grp.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;
import java.util.Set;

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
   
	@WebMethod
    Response validateEdit(final Group group);
	
	@WebMethod
    Response validateDelete(final String groupId);
	
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
    Response saveGroup(final @WebParam(name = "group", targetNamespace = "") Group group,
                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * This method retrieves an existing group object. Dependent objects such as
     * users are not retrieved. Null is returned if the groupId is not found.
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return - a Group Object if it is found, otherwise null will be returned.
     */
    @WebMethod
    Group getGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    Group getGroupLocalize(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                           final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                           final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * This method removes group from openIAM database for a particular groupId.
     *
     * @param groupId The id to be removed.
     * @return - a Response Object which contains operation status.
     */
    @WebMethod
    Response deleteGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                         final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets the number of child groups that are direct members of this Group
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return Integer, total number of groups that are direct members of this Group
     */
    @WebMethod
    @Deprecated
    int getNumOfChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns a paged List of child groups that are are direct members of this Group
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param deepFlag - shows that method returns Group List with all sub collections
     * @param from - where to start in the list
     * @param size - how many to return
     * @return a paged List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    @Deprecated
    List<Group> getChildGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                               final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                               final @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
                               final @WebParam(name = "from", targetNamespace = "") int from,
                               final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    @Deprecated
    List<Group> getChildGroupsLocalize(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                       final @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
                                       final @WebParam(name = "from", targetNamespace = "") int from,
                                       final @WebParam(name = "size", targetNamespace = "") int size,
                                       final @WebParam(name = "language", targetNamespace = "") Language language);
    /**
     * Gets the number of groups that are direct parents of this Group
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of groups that are direct parents of this Group
     */
    @WebMethod
    @Deprecated
    int getNumOfParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
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
    @Deprecated
    List<Group> getParentGroups(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                final @WebParam(name = "from", targetNamespace = "") int from,
                                final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    @Deprecated
    List<Group> getParentGroupsLocalize(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        final @WebParam(name = "from", targetNamespace = "") int from,
                                        final @WebParam(name = "size", targetNamespace = "") int size,
                                        final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Checks if a user belongs to a particular group or not. If a group has been marked as "Inherits from Parent", then
     * the system will check to see if the user belongs to one of the parent group objects.
     *
     * @param groupId
     * @param userId
     * @return   a Response Object which contains result of checking: true or false and operation status.
     */
    @WebMethod
    Response isUserInGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                           final @WebParam(name = "userId", targetNamespace = "") String userId);

   
    /**
     * This method adds the user to a group .<br>
     *
     * @param userId UserID to be added to group.
     * @param groupId  GroupID to which user will be added.
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    Response addUserToGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                            final @WebParam(name = "userId", targetNamespace = "") String userId,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    /**
     * This method removes user from a group .<br>
     *
     * @param groupId  Group ID from where user would be removed .
     * @param userId User ID which is to be removed from group .
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    Response removeUserFromGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                 final @WebParam(name = "userId", targetNamespace = "") String userId,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute - GroupAttribute object, which should be added
     * @return   a Response Object which contains an operation status and GroupAttribute ID.
     */
    @WebMethod
    Response addAttribute(final @WebParam(name = "attribute", targetNamespace = "") GroupAttribute attribute,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId - GroupAttribute ID
     * @return   a Response Object which contains an operation status.
     */
    @WebMethod
    Response removeAttribute(final @WebParam(name = "attributeId", targetNamespace = "") String attributeId,
                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Return a paged List of Groups based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    @Deprecated
    List<Group> findBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                          final @WebParam(name = "from", targetNamespace = "") int from,
                          final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Return a paged List of Groups based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    List<Group> findBeansLocalize(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                                  final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                  final @WebParam(name = "from", targetNamespace = "") int from,
                                  final @WebParam(name = "size", targetNamespace = "") int size,
                                  final @WebParam(name = "language", targetNamespace = "") Language language);
    /**
     * Return a paged List of Groups  for given groupOwner based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param ownerId - the User ID who is the owner for the searching groups
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Group objects. Returns null if no groups are found.
     */
    @WebMethod
    List<Group> findGroupsForOwner(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                   final @WebParam(name = "ownerId", targetNamespace = "") String ownerId,
                                   final @WebParam(name = "from", targetNamespace = "") int from,
                                   final @WebParam(name = "size", targetNamespace = "") int size,
                                   final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Returns total number of Groups based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of groups based on parameters, which are specified in GroupSearchBean object
     */
    @WebMethod
    int countBeans(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns total number of Groups for given groupOwner based on parameters, which are specified in GroupSearchBean object
     * @param searchBean -  GroupSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param ownerId - the User ID who is the owner for the searching groups
     * @return - Integer, total number of groups based on parameters, which are specified in GroupSearchBean object
     */
    @WebMethod
    int countGroupsForOwner(final @WebParam(name = "searchBean") GroupSearchBean searchBean,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                            final @WebParam(name = "ownerId", targetNamespace = "") String ownerId);

    /**
     * Gets a paged List of Groups directly entitled to the User specified by the userId
     * @param userId - the User ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param deepFlag - shows that method returns Group List with all sub collections
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @return a paged List of Groups directly entitled to the User specified by the userId
     */
    @WebMethod
    @Deprecated
    List<Group> getGroupsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                 @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                 @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
                                 @WebParam(name = "from") int from,
                                 @WebParam(name = "size") int size);
    @WebMethod
    @Deprecated
    List<Group> getGroupsForUserLocalize(@WebParam(name = "userId", targetNamespace = "") String userId,
                                         @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                         @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
                                         @WebParam(name = "from") int from,
                                         @WebParam(name = "size") int size,
                                         final @WebParam(name = "language", targetNamespace = "") Language language);
    /**
     * Gets the number of Groups directly entitled to this User specified by the userId
     * @param userId - the User ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this User specified by the userId
     */
    @Deprecated
    int getNumOfGroupsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
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
    List<Group> getGroupsForResource(final @WebParam(name = "resourceId") String resourceId,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                     final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag,
                                     final @WebParam(name = "from", targetNamespace = "") int from,
                                     final @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
    @Deprecated
    List<Group> getGroupsForResourceLocalize(final @WebParam(name = "resourceId") String resourceId,
                                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                             final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag,
                                             final @WebParam(name = "from", targetNamespace = "") int from,
                                             final @WebParam(name = "size", targetNamespace = "") int size,
                                             final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Gets the number of Groups directly entitled to this Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this Resource specified by the resourceId
     */
    @WebMethod
    @Deprecated
    int getNumOfGroupsforResource(final @WebParam(name = "resourceId") String resourceId,
                                  final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Groups directly entitled to the Role specified by the roleId
     * @param roleId - the Role ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @param deepFlag - if true then it shows that returned Groups contain other objects that are directly entitled to returned Groups
     * @return a paged List of Groups directly entitled to the Role specified by the roleId
     */
    @WebMethod
    @Deprecated
    List<Group> getGroupsForRole(final @WebParam(name = "roleId") String roleId,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                 final @WebParam(name = "from", targetNamespace = "") int from,
                                 final @WebParam(name = "size", targetNamespace = "") int size,
                                 final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag);

    @WebMethod
    @Deprecated
    List<Group> getGroupsForRoleLocalize(final @WebParam(name = "roleId") String roleId,
                                         final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                         final @WebParam(name = "from", targetNamespace = "") int from,
                                         final @WebParam(name = "size", targetNamespace = "") int size,
                                         final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag,
                                         final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Gets the number of Groups directly entitled to this Role specified by the roleId
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Groups directly entitled to this Role specified by the roleId
     */
    @WebMethod
    @Deprecated
    int getNumOfGroupsForRole(final @WebParam(name = "roleId") String roleId,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    /**
     * Makes Group specified by childGroupId a child of Group specified by groupId
     * @param groupId - the Group ID to which another group specified by childGroupId will be added
     * @param childGroupId - the Group ID which will be added to the group specified by groupId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addChildGroup(final @WebParam(name = "groupId") String groupId,
                           final @WebParam(name = "childGroupId") String childGroupId,
                           final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                           final @WebParam(name = "rights", targetNamespace = "") Set<String> rights);

    @WebMethod
    Response validateGroup2GroupAddition(final @WebParam(name = "groupId") String groupId,
                                         final @WebParam(name = "childGroupId") String childGroupId,
                                         final @WebParam(name = "rights", targetNamespace = "") Set<String> rights);
    
    /**
     * Remove Group specified by childGroupId from the membership list of Group specified by groupId
     * @param groupId - the Group ID from which another group specified by childGroupId will be deleted
     * @param childGroupId - the Group ID which will be deleted from the group specified by groupId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response removeChildGroup(final @WebParam(name = "groupId") String groupId,
                              final @WebParam(name = "childGroupId") String childGroupId,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Checks if User specified by userId can be added to the Group specified by groupId as a member
     * @param userId - the User ID
     * @param groupId - the Group ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be added to this Croup
     */
    @WebMethod
    Response canAddUserToGroup(final @WebParam(name = "userId", targetNamespace = "") String userId,
                               final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * Checks if User specified by userId can be removed from the Group specified by groupId as a member
     * @param userId - the User ID
     * @param groupId - the Group ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be removed from this Group
     */
	@WebMethod
    Response canRemoveUserFromGroup(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                    final @WebParam(name = "groupId", targetNamespace = "") String groupId);

    @WebMethod
    List<Group> findGroupsByAttributeValue(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                           final @WebParam(name = "attrValue", targetNamespace = "") String attrValue);
    @WebMethod
    List<Group> findGroupsByAttributeValueLocalize(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                                   final @WebParam(name = "attrValue", targetNamespace = "") String attrValue,
                                                   final @WebParam(name = "language", targetNamespace = "") Language language);
    
    
    /**
     * Does this group have any children?
     * @param groupId
     * @return
     */
    @WebMethod
    boolean hasAttachedEntities(final @WebParam(name = "groupId", targetNamespace = "") String groupId);
}