package org.openiam.idm.srvc.role.ws;

import org.openiam.base.TreeObjectId;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Interface permitting the management of Roles and related objects such as
 * groups and users.
 *
 * @author Suneet Shah
 * @version 1
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/role/service", name = "RoleDataService")
public interface RoleDataWebService {

	@WebMethod
    Response validateEdit(final Role role);
	
	@WebMethod
    Response validateDelete(final String roleId);
	
    /**
     * This method retrieves an existing Role object. Dependent objects such as
     * users are also retrieved. Null is returned if the Role is not found.
     *
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return - a Role Object if it is found, otherwise null will be returned.
     */
    @WebMethod
    Role getRoleLocalized(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                 		  @WebParam(name="requesterId", targetNamespace="") String requesterId,
                 		  @WebParam(name="language", targetNamespace="") Language language);

    @WebMethod
    List<RoleAttribute> getRoleAttributes(@WebParam(name = "roleId", targetNamespace = "") String roleId);

    /**
     * This method creates a new role or update existed one. For example:
     *
     * @param role - the Role object, which should be created or updated
     * @return - a Response Object. If operation succeed then Response object contains the primary key of saved role
     * otherwise it contains error code.
     */
    @WebMethod
    Response saveRole(@WebParam(name = "role", targetNamespace = "") Role role,
    				 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * This method removes role from openIAM database for a particular roleID.
     *
     * @param roleId - The Role ID to be removed.
     * @return - a Response Object which contains operation status.
     */
    @WebMethod
    Response removeRole(@WebParam(name = "roleId", targetNamespace = "")  String roleId,
                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

     /** * Role-Group Methods ****** */

    /**
     * Returns a paged List of Role objects that are linked to a Group.
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return  a paged List of Role objects that are linked to a Group. if no roles are found returns null
     */
    @WebMethod
    @Deprecated
    List<Role> getRolesInGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                               final @WebParam(name="requesterId", targetNamespace="") String requesterId,
                               final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag,
                               final @WebParam(name = "from", targetNamespace = "") int from,
                               final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    Role getRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                 @WebParam(name="requesterId", targetNamespace="") String requesterId);


    /**
     * This method adds particular roleId to a particular group.<br>
     *
     * @param roleId The roleId which is to be added to the group.
     * @param groupId  The group for which the roleId is to be added .
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addGroupToRole(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                            final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                            final @WebParam(name = "rightIds") Set<String> rightIds,
                            final @WebParam(name = "startDate", targetNamespace = "") Date startDate,
                            final @WebParam(name = "endDate", targetNamespace = "") Date endDate);
    
    @WebMethod
    Response validateGroup2RoleAddition(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                            			@WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId The roleId which is to be deleted from the group.
     * @param groupId The group from which the roleId is to be deleted
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response removeGroupFromRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                                 @WebParam(name = "groupId", targetNamespace = "") String groupId,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * This method adds particular user directly to a role.<br>
     *
     * @param roleId   The roleId to which the user will be associated.
     * @param userId   The userId to which the roleId is to be added .
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addUserToRole(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
    					   final @WebParam(name = "userId", targetNamespace = "")  String userId,
                           final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                           final @WebParam(name = "rightIds", targetNamespace = "") Set<String> rightIds,
                           final @WebParam(name = "startDate", targetNamespace = "") Date startDate,
                           final @WebParam(name = "endDate", targetNamespace = "") Date endDate);

    /**
     * Removes the association between a single role and role.
     *
     * @param roleId The roleId from which user specified by userId will be deleted.
     * @param userId The user whom will be deleted from role specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response removeUserFromRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                                @WebParam(name = "userId", targetNamespace = "") String userId,
                                final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Roles directly entitled to the User specified by the userId
     * @param userId - the User ID
     * @param requesterId -  the User ID who request this operation.  This param is required if delegation filter is set
     * @param deepFlag - shows that method returns Role List with all sub collections
     * @param from - where to start in the paged list
     * @param size - how many to return
     * @return a paged List of  Roles directly entitled to the User specified by the userId
     */
    @WebMethod
    @Deprecated
    List<Role> getRolesForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                               final @WebParam(name="requesterId", targetNamespace="") String requesterId,
                               final @WebParam(name="deepFlag", targetNamespace="") Boolean deepFlag,
                               final @WebParam(name = "from", targetNamespace = "") int from,
                               final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of Roles directly entitled to the User specified by the userId
     * @param userId - the User ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to the User specified by the userId
     */
    @WebMethod
    @Deprecated
    int getNumOfRolesForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                             final @WebParam(name="requesterId", targetNamespace="") String requesterId);


