package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementPageTemplateDAO;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Autowired
	private AuthProviderDao authProviderDAO;
	
	@Autowired
	private ContentProviderDao contentProviderDAO;
	
	@Autowired
	private URIPatternDao uriPatternDAO;
	
	@Autowired
	private MetadataElementDAO elementDAO;
	
	@Autowired
	private ManagedSysDAO managedSysDAO;
	
	@Autowired
	private MetadataElementPageTemplateDAO templateDAO;
	
	@Value("${org.openiam.resource.system.action.id}")
	private String systemActionId;

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
			entity.setAdminResource(dbObject.getAdminResource());
			if(entity.getAdminResource() == null) {
				final ResourceEntity adminResource = getNewAdminResource(entity);
				//resourceDao.save(adminResource);
				entity.setAdminResource(adminResource);
			}
			entity.setChildResources(dbObject.getChildResources());
			entity.setParentResources(dbObject.getParentResources());
			entity.setUsers(dbObject.getUsers());
			entity.setGroups(dbObject.getGroups());
			entity.setRoles(dbObject.getRoles());
			if(entity.getResourceType() != null) {
				entity.setResourceType(resourceTypeDao.findById(entity.getResourceType().getId()));
			}
			
			mergeAttribute(entity, dbObject);
			
			resourceDao.merge(entity);
		} else {
			entity.setAdminResource(getNewAdminResource(entity));
			resourceDao.save(entity);
		}
	}
	
	private ResourceEntity getNewAdminResource(final ResourceEntity entity) {
		final ResourceEntity adminResource = new ResourceEntity();
		adminResource.setName(String.format("RES_ADMIN_%s_%s", entity.getName(), RandomStringUtils.randomAlphanumeric(2)));
		adminResource.setResourceType(resourceTypeDao.findById(systemActionId));
		return adminResource;
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
		if(StringUtils.isBlank(entity.getId())) {
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
		
		if(parent.getResourceType() != null && !parent.getResourceType().isSupportsHierarchy()) {
			throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, parent.getResourceType().getDescription());
		}
		
		if(child.getResourceType() != null && !child.getResourceType().isSupportsHierarchy()) {
			throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, child.getResourceType().getDescription());
		}
	}
	
	@Override
	@Transactional
	public void validateResourceDeletion(final String resourceId) throws BasicDataServiceException {
		final ResourceEntity entity = resourceDao.findById(resourceId);
		if(entity != null) {
			
			final List<ManagedSysEntity> managedSystems = managedSysDAO.findByResource(resourceId);
			if(CollectionUtils.isNotEmpty(managedSystems)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_MANAGED_SYSTEM, managedSystems.get(0).getName());
			}
			
			final List<ContentProviderEntity> contentProviders = contentProviderDAO.getByResourceId(resourceId);
			if(CollectionUtils.isNotEmpty(contentProviders)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_CONTENT_PROVIDER, contentProviders.get(0).getName());
			}
			
			final List<URIPatternEntity> uriPatterns = uriPatternDAO.getByResourceId(resourceId);
			if(CollectionUtils.isNotEmpty(uriPatterns)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_URI_PATTERN, uriPatterns.get(0).getPattern());
			}
			
			final List<AuthProviderEntity> authProviders = authProviderDAO.getByResourceId(resourceId);
			if(CollectionUtils.isNotEmpty(authProviders)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER, authProviders.get(0).getName());
			}
			
			final List<MetadataElementEntity> metadataElements = elementDAO.getByResourceId(resourceId);
			if(CollectionUtils.isNotEmpty(metadataElements)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_METADATA_ELEMENT, metadataElements.get(0).getAttributeName());
			}
			
			final List<MetadataElementPageTemplateEntity> pageTemplates = templateDAO.getByResourceId(resourceId);
			if(CollectionUtils.isNotEmpty(pageTemplates)) {
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_PAGE_TEMPLATE, pageTemplates.get(0).getName());
			}
			
			final ResourceEntity searchBean = new ResourceEntity();
			searchBean.setAdminResource(new ResourceEntity(resourceId));
			final List<ResourceEntity> adminOfResources = resourceDao.getByExample(searchBean);
			if(CollectionUtils.isNotEmpty(adminOfResources)) {
				throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN, adminOfResources.get(0).getName());
			}
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
