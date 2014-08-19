package org.openiam.idm.srvc.grp.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.*;

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

    public GroupDataWebServiceImpl() {

    }

    protected Language getDefaultLanguage() {
        return languageConverter.convertToDTO(languageDataService.getDefaultLanguage(), false);
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

    private void validate(final Group group) throws BasicDataServiceException {
        if (group == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }

        if (StringUtils.isBlank(group.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME);
        }

        final GroupEntity found = groupManager.getGroupByName(group.getName(), null);
        if (found != null) {
            if (StringUtils.isBlank(group.getId()) && found != null) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Group name is already in use");
            }

            if (StringUtils.isNotBlank(group.getId()) && !group.getId().equals(found.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Group name is already in use");
            }
        }

        entityValidator.isValid(groupDozerConverter.convertToEntity(group, true));
    }

    @Override
    public Response saveGroup(final Group group, final String requesterId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validate(group);
            final GroupEntity entity = groupDozerConverter.convertToEntity(group, true);
            groupManager.saveGroup(entity, requesterId);
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
    @Deprecated
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
    public int getNumOfChildGroups(final String groupId, final String requesterId) {
        return  groupManager.getNumOfChildGroups(groupId, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getChildGroups(final String groupId, final String requesterId, final Boolean deepFlag,
            final int from, final int size) {
        return getChildGroupsLocalize(groupId, requesterId, deepFlag, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> getChildGroupsLocalize(final String groupId, final String requesterId, final Boolean deepFlag,
                                      final int from, final int size, final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.getChildGroupsLocalize(groupId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntityList, false);
    }

    @Override
    public int getNumOfParentGroups(final String groupId, final String requesterId) {
        return groupManager.getNumOfParentGroups(groupId, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getParentGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getParentGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.getParentGroupsLocalize(groupId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntityList, false);
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
    public Response addUserToGroup(final String groupId, final String userId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setAction(AuditAction.ADD_USER_TO_GROUP.value());
        UserEntity user = userManager.getUser(userId);
        LoginEntity userPrimaryIdentity =  UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        auditLog.setTargetUser(userId,userPrimaryIdentity.getLogin());
        GroupEntity groupEntity = groupManager.getGroup(groupId);
        auditLog.setTargetGroup(groupId, groupEntity.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add user to group: %s", groupId));
        try {
            if (groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }

            userManager.addUserToGroup(userId, groupId);
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
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAction(AuditAction.REMOVE_USER_FROM_GROUP.value());
        auditLog.setTargetUser(userId, null);
        auditLog.setAuditDescription(String.format("Remove user from group: %s", groupId));
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }

            userManager.removeUserFromGroup(userId, groupId);
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
        IdmAuditLog auditLog = new IdmAuditLog();
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
        IdmAuditLog auditLog = new IdmAuditLog();
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
    @Deprecated
    public List<Group> findBeans(final GroupSearchBean searchBean, final String requesterId, final int from,
            final int size) {
        return findBeansLocalize(searchBean, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> findBeansLocalize(final GroupSearchBean searchBean, final String requesterId, final int from, final int size,
                                         final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.findBeansLocalize(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
        List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
//        Collections.sort(groupList, new Comparator<Group>() {
//            @Override
//            public int compare(Group o1, Group o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
        return groupList;
    }

    @Override
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        return groupManager.countBeans(searchBean, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForUser(final String userId, final String requesterId, Boolean deepFlag,
            final int from, final int size) {
        return getGroupsForUserLocalize(userId, requesterId, deepFlag, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> getGroupsForUserLocalize(final String userId, final String requesterId, Boolean deepFlag,
                                        final int from, final int size, final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.getGroupsForUserLocalize(userId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntityList, false);
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        return groupManager.getNumOfGroupsForUser(userId, requesterId);
    }

    @Override
    @Deprecated
    public List<Group> getGroupsForResource(final String resourceId, final String requesterId, final boolean deepFlag,
        final int from, final int size) {
        return getGroupsForResourceLocalize(resourceId, requesterId, deepFlag, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final boolean deepFlag,
                                            final int from, final int size, final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.getGroupsForResourceLocalize(resourceId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntityList, false);
    }

    @Override
    public int getNumOfGroupsforResource(final String resourceId, final String requesterId) {
        return groupManager.getNumOfGroupsForResource(resourceId, requesterId);
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
    public List<Group> getGroupsForRoleLocalize(final String roleId, final String requesterId, final int from, final int size,
                                        boolean deepFlag, final Language language) {
        final List<GroupEntity> groupEntityList = groupManager.getGroupsForRoleLocalize(roleId, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntityList, deepFlag);
    }

    @Override
    public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        return groupManager.getNumOfGroupsForRole(roleId, requesterId);
    }

    @Override
    public Response addChildGroup(final String groupId, final String childGroupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setAction(AuditAction.ADD_CHILD_GROUP.value());
        GroupEntity groupEntity = groupManager.getGroup(groupId);
        auditLog.setTargetGroup(groupId, groupEntity.getName());
        GroupEntity groupEntityChild = groupManager.getGroup(childGroupId);
        auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add child group: %s to group: %s", childGroupId, groupId));

        try {
            if (groupId == null || childGroupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
            }

            if (groupId.equals(childGroupId)) {
                throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD,
                        "Cannot add group itself as child");
            }

            groupManager.validateGroup2GroupAddition(groupId, childGroupId);
            groupManager.addChildGroup(groupId, childGroupId);
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
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setAction(AuditAction.REMOVE_CHILD_GROUP.value());
        GroupEntity groupEntity = groupManager.getGroup(groupId);
        auditLog.setTargetGroup(groupId, groupEntity.getName());
        GroupEntity groupEntityChild = groupManager.getGroup(childGroupId);
        auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());
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
    public Response validateGroup2GroupAddition(String groupId, String childGroupId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            groupManager.validateGroup2GroupAddition(groupId, childGroupId);
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
}
