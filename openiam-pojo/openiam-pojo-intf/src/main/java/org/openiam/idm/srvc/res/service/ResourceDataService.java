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
     * Adds the user to resource.
     *
     * @param user the user
     * @return the resource user
     */
    @WebMethod
    Response addUserToResource(
            @WebParam(name = "user", targetNamespace = "")
            ResourceUser user);

    @WebMethod
    int getNumOfResourcesForRole(@WebParam(name = "roleId", targetNamespace = "") String roleId);	
    
    @WebMethod
    List<Resource> getResourcesForRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
    								   @WebParam(name = "from", targetNamespace = "") int from,
    								   @WebParam(name = "size", targetNamespace = "") int size);


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
    
    @WebMethod
    List<Resource> getParentResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    								@WebParam(name = "from", targetNamespace = "") int from,
    								@WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    int getNumOfParentResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);
    
    @WebMethod
    Response addChildResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    						  @WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId);
    
    @WebMethod
    Response deleteChildResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    						  	 @WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId);
    
    @WebMethod
    Response addGroupToResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    							@WebParam(name = "groupId", targetNamespace = "") final String groupId);
    
    @WebMethod
    Response removeGroupToResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    							   @WebParam(name = "groupId", targetNamespace = "") final String groupId);
    
    @WebMethod
    Response addRoleToResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    							@WebParam(name = "roleId", targetNamespace = "") final String roleId);
    
    @WebMethod
    Response removeRoleToResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    							   @WebParam(name = "roleId", targetNamespace = "") final String roleId);
}