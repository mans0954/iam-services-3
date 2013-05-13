package org.openiam.idm.srvc.grp.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.GroupAttributeDozerConverter;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
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
public class GroupDataWebServiceImpl implements GroupDataWebService {
	
	@Autowired
	private GroupDataService groupManager;
	
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
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(group == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(group.getGrpName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			final GroupEntity found = groupManager.getGroupByName(group.getGrpName());
			if(found != null) {
				if(StringUtils.isBlank(group.getGrpId()) && found != null) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			
				if(StringUtils.isNotBlank(group.getGrpId()) && !group.getGrpId().equals(found.getGrpId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			
			GroupEntity entity = groupDozerConverter.convertToEntity(group, false);
			groupManager.saveGroup(entity);
			response.setResponseValue(entity.getGrpId());
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
	public Group getGroup(final String groupId) {
		Group retVal = null;
		if(StringUtils.isNotBlank(groupId)) {
			final GroupEntity entity = groupManager.getGroup(groupId);
			retVal = groupDozerConverter.convertToDTO(entity, true);
		}
		return retVal;
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
	public int getNumOfChildGroups(final String groupId) {
		return groupManager.getNumOfChildGroups(groupId);
	}

	@Override
	public List<Group> getChildGroups(final String groupId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getChildGroups(groupId, from, size);
		final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
		return groupList;
	}
	
	@Override
	public int getNumOfParentGroups(final String groupId) {
		return groupManager.getNumOfParentGroups(groupId);
	}

	@Override
	public List<Group> getParentGroups(final String groupId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getParentGroups(groupId, from, size);
		final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
		return groupList;
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
	public List<Group> getGroupsForUser(final String userId, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.getGroupsForUser(userId, from, size);
		final List<Group> groupList = groupDozerConverter.convertToDTOList(groupEntityList, false);
		return groupList;
	}
	
	@Override
	public int getNumOfGroupsForUser(final String userId) {
		return groupManager.getNumOfGroupsForUser(userId);
	}

	@Override
	public Response addUserToGroup(final String groupId, final String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserGroupEntity entity = groupManager.getRecord(userId, groupId);
			
			if(entity != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			groupManager.addUserToGroup(groupId, userId);
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
			
			groupManager.removeUserFromGroup(groupId, userId);
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
	public List<Group> findBeans(final GroupSearchBean searchBean, final int from, final int size) {
		final List<GroupEntity> groupEntityList = groupManager.findBeans(searchBean, from, size);
		return groupDozerConverter.convertToDTOList(groupEntityList, (searchBean.isDeepCopy()));
	}

	@Override
	public int countBeans(final GroupSearchBean searchBean) {
		return groupManager.countBeans(searchBean);
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

	@Override
	@WebMethod
	public List<Group> getGroupsForRole(final String roleId, final int from, final int size) {
		final List<GroupEntity> entityList = groupManager.getGroupsForRole(roleId, from, size);
		final List<Group> groupList = groupDozerConverter.convertToDTOList(entityList, false);
		return groupList;
	}

	@Override
	@WebMethod
	public int getNumOfGroupsForRole(final String roleId) {
		return groupManager.getNumOfGroupsForRole(roleId);
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
			
			final GroupEntity group = groupManager.getGroup(groupId);
			final GroupEntity child = groupManager.getGroup(childGroupId);
			if(group == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(group.hasChildGroup(childGroupId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			if(causesCircularDependency(group, child, new HashSet<GroupEntity>())) {
				throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
			}
			
			groupManager.addChildGroup(groupId, childGroupId);
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

	
	private boolean causesCircularDependency(final GroupEntity parent, final GroupEntity child, final Set<GroupEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentGroups())) {
					for(final GroupEntity entity : parent.getParentGroups()) {
						retval = entity.getGrpId().equals(child.getGrpId());
						if(retval) {
							break;
						}
						causesCircularDependency(parent, entity, visitedSet);
					}
				}
			}
		}
		return retval;
	}
}
