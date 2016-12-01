package org.openiam.srvc.am;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.idm.searchbeans.ResourcePropSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.mq.constants.queue.am.AMQueue;
import org.openiam.mq.constants.queue.am.ResourceQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.srvc.audit.IdmAuditLogWebDataService;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("resourceDataService")
@WebService(endpointInterface = "org.openiam.srvc.am.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl extends AbstractApiService implements ResourceDataService {

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


    @Autowired
    private IdmAuditLogWebDataService auditLogService;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);
    @Autowired
    public ResourceDataServiceImpl(ResourceQueue queue) {
        super(queue);
    }

    @Override
    public List<ResourceProp> findResourceProps(final ResourcePropSearchBean sb, final int from, final int size) {
    	return resourceService.findBeansDTO(sb, from, size);
    }

    @Override
    public Resource getResource(final String resourceId, final Language language) {
        Resource resource = resourceService.findResourceDtoById(resourceId, language);
        return resource;
    }

    @Override
    //@LocalizedServiceGet
    //@Transactional(readOnly=true)
    public List<Resource> getResourcesByIds(final List<String> resourceIds, final Language language) {
        /*List<Resource> resourceList = null;
        try {
            if (CollectionUtils.isNotEmpty(resourceIds)) {
                final List<Resource> resourcesDtoList = resourceService.findResourcesDtoByIds(resourceIds);
                if (CollectionUtils.isNotEmpty(resourcesDtoList)) {
                    resourceList = resourcesDtoList;
                }
            }
        } catch (Throwable e) {
            log.error("Exception", e);
        }*/
        return resourceService.findResourcesDtoByIds(resourceIds, language);
    }

    @Override
    public int count(final ResourceSearchBean searchBean) {
        return resourceService.count(searchBean);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
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
            resourceService.validate(resource);
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

    /*private void validate(final Resource resource) throws BasicDataServiceException {
        if (resource == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Role object is null");
        }

        ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
        if (StringUtils.isEmpty(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Name is null or empty");
        }

	*//* duplicate name check *//*
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
    }*/

    @Override
    public Response saveResource(final Resource resource, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
          /*resourceService.validate(resource);
            final ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
            resourceService.save(entity, requesterId);*/

            final ResourceEntity entity = resourceService.saveResource(resource, requesterId);
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
        try {
            final ResourceTypeEntity entity = resourceTypeConverter.convertToEntity(resourceType, false);
            resourceService.save(entity);
            response.setResponseValue(entity.getId());
        } catch (Throwable e) {
            log.error("Can't save or update resource Type", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    //@LocalizedServiceGet
    public List<ResourceType> getAllResourceTypes(final Language language) {
        List<ResourceType> resourceTypeList = null;
        try {
            resourceTypeList = resourceService.getAllResourceTypesDto(language);
        } catch (Throwable e) {
            log.error("Can't get all resource types", e);
        }
        return resourceTypeList;
    }

    @Override
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
            auditLogService.addLog(idmAuditLog);
        }
        return response;
    }

    //DO NOT PUT TRANSACTIONAL HERE!!  The underlying collections that get modified won't persist.
    @Override
    //@Transactional
    public Response addUserToResource(final String resourceId,
                                      final String userId,
                                      final String requesterId,
                                      final Set<String> rightIds,
                                      final Date startDate,
                                      final Date endDate) {
        return resourceService.addUserToResource(resourceId, userId, requesterId, rightIds, startDate, endDate);
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
    public Response deleteResource(final String resourceId, final String requesterId) {
        return resourceService.deleteResource(resourceId, requesterId);
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
            auditLogService.addLog(idmAuditLog);
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
    public Response addChildResource(final String resourceId, 
    								 final String childResourceId, 
    								 final String requesterId, 
    								 final Set<String> rights,
    								 final Date startDate,
   								  	 final Date endDate) {
        return resourceService.addChildResource(resourceId, childResourceId, requesterId, rights, startDate, endDate);
    }

    @Override
    public Response deleteChildResource(final String resourceId, final String memberResourceId, final String requesterId) {
        return resourceService.deleteChildResource(resourceId, memberResourceId, requesterId);
    }

    @Override
    public Response addGroupToResource(final String resourceId, final String groupId,  final String requesterId,
    								   final Set<String> rightIds, final Date startDate, final Date endDate) {
        return resourceService.addGroupToResource(resourceId, groupId, requesterId, rightIds, startDate, endDate);
    }

    @Override
    public Response removeGroupToResource(final String resourceId, final String groupId, final String requesterId) {
        return resourceService.removeGroupToResource(resourceId, groupId, requesterId);
    }

    @Override
    public Response addRoleToResource(final String resourceId, 
    								  final String roleId, 
    								  final String requesterId, 
    								  final Set<String> rightIds,
    								  final Date startDate,
    								  final Date endDate) {

        return resourceService.addRoleToResource(resourceId, roleId, requesterId, rightIds, startDate, endDate);
    }

    @Override
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
            auditLogService.addLog(idmAuditLog);
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
    //@LocalizedServiceGet
    public List<Resource> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
        return resourceService.getResourcesDtoForRole(roleId, from, size, searchBean, language);
    }
    @Override
    @Deprecated
    public int getNumOfResourceForGroup(final String groupId, final ResourceSearchBean searchBean) {
        final ResourceSearchBean sb = new ResourceSearchBean();
        sb.addGroupId(groupId);
        return count(sb);
    }

    @Override
    //@LocalizedServiceGet
    @Deprecated
    public List<Resource> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
        return resourceService.getResourcesDtoForGroup(groupId, from, size, searchBean, language);
    }

    @Override
    @Deprecated
    public int getNumOfResourceForUser(final String userId, final ResourceSearchBean searchBean) {
    	final ResourceSearchBean sb = new ResourceSearchBean();
    	sb.addUserId(userId);
    	return count(sb);
    }

    @Override
    //@LocalizedServiceGet
    @Deprecated
    public List<Resource> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean, final Language language) {
        return resourceService.getResourcesDtoForUser(userId, from, size, searchBean, language);
    }

    @Override
    //@LocalizedServiceGet
    //@Transactional(readOnly=true)
    @Deprecated
    public List<Resource> getResourcesForUserByType(final String userId, final String resourceTypeId, final ResourceSearchBean searchBean, final Language language) {
      return resourceService.getResourcesDtoForUserByType(userId, resourceTypeId, searchBean, language);
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

/*    @Override
    //@LocalizedServiceGet
    //@Transactional(readOnly=true)
    public List<ResourceType> findResourceTypes(final ResourceTypeSearchBean searchBean, final int from, final int size, final Language lang) {
        return resourceService.findResourceTypesDto(searchBean, from, size, lang);
    }*/

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
    public Response validateAddChildResource(String resourceId, String childResourceId, final Set<String> rights, final Date startDate, final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            resourceService.validateResource2ResourceAddition(resourceId, childResourceId, rights, startDate, endDate);
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

	@Override
	public boolean isIndexed(String resourceId) {
		return resourceService.isIndexed(resourceId);
	}
}