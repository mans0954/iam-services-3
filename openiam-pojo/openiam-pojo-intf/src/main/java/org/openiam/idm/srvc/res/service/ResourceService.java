package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.List;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.Resource;

public interface ResourceService {

	public void deleteResource(final String resourceId);
	public void save(final ResourceEntity resource);
	public ResourceEntity findResourceById(final String resourceId);
	public List<ResourceEntity> findResourcesByIds(final Collection<String> resourceIdCollection);
	public ResourceEntity findResourceByName(final String name);
	public int count(final ResourceSearchBean searchBean);
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size);
	public int getNumOfChildResources(final String resourceId);
	public List<ResourceEntity> getParentResources(final  String resourceId, final int from, final int size);
	public int getNumOfParentResources(final String resourceId);
	public List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size);
	public int getNumOfResourceForGroup(final String groupId);
	public List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size);
	public int getNumOfResourceForUser(final String userId);
	public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size);
	
	public void save(final ResourceTypeEntity entity);
	public ResourceTypeEntity findResourceTypeById(final String id);
	public List<ResourceTypeEntity> getAllResourceTypes();
	public void save(final ResourcePropEntity entity);
	
	public void deleteResourceProp(final String id);
	public void deleteResourceUser(final String userId, final String resourceId);
	
	public ResourceUserEntity getResourceUser(final String userId, final String resourceId);
	public void save(final ResourceUserEntity entity);
	public List<ResourceEntity> getChildResources(final String resourceId, final int from, final int size);
	
	public void addChildResource(final String parentResourceId, final String childResourceId);
	public void deleteChildResource(final String resourceId, final String childResourceId);
	
	public ResourceGroupEntity getResourceGroup(final String resourceId, final String groupId);
	public void addResourceGroup(final String resourceId, final String groupId);
	public void deleteResourceGroup(final String resourceId, final String groupId);
	
	public ResourceRoleEntity getResourceRole(final String resourceId, final String roleId);
	public void saveResourceRole(final String resourceId, final String roleId);
	public void deleteResourceRole(final String resourceId, final String roleId);
	public int getNumOfResourcesForRole(final String roleId);
    public void evict(Object entity);
}