    /**
     * Return a paged List of Roles based on parameters, which are specified in RoleSearchBean object
     * @param searchBean -  RoleSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Roles objects. Returns null if no roles are found.
     */
    @WebMethod
    List<Role> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") RoleSearchBean searchBean,
                         final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                         final @WebParam(name = "from", targetNamespace = "") int from,
                         final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Returns total number of Roles based on parameters, which are specified in RoleSearchBean object
     * @param searchBean -  RoleSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of roles based on parameters, which are specified in RoleSearchBean object
     */
    @WebMethod
    int countBeans(final @WebParam(name = "searchBean", targetNamespace = "") RoleSearchBean searchBean,
                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets the number of Roles directly entitled to the Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to the Resource specified by the resourceId
     */
    @WebMethod
    @Deprecated
    List<Role> getRolesForResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                   final @WebParam(name = "deepFlag", targetNamespace = "") boolean deepFlag,
                                   final @WebParam(name = "from", targetNamespace = "") int from,
                                   final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of Roles directly entitled to the Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to the Resource specified by the resourceId
     */
    @WebMethod
    @Deprecated
    int getNumOfRolesForResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns a paged List of child roles that are are direct members of this Role
     *
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param deepFlag - shows if method returns Roles Collection with all sub collections
     * @param from - where to start in the list
     * @param size - how many to return
     * @return a paged List of Role objects. Returns null if no roles are found.
     */
    @WebMethod
    @Deprecated
    List<Role> getChildRoles(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                             final @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
                             final @WebParam(name = "from", targetNamespace = "") int from,
                             final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of child roles that are direct members of this Role
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return Integer, total number of roles that are direct members of this Role
     */
    @WebMethod
    @Deprecated
    int getNumOfChildRoles(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                           final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    /**
     * Returns a paged List of groups that are direct parents of this Role
     *
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return a paged List of of Role objects. Returns null if no roles are found.
     */
    @WebMethod
    List<Role> getParentRoles(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                              final @WebParam(name = "from", targetNamespace = "") int from,
                              final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Gets the number of roles that are direct parents of this Role
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of roles that are direct parents of this Role
     */
    @WebMethod
    @Deprecated
    int getNumOfParentRoles(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Makes Role specified by childRoleId a child of Role specified by roleId
     * @param roleId - the Role ID to which another group specified by childRoleId will be added
     * @param childRoleId - - the Role ID which will be added to the group specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addChildRole(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                          final @WebParam(name = "childRoleId", targetNamespace = "") String childRoleId,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                          final @WebParam(name = "rights", targetNamespace = "") Set<String> rights,
                          final @WebParam(name = "startDate", targetNamespace = "") Date startDate,
                          final @WebParam(name = "endDate", targetNamespace = "") Date endDate);
    
    @WebMethod
    Response canAddChildRole(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                             final @WebParam(name = "childRoleId", targetNamespace = "") String childRoleId,
                             final @WebParam(name = "rights", targetNamespace = "") Set<String> rights,
                             final @WebParam(name = "startDate", targetNamespace = "") Date startDate,
                             final @WebParam(name = "endDate", targetNamespace = "") Date endDate);

    /**
     * Remove Role specified by childRoleId from the membership list of Group specified by roleId
     * @param roleId - the Role ID from which another group specified by childRoleId will be deleted
     * @param childRoleId - the Role ID which will be deleted from the group specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response removeChildRole(final @WebParam(name = "roleId", targetNamespace = "") String roleId,
                             final @WebParam(name = "childRoleId", targetNamespace = "") String childRoleId,
                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets the number of Roles directly entitled to this Group specified by the groupId
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to this Group specified by the groupId
     */
    @WebMethod
    @Deprecated
    int getNumOfRolesForGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Checks if User specified by userId can be added to the Role specified by roleId as a member
     * @param userId - the User ID
     * @param roleId - the Role ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be added to this Role
     */
    @WebMethod
    Response canAddUserToRole(final @WebParam(name = "userId", targetNamespace = "") String userId,
                              final @WebParam(name = "roleId", targetNamespace = "") String roleId);

    /**
     * Checks if User specified by userId can be removed from the Role specified by roleId as a member
     * @param userId - the User ID
     * @param roleId - the Role ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be removed from this Role
     */
   	@WebMethod
    Response canRemoveUserFromRole(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                   final @WebParam(name = "roleId", targetNamespace = "") String roleId);

/*    @WebMethod
    List<Role> findRolesByAttributeValue(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                         final @WebParam(name = "attrValue", targetNamespace = "") String attrValue);*/

    @WebMethod
    List<TreeObjectId> getRolesWithSubRolesIds(final @WebParam(name = "roleIds", targetNamespace = "") List<String> roleIds,
                                               final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    
    @WebMethod
    boolean hasChildEntities(final @WebParam(name = "roleId", targetNamespace = "") String roleId);
}
