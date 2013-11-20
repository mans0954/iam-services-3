package org.openiam.idm.srvc.role.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.dto.RolePolicy;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Interface permitting the management of Roles and related objects such as
 * groups and users.
 *
 * @author Suneet Shah
 * @version 1
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/role/service", name = "RoleDataService")
public interface RoleDataWebService {

    /**
     * This method retrieves an existing Role object. Dependent objects such as
     * users are also retrieved. Null is returned if the Role is not found.
     *
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return - a Role Object if it is found, otherwise null will be returned.
     */
    @WebMethod
    Role getRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                 @WebParam(name="requesterId", targetNamespace="") String requesterId);

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
    Response removeRole(@WebParam(name = "roleId", targetNamespace = "")  String roleId);

    /** * Attribute Methods ****** */

    /**
     * Adds an attribute to the Role object.
     *
     * @param attribute
     */
    /*
    @WebMethod
    RoleAttributeResponse addAttribute(@WebParam(name = "attribute", targetNamespace = "") RoleAttribute attribute);
	*/

    /**
     * Update an attribute to the Role object.
     *
     * @param attribute
     */
    /*
    @WebMethod
    Response updateAttribute(@WebParam(name = "attribute", targetNamespace = "") RoleAttribute attribute);
	*/

    /**
     * Removes a RoleAttribute specified by the attribute.
     *
     * @param attributeId
     */
    /*
    @WebMethod
    Response removeAttribute(final @WebParam(name = "attributeId", targetNamespace = "") String attributeId);
	*/

    /** * Role-Group Methods ****** */

    /**
     * Returns a paged List of Role objects that are linked to a Group.
     *
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation. This param is required if delegation filter is set
     * @return  a paged List of Role objects that are linked to a Group. if no roles are found returns null
     */
    @WebMethod
    List<Role> getRolesInGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId,
                               final @WebParam(name="requesterId", targetNamespace="") String requesterId,
                               final @WebParam(name = "from", targetNamespace = "") int from,
                               final @WebParam(name = "size", targetNamespace = "") int size);


    /**
     * This method adds particular roleId to a particular group.<br>
     *
     * @param roleId The roleId which is to be added to the group.
     * @param groupId  The group for which the roleId is to be added .
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addGroupToRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
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
                                 @WebParam(name = "groupId", targetNamespace = "") String groupId);

    /**
     * This method adds particular user directly to a role.<br>
     *
     * @param roleId   The roleId to which the user will be associated.
     * @param userId   The userId to which the roleId is to be added .
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response addUserToRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                           @WebParam(name = "userId", targetNamespace = "")  String userId);

    /**
     * Removes the association between a single role and role.
     *
     * @param roleId The roleId from which user specified by userId will be deleted.
     * @param userId The user whom will be deleted from role specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    Response removeUserFromRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
                                @WebParam(name = "userId", targetNamespace = "") String userId);

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
    int getNumOfRolesForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                             final @WebParam(name="requesterId", targetNamespace="") String requesterId);

    /**
     * Create new role policy based on specified  RolePolicy object.
     *
     * @param rolePolicy - RolePolicy object which should be created
     * @return a RolePolicyResponse object which contains created RolePolicy object if operation succeed, otherwise response contains error
     */
    @WebMethod
    public RolePolicyResponse addRolePolicy( @WebParam(name = "rPolicy", targetNamespace = "")RolePolicy rolePolicy);


    /**
     * Update an role policy based on specified  RolePolicy object.
     *
     * @param rolePolicy - RolePolicy object which should be updated
     * @return a RolePolicyResponse object which contains updated RolePolicy object if operation succeed, otherwise response contains error
     */
    @WebMethod
    public RolePolicyResponse updateRolePolicy(@WebParam(name = "rolePolicy", targetNamespace = "") RolePolicy rolePolicy);

    /**
     * Returns a single RolePolicy object based on the rolePolicyId.
     *
     * @param rolePolicyId - the RolePolicy ID
     * @return a RolePolicyResponse object which contains single RolePolicy object if it is found, otherwise response contains error
     */
    @WebMethod
    public RolePolicyResponse getRolePolicy(@WebParam(name = "rolePolicyId", targetNamespace = "") String rolePolicyId);

    /**
     * Removes a RolePolicy specified by the rolePolicyId parameter.
     *
     * @param rolePolicyId - RolePolicy ID
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    public Response removeRolePolicy(final @WebParam(name = "rolePolicyId", targetNamespace = "") String rolePolicyId);

    /**
     * Return a paged List of Roles based on parameters, which are specified in RoleSearchBean object
     * @param searchBean -  RoleSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @param from - where to start in the list
     * @param size - how many to return
     * @return List of Roles objects. Returns null if no roles are found.
     */
    @WebMethod
    public List<Role> findBeans(final @WebParam(name="searchBean", targetNamespace="") RoleSearchBean searchBean,
                                final @WebParam(name="requesterId", targetNamespace="") String requesterId,
    							final @WebParam(name = "from", targetNamespace = "") int from,
    							final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Returns total number of Roles based on parameters, which are specified in RoleSearchBean object
     * @param searchBean -  RoleSearchBean object
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of roles based on parameters, which are specified in RoleSearchBean object
     */
    @WebMethod
    public int countBeans(final @WebParam(name="searchBean", targetNamespace="") RoleSearchBean searchBean,
                          final @WebParam(name="requesterId", targetNamespace="") String requesterId);

    /**
     * Gets the number of Roles directly entitled to the Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to the Resource specified by the resourceId
     */
    @WebMethod
    public List<Role> getRolesForResource(final @WebParam(name="resourceId", targetNamespace="") String resourceId,
                                          final @WebParam(name="requesterId", targetNamespace="") String requesterId,
    									  final @WebParam(name = "from", targetNamespace = "") int from,
    									  final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of Roles directly entitled to the Resource specified by the resourceId
     * @param resourceId - the Resource ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to the Resource specified by the resourceId
     */
    @WebMethod
    public int getNumOfRolesForResource(final @WebParam(name="resourceId", targetNamespace="") String resourceId,
                                        final @WebParam(name="requesterId", targetNamespace="") String requesterId);

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
    public List<Role> getChildRoles(final @WebParam(name="roleId", targetNamespace="") String roleId,
                                    final @WebParam(name="requesterId", targetNamespace="") String requesterId,
                                    final @WebParam(name="deepFlag", targetNamespace="") Boolean deepFlag,
			  					    final @WebParam(name = "from", targetNamespace = "") int from,
			  						final @WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets the number of child roles that are direct members of this Role
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return Integer, total number of roles that are direct members of this Role
     */
    @WebMethod
    public int getNumOfChildRoles(final @WebParam(name="roleId", targetNamespace="") String roleId,
                                  final @WebParam(name="requesterId", targetNamespace="") String requesterId);
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
    public List<Role> getParentRoles(final @WebParam(name="roleId", targetNamespace="") String roleId,
                                     final @WebParam(name="requesterId", targetNamespace="") String requesterId,
			  						 final @WebParam(name = "from", targetNamespace = "") int from,
			  						 final @WebParam(name = "size", targetNamespace = "") int size);
    /**
     * Gets the number of roles that are direct parents of this Role
     * @param roleId - the Role ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return - Integer, total number of roles that are direct parents of this Role
     */
    @WebMethod
    public int getNumOfParentRoles(final @WebParam(name="roleId", targetNamespace="") String roleId,
                                   final @WebParam(name="requesterId", targetNamespace="") String requesterId);

    /**
     * Makes Role specified by childRoleId a child of Role specified by roleId
     * @param roleId - the Role ID to which another group specified by childRoleId will be added
     * @param childRoleId - - the Role ID which will be added to the group specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    public Response addChildRole(final @WebParam(name="roleId", targetNamespace="") String roleId,
    						     final @WebParam(name="parentRoleId", targetNamespace="") String childRoleId);

    /**
     * Remove Role specified by childRoleId from the membership list of Group specified by roleId
     * @param roleId - the Role ID from which another group specified by childRoleId will be deleted
     * @param childRoleId - the Role ID which will be deleted from the group specified by roleId
     * @return a Response Object, containing the status of this operation.
     */
    @WebMethod
    public Response removeChildRole(final @WebParam(name="roleId", targetNamespace="") String roleId,
			 					    final @WebParam(name="parentRoleId", targetNamespace="") String childRoleId);

    /**
     * Gets the number of Roles directly entitled to this Group specified by the groupId
     * @param groupId - the Group ID
     * @param requesterId - the User ID who request this operation.  This param is required if delegation filter is set
     * @return the number of Roles directly entitled to this Group specified by the groupId
     */
    @WebMethod
    public int getNumOfRolesForGroup(final @WebParam(name="groupId", targetNamespace="") String groupId,
                                     final @WebParam(name="requesterId", targetNamespace="") String requesterId);

    /**
     * Checks if User specified by userId can be added to the Role specified by roleId as a member
     * @param userId - the User ID
     * @param roleId - the Role ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be added to this Role
     */
    @WebMethod
   	public Response canAddUserToRole(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
   									 final @WebParam(name = "roleId", targetNamespace = "") String roleId);

    /**
     * Checks if User specified by userId can be removed from the Role specified by roleId as a member
     * @param userId - the User ID
     * @param roleId - the Role ID
     * @return a Response Object, containing the status of this operation. if status is SUCCESS then the User can be removed from this Role
     */
   	@WebMethod
   	public Response canRemoveUserFromRole(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
   										  final @WebParam(name = "roleId", targetNamespace = "") String roleId);
}
