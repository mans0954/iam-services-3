package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
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
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("resourceDataService")
@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl extends AbstractBaseService implements ResourceDataService {

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

	private static final Log log = LogFactory
			.getLog(ResourceDataServiceImpl.class);

	public Resource getResource(String resourceId) {
		Resource resource = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE).setTargetResource(resourceId);
        try{
            if (resourceId != null) {
                final ResourceEntity entity = resourceService.findResourceById(resourceId);
                if (entity != null) {
                    resource = resourceConverter.convertToDTO(entity, true);
                }
            }
            auditBuilder.succeed();
        } catch(Throwable e) {
            log.error("Exception", e);
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return resource;
	}

	@WebMethod
	public int count(final ResourceSearchBean searchBean) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_NUM);
        try{
            if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
                auditBuilder.setAction(AuditAction.GET_ROOT_RESOURCE_NUM);
                final List<ResourceEntity> resultsEntities = resourceService.findBeans(searchBean, 0, Integer.MAX_VALUE);
                count = (resultsEntities != null) ? resultsEntities.size() : 0;
            } else {
                count = resourceService.count(searchBean);
            }
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SEARCH_RESOURCE);
        try{
            final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
            final List<ResourceEntity> resultsEntities = resourceService.findBeans(searchBean, from, size);
            resourceList = resourceConverter.convertToDTOList(resultsEntities, DozerMappingType.DEEP.equals(mappingType));
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  resourceList;
	}

	/*
	public Response addResource(Resource resource) {
		return saveOrUpdateResource(resource);
	}
	*/

	@Override
	public Response saveResource(Resource resource) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SAVE_RESOURCE);
		try {
			if (resource == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Role object is null");
			}
            auditBuilder.setRequestorUserId(resource.getRequestorUserId()).setTargetResource(resource.getResourceId());
            if(StringUtils.isBlank(resource.getResourceId())) {
                auditBuilder.setAction(AuditAction.ADD_RESOURCE);
            }

			ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
			if (StringUtils.isEmpty(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Name is null or empty");
			}

			/* duplicate name check */
			final ResourceEntity nameCheck = resourceService.findResourceByName(entity.getName());
			if (nameCheck != null) {
				if (StringUtils.isBlank(entity.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
				} else if (!nameCheck.getResourceId().equals(entity.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
				}
			}

			if (entity.getResourceType() == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_RESOURCE_TYPE, "Resource Type is not set");
			}

			resourceService.save(entity);
			response.setResponseValue(entity.getResourceId());
            auditBuilder.succeed();
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	public List<ResourceType> getAllResourceTypes() {
        List<ResourceType> resourceTypeList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ALL_RESOURCE_TYPE);
        try{
            final List<ResourceTypeEntity> resourceTypeEntities = resourceService.getAllResourceTypes();
            resourceTypeList = resourceTypeConverter.convertToDTOList(resourceTypeEntities, false);
            auditBuilder.succeed();
        } catch (Throwable e) {
            log.error("Can't get all resource types", e);
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return resourceTypeList;
	}

	public Response addResourceProp(final ResourceProp resourceProp) {
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_RESOURCE_PROP);
		return saveOrUpdateResourceProperty(resourceProp);
	}

	public Response updateResourceProp(final ResourceProp resourceProp) {
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.UPDATE_RESOURCE_PROP);
		return saveOrUpdateResourceProperty(resourceProp);
	}

	private Response saveOrUpdateResourceProperty(final ResourceProp prop) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
		try {
			if (prop == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Resource Property object is null");
			}

			final ResourcePropEntity entity = resourcePropConverter.convertToEntity(prop, false);
			if (StringUtils.isNotBlank(prop.getResourcePropId())) {
				final ResourcePropEntity dbObject = resourceService.findResourcePropById(prop.getResourcePropId());
				if (dbObject == null) {
					throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "No Resource Property object is found");
				}
			}

			if (StringUtils.isBlank(entity.getName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Property name is not set");
			}

			if (StringUtils.isBlank(entity.getPropValue())) {
				throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_VALUE_MISSING, "Resource Property value is not set");
			}

			if (entity == null || StringUtils.isBlank(entity.getResource().getResourceId())) {
				throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING, "Resource ID is not set for Resource Property object");
			}
            resourceService.save(entity);
			response.setResponseValue(entity.getResourcePropId());
            auditBuilder.succeed();
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't save or update resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	public Response removeResourceProp(String resourcePropId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_RESOURCE_PROP);
		try {
			if (StringUtils.isBlank(resourcePropId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Resource property ID is not specified");
			}

            resourceService.deleteResourceProp(resourcePropId);
            auditBuilder.succeed();
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete resource property", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeUserFromResource(final String resourceId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE).setTargetUser(userId).setAuditDescription(String.format("Remove user %s from resource: %s", userId, resourceId));
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
			}
            auditBuilder.succeed();
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addUserToResource(final String resourceId,
			final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_USER_TO_RESOURCE).setTargetUser(userId).setAuditDescription(String.format("Add user %s to resource: %s", userId, resourceId));
        try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
			}

			userDataService.addUserToResource(userId, resourceId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't add user to resource", e);
			response.setStatus(ResponseStatus.FAILURE);
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response deleteResource(final String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.DELETE_RESOURCE);
		try {
			if(resourceId == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Resource ID is not specified");
			}
			
			resourceService.validateResourceDeletion(resourceId);
			resourceService.deleteResource(resourceId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			response.setResponseValue(e.getResponseValue());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public List<Resource> getChildResources(final String resourceId, Boolean deepFlag, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_RESOURCE).setTargetResource(resourceId);
        try{
            final List<ResourceEntity> resultList = resourceService.getChildResources(resourceId, from, size);
            resourceList = resourceConverter.convertToDTOList(resultList, (deepFlag!=null)?deepFlag:false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  resourceList;
	}



	@Override
	public int getNumOfChildResources(final String resourceId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_RESOURCE_NUM).setTargetResource(resourceId);
        try{
            count = resourceService.getNumOfChildResources(resourceId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Resource> getParentResources(final String resourceId, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_RESOURCE).setTargetResource(resourceId);
        try{
            final List<ResourceEntity> resultList = resourceService.getParentResources(resourceId, from, size);
            resourceList = resourceConverter.convertToDTOList(resultList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  resourceList;
	}

	@Override
	public int getNumOfParentResources(final String resourceId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_RESOURCE_NUM).setTargetResource(resourceId);
        try{
            count = resourceService.getNumOfParentResources(resourceId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public Response addChildResource(final String parentResourceId, final String childResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_CHILD_RESOURCE).setTargetResource(parentResourceId)
                    .setAuditDescription(String.format("Add child resource: %s to resource: %s", childResourceId, parentResourceId));
		try {
			if (StringUtils.isBlank(parentResourceId)
					|| StringUtils.isBlank(childResourceId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Parent ResourceId or Child ResourceId is null");
			}
			resourceService.validateResource2ResourceAddition(parentResourceId, childResourceId);
			resourceService.addChildResource(parentResourceId, childResourceId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setResponseValue(e.getResponseValue());
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't add child resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}
	@Override
	public Response deleteChildResource(final String resourceId, final String memberResourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_CHILD_RESOURCE).setTargetResource(resourceId)
                .setAuditDescription(String.format("Remove child resource: %s from resource: %s", memberResourceId, resourceId));

		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Parent ResourceId or Child ResourceId is null");
			}

			resourceService.deleteChildResource(resourceId, memberResourceId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addGroupToResource(final String resourceId, final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_GROUP_TO_RESOURCE).setTargetGroup(groupId)
                .setAuditDescription(String.format("Add group: %s to resource: %s", groupId, resourceId));
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
			}

			resourceService.addResourceGroup(resourceId, groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't add group to resource resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeGroupToResource(final String resourceId, final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_GROUP_FROM_RESOURCE).setTargetGroup(groupId)
                .setAuditDescription(String.format("Remove group: %s from resource: %s", groupId, resourceId));

		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
			}

			resourceService.deleteResourceGroup(resourceId, groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete group from resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addRoleToResource(final String resourceId, final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_ROLE_TO_RESOURCE).setTargetRole(roleId)
                .setAuditDescription(String.format("Add role: %s to resource: %s", roleId, resourceId));
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
			}

			resourceService.addResourceToRole(resourceId, roleId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't add role to  resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeRoleToResource(final String resourceId, final String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_ROLE_FROM_RESOURCE).setTargetRole(roleId)
                .setAuditDescription(String.format("Remove role: %s from resource: %s", roleId, resourceId));
		try {
			if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
			}
			resourceService.deleteResourceRole(resourceId, roleId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Can't delete resource", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public int getNumOfResourcesForRole(final String roleId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_NUM_FOR_ROLE).setTargetRole(roleId);
        try{
            count = resourceService.getNumOfResourcesForRole(roleId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Resource> getResourcesForRole(final String roleId, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_FOR_ROLE).setTargetRole(roleId);
        try{
            final List<ResourceEntity> entityList = resourceService.getResourcesForRole(roleId, from, size);
		    resourceList = resourceConverter.convertToDTOList(entityList, false);
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return resourceList;
	}


	@Override
	public int getNumOfResourceForGroup(final String groupId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_NUM_FOR_GROUP).setTargetGroup(groupId);
        try{
            count = resourceService.getNumOfResourceForGroup(groupId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Resource> getResourcesForGroup(final String groupId, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_FOR_GROUP).setTargetGroup(groupId);
        try{
            final List<ResourceEntity> entityList = resourceService.getResourcesForGroup(groupId, from, size);
            resourceList = resourceConverter.convertToDTOList(entityList, false);
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return resourceList;
	}

	@Override
	public int getNumOfResourceForUser(final String userId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_NUM_FOR_USER).setTargetUser(userId);
        try{
            count = resourceService.getNumOfResourceForUser(userId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Resource> getResourcesForUser(final String userId, final int from, final int size) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_FOR_USER).setTargetUser(userId);
        try{
            final List<ResourceEntity> entityList = resourceService.getResourcesForUser(userId, from, size);
            resourceList = resourceConverter.convertToDTOList(entityList, false);
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return resourceList;
	}

    @Override
    public List<Resource> getResourcesForUserByType(final String userId, final String resourceTypeId) {
        List<Resource> resourceList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_RESOURCE_FOR_USER_BY_TYPE).setTargetUser(userId).addAttribute(AuditAttributeName.RESOURCE_TYPE, resourceTypeId);
        try{
            final List<ResourceEntity> entityList = resourceService.getResourcesForUserByType(userId, resourceTypeId);
            resourceList = resourceConverter.convertToDTOList(entityList, true);
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return resourceList;
    }

	@Override
	public Response canAddUserToResource(String userId, String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_ADD_USER_TO_RESOURCE).setTargetUser(userId).setAuditDescription(String.format("Check if user can be added to resource: %s", resourceId));
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId  is null");
			}

			if (userDataService.isHasResource(userId, resourceId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("User %s has already been added to resource: %s", userId, resourceId));
			}
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response canRemoveUserFromResource(String userId, String resourceId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_REMOVE_USER_FROM_RESOURCE).setTargetUser(userId).setAuditDescription(String.format("Check if user can be removed from resource: %s", resourceId));
		try {
			if (resourceId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId  is null");
			}
            auditBuilder.succeed();
		} catch (BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch (Throwable e) {
			log.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}
}