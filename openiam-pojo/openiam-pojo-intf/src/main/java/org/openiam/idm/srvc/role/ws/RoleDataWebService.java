package org.openiam.idm.srvc.role.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MembershipRoleSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
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
//@XmlSeeAlso({org.openiam.idm.srvc.user.dto.ObjectFactory.class,org.openiam.idm.srvc.org.dto.ObjectFactory.class,org.openiam.idm.srvc.continfo.dto.ObjectFactory.class,org.openiam.idm.srvc.grp.dto.ObjectFactory.class,org.openiam.idm.srvc.role.types.ObjectFactory.class,org.openiam.idm.srvc.role.dto.ObjectFactory.class,org.openiam.idm.srvc.meta.dto.ObjectFactory.class})
public interface RoleDataWebService {

    /**
     * Retrieves a role object based on the roleId.
     * Dependent objects include Group and Users collections that are associated with this Role.
     *
     * @param roleId
     * @return
     */
    @WebMethod
    Role getRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId);

    /**
     * Updates an existing role
     *
     * @param role
     */
    @WebMethod
    Response saveRole(
            @WebParam(name = "role", targetNamespace = "")
            org.openiam.idm.srvc.role.dto.Role role);

    /**
     * Removes a role.
     *
     * @param roleId
     */
    @WebMethod
    Response removeRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId);

    /** * Attribute Methods ****** */

    /**
     * Adds an attribute to the Role object.
     *
     * @param attribute
     */
    @WebMethod
    RoleAttributeResponse addAttribute(
            @WebParam(name = "attribute", targetNamespace = "")
            org.openiam.idm.srvc.role.dto.RoleAttribute attribute);

    /**
     * Update an attribute to the Role object.
     *
     * @param attribute
     */
    @WebMethod
    Response updateAttribute(
            @WebParam(name = "attribute", targetNamespace = "")
            org.openiam.idm.srvc.role.dto.RoleAttribute attribute);

    /**
     * Removes a RoleAttribute specified by the attribute.
     *
     * @param attributeId
     */
    @WebMethod
    Response removeAttribute(
            final @WebParam(name = "attributeId", targetNamespace = "") String attributeId);


    /** * Role-Group Methods ****** */

    /**
     * Returns an array of Role objects that are linked to a Group Returns null
     * if no roles are found.
     *
     * @param groupId
     * @return
     */
//    @WebMethod
//    List<Role> getRolesInGroup(
//            final @WebParam(name = "groupId", targetNamespace = "") String groupId,
//            final @WebParam(name = "from", targetNamespace = "") int from,
//            final @WebParam(name = "size", targetNamespace = "") int size);


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
    @WebMethod
    Response addGroupToRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId,
            @WebParam(name = "groupId", targetNamespace = "")
            String groupId);

    /**
     * Removes the association between a single group and role.
     *
     * @param roleId
     * @param groupId
     */
    @WebMethod
    Response removeGroupFromRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId,
            @WebParam(name = "groupId", targetNamespace = "")
            String groupId);

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
    @WebMethod
    Response addUserToRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * This method removes a particular user directly to a role.
     *
     * @param roleId
     * @param userId
     */
    @WebMethod
    Response removeUserFromRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * Returns an array of Role objects that indicate the Roles a user is
     * associated to.
     *
     * @param userId
     * @return
     */
//    @WebMethod
//    List<Role> getRolesForUser(
//            final @WebParam(name = "userId", targetNamespace = "") String userId,
//            final @WebParam(name = "from", targetNamespace = "") int from,
//            final @WebParam(name = "size", targetNamespace = "") int size);
//
//    @WebMethod
//    int getNumOfRolesForUser(final @WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Role Policy Methods ******
     */

    @WebMethod
    public RolePolicyResponse addRolePolicy(
            @WebParam(name = "rPolicy", targetNamespace = "")
            RolePolicy rPolicy);


    /**
     * Update an attribute to the Role object.
     *
     * @param rolePolicy
     */
    @WebMethod
    public RolePolicyResponse updateRolePolicy(
            @WebParam(name = "rolePolicy", targetNamespace = "")
            RolePolicy rolePolicy);

    /**
     * Returns a single RolePolicy object based on the attributeId.
     *
     * @param rolePolicyId
     * @return
     */
    @WebMethod
    public RolePolicyResponse getRolePolicy(
            @WebParam(name = "rolePolicyId", targetNamespace = "")
            String rolePolicyId);

    /**
     * Removes a RolePolicy specified by the rPolicy parameter.
     *
     * @param rolePolicyId
     */
    @WebMethod
    public Response removeRolePolicy(
            final @WebParam(name = "rolePolicyId", targetNamespace = "") String rolePolicyId);


    @WebMethod
    public List<Role> findBeans(final @WebParam(name="searchBean", targetNamespace="") RoleSearchBean searchBean,
    							final @WebParam(name = "from", targetNamespace = "") int from,
    							final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public int countBeans(final @WebParam(name="searchBean", targetNamespace="") RoleSearchBean searchBean);
    
//    @WebMethod
//    public List<Role> getRolesForResource(final @WebParam(name="resourceId", targetNamespace="") String resourceId,
//    									  final @WebParam(name = "from", targetNamespace = "") int from,
//    									  final @WebParam(name = "size", targetNamespace = "") int size);
//
//    @WebMethod
//    public int getNumOfRolesForResource(final @WebParam(name="resourceId", targetNamespace="") String resourceId);
    
    
    @WebMethod
    public List<Role> getChildRoles(final @WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean,
			  					    final @WebParam(name = "from", targetNamespace = "") int from,
			  						final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public int getNumOfChildRoles(final @WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean);
    
    @WebMethod
    public List<Role> getParentRoles(final @WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean,
			  						 final @WebParam(name = "from", targetNamespace = "") int from,
			  						 final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public int getNumOfParentRoles(final @WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean);
    
    @WebMethod
    public Response addChildRole(final @WebParam(name="roleId", targetNamespace="") String roleId,
    						 final @WebParam(name="parentRoleId", targetNamespace="") String childRoleId);
    
    @WebMethod
    public Response removeChildRole(final @WebParam(name="roleId", targetNamespace="") String roleId,
			 					final @WebParam(name="parentRoleId", targetNamespace="") String childRoleId);
    
//    @WebMethod
//    public int getNumOfRolesForGroup(final @WebParam(name="groupId", targetNamespace="") String groupId);
    @WebMethod
    public List<Role> getEntitlementRoles(@WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean,
                                          @WebParam(name="from", targetNamespace="") int from,
                                          @WebParam(name="size", targetNamespace="") int size);
    @WebMethod
    public int getNumOfEntitlementRoles(@WebParam(name="searchBean", targetNamespace="") MembershipRoleSearchBean searchBean);
    
}
