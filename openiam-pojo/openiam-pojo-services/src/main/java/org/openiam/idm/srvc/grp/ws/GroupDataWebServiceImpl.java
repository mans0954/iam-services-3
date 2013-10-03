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
            auditBuilder.setSourceUserId(group.getRequestorUserId()).setTargetGroup(group.getGrpId());
            if(StringUtils.isBlank(group.getGrpId())) {
                auditBuilder.setAction(AuditAction.ADD_GROUP);
            }


			if(StringUtils.isBlank(group.getGrpName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			final GroupEntity found = groupManager.getGroupByName(group.getGrpName(), null);
			if(found != null) {
				if(StringUtils.isBlank(group.getGrpId()) && found != null) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			
				if(StringUtils.isNotBlank(group.getGrpId()) && !group.getGrpId().equals(found.getGrpId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			
			GroupEntity entity = groupDozerConverter.convertToEntity(group, true);
			groupManager.saveGroup(entity);
			response.setResponseValue(entity.getGrpId());
            auditBuilder.setResult(AuditResult.SUCCESS);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
        return response;
	}

	@Override
	public Group getGroup(final String groupId, final String requesterId) {
		Group retVal = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_GROUP).setSourceUserId(requesterId).setTargetGroup(groupId);
        try{
            if(StringUtils.isNotBlank(groupId)) {
                final GroupEntity entity = groupManager.getGroup(groupId, requesterId);
                retVal = groupDozerConverter.convertToDTO(entity, true);
            }
            auditBuilder.setResult(AuditResult.SUCCESS);
        } catch(Throwable e) {
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
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
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			groupManager.deleteGroup(groupId);
            auditBuilder.setResult(AuditResult.SUCCESS);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
		}finally {
            auditLogService.enqueue(auditBuilder);
        }

		return response;
	}
	
	@Override
	public int getNumOfChildGroups(final String groupId, final String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_GROUP_NUM).setSourceUserId(requesterId).setTargetGroup(groupId);
        try{
            count = groupManager.getNumOfChildGroups(groupId, requesterId);
            auditBuilder.setResult(AuditResult.SUCCESS);
        } catch(Throwable e) {
            auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, e.getMessage());
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Group> getChildGroups(final  String groupId, final String requesterId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getChildGroups(groupId, requesterId, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, false);
	}
	
	@Override
	public int getNumOfParentGroups(final  String groupId, final String requesterId) {
		return groupManager.getNumOfParentGroups(groupId, requesterId);
	}

	@Override
	public List<Group> getParentGroups(final  String groupId, final String requesterId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getParentGroups(groupId, requesterId, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, false);
	}

	@Override
	public Response isUserInGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			response.setResponseValue(groupManager.isUserInCompiledGroupList(groupId, userId));
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addUserToGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}

			userManager.addUserToGroup(userId, groupId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Error while adding user to group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeUserFromGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			userManager.removeUserFromGroup(groupId, userId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Error while remove user from group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response addAttribute(final GroupAttribute attribute) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(attribute.getName()) || StringUtils.isBlank(attribute.getValue())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final GroupAttributeEntity entity = groupAttributeDozerConverter.convertToEntity(attribute, false);
			
			groupManager.saveAttribute(entity);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeAttribute(final String attributeId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attributeId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			groupManager.removeAttribute(attributeId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public List<Group> findBeans(final GroupSearchBean searchBean, final String requesterId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.findBeans(searchBean, requesterId, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, (searchBean.isDeepCopy()));
	}

	@Override
	public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
		return groupManager.countBeans(searchBean, requesterId);
	}

    @Override
    public List<Group> getGroupsForUser(final String userId, final String requesterId, final int from, final int size) {
        final List<GroupEntity> groupEntityList = groupManager.getGroupsForUser(userId,requesterId, from, size);
        return groupDozerConverter.convertToDTOList(groupEntityList, false);
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        return groupManager.getNumOfGroupsForUser(userId,requesterId);
    }

    @Override
	public List<Group> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getGroupsForResource(resourceId, requesterId, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, false);
	}

	@Override
	public int getNumOfGroupsforResource(final String resourceId, final String requesterId) {
		return groupManager.getNumOfGroupsForResource(resourceId, requesterId);
	}

	@Override
	public List<Group> getGroupsForRole(final String roleId, final String requesterId, final int from, final int size) {
		final List<GroupEntity> entityList = groupManager.getGroupsForRole(roleId, requesterId, from, size);
		return groupDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
		return groupManager.getNumOfGroupsForRole(roleId, requesterId);
	}

	@Override
	public Response addChildGroup(final String groupId, final String childGroupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || childGroupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(groupId.equals(childGroupId)) {
				throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
			}
			
			groupManager.validateGroup2GroupAddition(groupId, childGroupId);
			groupManager.addChildGroup(groupId, childGroupId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("can't add child group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	@WebMethod
	public Response removeChildGroup(final String groupId, final String childGroupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || childGroupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			groupManager.removeChildGroup(groupId, childGroupId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response canAddUserToGroup(String userId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			boolean has = userManager.isHasGroup(userId, groupId);
			
			if(has) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
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
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			log.error("Error while remove user from group", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
}
