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
	Response deleteResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

	/**
	 * Add a new resource from a transient resource object and sets resourceId
	 * in the returned object.
	 * 
	 * @param resource
	 * @return
	 */
	@WebMethod
	Response saveResource(
			@WebParam(name = "resource", targetNamespace = "") Resource resource);

	/**
	 * Find a resource.
	 * 
	 * @param resourceId
	 *            the resource id
	 * @return resource
	 */
	@WebMethod
	Resource getResource(
			@WebParam(name = "resourceId", targetNamespace = "") String resourceId);

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
	 * @param resourceProp
	 *            the resource prop
	 * @return the resource prop
	 */
	@WebMethod
	Response addResourceProp(
			@WebParam(name = "resourceProp", targetNamespace = "") ResourceProp resourceProp);

	/**
	 * Update a resource property.
	 * 
	 * @param resourceProp
	 *            the resource prop
	 * @return the resource prop
	 */
	@WebMethod
	Response updateResourceProp(
			@WebParam(name = "resourceProp", targetNamespace = "") ResourceProp resourceProp);

	/**
	 * Remove a resource property.
	 * 
	 * @param resourcePropId
	 *            the resource prop id
	 */
	@WebMethod
	Response removeResourceProp(
			@WebParam(name = "resourcePropId", targetNamespace = "") String resourcePropId);

	/**
	 * Entitles a User to a Resource
	 * @param resourceId - resource ID
	 * @param userId - user ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response addUserToResource(
			final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
			final @WebParam(name = "userId", targetNamespace = "") String userId);

	/**
	 * Disentitled a User from a Resource
	 * @param resourceId - resource ID
	 * @param userId - user ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response removeUserFromResource(
			final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
			final @WebParam(name = "userId", targetNamespace = "") String userId);

	/**
	 * Get the number of resources that a particular role is entitled to
	 * @param roleId - the Role ID
	 * @return
	 */
	@WebMethod
	int getNumOfResourcesForRole(
			@WebParam(name = "roleId", targetNamespace = "") String roleId);

	/**
	 * Gets a List of Resources that a Role is entitled to
	 * @param roleId - role ID
	 * @param from - where to start
	 * @param size - how many to return
	 * @return
	 */
	@WebMethod
	List<Resource> getResourcesForRole(
			@WebParam(name = "roleId", targetNamespace = "") String roleId,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

	/**
	 * Search a Resource
	 * @param searchBean - search bean, containing search parameters
	 * @param from - where to start
	 * @param size - how many to return
	 * @return - the search results
	 */
	@WebMethod
	List<Resource> findBeans(
			@WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

	/**
	 * count the number of resources based on the searchBean
	 * @param searchBean
	 * @return
	 */
	@WebMethod
	int count(
			@WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean);

	/**
	 * Find the child resources of a particular Resource
	 * @param resourceId - resource ID
	 * @param from - where to start
	 * @param size - how many to return
	 * @return
	 */
	@WebMethod
	List<Resource> getChildResources(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

	/**
	 * Gets the number of child resources for a particular Resource
	 * @param resourceId - the resource ID
	 * @return
	 */
	@WebMethod
	int getNumOfChildResources(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

	/**
	 * Find the parent resources of a particular Resource
	 * @param resourceId - resource ID
	 * @param from - where to start
	 * @param size - how many to return
	 * @return
	 */
	@WebMethod
	List<Resource> getParentResources(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

	/**
	 * Gets the number of parent resources for a particular Resource
	 * @param resourceId - the resource ID
	 * @return
	 */
	@WebMethod
	int getNumOfParentResources(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

	/**
	 * add a child resource to a parent
	 * @param resourceId - the parent resource ID
	 * @param memberResourceId - the child resource ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response addChildResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId);

	/**
	 * Removes a child resource from a  parent
	 * @param resourceId - the parent resource ID
	 * @param memberResourceId
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response deleteChildResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId);

	/**
	 * Entitles a Group to a Resource
	 * @param resourceId - the resource ID
	 * @param groupId - the group ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response addGroupToResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "groupId", targetNamespace = "") final String groupId);

	/**
	 * Disentitles a Group from a Resource
	 * @param resourceId - the resource ID
	 * @param groupId - the group ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response removeGroupToResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "groupId", targetNamespace = "") final String groupId);

	/**
	 * entitle a Role to a Resource
	 * @param resourceId - the resource ID
	 * @param roleId - the role ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response addRoleToResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "roleId", targetNamespace = "") final String roleId);

	/**
	 * Disentitles a Resource from a Role
	 * @param resourceId - the Resource ID
	 * @param roleId - the Role ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	Response removeRoleToResource(
			@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
			@WebParam(name = "roleId", targetNamespace = "") final String roleId);

	/**
	 * Gets the number of resources entitled to a Group
	 * @param groupId - the Group ID
	 * @return
	 */
	@WebMethod
	int getNumOfResourceForGroup(
			@WebParam(name = "groupId", targetNamespace = "") String groupId);

	/**
	 * Gets Resources that a Group is entitled to
	 * @param groupId - the Group ID
	 * @param from - where to start
	 * @param size - how many to return
	 * @return
	 */
	@WebMethod
	List<Resource> getResourcesForGroup(
			@WebParam(name = "groupId", targetNamespace = "") String groupId,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

	/**
	 * Gets the number of Resources that a User is Entitled to
	 * @param userId - the User ID
	 * @return
	 */
	@WebMethod
	int getNumOfResourceForUser(
			@WebParam(name = "userId", targetNamespace = "") String userId);

	/**
	 * Gets Resources that a User is entitled to
	 * @param userId - the User ID
	 * @param from - where to start
	 * @param size - how many to return
	 * @return
	 */
	@WebMethod
	List<Resource> getResourcesForUser(
			@WebParam(name = "userId", targetNamespace = "") String userId,
			@WebParam(name = "from", targetNamespace = "") int from,
			@WebParam(name = "size", targetNamespace = "") int size);

    /**
     * Gets Resources that a User is entitled to by Resource type
     * @param userId - the User ID
     * @param resourceTypeId - resource type ID
     * @return
     */
    @WebMethod
    public List<Resource> getResourcesForUserByType(@WebParam(name = "userId", targetNamespace = "") final String userId,
                                                    @WebParam(name = "resourceTypeId", targetNamespace = "") final String resourceTypeId);
	/**
	 * Tells the caller if the user can be entitled to this resource
	 * @param userId - the User ID
	 * @param resourceId - the Resource ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	public Response canAddUserToResource(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
										 final @WebParam(name = "resourceId", targetNamespace = "") String resourceId);
	
	/**
	 * Tells the caller if the user can be disentitled from this resource
	 * @param userId - the User ID
	 * @param resourceId - the Resource ID
	 * @return a Response Object, with details about the result of the operation
	 */
	@WebMethod
	public Response canRemoveUserFromResource(final @WebParam(name = "userId", targetNamespace = "")  String userId, 
										 	  final @WebParam(name = "resourceId", targetNamespace = "") String resourceId); 

}