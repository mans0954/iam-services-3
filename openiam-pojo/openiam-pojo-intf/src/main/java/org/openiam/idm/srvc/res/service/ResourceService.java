package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;

public interface ResourceService {

	public void deleteResource(final String resourceId);
	public void save(final ResourceEntity resource, final String requestorId);
	public ResourceEntity findResourceById(final String resourceId);
	public List<ResourceEntity> findResourcesByIds(final Collection<String> resourceIdCollection);
	public ResourceEntity findResourceByName(final String name);
	public int count(final ResourceSearchBean searchBean);
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size);
	public int getNumOfChildResources(final String resourceId);
	public List<ResourceEntity> getParentResources(final  String resourceId, final int from, final int size);
	public int getNumOfParentResources(final String resourceId);
	public List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);
	public int getNumOfResourceForGroup(final String groupId, final ResourceSearchBean searchBean);
	public List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);
	public int getNumOfResourceForUser(final String userId, final ResourceSearchBean searchBean);
	public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForUserByType(String userId, String resourceTypeId, final ResourceSearchBean searchBean);

	public void save(final ResourceTypeEntity entity);
	public ResourceTypeEntity findResourceTypeById(final String id);
	public List<ResourceTypeEntity> getAllResourceTypes();

    public void save(final ResourcePropEntity entity);
    public ResourcePropEntity findResourcePropById(final String id);
	public void deleteResourceProp(final String id);

	public List<ResourceEntity> getChildResources(final String resourceId, final int from, final int size);
	
	public void addChildResource(final String parentResourceId, final String childResourceId);
	public void deleteChildResource(final String resourceId, final String childResourceId);

	public void addResourceGroup(final String resourceId, final String groupId);
	public void deleteResourceGroup(final String resourceId, final String groupId);

	public void deleteResourceRole(final String resourceId, final String roleId);
	public int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean);
    public void addResourceToRole(final String resourceId, final String roleId);
    
    public void validateResource2ResourceAddition(final String parentId, final String memberId) throws BasicDataServiceException;
    
    public Resource getResourceDTO(final String resourceId);
    
    public void validateResourceDeletion(final String resourceId) throws BasicDataServiceException;
    
    public List<ResourceTypeEntity> findResourceTypes(ResourceTypeSearchBean searchBean, int from, int size);
}
