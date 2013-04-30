package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceRoleDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.dozer.converter.ResourceUserDozerConverter;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementPageTemplateDAO;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
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
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserMgr;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
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
	private ResourcePropDozerConverter resourcePropConverter;
    
    @Autowired
    private ResourceTypeDozerConverter resourceTypeConverter;
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserDataService userManager;
    
    @Autowired
    private GroupDataService groupService;
    
    @Autowired
    private RoleDataService roleService;
    
    @Autowired
    private AuthProviderDao authProviderDAO;
    
    @Autowired
    private ContentProviderDao contentProviderDAO;
    
    @Autowired
    private MetadataElementDAO metadataElementDAO;
    
    @Autowired
    private MetadataElementPageTemplateDAO templateDAO;
    
    @Autowired
    private URIPatternDao uriPatternDAO;
    
    

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);

    public Resource getResource(String resourceId) {
    	final ResourceEntity entity = resourceService.findResourceById(resourceId);
    	final Resource resource = (entity != null) ? resourceConverter.convertToDTO(entity, true) : null;
    	return resource;
    }

    @WebMethod
    public int count(final ResourceSearchBean searchBean) {
    	return resourceService.count(searchBean);
    }

    @Override
    public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
        final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
        final List<ResourceEntity> resultsEntities = resourceService.findBeans(searchBean, from, size);
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
    			throw new BasicDataServiceException(ResponseCode.NO_NAME);
    		}
    		
    		
    		/* duplicate name check */
    		final ResourceEntity nameCheck = resourceService.findResourceByName(entity.getName());
    		if(nameCheck != null) {
    			if(StringUtils.isBlank(entity.getResourceId())) {
    				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
    			} else  if(!nameCheck.getResourceId().equals(entity.getResourceId())) {
    				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
    			}
    		}
    		
    		if(entity.getResourceType() == null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_RESOURCE_TYPE);
    		}
    		
    		resourceService.save(entity);
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
    		resourceService.save(entity);
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

    public ResourceType getResourceType(String id) {
    	ResourceType retVal = null;
    	if(id != null) {
    		final ResourceTypeEntity entity = resourceService.findResourceTypeById(id);
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
    		resourceService.save(entity);
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

    public List<ResourceType> getAllResourceTypes() {
        final List<ResourceTypeEntity> resourceTypeEntities = resourceService.getAllResourceTypes();
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
    		
    		if(StringUtils.isBlank(entity.getName())) {
    			throw new BasicDataServiceException(ResponseCode.NO_NAME);
    		}
    		
    		if(StringUtils.isBlank(entity.getPropValue())) {
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_VALUE_MISSING);
    		}
    		
    		if(StringUtils.isBlank(entity.getResourceId())) {
    			throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING);
    		}
    		
    		resourceService.save(entity);
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
    		
    		resourceService.deleteResourceProp(resourcePropId);
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

	@Override
	public Response removeUserFromResource(final String resourceId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceId == null || userId== null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		resourceService.deleteResourceUser(userId, resourceId);
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
    public Response addUserToResource(final String resourceId, final String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(resourceId == null || userId== null) {
    			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    		}
    		
    		final ResourceUserEntity entity = resourceService.getResourceUser(userId, resourceId);
    		
    		if(entity != null) {
    			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
    		}
    		
    		final ResourceEntity resource = resourceService.findResourceById(resourceId);
    		final UserEntity user = userManager.getUser(userId);
    		if(resource == null || user == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		final ResourceUserEntity toSave = new ResourceUserEntity();
    		toSave.setUserId(userId);
    		toSave.setResourceId(resourceId);
    		
    		resourceService.save(toSave);
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
	//@Transactional
	public Response deleteResource(final String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			final List<AuthProviderEntity> authProvierList = authProviderDAO.getByResourceId(resourceId);
			final List<ContentProviderEntity> contentProviderList = contentProviderDAO.getByResourceId(resourceId);
			final List<MetadataElementEntity> metadataElementList = metadataElementDAO.getByResourceId(resourceId);
			final List<MetadataElementPageTemplateEntity> pageTemplateList = templateDAO.getByResourceId(resourceId);
			final List<URIPatternEntity> uriPatternList = uriPatternDAO.getByResourceId(resourceId);
			
			if(CollectionUtils.isNotEmpty(authProvierList)) {
				response.setResponseValue(authProvierList.get(0).getName());
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER);
			}
			
			if(CollectionUtils.isNotEmpty(contentProviderList)) {
				response.setResponseValue(contentProviderList.get(0).getName());
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_CONTENT_PROVIDER);
			}
			
			if(CollectionUtils.isNotEmpty(metadataElementList)) {
				response.setResponseValue(metadataElementList.get(0).getAttributeName());
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_METADATA_ELEMENT);
			}
			
			if(CollectionUtils.isNotEmpty(pageTemplateList)) {
				response.setResponseValue(pageTemplateList.get(0).getName());
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_PAGE_TEMPLATE);
			}
			
			if(CollectionUtils.isNotEmpty(uriPatternList)) {
				response.setResponseValue(uriPatternList.get(0).getPattern());
				throw new BasicDataServiceException(ResponseCode.LINKED_TO_URI_PATTERN);
			}
			
			resourceService.deleteResource(resourceId);
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
		final List<ResourceEntity> resultList = resourceService.getChildResources(resourceId, from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfChildResources(final String resourceId) {
		return resourceService.getNumOfChildResources(resourceId);
	}
	
	@Override
	public List<Resource> getParentResources(final  String resourceId, final int from, final int size) {
		final List<ResourceEntity> resultList = resourceService.getParentResources(resourceId, from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfParentResources(final String resourceId) {
		return resourceService.getNumOfParentResources(resourceId);
	}

	@Override
	public Response addChildResource(final String resourceId, final String memberResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final ResourceEntity parent = resourceService.findResourceById(resourceId);
			if(parent == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceEntity child = resourceService.findResourceById(memberResourceId);
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
			
			resourceService.addChildResource(resourceId, memberResourceId);
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
			
			final ResourceEntity parent = resourceService.findResourceById(resourceId);
			if(parent == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceEntity child = resourceService.findResourceById(memberResourceId);
			if(child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			resourceService.deleteChildResource(resourceId, memberResourceId);
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
			
			final ResourceEntity resource = resourceService.findResourceById(resourceId);
			final GroupEntity group = groupService.getGroup(groupId);
			
			if(resource == null || group == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceGroupEntity record = resourceService.getResourceGroup(resourceId, groupId);
			if(record != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			resourceService.addResourceGroup(resourceId, groupId);
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
			
			final ResourceEntity resource = resourceService.findResourceById(resourceId);
			if(resource == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			resourceService.deleteResourceGroup(resourceId, groupId);
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
			
			final ResourceEntity resource = resourceService.findResourceById(resourceId);
			final RoleEntity role = roleService.getRole(roleId);
			if(resource == null && role == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ResourceRoleEntity dbObject = resourceService.getResourceRole(resourceId, roleId); 
			if(dbObject != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			resourceService.saveResourceRole(resourceId, roleId);
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
			
			resourceService.deleteResourceRole(resourceId, roleId);
			
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
		return resourceService.getNumOfResourcesForRole(roleId);
	}

	@Override
	public List<Resource> getResourcesForRole(final String roleId, final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService.getResourcesForRole(roleId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(entityList, false);
		return resourceList;
	}

	@Override
	public int getNumOfResourceForGroup(final String groupId) {
		return resourceService.getNumOfResourceForGroup(groupId);
	}

	@Override
	public List<Resource> getResourcesForGroup(final String groupId, final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService.getResourcesForGroup(groupId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(entityList, false);
		return resourceList;
	}

	@Override
	public int getNumOfResourceForUser(final String userId) {
		return resourceService.getNumOfResourceForUser(userId);
	}

	@Override
	public List<Resource> getResourcesForUser(final String userId, final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService.getResourcesForUser(userId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(entityList, false);
		return resourceList;
	}
}