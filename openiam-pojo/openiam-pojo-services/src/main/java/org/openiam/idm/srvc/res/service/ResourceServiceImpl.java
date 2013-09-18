package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceServiceImpl implements ResourceService {
	
	@Autowired
    private ResourceDAO resourceDao;
    @Autowired
    private RoleDAO roleDao;
    @Autowired
    private GroupDAO groupDao;
	@Autowired
    private ResourceTypeDAO resourceTypeDao;
	
	@Autowired
    private ResourcePropDAO resourcePropDao;
	
	@Autowired
  	private ResourceDozerConverter dozerConverter;

	@Override
    @Transactional
	public void deleteResource(String resourceId) {
		if(StringUtils.isNotBlank(resourceId)) {
			final ResourceEntity entity = resourceDao.findById(resourceId);
			if(entity != null) {
				resourceDao.delete(entity);
			}
		}
	}

	@Override
    @Transactional
	public void save(ResourceEntity entity) {
		if(StringUtils.isNotBlank(entity.getResourceId())) {
			final ResourceEntity dbObject = resourceDao.findById(entity.getResourceId());
			entity.setChildResources(dbObject.getChildResources());
			entity.setParentResources(dbObject.getParentResources());
			entity.setUsers(dbObject.getUsers());
			entity.setGroups(dbObject.getGroups());
			entity.setRoles(dbObject.getRoles());
			if(entity.getResourceType() != null) {
				entity.setResourceType(resourceTypeDao.findById(entity.getResourceType().getResourceTypeId()));
			}
			
			mergeAttribute(entity, dbObject);
			
			resourceDao.merge(entity);
		} else {
			resourceDao.save(entity);
		}
	}

	private void mergeAttribute(final ResourceEntity bean, final ResourceEntity dbObject) {
		final Set<ResourcePropEntity> renewedProperties = new HashSet<ResourcePropEntity>();
		
		Set<ResourcePropEntity> beanProps = (bean.getResourceProps() != null) ? bean.getResourceProps() : new HashSet<ResourcePropEntity>();
		Set<ResourcePropEntity> dbProps = (dbObject.getResourceProps() != null) ? dbObject.getResourceProps() : new HashSet<ResourcePropEntity>();
		
		/* delete */
		/*
		for(final Iterator<ResourcePropEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
			final ResourcePropEntity dbProp = dbIt.next();
			
			boolean contains = false;
			for(final Iterator<ResourcePropEntity> it = beanProps.iterator(); it.hasNext();) {
				final ResourcePropEntity beanProp = it.next();
				if(StringUtils.equals(dbProp.getResourcePropId(), beanProp.getResourcePropId())) {
					contains = true;
					break;
				}
			}
			
			if(!contains) {
				dbIt.remove();
			}
		}
		*/
			
		/* update */
		for(ResourcePropEntity dbProp : dbProps) {
			for(final ResourcePropEntity beanProp : beanProps) {
				if(StringUtils.equals(dbProp.getResourcePropId(), beanProp.getResourcePropId())) {
					dbProp.setPropValue(beanProp.getPropValue());
					dbProp.setMetadataId(beanProp.getMetadataId());
					dbProp.setName(beanProp.getName());
					renewedProperties.add(dbProp);
					break;
				}
			}
		}
		
		/* add */
		for(final ResourcePropEntity beanProp : beanProps) {
			boolean contains = false;
			for(ResourcePropEntity dbProp : dbProps) {
				if(StringUtils.equals(dbProp.getResourcePropId(), beanProp.getResourcePropId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				beanProp.setResource(bean);
				//dbProps.add(beanProp);
				renewedProperties.add(beanProp);
			}
		}
		
		bean.setResourceProps(renewedProperties);
	}

	@Override
    @Transactional(readOnly = true)
	public ResourceEntity findResourceById(String resourceId) {
		return resourceDao.findById(resourceId);
	}

	@Override
    @Transactional(readOnly = true)
	public int count(ResourceSearchBean searchBean) {
		//final ResourceEntity entity = resourceSearchBeanConverter.convert(searchBean);
    	return resourceDao.count(searchBean);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
		//final ResourceEntity resource = resourceSearchBeanConverter.convert(searchBean);
		List<ResourceEntity> resultsEntities = null;
		//if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
		//	resultsEntities = resourceDao.getRootResources(resource, from, size);
		//} else {
			resultsEntities = resourceDao.getByExample(searchBean, from, size);
		//}
		return resultsEntities;
	        
	}

	@Override
    @Transactional(readOnly = true)
    public ResourcePropEntity findResourcePropById(String id) {
        return resourcePropDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
	public ResourceEntity findResourceByName(String name) {
		return resourceDao.findByName(name);
	}

	@Override
    @Transactional
	public void save(ResourceTypeEntity entity) {
		if(StringUtils.isBlank(entity.getResourceTypeId())) {
			resourceTypeDao.save(entity);
		} else {
			resourceTypeDao.merge(entity);
		}
	}

	@Override
    @Transactional(readOnly = true)
	public ResourceTypeEntity findResourceTypeById(String id) {
		return resourceTypeDao.findById(id);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceTypeEntity> getAllResourceTypes() {
		return resourceTypeDao.findAll();
	}

	@Override
    @Transactional
	public void save(ResourcePropEntity entity) {
		if(StringUtils.isBlank(entity.getResourcePropId())) {
			resourcePropDao.save(entity);
		} else {
			resourcePropDao.merge(entity);
		}
	}

	@Override
    @Transactional
	public void deleteResourceProp(String id) {
		final ResourcePropEntity entity = resourcePropDao.findById(id);
		if(entity != null) {
			resourcePropDao.delete(entity);
		}
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> getChildResources(String resourceId, int from, int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		final List<ResourceEntity> resultList = resourceDao.getByExample(example, from, size);
		return resultList;
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfChildResources(String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		return resourceDao.count(example);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> getParentResources(String resourceId, int from, int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		return resourceDao.getByExample(example, from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfParentResources(String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		return resourceDao.count(example);
	}

	@Override
    @Transactional
	public void addChildResource(String parentResourceId, String childResourceId) {
		final ResourceEntity parent = resourceDao.findById(parentResourceId);
		final ResourceEntity child = resourceDao.findById(childResourceId);
		parent.addChildResource(child);
		resourceDao.save(parent);
	}

	@Override
    @Transactional
	public void deleteChildResource(String resourceId, String childResourceId) {
		final ResourceEntity parent = resourceDao.findById(resourceId);
		final ResourceEntity child = resourceDao.findById(childResourceId);
		parent.removeChildResource(child);
		resourceDao.save(parent);
	}

	@Override
    @Transactional
	public void addResourceGroup(String resourceId, String groupId) {
		ResourceEntity resourceEntity = resourceDao.findById(resourceId);
        GroupEntity groupEntity = groupDao.findById(groupId);
        resourceEntity.getGroups().add(groupEntity);
	}

	@Override
    @Transactional
	public void deleteResourceGroup(String resourceId, String groupId) {
        ResourceEntity resourceEntity = resourceDao.findById(resourceId);
        GroupEntity groupEntity = groupDao.findById(groupId);
        resourceEntity.getGroups().remove(groupEntity);
	}

	@Override
    @Transactional
    public void addResourceToRole(String resourceId, String roleId) {
        ResourceEntity resourceEntity = resourceDao.findById(resourceId);
        RoleEntity roleEntity = roleDao.findById(roleId);
        resourceEntity.getRoles().add(roleEntity);
	}


	@Override
    @Transactional
	public void deleteResourceRole(String resourceId, String roleId) {
        ResourceEntity resourceEntity = resourceDao.findById(resourceId);
        RoleEntity roleEntity = roleDao.findById(roleId);
        resourceEntity.getRoles().remove(roleEntity);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfResourcesForRole(String roleId) {
		return resourceDao.getNumOfResourcesForRole(roleId);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> getResourcesForRole(String roleId, int from, int size) {
		return resourceDao.getResourcesForRole(roleId, from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfResourceForGroup(String groupId) {
		return resourceDao.getNumOfResourcesForGroup(groupId);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> getResourcesForGroup(String groupId, int from, int size) {
		return resourceDao.getResourcesForGroup(groupId, from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfResourceForUser(String userId) {
		return resourceDao.getNumOfResourcesForUser(userId);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> getResourcesForUser(String userId, int from, int size) {
		return resourceDao.getResourcesForUser(userId, from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ResourceEntity> findResourcesByIds(
			Collection<String> resourceIdCollection) {
		return resourceDao.findByIds(resourceIdCollection);
	}

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getResourcesForManagedSys(String mngSysId, int from, int size) {
        return resourceDao.getResourcesForManagedSys(mngSysId, from, size);
    }

	@Override
	@Transactional
	public void validateResource2ResourceAddition(final String parentId, final String memberId) throws BasicDataServiceException {
		final ResourceEntity parent = resourceDao.findById(parentId);
		final ResourceEntity child = resourceDao.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<ResourceEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(parent.hasChildResoruce(child)) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
		
		if(parent.getResourceType() != null && child.getResourceType() != null &&
		  !parent.getResourceType().equals(child.getResourceType())) {
			throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPES_NOT_EQUAL);
		}
	}
	
	private boolean causesCircularDependency(final ResourceEntity parent, final ResourceEntity child, final Set<ResourceEntity> visitedSet) {
		boolean retval = false;
		if (parent != null && child != null) {
			if (!visitedSet.contains(child)) {
				visitedSet.add(child);
				if (CollectionUtils.isNotEmpty(parent.getParentResources())) {
					for (final ResourceEntity entity : parent.getParentResources()) {
						retval = entity.getResourceId().equals(
								child.getResourceId());
						if (retval) {
							break;
						}
						causesCircularDependency(parent, entity, visitedSet);
					}
				}
			}
		}
		return retval;
	}

	@Override
	public Resource getResourceDTO(String resourceId) {
		return dozerConverter.convertToDTO(resourceDao.findById(resourceId), true);
	}
}
