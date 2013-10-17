package org.openiam.idm.srvc.grp.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.GroupAttributeDozerConverter;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <code>GroupDataServiceImpl</code> provides a service to manage groups as
 * well as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 * 
 * @author Suneet Shah
 * @version 2.0
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.grp.ws.GroupDataWebService", 
		targetNamespace = "urn:idm.openiam.org/srvc/grp/service", 
		portName = "GroupDataWebServicePort", 
		serviceName = "GroupDataWebService")
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
    
    @Autowired
    private GroupSearchBeanConverter groupSearchBeanConverter;
		
	private static final Log log = LogFactory.getLog(GroupDataWebServiceImpl.class);

	public GroupDataWebServiceImpl() {

	}

	@Override
	public Response saveGroup(final Group group) {
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SAVE_GROUP);
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(group == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
            auditBuilder.setRequestorUserId(group.getRequestorUserId()).setTargetGroup(group.getGrpId());
            if(StringUtils.isBlank(group.getGrpId())) {
                auditBuilder.setAction(AuditAction.ADD_GROUP);
            }


			if(StringUtils.isBlank(group.getGrpName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			final GroupEntity found = groupManager.getGroupByName(group.getGrpName(), null);
			if(found != null) {
				if(StringUtils.isBlank(group.getGrpId()) && found != null) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Group name is already in use");
				}
			
				if(StringUtils.isNotBlank(group.getGrpId()) && !group.getGrpId().equals(found.getGrpId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Group name is already in use");
				}
			}
			
			GroupEntity entity = groupDozerConverter.convertToEntity(group, true);
			groupManager.saveGroup(entity);
			response.setResponseValue(entity.getGrpId());
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
        return response;
	}

	@Override
	public Group getGroup(final String groupId, final String requesterId) {
		Group retVal = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            if(StringUtils.isNotBlank(groupId)) {
                final GroupEntity entity = groupManager.getGroup(groupId, requesterId);
                retVal = groupDozerConverter.convertToDTO(entity, true);
            }
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return retVal;
	}

	@Override
	public Response deleteGroup(final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.DELETE_GROUP).setTargetGroup(groupId);
		try {
			if(StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId is null or empty");
			}
			
			groupManager.deleteGroup(groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }

		return response;
	}
	
	@Override
	public int getNumOfChildGroups(final String groupId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_GROUP_NUM).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            count = groupManager.getNumOfChildGroups(groupId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Group> getChildGroups(final  String groupId, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_GROUP).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            final List<GroupEntity> groupEntityList = groupManager.getChildGroups(groupId, requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return  groupList;
	}
	
	@Override
	public int getNumOfParentGroups(final  String groupId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_GROUP_NUM).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            count = groupManager.getNumOfParentGroups(groupId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Group> getParentGroups(final  String groupId, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_GROUP).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            final List<GroupEntity> groupEntityList = groupManager.getParentGroups(groupId, requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  groupList;
	}

	@Override
	public Response isUserInGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.IS_USER_GROUP).setTargetUser(userId).setAuditDescription(String.format("Check if user is member of group: %s", groupId));
		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or UserId  is null or empty");
			}
			
			response.setResponseValue(groupManager.isUserInCompiledGroupList(groupId, userId));
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}  finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addUserToGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_USER_TO_GROUP).setTargetUser(userId).setAuditDescription(String.format("Add user to group: %s", groupId));
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
			}

			userManager.addUserToGroup(userId, groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			log.error("Error while adding user to group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeUserFromGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_USER_FROM_GROUP).setTargetUser(userId).setAuditDescription(String.format("Remove user from group: %s", groupId));
		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
			}
			
			userManager.removeUserFromGroup(groupId, userId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			log.error("Error while remove user from group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addAttribute(final GroupAttribute attribute) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_ATTRIBUTE_TO_GROUP);

		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute object is null");
			}
            auditBuilder.setTargetGroup(attribute.getGroupId());

			if(StringUtils.isBlank(attribute.getName()) || StringUtils.isBlank(attribute.getValue())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute name is missed");
			}
			
			final GroupAttributeEntity entity = groupAttributeDozerConverter.convertToEntity(attribute, false);
			
			groupManager.saveAttribute(entity);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeAttribute(final String attributeId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_GROUP_ATTRIBUTE);
		try {
			if(attributeId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Attribute Id is null");
			}
			
			groupManager.removeAttribute(attributeId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public List<Group> findBeans(final GroupSearchBean searchBean, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SEARCH_GROUP).setRequestorUserId(requesterId);
        try{
            final List<GroupEntity> groupEntityList =  groupManager.findBeans(searchBean, requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, (searchBean.isDeepCopy()));
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  groupList;
	}

	@Override
	public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_NUM).setRequestorUserId(requesterId);
        try{
            count = groupManager.countBeans(searchBean, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

    @Override
    public List<Group> getGroupsForUser(final String userId, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_FOR_USER).setRequestorUserId(requesterId).setTargetUser(userId);
        try{
            final List<GroupEntity> groupEntityList =  groupManager.getGroupsForUser(userId,requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  groupList;
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_NUM_FOR_USER).setRequestorUserId(requesterId).setTargetUser(userId);
        try{
            count = groupManager.getNumOfGroupsForUser(userId,requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
    }

    @Override
	public List<Group> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_FOR_RESOURCE).setRequestorUserId(requesterId).setTargetResource(resourceId);
        try{
            final List<GroupEntity> groupEntityList =  groupManager.getGroupsForResource(resourceId,requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  groupList;
	}

	@Override
	public int getNumOfGroupsforResource(final String resourceId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_NUM_FOR_RESOURCE).setRequestorUserId(requesterId).setTargetResource(resourceId);
        try{
            count = groupManager.getNumOfGroupsForResource(resourceId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Group> getGroupsForRole(final String roleId, final String requesterId, final int from, final int size) {
        List<Group> groupList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_FOR_ROLE).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            final List<GroupEntity> groupEntityList =  groupManager.getGroupsForRole(roleId,requesterId, from, size);
            groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  groupList;
	}

	@Override
	public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP_NUM_FOR_ROLE).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            count = groupManager.getNumOfGroupsForRole(roleId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public Response addChildGroup(final String groupId, final String childGroupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_CHILD_GROUP).setTargetGroup(groupId).setAuditDescription(String.format("Add child group: %s to group: %s", childGroupId, groupId));

		try {
			if(groupId == null || childGroupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
			}
			
			if(groupId.equals(childGroupId)) {
				throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD, "Cannot add group itself as child");
			}
			
			groupManager.validateGroup2GroupAddition(groupId, childGroupId);
			groupManager.addChildGroup(groupId, childGroupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			log.error("can't add child group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	@WebMethod
	public Response removeChildGroup(final String groupId, final String childGroupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_CHILD_GROUP).setTargetGroup(groupId).setAuditDescription(String.format("Remove child group: %s from group: %s", childGroupId, groupId));

        try {
			if(groupId == null || childGroupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
			}
			
			groupManager.removeChildGroup(groupId, childGroupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response canAddUserToGroup(String userId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_ADD_USER_TO_GROUP).setTargetUser(userId).setAuditDescription(String.format("Check if user can be added to group: %s", groupId));
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"GroupId is null");
			}
			
			boolean has = userManager.isHasGroup(userId, groupId);
			
			if(has) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS,String.format("User %s has already been added to group: %s", userId, groupId));
			}
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			log.error("Error while adding user to group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response canRemoveUserFromGroup(String userId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_REMOVE_USER_FROM_GROUP).setTargetUser(userId).setAuditDescription(String.format("Check if user can be removed from group: %s", groupId));

		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"GroupId or UserId is null");
			}
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			log.error("Error while remove user from group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}
}
