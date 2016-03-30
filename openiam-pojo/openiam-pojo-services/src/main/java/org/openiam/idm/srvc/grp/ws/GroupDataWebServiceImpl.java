package org.openiam.idm.srvc.grp.ws;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.GroupAttributeDozerConverter;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDataService;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.UserUtils;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>GroupDataServiceImpl</code> provides a service to manage groups as well
 * as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 * 
 * @author Suneet Shah
 * @version 2.0
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.grp.ws.GroupDataWebService", targetNamespace = "urn:idm.openiam.org/srvc/grp/service", portName = "GroupDataWebServicePort", serviceName = "GroupDataWebService")
@Service("groupWS")
public class GroupDataWebServiceImpl extends AbstractBaseService implements GroupDataWebService {
    @Autowired
    private GroupDataService groupManager;

    @Autowired
    private RoleDataService roleDataService;

    @Autowired
    private UserDataService userManager;

    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    private GroupAttributeDozerConverter groupAttributeDozerConverter;

    private static final Log log = LogFactory.getLog(GroupDataWebServiceImpl.class);

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private LanguageDozerConverter languageConverter;

    @Autowired
    protected LanguageDataService languageDataService;
    
    @Autowired
    private AccessRightProcessor accessRightProcessor;
    @Autowired
    @Qualifier("groupEntityValidator")
    private EntityValidator groupEntityValidator;

    public GroupDataWebServiceImpl() {

    }

    protected Language getDefaultLanguage() {
        return languageDataService.getDefaultLanguage();
    }

