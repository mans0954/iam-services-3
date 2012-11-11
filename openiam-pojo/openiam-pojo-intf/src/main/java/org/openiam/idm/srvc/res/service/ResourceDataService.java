package org.openiam.idm.srvc.res.service;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/res/service", name = "ResourceDataWebService")
public interface ResourceDataService {

    /**
     * Add a new resource from a transient resource object and sets resourceId
     * in the returned object.
     *
     * @param resource
     * @return
     */
    @WebMethod
    Resource addResource(
            @WebParam(name = "resource", targetNamespace = "")
            Resource resource);

    /**
     * Find a resource.
     *
     * @param resourceId the resource id
     * @return resource
     */
    @WebMethod
    Resource getResource(
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId);
    
    /**
     * Update a resource.
     *
     * @param resource the resource
     * @return the resource
     */
    @WebMethod
    Resource updateResource(
            @WebParam(name = "resource", targetNamespace = "")
            Resource resource);

    /**
     * Remove a resource.
     *
     * @param resourceId the resource id
     */
    @WebMethod
    void removeResource(
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId);

    /**
     * Add a new resource type.
     *
     * @param resourceType the resourceType
     * @return the resource type
     */
    @WebMethod
    ResourceType addResourceType(
            @WebParam(name = "resourceType", targetNamespace = "")
            ResourceType resourceType);

    /**
     * Find a resource type.
     *
     * @param resourceTypeId the resource type id
     * @return the resource type
     */
    @WebMethod
    ResourceType getResourceType(
            @WebParam(name = "resourceTypeId", targetNamespace = "")
            String resourceTypeId);

    /**
     * Update a resource type.
     *
     * @param resourceType the resource type
     * @return the resource type
     */
    @WebMethod
    ResourceType updateResourceType(
            @WebParam(name = "resourceType", targetNamespace = "")
            ResourceType resourceType);

    /**
     * Find all resource types.
     *
     * @return the all resource types
     */
    @WebMethod
    List<ResourceType> getAllResourceTypes();

    /**
     * Remove a resource type.
     *
     * @param resourceTypeId the resource type id
     */
    @WebMethod
    void removeResourceType(
            @WebParam(name = "resourceTypeId", targetNamespace = "")
            String resourceTypeId);

    /**
     * Add a resource property.
     *
     * @param resourceProp the resource prop
     * @return the resource prop
     */
    @WebMethod
    ResourceProp addResourceProp(
            @WebParam(name = "resourceProp", targetNamespace = "")
            ResourceProp resourceProp);

    /**
     * Find a resource property.
     *
     * @param resourcePropId the resource prop id
     * @return the resource prop
     */
    @WebMethod
    ResourceProp getResourceProp(
            @WebParam(name = "resourcePropId", targetNamespace = "")
            String resourcePropId);

    /**
     * Update a resource property.
     *
     * @param resourceProp the resource prop
     * @return the resource prop
     */
    @WebMethod
    ResourceProp updateResourceProp(
            @WebParam(name = "resourceProp", targetNamespace = "")
            ResourceProp resourceProp);

    /**
     * Remove a resource property.
     *
     * @param resourcePropId the resource prop id
     */
    @WebMethod
    void removeResourceProp(
            @WebParam(name = "resourcePropId", targetNamespace = "")
            String resourcePropId);

    /**
     * Remove all resource properties.
     *
     * @return the int count
     */
    @WebMethod
    int removeAllResourceProps();

    /**
     * Add a resource role.
     *
     * @param resourceRole the resource role
     * @return the resource role
     */
    @WebMethod
    ResourceRole addResourceRole(
            @WebParam(name = "resourceRole", targetNamespace = "")
            ResourceRole resourceRole);

    /**
     * Find resource role.
     *
     * @param resourceRoleId the resource role id
     * @return the resource role
     */
    @WebMethod
    ResourceRole getResourceRole(
            @WebParam(name = "resourceRoleId", targetNamespace = "")
            ResourceRoleId resourceRoleId);

    /**
     * Update resource role.
     *
     * @param resourceRole the resource role
     * @return the resource role
     */
    @WebMethod
    ResourceRole updateResourceRole(
            @WebParam(name = "resourceRole", targetNamespace = "")
            ResourceRole resourceRole);

    /**
     * Find all resource roles.
     *
     * @return the all resource roles
     */
    @WebMethod
    List<ResourceRole> getAllResourceRoles();

    /**
     * Remove resource role.
     *
     * @param resourceRoleId the resource role id
     */
    @WebMethod
    void removeResourceRole(
            @WebParam(name = "resourceRoleId", targetNamespace = "")
            ResourceRoleId resourceRoleId);

    /**
     * Remove all resource roles.
     */
    @WebMethod
    void removeAllResourceRoles();

    /**
     * Adds the user to resource.
     *
     * @param user the user
     * @return the resource user
     */
    @WebMethod
    ResourceUser addUserToResource(
            @WebParam(name = "user", targetNamespace = "")
            ResourceUser user);

    /**
     * Gets the user resources.
     *
     * @param userId the user id
     * @return the user resources
     */
    @WebMethod
    List<ResourceUser> getUserResources(
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * Removes the user from all resources.
     *
     * @param userId the user id
     */
    @WebMethod
    void removeUserFromAllResources(
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    /**
     * Check if is user authorized.
     *
     * @param userId     the user id
     * @param resourceId the resource id
     * @return true, if is user authorized
     */
    @WebMethod
    boolean isUserAuthorized(
            @WebParam(name = "userId", targetNamespace = "")
            String userId,
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId);


    /**
     * Returns a list of Resource objects that are linked to a Role.
     *
     * @param roleId   the role id
     * @return the resources for role
     */
    @WebMethod
    List<Resource> getResourcesForRole(
            @WebParam(name = "roleId", targetNamespace = "")
            String roleId);

    /**
     * Returns a list of Resource objects that are linked to the list of Roles.
     *
     * @param roleIdList the role id list
     * @return the resources for roles
     */

    @WebMethod
    List<Resource> getResourcesForRoles(
            @WebParam(name = "roleIdList", targetNamespace = "")
            List<String> roleIdList);

    /**
     *
     * @param resourceId
     * @return
     */
    @WebMethod
    List<Role> getRolesForResource(
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId);


    List<Resource> getResourceObjForUser(String userId);

    ResourcePrivilege addResourcePrivilege(
            @WebParam(name = "resourcePrivilege", targetNamespace = "")
            ResourcePrivilege resourcePrivilege);

    void removeResourcePrivilege(
            @WebParam(name = "resourcePrivilegeId", targetNamespace = "")
            String resourcePrivilegeId);

    ResourcePrivilege updateResourcePrivilege(
            @WebParam(name = "resourcePrivilege", targetNamespace = "")
            ResourcePrivilege instance);


    List<ResourcePrivilege> getPrivilegesByResourceId(
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId);

    List<ResourcePrivilege> getPrivilegesByEntitlementType(
            @WebParam(name = "resourceId", targetNamespace = "")
            String resourceId,
            @WebParam(name = "type", targetNamespace = "")
            String type);

    @WebMethod
    List<Resource> findBeans(@WebParam(name = "searchBean", targetNamespace = "")  ResourceSearchBean searchBean,
    						 @WebParam(name = "from", targetNamespace = "") int from,
    						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    int count(@WebParam(name="searchBean", targetNamespace="") ResourceSearchBean searchBean);
}