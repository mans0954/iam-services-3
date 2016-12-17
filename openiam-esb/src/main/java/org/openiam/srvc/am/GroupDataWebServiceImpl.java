package org.openiam.srvc.am;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.GroupAttributeDozerConverter;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDataService;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.mq.constants.queue.am.AMQueue;
import org.openiam.mq.constants.queue.am.GroupQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.srvc.audit.IdmAuditLogWebDataService;
import org.openiam.util.SpringSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
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

@WebService(endpointInterface = "org.openiam.srvc.am.GroupDataWebService", targetNamespace = "urn:idm.openiam.org/srvc/grp/service", portName = "GroupDataWebServicePort", serviceName = "GroupDataWebService")
@Service("groupWS")
public class GroupDataWebServiceImpl extends AbstractApiService implements GroupDataWebService {
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

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private LanguageDozerConverter languageConverter;

    @Autowired
    protected LanguageDataService languageDataService;
    
    @Autowired
    private AccessRightProcessor accessRightProcessor;
    @Autowired
    private IdmAuditLogWebDataService auditLogService;
    @Autowired
    public GroupDataWebServiceImpl(GroupQueue queue) {
        super(queue);
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

        final GroupEntity nameEntity = groupManager.getGroupByNameAndManagedSystem(entity.getName(), entity.getManagedSysId(), null);
        if(nameEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
				throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Role Name + Managed Sys combination taken");
			}
		}

