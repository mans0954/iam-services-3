package org.openiam.idm.srvc.res.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RoleDataService roleService;
    @Autowired
    private GroupDataService groupDataService;
    @Autowired
    private ResourceTypeDozerConverter resourceTypeConverter;
    
    @Autowired
    private LanguageDozerConverter languageConverter;

    @Autowired
    protected SysConfiguration sysConfiguration;
    
    @Autowired
    private AccessRightProcessor accessRightProcessor;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);


    @Override
    @Cacheable(value="resourcePropCache", key="{ #resourceId, #propName}")
    public String getResourcePropValueByName(@WebParam(name = "resourceId", targetNamespace = "") String resourceId, @WebParam(name = "propName", targetNamespace = "") String propName) {
        return resourceService.getResourcePropValueByName(resourceId, propName);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    @Cacheable(value="resources", key="{ #resourceId,#language}")
    public Resource getResource(final String resourceId, final Language language) {
        Resource resource = null;
        try {
            if (resourceId != null) {
                final ResourceEntity entity = resourceService.findResourceById(resourceId);
                if (entity != null) {
                    resource = resourceConverter.convertToDTO(entity, true);
                }
            }
        } catch (Throwable e) {
            log.error("Exception", e);
        }
        return resource;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<Resource> getResourcesByIds(final List<String> resourceIds, final Language language) {
        List<Resource> resourceList = null;
        try {
            if (CollectionUtils.isNotEmpty(resourceIds)) {
                final List<ResourceEntity> entityList = resourceService.findResourcesByIds(resourceIds);
                if (CollectionUtils.isNotEmpty(entityList)) {
                    resourceList = resourceConverter.convertToDTOList(entityList, true);
                }
            }
        } catch (Throwable e) {
            log.error("Exception", e);
        }
        return resourceList;
    }

    @WebMethod
    public int count(final ResourceSearchBean searchBean) {
        int count = 0;
        if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
            final List<ResourceEntity> resultsEntities = resourceService
                    .findBeans(searchBean, 0, Integer.MAX_VALUE, null);
            count = (resultsEntities != null) ? resultsEntities.size() : 0;
        } else {
            count = resourceService.count(searchBean);
        }

        return count;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
//    @Cacheable(value="resources", key="{ #searchBean.cacheUniqueBeanKey, #from, #size, #language}")
    public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final Language language) {
        final List<ResourceEntity> entityList = resourceService.findBeans(searchBean, from, size, languageConverter.convertToEntity(language, false));
        final List<Resource> dtoList = resourceConverter.convertToDTOList(entityList,searchBean.isDeepCopy());
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
    }

    @Override
    public Response validateEdit(Resource resource) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validate(resource);
        } catch (BasicDataServiceException e) {
        	response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't validate resource", e);
            response.setErrorText(e.getMessage());
            response.fail();
        }
        return response;
    }

    private void validate(final Resource resource) throws BasicDataServiceException {
        if (resource == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Role object is null");
        }

        ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
        if (StringUtils.isEmpty(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Name is null or empty");
        }

	/* duplicate name check */
        final ResourceEntity nameCheck = resourceService.findResourceByName(entity.getName());
        if (nameCheck != null) {
            if (StringUtils.isBlank(entity.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
            } else if (!nameCheck.getId().equals(entity.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
            }
        }

        if (entity.getResourceType() == null || StringUtils.isBlank(entity.getResourceType().getId())) {
            throw new BasicDataServiceException(ResponseCode.INVALID_RESOURCE_TYPE, "Resource Type is not set");
        }

        entityValidator.isValid(entity);
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response saveResource(Resource resource, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validate(resource);
            final ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
            resourceService.save(entity, requesterId);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save or update resource", e);
            response.setErrorText(e.getMessage());
            response.fail();
        }
        return response;
    }

    @Override
    public Response saveResourceType(ResourceType resourceType, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.SAVE_RESOURCE.value());
        idmAuditLog.setRequestorUserId(requesterId);
        if (StringUtils.isBlank(resourceType.getId())) {
            idmAuditLog.setAction(AuditAction.ADD_RESOURCE.value());
        }
        try {
            final ResourceTypeEntity entity = resourceTypeConverter.convertToEntity(resourceType, false);
            resourceService.save(entity);
            response.setResponseValue(entity.getId());
            idmAuditLog.succeed();
        } catch (Throwable e) {
            log.error("Can't save or update resource Type", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @LocalizedServiceGet
    public List<ResourceType> getAllResourceTypes(final Language language) {
        List<ResourceType> resourceTypeList = null;
        try {
            final List<ResourceTypeEntity> resourceTypeEntities = resourceService.getAllResourceTypes();
            resourceTypeList = resourceTypeConverter.convertToDTOList(resourceTypeEntities, false);
        } catch (Throwable e) {
            log.error("Can't get all resource types", e);
        }
        return resourceTypeList;
    }

    @CacheEvict(value = "resourcePropCache", allEntries=true)
    public Response addResourceProp(final ResourceProp resourceProp, String requesterId) {
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.ADD_RESOURCE_PROP.value());
        idmAuditLog.setRequestorUserId(requesterId);
        return saveOrUpdateResourceProperty(resourceProp, idmAuditLog);
    }

    @CacheEvict(value = "resourcePropCache", allEntries=true)
    public Response updateResourceProp(final ResourceProp resourceProp, String requesterId) {
    	IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.UPDATE_RESOURCE_PROP.value());
        idmAuditLog.setRequestorUserId(requesterId);
        return saveOrUpdateResourceProperty(resourceProp, idmAuditLog);
    }

    @CacheEvict(value = "resourcePropCache", allEntries=true)
    private Response saveOrUpdateResourceProperty(final ResourceProp prop, IdmAuditLogEntity idmAuditLog) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (prop == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Resource Property object is null");
            }

            final ResourcePropEntity entity = resourcePropConverter.convertToEntity(prop, false);
            if (StringUtils.isNotBlank(prop.getId())) {
                final ResourcePropEntity dbObject = resourceService.findResourcePropById(prop.getId());
                if (dbObject == null) {
                    throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND,
                            "No Resource Property object is found");
                }
            }

            if (StringUtils.isBlank(entity.getName())) {
                throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Property name is not set");
            }

            if (StringUtils.isBlank(entity.getValue())) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_VALUE_MISSING,
                        "Resource Property value is not set");
            }

            if (entity == null || StringUtils.isBlank(entity.getResource().getId())) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING,
                        "Resource ID is not set for Resource Property object");
            }
            resourceService.save(entity);
            response.setResponseValue(entity.getId());
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't save or update resource property", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @CacheEvict(value = "resourcePropCache", allEntries=true)
    public Response removeResourceProp(String resourcePropId, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_RESOURCE_PROP.value());
        try {
            if (StringUtils.isBlank(resourcePropId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                        "Resource property ID is not specified");
            }

            resourceService.deleteResourceProp(resourcePropId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete resource property", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response removeUserFromResource(final String resourceId, final String userId, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

        idmAuditLog.setAuditDescription(String.format("Remove user %s from resource: %s", userId, resourceId));
        try {
            if (resourceId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
            }
            userDataService.removeUserFromResource(userId, resourceId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    //DO NOT PUT TRANSACTIONAL HERE!!  The underlying collections that get modified won't persist.
    @Override
    //@Transactional
    @CacheEvict(value = "resources", allEntries=true)
    public Response addUserToResource(final String resourceId, 
    								  final String userId, 
    								  final String requesterId, 
    								  final Set<String> rightIds,
    								  final Date startDate,
    								  final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_USER_TO_RESOURCE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

        idmAuditLog.setAuditDescription(String.format("Add user %s to resource: %s", userId, resourceId));
        try {
            if (resourceId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
            }

            userDataService.addUserToResource(userId, resourceId, rightIds, startDate, endDate);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't add user to resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    public Response validateDelete(String resourceId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            resourceService.validateResourceDeletion(resourceId);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseValue(e.getResponseValue());
        } catch (Throwable e) {
            log.error("Can't delete resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response deleteResource(final String resourceId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (resourceId == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Resource ID is not specified");
            }

            resourceService.validateResourceDeletion(resourceId);
            resourceService.deleteResource(resourceId);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseValue(e.getResponseValue());
        } catch (Throwable e) {
            log.error("Can't delete resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteResourceType(final String resourceTypeId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.DELETE_RESOURCE_TYPE.value());
        try {
            if (resourceTypeId == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Resource Type ID is not specified");
            }
            resourceService.deleteResourceType(resourceTypeId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseValue(e.getResponseValue());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete resource Type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Resource> getChildResources(final String resourceId, Boolean deepFlag, final int from, final int size, final Language language) {
        final ResourceSearchBean sb = new ResourceSearchBean();
        sb.addParentId(resourceId);
        sb.setDeepCopy(deepFlag);
        return findBeans(sb, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfChildResources(final String resourceId) {
    	 final ResourceSearchBean sb = new ResourceSearchBean();
         sb.addParentId(resourceId);
         return count(sb);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Resource> getParentResources(final String resourceId, final int from, final int size, final Language language) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
        sb.addChildId(resourceId);
        sb.setDeepCopy(false);
        return findBeans(sb, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfParentResources(final String resourceId) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
        sb.addChildId(resourceId);
        return count(sb);
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response addChildResource(final String resourceId, 
    								 final String childResourceId, 
    								 final String requesterId, 
    								 final Set<String> rights,
    								 final Date startDate,
   								  	 final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_CHILD_RESOURCE.value());
        ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        ResourceEntity resourceEntityChild = resourceService.findResourceById(childResourceId);
        idmAuditLog.setTargetResource(childResourceId, resourceEntityChild.getName());

        idmAuditLog.setAuditDescription(
                        String.format("Add child resource: %s to resource: %s", childResourceId, resourceId));
        try {
            resourceService.validateResource2ResourceAddition(resourceId, childResourceId, rights);
            resourceService.addChildResource(resourceId, childResourceId, rights, startDate, endDate);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setResponseValue(e.getResponseValue());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't add child resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response deleteChildResource(final String resourceId, final String memberResourceId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_CHILD_RESOURCE.value());
        ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        ResourceEntity resourceEntityChild = resourceService.findResourceById(memberResourceId);
        idmAuditLog.setTargetResource(memberResourceId, resourceEntityChild.getName());

        idmAuditLog.setAuditDescription(
                        String.format("Remove child resource: %s from resource: %s", memberResourceId, resourceId));

        try {
            if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                        "Parent ResourceId or Child ResourceId is null");
            }

            resourceService.deleteChildResource(resourceId, memberResourceId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response addGroupToResource(final String resourceId, 
    								   final String groupId, 
    								   final String requesterId, 
    								   final Set<String> rightIds,
    								   final Date startDate,
     								   final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_GROUP_TO_RESOURCE.value());
        Group group = groupDataService.getGroupDTO(groupId);
        idmAuditLog.setTargetGroup(groupId, group.getName());
        Resource resource = getResource(resourceId, null);
        idmAuditLog.setTargetResource(resourceId, resource.getName());

        idmAuditLog.setAuditDescription(String.format("Add group: %s to resource: %s", groupId, resourceId));
        try {
            if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
            }

            resourceService.addResourceGroup(resourceId, groupId, rightIds, startDate, endDate);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't add group to resource resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response removeGroupToResource(final String resourceId, final String groupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_GROUP_FROM_RESOURCE.value());
        idmAuditLog.setAuditDescription(String.format("Remove group: %s from resource: %s", groupId, resourceId));

        try {
            final GroupEntity groupEntity = groupDataService.getGroup(groupId);
            final ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
            if(groupEntity == null || resourceEntity == null) {
            	throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            
            idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
            idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        	
            if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
            }

            resourceService.deleteResourceGroup(resourceId, groupId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete group from resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response addRoleToResource(final String resourceId, 
    								  final String roleId, 
    								  final String requesterId, 
    								  final Set<String> rightIds,
    								  final Date startDate,
    								  final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_ROLE_TO_RESOURCE.value());
        
        idmAuditLog.setAuditDescription(String.format("Add role: %s to resource: %s", roleId, resourceId));
        try {
            if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
            }
            
            final RoleEntity roleEntity = roleService.getRoleLocalized(roleId, requesterId, null);
            if(roleEntity == null) {
            	throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            
            idmAuditLog.setTargetRole(roleId, roleEntity.getName());
            final ResourceEntity resourceEntity  = resourceService.findResourceById(resourceId);
            if(resourceEntity == null) {
            	throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

            resourceService.addResourceToRole(resourceId, roleId, rightIds, startDate, endDate);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't add role to  resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "resources", allEntries=true)
    public Response removeRoleToResource(final String resourceId, final String roleId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity ();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_ROLE_FROM_RESOURCE.value());
        RoleEntity roleEntity = roleService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Remove role: %s from resource: %s", roleId, resourceId));
        try {
            if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
            }
            resourceService.deleteResourceRole(resourceId, roleId);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't delete resource", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    @Deprecated
    public int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addRoleId(roleId);
    	return count(sb);
    }

    @Override
    @Deprecated
    @LocalizedServiceGet
    public List<Resource> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addRoleId(roleId);
    	sb.setDeepCopy(false);
    	return findBeans(sb, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfResourceForGroup(final String groupId, final ResourceSearchBean searchBean) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addGroupId(groupId);
    	return count(sb);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Resource> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addGroupId(groupId);
    	sb.setDeepCopy(false);
    	return findBeans(sb, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfResourceForUser(final String userId, final ResourceSearchBean searchBean) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addUserId(userId);
    	return count(sb);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Resource> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addUserId(userId);
    	sb.setDeepCopy(false);
    	return findBeans(sb, from, size, language);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    @Deprecated
    public List<Resource> getResourcesForUserByType(final String userId, final String resourceTypeId, final ResourceSearchBean searchBean, final Language language) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addUserId(userId);
    	sb.setDeepCopy(false);
    	sb.setResourceTypeId(resourceTypeId);
    	return findBeans(sb, 0, Integer.MAX_VALUE, language);
    }

    @Override
    public Response canAddUserToResource(String userId, String resourceId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
         try {
            if (resourceId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId  is null");
            }

            if (userDataService.isHasResource(userId, resourceId)) {
                throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format(
                        "User %s has already been added to resource: %s", userId, resourceId));
            }
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Exception", e);
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
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId  is null");
            }
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<ResourceType> findResourceTypes(final ResourceTypeSearchBean searchBean, final int from, final int size, final Language language) {
    	final boolean deepCopy = (searchBean != null) ? searchBean.isDeepCopy() : false;
        final List<ResourceTypeEntity> entityList = resourceService.findResourceTypes(searchBean, from, size);
        return resourceTypeConverter.convertToDTOList(entityList, deepCopy);
    }

    @Override
    public int countResourceTypes(final ResourceTypeSearchBean searchBean) {
        return resourceService.countResourceTypes(searchBean);
    }

    @Override
    public Response validateAddChildResource(String resourceId, String childResourceId, final Set<String> rights) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            resourceService.validateResource2ResourceAddition(resourceId, childResourceId, rights);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

	@Override
	public boolean isMemberOfAnyEntity(final String resourceId) {
		return resourceService.isMemberOfAnyEntity(resourceId);
	}
}