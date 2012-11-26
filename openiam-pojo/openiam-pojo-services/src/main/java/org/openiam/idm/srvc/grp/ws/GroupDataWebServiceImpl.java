package org.openiam.idm.srvc.grp.ws;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.DozerBeanMapper;
import org.hibernate.HibernateException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.*;
import org.openiam.idm.srvc.grp.service.*;

import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.ws.UserListResponse;
import org.openiam.util.DozerMappingType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.GroupAttributeDozerConverter;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.data.DataException;
import org.openiam.exception.data.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

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
public class GroupDataWebServiceImpl implements GroupDataWebService {
	
	@Autowired
	private GroupDataService groupManager;
	
    @Autowired
    private GroupDozerConverter groupDozerConverter;
    
    @Autowired
    private UserDozerConverter userDozerConverter;
    
    @Autowired
    private GroupAttributeDozerConverter groupAttributeDozerConverter;
    
    @Autowired
    private GroupSearchBeanConverter groupSearchBeanConverter;
		
	private static final Log log = LogFactory.getLog(GroupDataWebServiceImpl.class);

	public GroupDataWebServiceImpl() {

	}

	@Override
	public GroupResponse addGroup(final Group group) {
		final GroupResponse response = new GroupResponse(ResponseStatus.SUCCESS);
		try {
			if(group == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(group.getGrpName())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_GROUP_NAME);
			}
			
			final GroupEntity entity = groupDozerConverter.convertToEntity(group, false);
			
			groupManager.saveGroup(entity);
			response.setGroup(groupDozerConverter.convertToDTO(entity, false));
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
	public GroupResponse updateGroup(final Group group) {
		final GroupResponse response = new GroupResponse(ResponseStatus.SUCCESS);
		try {
			if(group == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(group.getGrpName())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_GROUP_NAME);
			}
			
			final GroupEntity entity = groupDozerConverter.convertToEntity(group, false);
			groupManager.saveGroup(entity);
			response.setGroup(groupDozerConverter.convertToDTO(entity, false));
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
	public GroupResponse getGroup(final String groupId) {
		final GroupResponse response = new GroupResponse(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final GroupEntity entity = groupManager.getGroup(groupId);
			response.setGroup(groupDozerConverter.convertToDTO(entity, true));
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
	public Response deleteGroup(final String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			groupManager.deleteGroup(groupId);
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
	public GroupListResponse getChildGroups(final String groupId, final int from, final int size) {
		final GroupListResponse response = new GroupListResponse(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(groupId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			
			final List<GroupEntity> groupEntityList = groupManager.getChildGroups(groupId, from, size);
			final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
			response.setGroupList(groupList);
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
	public GroupListResponse getParentGroups(final String groupId, final int from, final int size) {
		final GroupListResponse response = new GroupListResponse(ResponseStatus.SUCCESS);
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<GroupEntity> groupEntityList = groupManager.getParentGroups(groupId, from, size);
			final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
			response.setGroupList(groupList);
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
	public GroupListResponse getGroupsForUser(final String userId, final int from, final int size) {
		final GroupListResponse response = new GroupListResponse(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<GroupEntity> groupEntityList = groupManager.getGroupsForUser(userId, from, size);
			final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
			response.setGroupList(groupList);
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
	public GroupListResponse getCompiledGroupsForUser(final String userId) {
		final GroupListResponse response = new GroupListResponse(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<Group> groupList = groupManager.getCompiledGroupsForUser(userId);
			response.setGroupList(groupList);
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
	public UserListResponse getUsersByGroup(final String groupId, final int from, final int size) {
		final UserListResponse response = new UserListResponse(ResponseStatus.SUCCESS);
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<UserEntity> userEntityList = groupManager.getUsersInGroup(groupId, from, size);
			final List<User> userList = userDozerConverter.convertToDTOList(userEntityList, false);
			response.setUserList(userList);
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
			
			if(groupManager.isUserInGroup(groupId, userId)) {
				throw new BasicDataServiceException(ResponseCode.MEMBERSHIP_EXISTS);
			}
			
			groupManager.addUserToGroup(groupId, userId);
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
	public Response removeUserFromGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			groupManager.removeUserFromGroup(groupId, userId);
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
	public List<Group> findBeans(final GroupSearchBean searchBean, final int from, final int size) {
		final GroupEntity entity = groupSearchBeanConverter.convert(searchBean);
		final List<GroupEntity> groupEntityList = groupManager.findBeans(entity, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, (searchBean.isDeepCopy()));
	}

	@Override
	public int countBeans(final GroupSearchBean searchBean) {
		final GroupEntity entity = groupSearchBeanConverter.convert(searchBean);
		return groupManager.countBeans(entity);
	}

	@Override
	public List<Group> getGroupsForResource(final String resourceId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getGroupsForResource(resourceId, from, size);
		final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
		return groupList;
	}

	@Override
	public int getNumOfGroupsforResource(final String resourceId) {
		return groupManager.getNumOfGroupsForResource(resourceId);
	}
}