    @Override
    public Response validateEdit(Group group) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validate(group);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't validate", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response validateDelete(String groupId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validateDeleteInternal(groupId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't validate", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
        }
        return response;
    }

    private void validateDeleteInternal(final String groupId) throws BasicDataServiceException {
        if (StringUtils.isBlank(groupId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId is null or empty");
        }
    }

    private void validate(final Group entity) throws BasicDataServiceException {
        if (entity == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }

        if (StringUtils.isBlank(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME);
        }

        final GroupEntity nameEntity = groupManager.getGroupByNameAndManagedSystem(entity.getName(), entity.getManagedSysId(), null, null);
        if(nameEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
				throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Role Name + Managed Sys combination taken");
			}
		}

        entityValidator.isValid(groupDozerConverter.convertToEntity(entity, true));
    }

    @Override
    public Response saveGroup(final Group group, final String requesterId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validate(group);
            final GroupEntity entity = groupDozerConverter.convertToEntity(group, true);
            groupManager.saveGroup(entity, group.getOwner(), requesterId);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            log.error("Error save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
        }
        return response;
    }

    @Override
    @Transactional(readOnly=true)
    public  Group getGroup(final String groupId, final String requesterId) {
        return getGroupLocalize(groupId, requesterId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public Group getGroupLocalize(final String groupId, final String requesterId, final Language language) {
        Group retVal = null;
        if (StringUtils.isNotBlank(groupId)) {
            final GroupEntity entity = groupManager.getGroupLocalize(groupId, requesterId, languageConverter.convertToEntity(language, false));
            retVal = groupDozerConverter.convertToDTO(entity, true);
        }
        return retVal;
    }

    @Override
    public Response deleteGroup(final String groupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validateDeleteInternal(groupId);

            groupManager.deleteGroup(groupId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't delete", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }

        return response;
    }

    @Override
    @Deprecated
    public int getNumOfChildGroups(final String groupId, final String requesterId) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addParentId(groupId);
        return countBeans(sb, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getChildGroups(final String groupId, final String requesterId, final Boolean deepFlag,
            final int from, final int size) {
        return getChildGroupsLocalize(groupId, requesterId, deepFlag, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getChildGroupsLocalize(final String groupId, final String requesterId, final Boolean deepFlag,
                                      final int from, final int size, final Language language) {
        final GroupSearchBean sb = new GroupSearchBean();
        sb.addParentId(groupId);
        sb.setDeepCopy(deepFlag);
        return findBeansLocalize(sb, requesterId, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfParentGroups(final String groupId, final String requesterId) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addChildId(groupId);
    	return countBeans(sb, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getParentGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getParentGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
        sb.addChildId(groupId);
        return findBeansLocalize(sb, requesterId, from, size, language);
    }

    @Override
    public Response isUserInGroup(final String groupId, final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                        "GroupId or UserId  is null or empty");
            }

            response.setResponseValue(groupManager.isUserInCompiledGroupList(groupId, userId));
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response addUserToGroup(final String groupId, 
    							   final String userId, 
    							   final String requesterId, 
    							   final Set<String> rightIds,
    							   final Date startDate,
    							   final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_USER_TO_GROUP.value());
        UserEntity user = userManager.getUser(userId);
        LoginEntity userPrimaryIdentity =  UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        auditLog.setTargetUser(userId,userPrimaryIdentity.getLogin());
        GroupEntity groupEntity = groupManager.getGroup(groupId);
        auditLog.setTargetGroup(groupId, groupEntity.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add user %s to group: %s", userId, groupId));
        try {
            if (groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }
            
            if(startDate != null && endDate != null && startDate.after(endDate)) {
            	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }

            userManager.addUserToGroup(userId, groupId, rightIds, startDate, endDate);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("Error while adding user to group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response removeUserFromGroup(final String groupId, final String userId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        final IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }
            
            final GroupEntity groupEntity = groupManager.getGroupLocalize(groupId, null);
            if(groupEntity != null) {
            	auditLog.setRequestorUserId(requesterId);
            	auditLog.setAction(AuditAction.REMOVE_USER_FROM_GROUP.value());
            	auditLog.setTargetUser(userId, null);
            	auditLog.setTargetGroup(groupId, groupEntity.getName());
            	auditLog.setAuditDescription(String.format("Remove user %s from group: %s", userId, groupId));

            	userManager.removeUserFromGroup(userId, groupId);
            }
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("Error while remove user from group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response addAttribute(final GroupAttribute attribute, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE_TO_GROUP.value());
        auditLog.setRequestorUserId(requesterId);
        try {
            if (attribute == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute object is null");
            }
            GroupEntity groupEntity = groupManager.getGroup(attribute.getGroupId());
            auditLog.setTargetGroup(attribute.getGroupId(), groupEntity.getName());

            if (StringUtils.isBlank(attribute.getName()) || StringUtils.isBlank(attribute.getValue())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute name is missed");
            }

            final GroupAttributeEntity entity = groupAttributeDozerConverter.convertToEntity(attribute, false);

            groupManager.saveAttribute(entity);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response removeAttribute(final String attributeId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAction(AuditAction.REMOVE_GROUP_ATTRIBUTE.value());
        try {
            if (attributeId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute Id is null");
            }

            groupManager.removeAttribute(attributeId);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    /**
     * Without @Localization for internal use only
     */
    @Transactional(readOnly=true)
    public List<Group> findBeans(final GroupSearchBean searchBean, final String requesterId, final int from,
            final int size) {
        final List<GroupEntity> entityList = groupManager.findBeans(searchBean, requesterId, from, size);
        List<Group> dtoList = groupDozerConverter.convertToDTOList(entityList, false);
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
    }

    @Override
    @LocalizedServiceGet
    public List<Group> findBeansLocalize(final GroupSearchBean searchBean, final String requesterId, final int from, final int size,
                                         final Language language) {
        final List<GroupEntity> entityList = groupManager.findBeansLocalize(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
        final List<Group> dtoList = groupDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
    }

    @Override
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        return groupManager.countBeans(searchBean, requesterId);
    }

    @Override
    @LocalizedServiceGet
    public List<Group> findGroupsForOwner(final GroupSearchBean searchBean, final String requesterId, String ownerId, final int from, final int size,
                                         final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.findGroupsForOwner(searchBean, requesterId, ownerId, from, size, languageConverter.convertToEntity(language, false));
        List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
        return groupList;
    }
    @Override
    public int countGroupsForOwner(final GroupSearchBean searchBean, final String requesterId, String ownerId) {
        return groupManager.countGroupsForOwner(searchBean, requesterId, ownerId);
    }


    @Override
    /**
     * Without localization proxy, for internal use only
     */
    public List<Group> getGroupsForUser(final String userId, final String requesterId, Boolean deepFlag,
            final int from, final int size) {
        return groupManager.getGroupsDtoForUser(userId, requesterId, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getGroupsForUserLocalize(final String userId, final String requesterId, Boolean deepFlag,
                                        final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
    	sb.setDeepCopy(deepFlag);
    	return findBeansLocalize(sb, requesterId, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
    	return countBeans(sb, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForResource(final String resourceId, final String requesterId, final boolean deepFlag,
        final int from, final int size) {
        return getGroupsForResourceLocalize(resourceId, requesterId, deepFlag, from, size, null);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final boolean deepFlag,
                                            final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
        sb.addResourceId(resourceId);
        sb.setDeepCopy(deepFlag);
        return findBeansLocalize(sb, requesterId, from, size, language);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsforResource(final String resourceId, final String requesterId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addResourceId(resourceId);
    	return countBeans(sb, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForRole(final String roleId, final String requesterId, final int from, final int size,
            boolean deepFlag) {
        return getGroupsForRoleLocalize(roleId, requesterId, from, size, deepFlag, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Deprecated
    public List<Group> getGroupsForRoleLocalize(final String roleId, final String requesterId, final int from, final int size,
                                        boolean deepFlag, final Language language) {
        final GroupSearchBean sb = new GroupSearchBean();
        sb.addRoleId(roleId);
        sb.setDeepCopy(deepFlag);
        return findBeans(sb, requesterId, from, size);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addRoleId(roleId);
    	return countBeans(sb, requesterId);
    }

    @Override
    public Response addChildGroup(final String groupId, 
    							  final String childGroupId, 
    							  final String requesterId, 
    							  final Set<String> rights,
    							  final Date startDate,
   							   	  final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_CHILD_GROUP.value());
        //GroupEntity groupEntity = groupManager.getGroup(groupId);
        //auditLog.setTargetGroup(groupId, groupEntity.getName());
        //GroupEntity groupEntityChild = groupManager.getGroup(childGroupId);
        //auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add child group: %s to group: %s", childGroupId, groupId));

        try {
        	if(startDate != null && endDate != null && startDate.after(endDate)) {
            	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }
        	
        	GroupEntity groupEntity = groupManager.getGroupLocalize(groupId, null);
        	GroupEntity groupEntityChild = groupManager.getGroupLocalize(childGroupId, null);
        	if(groupEntity == null || groupEntityChild == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	auditLog.setTargetGroup(groupId, groupEntity.getName());
            auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());
        	
            if (groupId == null || childGroupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
            }

            if (groupId.equals(childGroupId)) {
                throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD,
                        "Cannot add group itself as child");
            }

            groupManager.validateGroup2GroupAddition(groupId, childGroupId, rights, startDate, endDate);
            groupManager.addChildGroup(groupId, childGroupId, rights, startDate, endDate);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("can't add child group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    @WebMethod
    public Response removeChildGroup(final String groupId, final String childGroupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.REMOVE_CHILD_GROUP.value());
        Group groupDto = groupManager.getGroupDTO(groupId);
        auditLog.setTargetGroup(groupId, groupDto.getName());
        Group groupChild = groupManager.getGroupDTO(childGroupId);
        auditLog.setTargetGroup(childGroupId, groupChild.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Remove child group: %s from group: %s", childGroupId, groupId));

        try {
            if (groupId == null || childGroupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
            }

            groupManager.removeChildGroup(groupId, childGroupId);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response canAddUserToGroup(String userId, String groupId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId is null");
            }

            boolean has = userManager.isHasGroup(userId, groupId);

            if (has) {
                throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format(
                        "User %s has already been added to group: %s", userId, groupId));
            }

/*            GroupEntity groupEntity = groupManager.getGroup(groupId);
            if (groupEntity != null) {
                if (!((groupEntity.getMaxUserNumber() == null) || (userManager.getNumOfUsersForGroup(groupId, null) < groupEntity.getMaxUserNumber()))) {
                    throw new BasicDataServiceException(ResponseCode.GROUP_LIMIT_OF_USERS_EXCEEDED, "group's limit of user count exceeded");
                }
            } else {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "groupEntity is null");
            }*/

        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Error while adding user to group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response canRemoveUserFromGroup(String userId, String groupId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or UserId is null");
            }
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Error while remove user from group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response validateGroup2GroupAddition(String groupId, String childGroupId, final Set<String> rights, final Date startDate, final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            groupManager.validateGroup2GroupAddition(groupId, childGroupId, rights, startDate, endDate);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("can't validate", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @Deprecated
    public List<Group> findGroupsByAttributeValue(String attrName, String attrValue) {
        return findGroupsByAttributeValueLocalize(attrName, attrValue, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> findGroupsByAttributeValueLocalize(String attrName, String attrValue, final Language language) {
        return groupDozerConverter.convertToDTOList(
                groupManager.findGroupsByAttributeValueLocalize(attrName, attrValue, languageConverter.convertToEntity(language, false)), true);
    }

	@Override
	public boolean hasAttachedEntities(String groupId) {
		return groupManager.hasAttachedEntities(groupId);
	}

    @Override
    public Response removeRoleFromGroup(String roleId, String groupId, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_ROLE_FROM_GROUP.value());
        GroupEntity groupEntity = groupManager.getGroup(groupId);
        idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Remove role %s from group: %s", roleId, groupId));
        try {
            if(groupId == null || roleId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
            }

            groupManager.removeRoleFromGroup(roleId, groupId);
            idmAuditLog.succeed();
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch(Throwable e) {
            log.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        }finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    public SaveTemplateProfileResponse saveGroupRequest(final GroupRequestModel request){
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
        try {
            if(request == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
            }

            groupManager.saveGroupRequest(request);
            response.setResponseValue(request.getTargetObject().getId());
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (PageTemplateException e){
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        }catch(Throwable e) {
            log.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    public SaveTemplateProfileResponse validateGroupRequest(final GroupRequestModel request) {
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
        try {
            groupManager.validateGroupRequest(request);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (PageTemplateException e){
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't validate", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
        }
        return response;
    }

/*
        HOW TO DO IT???
    public List<GroupOwner> getOwnersBeansForGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId){
        return groupManager.getOwnersBeansForGroup(groupId);
    }*/
}
