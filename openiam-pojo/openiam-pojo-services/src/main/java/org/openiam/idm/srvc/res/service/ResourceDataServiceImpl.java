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
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

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
    private ResourceTypeDozerConverter resourceTypeConverter;

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

    /**
     * Add a resource role
     *
     * @param resourceRole
     * @return
     */
    public Response addResourceRole(ResourceRole resourceRole) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceRole == null || resourceRole.getId() == null || resourceRole.getId().getResourceId() == null || resourceRole.getId().getRoleId() == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourceRoleEntity entity = resourceRoleConverter.convertToEntity(resourceRole, true);
    		resourceRoleDao.save(entity);
    		response.setResponseValue(resourceRoleConverter.convertToDTO(entity, true));
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

    public Response removeResourceRole(ResourceRoleId resourceRoleId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceRoleId == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourceRoleEmbeddableId id = new ResourceRoleEmbeddableId(resourceRoleId);
    		
    		final ResourceRoleEntity entity = resourceRoleDao.findById(id);
    		if(entity == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		resourceRoleDao.delete(entity);
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

    /**
     * Returns a list of Resource objects that are linked to a Role.
     *
     * @param roleId
     * @return
     */
    public List<Resource> getResourcesForRole(String roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId is null");
        }
        List<ResourceEntity> resourceEntities = resourceDao.findResourcesForRole(roleId);
        return resourceConverter.convertToDTOList(resourceEntities, false);
    }

    /**
     * Returns a list of Resource objects that are linked to the list of Roles.
     *
     * @param roleIdList
     * @return
     */
    public List<Resource> getResourcesForRoles(List<String> roleIdList) {
        if (roleIdList == null) {
            throw new IllegalArgumentException("roleIdList is null");
        }
        List<ResourceEntity> resourceEntities = resourceDao.findResourcesForRoles(roleIdList);
        return resourceConverter.convertToDTOList(resourceEntities, false);
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
	public Response deleteResource(final String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			final ResourceEntity entity = resourceDao.findById(resourceId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getChildResources())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_CHILDREN);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getResourceGroups())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_GROUPS);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getResourceRoles())) {
				throw new BasicDataServiceException(ResponseCode.HANGING_ROLES);
			}
			
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
}