package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.DozerUtils;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceRoleDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.dozer.converter.ResourceUserDozerConverter;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("resourceDataService")
@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl implements ResourceDataService {

	@Autowired
	private ResourceDozerConverter resourceConverter;
	
	@Autowired
	private ResourceRoleDozerConverter resourceRoleConverter;
	
	@Autowired
	private ResourcePropDozerConverter resourcePropConverter;
	
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
    private GroupDAO groupDAO;
    
    @Autowired
    private RoleDAO roleDAO;
    
    @Autowired
    private ResourceTypeDozerConverter resourceTypeConverter;
    

    @Autowired
    private ResourceGroupDAO resourceGroupDAO;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);

    @Autowired
    private ResourceSearchBeanConverter resourceSearchBeanConverter;
    
    @Autowired
    private ResourceUserDozerConverter resourceUserConverter;

    public Resource getResource(String resourceId) {
    	Resource resource = null;
    	if(resourceId != null) {
    		 final ResourceEntity entity = resourceDao.findById(resourceId);
    		 if(entity != null) {
    			 resource = resourceConverter.convertToDTO(entity, true);
    		 }
    	}
    	return resource;
    }

    @WebMethod
    public int count(final ResourceSearchBean searchBean) {
    	final ResourceEntity entity = resourceSearchBeanConverter.convert(searchBean);
    	return resourceDao.count(entity);
    }

    @Override
    public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
        final ResourceEntity resource = resourceSearchBeanConverter.convert(searchBean);
        final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
        List<ResourceEntity> resultsEntities = null;
        if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
            resultsEntities = resourceDao.getRootResources(resource, from, size);
        } else {
            resultsEntities = resourceDao.getByExample(resource, from, size);
        }
        
        return resourceConverter.convertToDTOList(resultsEntities, DozerMappingType.DEEP.equals(mappingType));
    }
    
    public Response addResource(Resource resource) {
    	return saveOrUpdateResource(resource);
    }

    public Response updateResource(Resource resource) {
        return saveOrUpdateResource(resource);
    }
    
    private Response saveOrUpdateResource(final Resource resource) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resource == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
    		if(StringUtils.isEmpty(entity.getName())) {
    			throw new BasicDataServiceException(ResponseCode.NO_RESOURCE_NAME);
    		}
    		
    		
    		/* duplicate name check */
    		final ResourceEntity nameCheck = resourceDao.findByName(entity.getName());
    		if(nameCheck != null) {
    			if(StringUtils.isBlank(entity.getResourceId())) {
    				throw new BasicDataServiceException(ResponseCode.RESOURCE_NAME_EXISTS);
    			} else  if(!nameCheck.getResourceId().equals(entity.getResourceId())) {
    				throw new BasicDataServiceException(ResponseCode.RESOURCE_NAME_EXISTS);
    			}
    		}
    		
    		if(entity.getResourceType() == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_RESOURCE_TYPE);
    		}
    		
    		/* merge */
    		if(StringUtils.isNotBlank(entity.getResourceId())) {
    			final ResourceEntity dbObject = resourceDao.findById(resource.getResourceId());
    			if(dbObject == null) {
    				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    			}
    			//TODO: extend this merge
    			dbObject.setResourceType(entity.getResourceType());
    			dbObject.setDescription(entity.getDescription());
    			dbObject.setDomain(entity.getDomain());
    			dbObject.setIsPublic(entity.getIsPublic());
    			dbObject.setIsSSL(entity.getIsSSL());
    			dbObject.setManagedSysId(entity.getManagedSysId());
    			dbObject.setName(entity.getName());
    			dbObject.setURL(entity.getURL());
    			dbObject.setIsUrlProtector(entity.getIsUrlProtector());
    			resourceDao.update(dbObject);
    		} else {
    			resourceDao.save(entity);
    		}
    		
    		response.setResponseValue(entity.getResourceId());
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't save or update resource", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
    	return response;
    }

    public Response addResourceType(ResourceType val) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(val == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		final ResourceTypeEntity entity = resourceTypeConverter.convertToEntity(val, true);
    		resourceTypeDao.save(entity);
    		response.setResponseValue(entity.getResourceTypeId());
    	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
    }

    public ResourceType getResourceType(String resourceTypeId) {
    	ResourceType retVal = null;
    	if(resourceTypeId != null) {
    		final ResourceTypeEntity entity = resourceTypeDao.findById(resourceTypeId);
    		if(entity != null) {
    			retVal = resourceTypeConverter.convertToDTO(entity, false);
    		}
    	}
    	return retVal;
    }

    public Response updateResourceType(ResourceType resourceType) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceType == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		final ResourceTypeEntity entity = resourceTypeConverter.convertToEntity(resourceType, false);
    		resourceTypeDao.update(entity);
    	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
    }

    public List<ResourceType> getAllResourceTypes() {
        final List<ResourceTypeEntity> resourceTypeEntities = resourceTypeDao.findAll();
        return resourceTypeConverter.convertToDTOList(resourceTypeEntities, false);
    }

    public Response addResourceProp(final ResourceProp resourceProp) {
    	return saveOrUpdateResourceProperty(resourceProp);
    }

    public Response updateResourceProp(final ResourceProp resourceProp) {
        return saveOrUpdateResourceProperty(resourceProp);
    }
    
    private Response saveOrUpdateResourceProperty(final ResourceProp prop) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(prop == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourcePropEntity entity = resourcePropConverter.convertToEntity(prop, false);
    		if(StringUtils.isNotBlank(prop.getResourcePropId())) {
    			final ResourcePropEntity dbObject = resourcePropDao.findById(prop.getResourcePropId());
    			if(dbObject == null) {
    				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    			}
    		}
    		
    		if(StringUtils.isBlank(entity.getName())) {
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_NAME_MISSING);
    		}
    		
    		if(StringUtils.isBlank(entity.getPropValue())) {
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_VALUE_MISSING);
    		}
    		
    		if(StringUtils.isBlank(entity.getResourceId())) {
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING);
    		}
    		
    		if(StringUtils.isNotBlank(entity.getResourcePropId())) {
    			resourcePropDao.update(entity);
    		} else {
    			resourcePropDao.save(entity);
    		}
    		response.setResponseValue(entity.getResourcePropId());
    	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't save or update resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
    }

    public Response removeResourceProp(String resourcePropId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(StringUtils.isBlank(resourcePropId)) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourcePropEntity entity = resourcePropDao.findById(resourcePropId);
    		if(entity == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		resourcePropDao.delete(entity);
    	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
    	return response;
    }


    public Response addUserToResource(ResourceUser resourceUser) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceUser == null || resourceUser.getId() == null || resourceUser.getId().getResourceId() == null || resourceUser.getId().getUserId() == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourceUserEntity entity = resourceUserConverter.convertToEntity(resourceUser, true);
    		resourceUserDao.save(entity);
    		response.setResponseValue(resourceUserConverter.convertToDTO(entity, true));
    	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
    }

	@Override
	@Transactional
	public Response deleteResource(final String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			final ResourceEntity entity = resourceDao.findById(resourceId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			/*
			if(CollectionUtils.isNotEmpty(entity.getChildResources())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_CHILDREN);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getResourceGroups())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_GROUPS);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getResourceRoles())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_ROLES);
			}
			*/
			resourceGroupDAO.deleteByResourceId(resourceId);
			resourceRoleDao.deleteByResourceId(resourceId);
			resourceDao.delete(entity);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public List<Resource> getChildResources(final String resourceId, final int from, final int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		final List<ResourceEntity> resultList = resourceDao.getByExample(example, from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfChildResources(final String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity parent = new ResourceEntity();
		parent.setResourceId(resourceId);
		example.addParentResource(parent);
		return resourceDao.count(example);
	}
	
	@Override
	public List<Resource> getParentResources(final  String resourceId, final int from, final int size) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		final List<ResourceEntity> resultList = resourceDao.getByExample(example, from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfParentResources(final String resourceId) {
		final ResourceEntity example = new ResourceEntity();
		final ResourceEntity child = new ResourceEntity();
		child.setResourceId(resourceId);
		example.addChildResource(child);
		return resourceDao.count(example);
	}

	@Override
	public Response addChildResource(final String resourceId, final String memberResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity parent = resourceDao.findById(resourceId);
			if(parent == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceEntity child = resourceDao.findById(memberResourceId);
			if(child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(parent.hasChildResoruce(child)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			if(!parent.getResourceType().equals(child.getResourceType())) {
				throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPES_NOT_EQUAL);
			}
			
			if(parent.equals(child)) {
				throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
			}
			
			/* now check that this doesn't cause a circular dependency */
			if(causesCircularDependency(parent, child, new HashSet<ResourceEntity>())) {
				throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
			}
			
			parent.addChildResource(child);
			resourceDao.save(parent);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
	
	private boolean causesCircularDependency(final ResourceEntity parent, final ResourceEntity child, final Set<ResourceEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentResources())) {
					for(final ResourceEntity entity : parent.getParentResources()) {
						retval = entity.getResourceId().equals(child.getResourceId());
						if(retval) {
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
	public Response deleteChildResource(final String resourceId, final String memberResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity parent = resourceDao.findById(resourceId);
			if(parent == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceEntity child = resourceDao.findById(memberResourceId);
			if(child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			parent.removeChildResource(child);
			resourceDao.save(parent);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addGroupToResource(final String resourceId, final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity resource = resourceDao.findById(resourceId);
			final GroupEntity group = groupDAO.findById(groupId);
			
			if(resource == null || group == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceGroupEntity record = resourceGroupDAO.getRecord(resourceId, groupId);
			if(record != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			final ResourceGroupEntity entity = new ResourceGroupEntity();
			entity.setGroupId(groupId);
			entity.setResourceId(resourceId);
			
			resourceGroupDAO.save(entity);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeGroupToResource(final String resourceId, final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity resource = resourceDao.findById(resourceId);
			if(resource == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceGroupEntity entity = resourceGroupDAO.getRecord(resourceId, groupId);
			if(entity != null) {
				resourceGroupDAO.delete(entity);
			}
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addRoleToResource(final String resourceId, final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity resource = resourceDao.findById(resourceId);
			final RoleEntity role = roleDAO.findById(roleId);
			if(resource == null && role == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(roleId, resourceId);
			final ResourceRoleEntity dbObject = resourceRoleDao.findById(id);
			if(dbObject != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			final ResourceRoleEntity entity = new ResourceRoleEntity();
			entity.setId(id);
			resourceRoleDao.save(entity);
			
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeRoleToResource(final String resourceId, final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(roleId, resourceId);
			final ResourceRoleEntity dbObject = resourceRoleDao.findById(id);
			if(dbObject == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			resourceRoleDao.delete(dbObject);
			
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public int getNumOfResourcesForRole(final String roleId) {
		return resourceDao.getNumOfResourcesForRole(roleId);
	}

	@Override
	public List<Resource> getResourcesForRole(final String roleId, final int from, final int size) {
		final List<ResourceEntity> entityList = resourceDao.getResourcesForRole(roleId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(entityList, false);
		return resourceList;
	}
}