        groupManager.isValid(groupDozerConverter.convertToEntity(entity, true));
    }

    @Override
    public Response saveGroup(final Group group) {
        return groupManager.saveGroup(group);
    }

    @Override
    @Transactional(readOnly=true)
    public  Group getGroup(final String groupId) {
        return getGroupLocalize(groupId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public Group getGroupLocalize(final String groupId, final Language language) {
        Group retVal = null;
        if (StringUtils.isNotBlank(groupId)) {
            final GroupEntity entity = groupManager.getGroupLocalize(groupId, languageConverter.convertToEntity(language, false));
            retVal = groupDozerConverter.convertToDTO(entity, true);
        }
        return retVal;
    }

    @Override
    public Response deleteGroup(final String groupId) {
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
    public int getNumOfChildGroups(final String groupId) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addParentId(groupId);
        return countBeans(sb);
    }

    @Override
    @Deprecated
    public List<Group> getChildGroups(final String groupId, final Boolean deepFlag,
            final int from, final int size) {
        return getChildGroupsLocalize(groupId, deepFlag, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getChildGroupsLocalize(final String groupId, final Boolean deepFlag,
                                      final int from, final int size, final Language language) {
        final GroupSearchBean sb = new GroupSearchBean();
        sb.addParentId(groupId);
        sb.setDeepCopy(deepFlag);
        sb.setLanguage(getDefaultLanguage());
        return findBeans(sb, from, size);
    }

    @Override
    @Deprecated
    public int getNumOfParentGroups(final String groupId) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addChildId(groupId);
    	return countBeans(sb);
    }

    @Override
    @Deprecated
    public List<Group> getParentGroups(final String groupId, final int from, final int size) {
        return getParentGroupsLocalize(groupId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getParentGroupsLocalize(final String groupId, final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
        sb.addChildId(groupId);
        sb.setLanguage(language);
        return findBeans(sb, from, size);
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
    							   final Set<String> rightIds,
    							   final Date startDate,
    							   final Date endDate) {
        return groupManager.addUserToGroup(groupId, userId, rightIds, startDate, endDate);
    }

    @Override
    public Response removeUserFromGroup(final String groupId, final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        final IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }
            
            final GroupEntity groupEntity = groupManager.getGroupLocalize(groupId, null);
            if(groupEntity != null) {
            	auditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
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
            auditLogService.addLog(auditLog);
        }
        return response;
    }

    @Override
    public Response addAttribute(final GroupAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE_TO_GROUP.value());
        auditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
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
            auditLogService.addLog(auditLog);
        }
        return response;
    }

    @Override
    public Response removeAttribute(final String attributeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
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
            auditLogService.addLog(auditLog);
        }
        return response;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly=true)
    public List<Group> findBeans(final GroupSearchBean searchBean, final int from,
            final int size) {
        final List<GroupEntity> entityList = groupManager.findBeans(searchBean, from, size);
        List<Group> dtoList = groupDozerConverter.convertToDTOList(entityList, false);
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
    }

    @Override
    public int countBeans(final GroupSearchBean searchBean) {
        return groupManager.countBeans(searchBean);
    }

    @Override
    @LocalizedServiceGet
    public List<Group> findGroupsForOwner(final GroupSearchBean searchBean, String ownerId, final int from, final int size) {
        final List<Group> groupEntityList = groupManager.findGroupsDtoForOwner(searchBean, ownerId, from, size);
        return groupEntityList;
    }
    @Override
    public int countGroupsForOwner(final GroupSearchBean searchBean, String ownerId) {
        return groupManager.countGroupsForOwner(searchBean, ownerId);
    }


    @Override
    /**
     * Without localization proxy, for internal use only
     */
    public List<Group> getGroupsForUser(final String userId, Boolean deepFlag,
            final int from, final int size) {
        return groupManager.getGroupsDtoForUser(userId, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getGroupsForUserLocalize(final String userId, Boolean deepFlag,
                                        final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
    	sb.setDeepCopy(deepFlag);
    	sb.setLanguage(language);
    	return findBeans(sb, from, size);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForUser(final String userId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
    	return countBeans(sb);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForResource(final String resourceId, final boolean deepFlag,
        final int from, final int size) {
        return getGroupsForResourceLocalize(resourceId, deepFlag, from, size, null);
    }

    @Override
    @LocalizedServiceGet
    @Deprecated
    public List<Group> getGroupsForResourceLocalize(final String resourceId, final boolean deepFlag,
                                            final int from, final int size, final Language language) {
    	final GroupSearchBean sb = new GroupSearchBean();
        sb.addResourceId(resourceId);
        sb.setDeepCopy(deepFlag);
        sb.setLanguage(language);
        return findBeans(sb, from, size);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsforResource(final String resourceId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addResourceId(resourceId);
    	return countBeans(sb);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForRole(final String roleId, final int from, final int size,
            boolean deepFlag) {
        return getGroupsForRoleLocalize(roleId, from, size, deepFlag, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Deprecated
    public List<Group> getGroupsForRoleLocalize(final String roleId, final int from, final int size,
                                        boolean deepFlag, final Language language) {
        final GroupSearchBean sb = new GroupSearchBean();
        sb.addRoleId(roleId);
        sb.setDeepCopy(deepFlag);
        return findBeans(sb, from, size);
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForRole(final String roleId) {
        final GroupSearchBean sb = new GroupSearchBean();
    	sb.addRoleId(roleId);
    	return countBeans(sb);
    }

    @Override
    public Response addChildGroup(final String groupId, 
    							  final String childGroupId, 
    							  final Set<String> rights,
    							  final Date startDate,
   							   	  final Date endDate) {

        return groupManager.addChildGroup(groupId, childGroupId, rights, startDate, endDate);
    }

    @Override
    @WebMethod
    public Response removeChildGroup(final String groupId, final String childGroupId) {
        return groupManager.removeChildGroup(groupId, childGroupId);
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
    public Response removeRoleFromGroup(String roleId, String groupId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
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
            auditLogService.addLog(idmAuditLog);
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
        } catch (PageTemplateException e){
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
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
        } catch (PageTemplateException e){
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
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

/*
        HOW TO DO IT???
    public List<GroupOwner> getOwnersBeansForGroup(final @WebParam(name = "groupId", targetNamespace = "") String groupId){
        return groupManager.getOwnersBeansForGroup(groupId);
    }*/
}
