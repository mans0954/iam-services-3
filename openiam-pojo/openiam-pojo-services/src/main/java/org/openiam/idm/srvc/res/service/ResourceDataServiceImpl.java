package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("resourceDataService")
@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl implements ResourceDataService {

	@Autowired
	private ResourceDozerConverter resourceConverter;
	
	@Autowired
	private ResourcePropDozerConverter resourcePropConverter;

	@Autowired
    private UserDataService userDataService;
	@Autowired
    private ResourceService resourceService;

	@Autowired
	private ResourceTypeDozerConverter resourceTypeConverter;

	@Autowired
	private ResourceGroupDAO resourceGroupDAO;

	private static final Log log = LogFactory
			.getLog(ResourceDataServiceImpl.class);

	public Resource getResource(String resourceId) {
		Resource resource = null;
		if (resourceId != null) {
			final ResourceEntity entity = resourceService.findResourceById(resourceId);
			if (entity != null) {
				resource = resourceConverter.convertToDTO(entity, true);
			}
		}
		return resource;
	}

	@WebMethod
	public int count(final ResourceSearchBean searchBean) {
		if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
			final List<ResourceEntity> resultsEntities = resourceService.findBeans(searchBean, 0, Integer.MAX_VALUE);
			return (resultsEntities != null) ? resultsEntities.size() : 0;
		} else {
			return resourceService.count(searchBean);
		}
	}

	@Override
	public List<Resource> findBeans(final ResourceSearchBean searchBean,
			final int from, final int size) {
		final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
        final List<ResourceEntity> resultsEntities = resourceService.findBeans(searchBean, 0, Integer.MAX_VALUE);
		return resourceConverter.convertToDTOList(resultsEntities, DozerMappingType.DEEP.equals(mappingType));
	}

	/*
	public Response addResource(Resource resource) {
		return saveOrUpdateResource(resource);
	}
	*/

	@Override
	public Response saveResource(Resource resource) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resource == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}

			ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
			if (StringUtils.isEmpty(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}

			/* duplicate name check */
			final ResourceEntity nameCheck = resourceDao.findByName(entity
					.getName());
			if (nameCheck != null) {
				if (StringUtils.isBlank(entity.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				} else if (!nameCheck.getResourceId().equals(entity.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}

			if (entity.getResourceType() == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_RESOURCE_TYPE);
			}

			/* merge */
			if (StringUtils.isNotBlank(entity.getResourceId())) {
				final ResourceEntity dbObject = resourceDao.findById(resource
						.getResourceId());
				if (dbObject == null) {
					throw new BasicDataServiceException(
							ResponseCode.OBJECT_NOT_FOUND);
				}
				// TODO: extend this merge
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

			resourceService.save(entity);
			response.setResponseValue(entity.getResourceId());
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}


	public Response addResourceType(ResourceType val) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (val == null) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			final ResourceTypeEntity entity = resourceTypeConverter
					.convertToEntity(val, true);
            resourceService.save(entity);
			response.setResponseValue(entity.getResourceTypeId());
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	public ResourceType getResourceType(String resourceTypeId) {
		ResourceType retVal = null;
		if (resourceTypeId != null) {
			final ResourceTypeEntity entity = resourceTypeDao
					.findById(resourceTypeId);
			if (entity != null) {
				retVal = resourceTypeConverter.convertToDTO(entity, false);
			}
		}
		return retVal;
	}

	public Response updateResourceType(ResourceType resourceType) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resourceType == null) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			final ResourceTypeEntity entity = resourceTypeConverter
					.convertToEntity(resourceType, false);
            resourceService.save(entity);
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't save resource type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	public List<ResourceType> getAllResourceTypes() {
		final List<ResourceTypeEntity> resourceTypeEntities = resourceService
				.getAllResourceTypes();
		return resourceTypeConverter.convertToDTOList(resourceTypeEntities,
				false);
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
			if (prop == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			final ResourcePropEntity entity = resourcePropConverter
					.convertToEntity(prop, false);
			if (StringUtils.isNotBlank(prop.getResourcePropId())) {
				final ResourcePropEntity dbObject = resourceService
						.findResourcePropById(prop.getResourcePropId());
				if (dbObject == null) {
					throw new BasicDataServiceException(
							ResponseCode.OBJECT_NOT_FOUND);
				}
			}

			if (StringUtils.isBlank(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}

			if (StringUtils.isBlank(entity.getPropValue())) {
				throw new BasicDataServiceException(
						ResponseCode.RESOURCE_PROP_VALUE_MISSING);
			}

			if (StringUtils.isBlank(entity.getResourceId())) {
				throw new BasicDataServiceException(
						ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING);
			}


            resourceService.save(entity);

			response.setResponseValue(entity.getResourcePropId());
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't save or update resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	public Response removeResourceProp(String resourcePropId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourcePropId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

            resourceService.deleteResourceProp(resourcePropId);
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't delete resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeUserFromResource(final String resourceId,
			final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addUserToResource(final String resourceId,
			final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			userDataService.addUserToResource(userId, resourceId);


		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		}
		return response;
	}

	@Override
	public Response deleteResource(final String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			resourceService.deleteResource(resourceId);

		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public List<Resource> getChildResources(final String resourceId,
			final int from, final int size) {

		final List<ResourceEntity> resultList = resourceService.getChildResources(resourceId,
				from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfChildResources(final String resourceId) {
		return resourceService.getNumOfChildResources(resourceId);
	}

	@Override
	public List<Resource> getParentResources(final String resourceId,
			final int from, final int size) {
		final List<ResourceEntity> resultList = resourceService.getParentResources(resourceId, from, size);
		return resourceConverter.convertToDTOList(resultList, false);
	}

	@Override
	public int getNumOfParentResources(final String resourceId) {
		return resourceService.getNumOfParentResources(resourceId);
	}

	@Override
	public Response addChildResource(final String parentResourceId, final String childResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(parentResourceId)
					|| StringUtils.isBlank(childResourceId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			resourceService.addChildResource(parentResourceId, childResourceId);


		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	/*private boolean causesCircularDependency(final ResourceEntity parent,
			final ResourceEntity child, final Set<ResourceEntity> visitedSet) {
		boolean retval = false;
		if (parent != null && child != null) {
			if (!visitedSet.contains(child)) {
				visitedSet.add(child);
				if (CollectionUtils.isNotEmpty(parent.getParentResources())) {
					for (final ResourceEntity entity : parent
							.getParentResources()) {
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
*/
	@Override
	public Response deleteChildResource(final String resourceId,
			final String memberResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourceId)
					|| StringUtils.isBlank(memberResourceId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			resourceService.deleteChildResource(resourceId, memberResourceId);

		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addGroupToResource(final String resourceId,
			final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			resourceService.addResourceGroup(resourceId, groupId);


		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeGroupToResource(final String resourceId,
			final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			resourceService.deleteResourceGroup(resourceId, groupId);

		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addRoleToResource(final String resourceId,
			final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			resourceService.addResourceToRole(resourceId, roleId);


		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeRoleToResource(final String resourceId,
			final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}
			resourceService.deleteResourceRole(resourceId, roleId);

		} catch (Throwable e) {
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
	public List<Resource> getResourcesForRole(final String roleId,
			final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService
				.getResourcesForRole(roleId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(
				entityList, false);
		return resourceList;
	}

	@Override
	public List<Resource> getResourcesForManagedSys(final String mngSysId,
			final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService
				.getResourcesForManagedSys(mngSysId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(
				entityList, false);
		return resourceList;
	}

	@Override
	public int getNumOfResourceForGroup(final String groupId) {
		return resourceService.getNumOfResourceForGroup(groupId);
	}

	@Override
	public List<Resource> getResourcesForGroup(final String groupId,
			final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService
				.getResourcesForGroup(groupId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(
				entityList, false);
		return resourceList;
	}

	@Override
	public int getNumOfResourceForUser(final String userId) {
		return resourceService.getNumOfResourceForUser(userId);
	}

	@Override
	public List<Resource> getResourcesForUser(final String userId,
			final int from, final int size) {
		final List<ResourceEntity> entityList = resourceService
				.getResourcesForUser(userId, from, size);
		final List<Resource> resourceList = resourceConverter.convertToDTOList(
				entityList, false);
		return resourceList;
	}

	@Override
	public Response canAddUserToResource(String userId, String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}

			if (userDataService.isHasResource(userId, resourceId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}

		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response canRemoveUserFromResource(String userId, String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}

		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
}