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
import org.openiam.dozer.converter.ResourceUserDozerConverter;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePrivilegeEntity;
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

@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl implements ResourceDataService {

	@Autowired
	private ResourceDozerConverter resourceConverter;
	
	@Autowired
	private ResourcePropDozerConverter resourcePropConverter;
	
    private ResourceDAO resourceDao;
    private ResourceTypeDAO resourceTypeDao;
    private ResourcePropDAO resourcePropDao;
    private ResourceRoleDAO resourceRoleDao;
    private ResourceUserDAO resourceUserDao;
    private ResourcePrivilegeDAO resourcePrivilegeDao;
    private DozerUtils dozerUtils;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);

    @Autowired
    private ResourceSearchBeanConverter resourceSearchBeanConverter;
    
    @Autowired
    private ResourceUserDozerConverter resourceUserConverter;

    @Required
    public void setResourceDao(ResourceDAO resourceDao) {
        this.resourceDao = resourceDao;
    }

    @Required
    public void setResourceTypeDao(ResourceTypeDAO resourceTypeDao) {
        this.resourceTypeDao = resourceTypeDao;
    }

    @Required
    public void setResourcePropDao(ResourcePropDAO resourcePropDao) {
        this.resourcePropDao = resourcePropDao;
    }

    @Required
    public void setResourceRoleDao(ResourceRoleDAO resourceRoleDao) {
        this.resourceRoleDao = resourceRoleDao;
    }

    @Required
    public void setResourcePrivilegeDao(ResourcePrivilegeDAO resourcePrivilegeDao) {
        this.resourcePrivilegeDao = resourcePrivilegeDao;
    }

    @Required
    public void setResourceUserDao(ResourceUserDAO resourceUserDao) {
        this.resourceUserDao = resourceUserDao;
    }
    
    @Required
    public void setDozerUtils(final DozerUtils dozerUtils) {
    	this.dozerUtils = dozerUtils;
    }

    /**
     * Find a resource.
     *
     * @param resourceId
     * @return resource
     */
    public Resource getResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId is null");

        final ResourceEntity entity = resourceDao.findById(resourceId);
        if(entity != null) {
        	return resourceConverter.convertToDTO(entity, true);
        } else {
        	return null;
        }
        //return dozerUtils.getDozerDeepMappedResource(new Resource(resourceDao.findById(resourceId)));
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
    
    /**
     * Add a new resource from a transient resource object and sets resourceId
     * in the returned object.
     *
     * @param resource
     * @return
     */
    public Response addResource(Resource resource) {
    	return saveOrUpdateResource(resource);
    }

    /**
     * Update a resource.
     *
     * @param resource
     * @return
     */
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

    /**
     * Remove a resource
     *
     * @param resourceId
     */
    public void removeResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId is null");
        ResourceEntity obj = resourceDao.findById(resourceId);
        resourceDao.delete(obj);
    }

    // ResourceType -----------------------------------

    /**
     * Add a new resource type
     *
     * @param val
     * @return
     */
    public ResourceType addResourceType(ResourceType val) {
        if (val == null)
            throw new IllegalArgumentException("ResourcType is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.add(new ResourceTypeEntity(val));
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Find a resource type
     *
     * @param resourceTypeId
     * @return
     */
    public ResourceType getResourceType(String resourceTypeId) {
        if (resourceTypeId == null)
            throw new IllegalArgumentException("resourceTypeId is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.findById(resourceTypeId);
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Update a resource type
     *
     * @param resourceType
     * @return
     */
    public ResourceType updateResourceType(ResourceType resourceType) {
        if (resourceType == null)
            throw new IllegalArgumentException("resourceType object is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.update(new ResourceTypeEntity(resourceType));
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Find all resource types
     *
     * @return
     */
    public List<ResourceType> getAllResourceTypes() {
        List<ResourceTypeEntity> resourceTypeEntities = resourceTypeDao.findAllResourceTypes();
        List<ResourceType> resourceTypes = null;
        if (resourceTypeEntities != null) {
            resourceTypes = new LinkedList<ResourceType>();
            for (ResourceTypeEntity resourceTypeEntity : resourceTypeEntities) {
                resourceTypes.add(new ResourceType(resourceTypeEntity));
            }
        }
        return resourceTypes;
    }

    /**
     * Remove a resource type
     *
     * @param resourceTypeId
     */
    public void removeResourceType(String resourceTypeId) {
        if (resourceTypeId == null)
            throw new IllegalArgumentException("resourceTypeId is null");
        ResourceTypeEntity obj = this.resourceTypeDao.findById(resourceTypeId);
        this.resourceTypeDao.remove(obj);
    }

    /**
     * Add a resource property.
     *
     * @param resourceProp
     * @return
     */
    public Response addResourceProp(final ResourceProp resourceProp) {
    	return saveOrUpdateResourceProperty(resourceProp);
    }

    /**
     * Update a resource property
     *
     * @param resourceProp
     */
    public Response updateResourceProp(final ResourceProp resourceProp) {
        return saveOrUpdateResourceProperty(resourceProp);
    }
    
    private Response saveOrUpdateResourceProperty(final ResourceProp prop) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(prop == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
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
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_MISSING);
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
    public ResourceRole addResourceRole(ResourceRole resourceRole) {

        if (resourceRole == null)
            throw new IllegalArgumentException("ResourceRole object is null");

        ResourceRoleEntity propEntity = resourceRoleDao.add(new ResourceRoleEntity(resourceRole));
        return new ResourceRole(propEntity);
    }

    /**
     * Find resource role
     *
     * @param resourceRoleId
     * @return
     */
    public ResourceRole getResourceRole(ResourceRoleId resourceRoleId) {
        if (resourceRoleId == null)
            throw new IllegalArgumentException("resourceRoleId is null");

        ResourceRoleEntity roleEntity = resourceRoleDao.findById(new ResourceRoleEmbeddableId(resourceRoleId));
        return new ResourceRole(roleEntity);
    }

    public List<Role> getRolesForResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceRoleId is null");

        return dozerUtils.getDozerDeepMappedRoleList(resourceRoleDao.findRolesForResource(resourceId));
    }

    /**
     * Update resource role.
     *
     * @param resourceRole
     * @return
     */
    public ResourceRole updateResourceRole(ResourceRole resourceRole) {
        if (resourceRole == null)
            throw new IllegalArgumentException("resourceRole object is null");

        ResourceRoleEntity resourceRoleEntity = resourceRoleDao.update(new ResourceRoleEntity(resourceRole));
        return new ResourceRole(resourceRoleEntity);
    }

    /**
     * Find all resource roles
     *
     * @return
     */
    public List<ResourceRole> getAllResourceRoles() {
        final List<ResourceRoleEntity> resourceRoleEntityList = resourceRoleDao.findAllResourceRoles();
        List<ResourceRole> resourceRoleList = null;
        if (resourceRoleEntityList != null) {
            resourceRoleList = new LinkedList<ResourceRole>();
            for (ResourceRoleEntity resourceRoleEntity : resourceRoleEntityList) {
                resourceRoleList.add(new ResourceRole(resourceRoleEntity));
            }
        }
        return resourceRoleList;
    }

    /**
     * Remove resource role.
     *
     * @param resourceRoleId
     */
    public void removeResourceRole(ResourceRoleId resourceRoleId) {
        if (resourceRoleId == null)
            throw new IllegalArgumentException("resourceRoleId is null");
        final ResourceRoleEntity obj = this.resourceRoleDao.findById(new ResourceRoleEmbeddableId(resourceRoleId));
        resourceRoleDao.remove(obj);
    }

    /**
     * Remove all resource roles
     */
    public void removeAllResourceRoles() {
        this.resourceRoleDao.removeAllResourceRoles();
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

    /*
      * (non-Javadoc)
      *
      * @see
      * org.openiam.idm.srvc.res.service.ResourceDataService#addUserToResource
      * (org.openiam.idm.srvc.res.dto.ResourceUser)
      */
    public ResourceUser addUserToResource(ResourceUser resourceUser) {
        if (resourceUser == null) {
            throw new IllegalArgumentException("ResourceUser object is null");
        }
        final ResourceUserEntity entity = resourceUserConverter.convertToEntity(resourceUser, false);
        ResourceUserEntity resourceUserEntity = resourceUserDao.add(entity);
        return resourceUserConverter.convertToDTO(resourceUserEntity, false);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.openiam.idm.srvc.res.service.ResourceDataService#getUserResources
      * (java.lang.String)
      */
    public List<ResourceUser> getUserResources(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }
        List<ResourceUserEntity> resourceUserEntityList = resourceUserDao.findAllResourceForUsers(userId);
        return resourceUserConverter.convertToDTOList(resourceUserEntityList, false);
    }

    public List<Resource> getResourceObjForUser(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }

        List<ResourceEntity> entities = resourceDao.findResourcesForUserRole(userId);
        return resourceConverter.convertToDTOList(entities, false);
    }


    /*
      * (non-Javadoc)
      *
      * @seeorg.openiam.idm.srvc.res.service.ResourceDataService#
      * removeUserFromAllResources(java.lang.String)
      */
    public void removeUserFromAllResources(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }
        resourceUserDao.removeUserFromAllResources(userId);
    }

    public boolean isUserAuthorized(String userId, String resourceId) {
        log.info("isUserAuthorized called.");
        final List<ResourceUser> resList = getUserResources(userId);
        log.info("- resList= " + resList);
        if (resList == null) {
            log.info("resource list for user is null");
            return false;
        }
        for (final ResourceUser ru : resList) {
            log.info("resource id = " + ru.getId().getResourceId());
            if (ru.getId().getResourceId().equalsIgnoreCase(resourceId)) {
                return true;
            }
        }
        return false;

    }


    public boolean isUserAuthorizedByProperty(String userId, String propertyName, String propertyValue) {
        log.info("isUserAuthorized called.");

        if (propertyName == null || propertyName.length() == 0) {
            return false;
        }
        if (propertyValue == null || propertyValue.length() == 0) {
            return false;
        }

        List<Resource> resList = getResourceObjForUser(userId);

        log.info("- resList= " + resList);
        if (resList == null) {
            log.info("resource list for user is null");
            return false;
        }
        for (Resource res : resList) {

            ResourceProp prop = res.getResourceProperty(propertyName);
            if (prop != null) {
                String val = prop.getPropValue();
                if (val != null && val.length() > 0) {
                    val = val.toLowerCase();
                    propertyValue = propertyValue.toLowerCase();

                    if (propertyValue.contains(val)) {
                        return true;
                    }

                }
            }
        }

        return false;

    }

    @Override
    public ResourcePrivilege addResourcePrivilege(ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        ResourcePrivilegeEntity resourcePrivilegeEntity = resourcePrivilegeDao.add(new ResourcePrivilegeEntity(resourcePrivilege));

        return new ResourcePrivilege(resourcePrivilegeEntity);
    }

    @Override
    public void removeResourcePrivilege(String resourcePrivilegeId) {
        if (resourcePrivilegeId == null) {
            throw new IllegalArgumentException("resourcePrivilegeId object is null");
        }
        ResourcePrivilegeEntity privilegeEntity = resourcePrivilegeDao.findById(resourcePrivilegeId);
        resourcePrivilegeDao.remove(privilegeEntity);
    }

    @Override
    public ResourcePrivilege updateResourcePrivilege(ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        ResourcePrivilegeEntity privilegeEntity = resourcePrivilegeDao.update(new ResourcePrivilegeEntity(resourcePrivilege));
        return new ResourcePrivilege(privilegeEntity);
    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByResourceId(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        List<ResourcePrivilegeEntity> privilegeEntities = resourcePrivilegeDao.findPrivilegesByResourceId(resourceId);
        List<ResourcePrivilege> privilegeList = null;
        if (privilegeEntities != null) {
            privilegeList = new LinkedList<ResourcePrivilege>();
            for (ResourcePrivilegeEntity privilegeEntity : privilegeEntities) {
                privilegeList.add(new ResourcePrivilege(privilegeEntity));
            }
        }
        return privilegeList;
    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByEntitlementType(String resourceId, String type) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type object is null");
        }
        List<ResourcePrivilegeEntity> privilegeEntities = resourcePrivilegeDao.findPrivilegesByEntitlementType(resourceId, type);
        List<ResourcePrivilege> privilegeList = null;
        if (privilegeEntities != null) {
            privilegeList = new LinkedList<ResourcePrivilege>();
            for (ResourcePrivilegeEntity privilegeEntity : privilegeEntities) {
                privilegeList.add(new ResourcePrivilege(privilegeEntity));
            }
        }
        return privilegeList;
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
}