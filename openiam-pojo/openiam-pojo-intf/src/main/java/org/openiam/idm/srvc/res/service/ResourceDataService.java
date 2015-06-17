package org.openiam.idm.srvc.res.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;
import java.util.Set;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/res/service", name = "ResourceDataWebService")
public interface ResourceDataService {

	@WebMethod
	boolean isMemberOfAnyEntity(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId);
	
    @WebMethod
    Response validateDelete(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    /**
     * Validate if the resource can be created
     * 
     * @param resource
     *            - the Resource
     * @return
     */
    @WebMethod
    Response validateEdit(final @WebParam(name = "resource", targetNamespace = "") Resource resource);

    /**
     * Deletes a Resoruce
     * 
     * @param resourceId
     *            - the resource id
     * @return
     */
    @WebMethod
    Response deleteResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Add a new resource from a transient resource object and sets resourceId
     * in the returned object.
     * 
     * @param resource
     * @return
     */
    @WebMethod
    Response saveResource(final @WebParam(name = "resource", targetNamespace = "") Resource resource,
                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Find a resource.
     * 
     * @param resourceId
     *            the resource id
     * @return resource
     */
    @WebMethod
    Resource getResource(
    		@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    		final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Find   resource list.
     *
     * @param resourceIds
     *            the resource id list
     * @return resource
     */
    @WebMethod
    List<Resource> getResourcesByIds(final @WebParam(name = "resourceIds", targetNamespace = "") List<String> resourceIds,
                                     final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    String getResourcePropValueByName(@WebParam(name = "resourceId", targetNamespace = "") String resourceId, @WebParam(name = "propName", targetNamespace = "") String propName);

    /**
     * Find all resource types.
     * 
     * @return the all resource types
     */
    @WebMethod
    List<ResourceType> getAllResourceTypes(final Language language);

    @WebMethod
    List<ResourceType> findResourceTypes(final ResourceTypeSearchBean searchBean, final int from, final int size, final Language language);

    /**
     * Add a resource property.
     * 
     * @param resourceProp
     *            the resource prop
     * @return the resource prop
     */
    @WebMethod
    Response addResourceProp(@WebParam(name = "resourceProp", targetNamespace = "") ResourceProp resourceProp,
                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Update a resource property.
     * 
     * @param resourceProp
     *            the resource prop
     * @return the resource prop
     */
    @WebMethod
    Response updateResourceProp(@WebParam(name = "resourceProp", targetNamespace = "") ResourceProp resourceProp,
                                final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Remove a resource property.
     * 
     * @param resourcePropId
     *            the resource prop id
     */
    @WebMethod
    Response removeResourceProp(@WebParam(name = "resourcePropId", targetNamespace = "") String resourcePropId,
                                final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Entitles a User to a Resource
     * 
     * @param resourceId
     *            - resource ID
     * @param userId
     *            - user ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response addUserToResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    						   final @WebParam(name = "userId", targetNamespace = "") String userId,
    						   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    						   final @WebParam(name = "rightIds", targetNamespace = "") Set<String> rightIds);

    /**
     * Disentitled a User from a Resource
     * 
     * @param resourceId
     *            - resource ID
     * @param userId
     *            - user ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response removeUserFromResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
	    final @WebParam(name = "userId", targetNamespace = "") String userId,
        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Get the number of resources that a particular role is entitled to
     * 
     * @param roleId
     *            - the Role ID
     * @return
     */
    @WebMethod
    @Deprecated
    int getNumOfResourcesForRole(@WebParam(name = "roleId", targetNamespace = "") String roleId,
	    @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean);

    /**
     * Gets a List of Resources that a Role is entitled to
     * 
     * @param roleId
     *            - role ID
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getResourcesForRole(
    	final @WebParam(name = "roleId", targetNamespace = "") String roleId,
    	final @WebParam(name = "from", targetNamespace = "") int from,
	    final @WebParam(name = "size", targetNamespace = "") int size,
	    final @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
	    final @WebParam(name = "language", targetNamespace = "") Language language
    );

    /**
     * Search a Resource
     * 
     * @param searchBean
     *            - search bean, containing search parameters
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return - the search results
     */
    @WebMethod
    List<Resource> findBeans(
    			final @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
    			final @WebParam(name = "from", targetNamespace = "") int from,
    			final @WebParam(name = "size", targetNamespace = "") int size,
    			final @WebParam(name = "language", targetNamespace = "") Language language
    );

    /**
     * count the number of resources based on the searchBean
     * 
     * @param searchBean
     * @return
     */
    @WebMethod
    int count(@WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean);

    /**
     * Find the child resources of a particular Resource
     * 
     * @param resourceId
     *            - resource ID
     * @param deepFlag
     *            - shows that method returns Resource collection with all sub
     *            collections
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getChildResources(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    								 final @WebParam(name = "deepFlag", targetNamespace = "") Boolean deepFlag,
    								 final @WebParam(name = "from", targetNamespace = "") int from,
    								 final @WebParam(name = "size", targetNamespace = "") int size,
    								 final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Gets the number of child resources for a particular Resource
     * 
     * @param resourceId
     *            - the resource ID
     * @return
     */
    @WebMethod
    @Deprecated
    int getNumOfChildResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

    /**
     * Find the parent resources of a particular Resource
     * 
     * @param resourceId
     *            - resource ID
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getParentResources(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    								  final @WebParam(name = "from", targetNamespace = "") int from,
    								  final @WebParam(name = "size", targetNamespace = "") int size,
    								  final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Gets the number of parent resources for a particular Resource
     * 
     * @param resourceId
     *            - the resource ID
     * @return
     */
    @WebMethod
    @Deprecated
    int getNumOfParentResources(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId);

    /**
     * add a child resource to a parent
     * 
     * @param resourceId
     *            - the parent resource ID
     * @param memberResourceId
     *            - the child resource ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response addChildResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    						  @WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId,
    						  @WebParam(name = "requesterId", targetNamespace = "") final String requesterId,
    						  @WebParam(name = "rights", targetNamespace = "") final Set<String> rights);

    @WebMethod
    Response validateAddChildResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
    								  @WebParam(name = "childResourceId", targetNamespace = "") final String childResourceId,
    								  @WebParam(name = "rights", targetNamespace = "") final Set<String> rights);

    /**
     * Removes a child resource from a parent
     * 
     * @param resourceId
     *            - the parent resource ID
     * @param memberResourceId
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response deleteChildResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
	    @WebParam(name = "memberResourceId", targetNamespace = "") final String memberResourceId,
        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Entitles a Group to a Resource
     * 
     * @param resourceId
     *            - the resource ID
     * @param groupId
     *            - the group ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response addGroupToResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    							final @WebParam(name = "groupId", targetNamespace = "") String groupId,
    							final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    							final @WebParam(name = "rightIds", targetNamespace = "") Set<String> rightIds);

    /**
     * Disentitles a Group from a Resource
     * 
     * @param resourceId
     *            - the resource ID
     * @param groupId
     *            - the group ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response removeGroupToResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    							   final @WebParam(name = "groupId", targetNamespace = "") String groupId,
    							   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * entitle a Role to a Resource
     * 
     * @param resourceId
     *            - the resource ID
     * @param roleId
     *            - the role ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response addRoleToResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    						   final @WebParam(name = "roleId", targetNamespace = "") String roleId,
    						   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
    						   final @WebParam(name = "rightIds", targetNamespace = "") Set<String> rightIds);

    /**
     * Disentitles a Resource from a Role
     * 
     * @param resourceId
     *            - the Resource ID
     * @param roleId
     *            - the Role ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response removeRoleToResource(final @WebParam(name = "resourceId", targetNamespace = "") String resourceId,
    							  final @WebParam(name = "roleId", targetNamespace = "") String roleId,
    							  final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets the number of resources entitled to a Group
     * 
     * @param groupId
     *            - the Group ID
     * @return
     */
    @WebMethod
    @Deprecated
    int getNumOfResourceForGroup(@WebParam(name = "groupId", targetNamespace = "") String groupId,
	    @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean);

    /**
     * Gets Resources that a Group is entitled to
     * 
     * @param groupId
     *            - the Group ID
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getResourcesForGroup(
    		final @WebParam(name = "groupId", targetNamespace = "") String groupId,
    		final @WebParam(name = "from", targetNamespace = "") int from,
    		final @WebParam(name = "size", targetNamespace = "") int size,
    		final@WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
    		final @WebParam(name = "language", targetNamespace = "") Language language);


    /**
     * Gets the number of Resources that a User is Entitled to
     * 
     * @param userId
     *            - the User ID
     * @return
     */
    @WebMethod
    @Deprecated
    int getNumOfResourceForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
	    @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean);

    /**
     * Gets Resources that a User is entitled to
     * 
     * @param userId
     *            - the User ID
     * @param from
     *            - where to start
     * @param size
     *            - how many to return
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getResourcesForUser(
    		final @WebParam(name = "userId", targetNamespace = "") String userId,
    		final @WebParam(name = "from", targetNamespace = "") int from,
    		final @WebParam(name = "size", targetNamespace = "") int size,
    		final @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
    		final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Gets Resources that a User is entitled to by Resource type
     * 
     * @param userId
     *            - the User ID
     * @param resourceTypeId
     *            - resource type ID
     * @return
     */
    @WebMethod
    @Deprecated
    List<Resource> getResourcesForUserByType(
            final @WebParam(name = "userId", targetNamespace = "") String userId,
            final @WebParam(name = "resourceTypeId", targetNamespace = "") String resourceTypeId,
            final @WebParam(name = "searchBean", targetNamespace = "") ResourceSearchBean searchBean,
            final @WebParam(name = "language", targetNamespace = "") Language language);

    /**
     * Tells the caller if the user can be entitled to this resource
     * 
     * @param userId
     *            - the User ID
     * @param resourceId
     *            - the Resource ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response canAddUserToResource(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                  final @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    /**
     * Tells the caller if the user can be disentitled from this resource
     * 
     * @param userId
     *            - the User ID
     * @param resourceId
     *            - the Resource ID
     * @return a Response Object, with details about the result of the operation
     */
    @WebMethod
    Response canRemoveUserFromResource(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                       final @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    @WebMethod
    int countResourceTypes(@WebParam(name = "searchBean", targetNamespace = "") ResourceTypeSearchBean searchBean);

    @WebMethod
    Response saveResourceType(@WebParam(name = "searchBean", targetNamespace = "") ResourceType resourceType,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    Response deleteResourceType(final @WebParam(name = "resourceTypeId", targetNamespace = "") String resourceTypeId,
                                final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

}