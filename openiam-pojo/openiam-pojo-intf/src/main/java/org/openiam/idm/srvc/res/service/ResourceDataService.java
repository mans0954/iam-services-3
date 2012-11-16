package org.openiam.idm.srvc.res.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/res/service", name = "ResourceDataWebService")
public interface ResourceDataService {
	
	@WebMethod
	Response deleteResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

    /**
     * Add a new resource from a transient resource object and sets resourceId
     * in the returned object.
     *
     * @param resource
     * @return
     */
    @WebMethod
    Response addResource(
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
    Response updateResource(
            @WebParam(name = "resource", targetNamespace = "")
            Resource resource);

    /**
     * Add a new resource type.
     *
     * @param resourceType the resourceType
     * @return the resource type
     */
    @WebMethod
    Response addResourceType(
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
    Response updateResourceType(
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
     * Add a resource property.
     *
     * @param resourceProp the resource prop
     * @return the resource prop
     */
    @WebMethod
    Response addResourceProp(
            @WebParam(name = "resourceProp", targetNamespace = "")
            ResourceProp resourceProp);

    /**
     * Update a resource property.
     *
     * @param resourceProp the resource prop
     * @return the resource prop
     */
    @WebMethod
    Response updateResourceProp(
            @WebParam(name = "resourceProp", targetNamespace = "")
            ResourceProp resourceProp);

    /**
     * Remove a resource property.
     *
     * @param resourcePropId the resource prop id
     */
    @WebMethod
    Response removeResourceProp(
            @WebParam(name = "resourcePropId", targetNamespace = "")
            String resourcePropId);

    /**
     * Add a resource role.
     *
     * @param resourceRole the resource role
     * @return the resource role
     */
    @WebMethod
    Response addResourceRole(
            @WebParam(name = "resourceRole", targetNamespace = "")
            ResourceRole resourceRole);

    /**
     * Remove resource role.
     *
     * @param resourceRoleId the resource role id
     */
    @WebMethod
    Response removeResourceRole(
            @WebParam(name = "resourceRoleId", targetNamespace = "")
            ResourceRoleId resourceRoleId);

    /**
     * Adds the user to resource.
     *
     * @param user the user
     * @return the resource user
     */
    @WebMethod
    Response addUserToResource(
            @WebParam(name = "user", targetNamespace = "")
            ResourceUser user);

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


    @WebMethod
    List<Resource> findBeans(@WebParam(name = "searchBean", targetNamespace = "")  ResourceSearchBean searchBean,
    						 @WebParam(name = "from", targetNamespace = "") int from,
    						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    int count(@WebParam(name="searchBean", targetNamespace="") ResourceSearchBean searchBean);
    
    @WebMethod
    List<Resource> getChildResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    								@WebParam(name = "from", targetNamespace = "") int from,
    								@WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    int getNumOfChildResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);
}