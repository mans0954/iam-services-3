package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceGroup;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {
	
	@Autowired
    private ResourceDAO resourceDao;
	
	@Autowired
    private ResourceTypeDAO resourceTypeDao;
	
	@Autowired
    private ResourcePropDAO resourcePropDao;
	
	@Autowired
    private ResourceRoleDAO resourceRoleDao;
    
    @Autowired
    private ResourceUserDAO resourceUserDao;
    
    @Autowired
    private ResourceGroupDAO resourceGroupDAO;
    
    @Autowired
    private ResourceSearchBeanConverter resourceSearchBeanConverter;

	@Override
	public void deleteResource(String resourceId) {
		if(StringUtils.isNotBlank(resourceId)) {
			final ResourceEntity entity = resourceDao.findById(resourceId);
			if(entity != null) {
				resourceGroupDAO.deleteByResourceId(resourceId);
				resourceRoleDao.deleteByResourceId(resourceId);
				resourceUserDao.deleteByResourceId(resourceId);
				resourceDao.delete(entity);
				resourceDao.delete(entity);
			}
		}
	}

	@Override
	public void save(ResourceEntity entity) {
		if(StringUtils.isNotBlank(entity.getResourceId())) {
			final ResourceEntity dbObject = resourceDao.findById(entity.getResourceId());
			entity.setChildResources(dbObject.getChildResources());
			entity.setParentResources(dbObject.getParentResources());
			entity.setResourceGroups(dbObject.getResourceGroups());
			entity.setResourceProps(dbObject.getResourceProps());
			entity.setResourceUsers(dbObject.getResourceUsers());
			entity.setResourceRoles(dbObject.getResourceRoles());
			if(entity.getResourceType() != null) {
				final ResourceTypeEntity resourceType = resourceTypeDao.findById(entity.getResourceType().getResourceTypeId());
				entity.setResourceType(resourceType);
			}
			resourceDao.merge(entity);
		} else {
			resourceDao.save(entity);
		}
	}

	@Override
	public ResourceEntity findResourceById(String resourceId) {
		return resourceDao.findById(resourceId);
	}

	@Override
	public int count(ResourceSearchBean searchBean) {
		final ResourceEntity entity = resourceSearchBeanConverter.convert(searchBean);
    	return resourceDao.count(entity);
	}

	@Override
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
		final ResourceEntity resource = resourceSearchBeanConverter.convert(searchBean);
		List<ResourceEntity> resultsEntities = null;
		if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
			resultsEntities = resourceDao.getRootResources(resource, from, size);
		} else {
			resultsEntities = resourceDao.getByExample(resource, from, size);
		}
		return resultsEntities;
	        
	}

	@Override
	public ResourceEntity findResourceByName(String name) {
		return resourceDao.findByName(name);
	}

	@Override
	public void save(ResourceTypeEntity entity) {
		if(StringUtils.isBlank(entity.getResourceTypeId())) {
			resourceTypeDao.save(entity);
		} else {
			resourceTypeDao.merge(entity);
		}
	}

	@Override
	public ResourceTypeEntity findResourceTypeById(String id) {
		return resourceTypeDao.findById(id);
	}

	@Override
	public List<ResourceTypeEntity> getAllResourceTypes() {
		return resourceTypeDao.findAll();
	}

	@Override
	public void save(ResourcePropEntity entity) {
		if(StringUtils.isBlank(entity.getResourcePropId())) {
			resourcePropDao.save(entity);
		} else {
			resourcePropDao.merge(entity);
		}
	}

	@Override
	public void deleteResourceProp(String id) {
		final ResourcePropEntity entity = resourcePropDao.findById(id);
		if(entity != null) {
			resourcePropDao.delete(entity);
		}
	}

	@Override
	public void deleteResourceUser(String userId, String resourceId) {
		final ResourceUserEntity entity = resourceUserDao.getRecord(resourceId, userId);
		if(entity != null) {
			resourceUserDao.delete(entity);
		}
	}

	@Override
	public ResourceUserEntity getResourceUser(String userId, String resourceId) {
		return resourceUserDao.getRecord(resourceId, userId);
	}

	@Override
	public void save(ResourceUserEntity entity) {
		resourceUserDao.save(entity);
	}

	@Override
	public List<ResourceEntity> getChildResources(String resourceId, int from, int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		final List<ResourceEntity> resultList = resourceDao.getByExample(example, from, size);
		return resultList;
	}

	@Override
	public int getNumOfChildResources(String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		return resourceDao.count(example);
	}

	@Override
	public List<ResourceEntity> getParentResources(String resourceId, int from, int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		return resourceDao.getByExample(example, from, size);
	}

	@Override
	public int getNumOfParentResources(String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		return resourceDao.count(example);
	}

	@Override
	public void addChildResource(String parentResourceId, String childResourceId) {
		final ResourceEntity parent = resourceDao.findById(parentResourceId);
		final ResourceEntity child = resourceDao.findById(childResourceId);
		parent.addChildResource(child);
		resourceDao.save(parent);
	}

	@Override
	public void deleteChildResource(String resourceId, String childResourceId) {
		final ResourceEntity parent = resourceDao.findById(resourceId);
		final ResourceEntity child = resourceDao.findById(childResourceId);
		parent.removeChildResource(child);
		resourceDao.save(parent);
	}

	@Override
	public ResourceGroupEntity getResourceGroup(String resourceId, String groupId) {
		return resourceGroupDAO.getRecord(resourceId, groupId);
	}

	@Override
	public void addResourceGroup(String resourceId, String groupId) {
		final ResourceGroupEntity entity = new ResourceGroupEntity();
		entity.setGroupId(groupId);
		entity.setResourceId(resourceId);
		resourceGroupDAO.save(entity);
	}

	@Override
	public void deleteResourceGroup(String resourceId, String groupId) {
		final ResourceGroupEntity entity = getResourceGroup(resourceId, groupId);
		if(entity != null) {
			resourceGroupDAO.delete(entity);
		}
	}

	@Override
	public ResourceRoleEntity getResourceRole(String resourceId, String roleId) {
		final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(roleId, resourceId);
		return resourceRoleDao.findById(id);
	}

	@Override
	public void saveResourceRole(String resourceId, String roleId) {
		final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(roleId, resourceId);
		final ResourceRoleEntity entity = new ResourceRoleEntity();
		entity.setId(id);
		resourceRoleDao.save(entity);
	}

	@Override
	public void deleteResourceRole(String resourceId, String roleId) {
		final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(roleId, resourceId);
		final ResourceRoleEntity entity = resourceRoleDao.findById(id);
		if(entity != null) {
			resourceRoleDao.delete(entity);
		}
	}

	@Override
	public int getNumOfResourcesForRole(String roleId) {
		return resourceDao.getNumOfResourcesForRole(roleId);
	}

	@Override
	public List<ResourceEntity> getResourcesForRole(String roleId, int from, int size) {
		return resourceDao.getResourcesForRole(roleId, from, size);
	}

	@Override
	public int getNumOfResourceForGroup(String groupId) {
		return resourceDao.getNumOfResourcesForGroup(groupId);
	}

	@Override
	public List<ResourceEntity> getResourcesForGroup(String groupId, int from, int size) {
		return resourceDao.getResourcesForGroup(groupId, from, size);
	}

	@Override
	public int getNumOfResourceForUser(String userId) {
		return resourceDao.getNumOfResourcesForUser(userId);
	}

	@Override
	public List<ResourceEntity> getResourcesForUser(String userId, int from, int size) {
		return resourceDao.getResourcesForUser(userId, from, size);
	}

	@Override
	public List<ResourceEntity> findResourcesByIds(
			Collection<String> resourceIdCollection) {
		return resourceDao.findByIds(resourceIdCollection);
	}

}
