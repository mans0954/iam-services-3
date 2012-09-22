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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.DozerBeanMapper;
import org.hibernate.HibernateException;
import org.openiam.idm.srvc.grp.dto.*;
import org.openiam.idm.srvc.grp.service.*;

import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.ws.UserListResponse;
import org.openiam.util.DozerMappingType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.DozerUtils;
import org.openiam.exception.data.DataException;
import org.openiam.exception.data.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;

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
public class GroupDataWebServiceImpl implements GroupDataWebService {
	protected GroupDataService groupManager;
		
	private static final Log log = LogFactory.getLog(GroupDataWebServiceImpl.class);
	private DozerUtils dozerUtils;

	@Required
	public void setDozerUtils(final DozerUtils dozerUtils) {
		this.dozerUtils = dozerUtils;
	}
	
	public GroupDataWebServiceImpl() {

	}

	public void setGroupManager(GroupDataService groupManager) {
		this.groupManager = groupManager;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#addAttribute(org.openiam.idm.srvc.grp.dto.GroupAttribute)
	 */
	public Response addAttribute(GroupAttribute attribute) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.addAttribute(attribute);
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#addGroup(org.openiam.idm.srvc.grp.dto.Group)
	 */
	public GroupResponse addGroup(Group grp) {
		final GroupResponse resp = new GroupResponse(ResponseStatus.SUCCESS);
		groupManager.addGroup(grp);
		if (grp.getGrpId() == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroup(grp);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#addUserToGroup(java.lang.String, java.lang.String)
	 */
	public Response addUserToGroup(String grpId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.addUserToGroup(grpId, userId); 
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getAllAttributes(java.lang.String)
	 */
	public GroupAttrMapResponse getAllAttributes(String groupId) {
		final GroupAttrMapResponse resp = new GroupAttrMapResponse(ResponseStatus.SUCCESS);
		final Map<String, GroupAttribute> attrMap = groupManager.getAllAttributes(groupId); 
		if (MapUtils.isEmpty(attrMap)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupAttrMap(attrMap);
		}
		return resp;

	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getAllGroups()
	 */
	public GroupListResponse getAllGroups() {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getAllGroups(); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getAllGroupsWithDependents(boolean)
	 */
	/*
	public GroupListResponse getAllGroupsWithDependents(boolean subgroups) {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getAllGroupsWithDependents(subgroups); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}
	*/

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getAttribute(java.lang.String)
	 */
	public GroupAttributeResponse getAttribute(String attrId) {
		final GroupAttributeResponse resp = new GroupAttributeResponse(ResponseStatus.SUCCESS);
		final GroupAttribute attr = groupManager.getAttribute(attrId);  
		if (attr == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupAttr(attr);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getChildGroups(java.lang.String, boolean)
	 */
	public GroupListResponse getChildGroups(String parentGroupId,		boolean subgroups) {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getChildGroups(parentGroupId, subgroups); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getGroup(java.lang.String)
	 */
	public GroupResponse getGroup(String grpId) {
		final GroupResponse resp = new GroupResponse(ResponseStatus.SUCCESS);
		final Group grp = groupManager.getGroup(grpId); 
		if (grp == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroup(dozerUtils.getDozerDeepMappedGroup(grp));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getGroupWithDependants(java.lang.String)
	 */
	public GroupResponse getGroupWithDependants(String grpId) {
		final GroupResponse resp = new GroupResponse(ResponseStatus.SUCCESS);
		final Group grp = groupManager.getGroupWithDependants(grpId); 
		if (grp == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroup(dozerUtils.getDozerDeepMappedGroup(grp));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getGroupsNotLinkedToUser(java.lang.String, java.lang.String, boolean)
	 */
	public GroupListResponse getGroupsNotLinkedToUser(String userId,
			String parentGroupId, boolean nested) {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getGroupsNotLinkedToUser(userId, parentGroupId, nested); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getParentGroup(java.lang.String, boolean)
	 */
	public GroupListResponse getParentGroups(String groupId, boolean dependants) {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final Group group  = groupManager.getGroup(groupId);
		if (group == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(group.getParentGroups()));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getUserInGroups(java.lang.String)
	 */
	public GroupListResponse getUserInGroups(String userId) {
		log.info("getUserInGroups: userId=" + userId);
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getUserInGroups(userId); 
		if (org.springframework.util.CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}
	
	
	public GroupListResponse getUserInGroupsAsFlatList(	String userId) {
		log.info("getUserInGroupsAsFlatList: userId=" + userId);
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.getUserInGroupsAsFlatList(userId); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#getUsersByGroup(java.lang.String)
	 */
	public UserListResponse getUsersByGroup(String grpId) {
		final UserListResponse resp = new UserListResponse(ResponseStatus.SUCCESS);
		final List<User> userList = groupManager.getUsersByGroup(grpId); 
		if (CollectionUtils.isEmpty(userList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setUserList(dozerUtils.getDozerDeepMappedUserList(userList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#isUserInGroup(java.lang.String, java.lang.String)
	 */
	public Response isUserInGroup(String groupId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		final boolean retval = groupManager.isUserInGroup(groupId, userId); 
		resp.setResponseValue(new Boolean(retval));
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#removeAllAttributes(java.lang.String)
	 */
	public Response removeAllAttributes(String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.removeAllAttributes(groupId); 
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#removeAttribute(org.openiam.idm.srvc.grp.dto.GroupAttribute)
	 */
	public Response removeAttribute(GroupAttribute attr) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.removeAttribute(attr); 
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#removeChildGroups(java.lang.String)
	 */
	/*
	public Response removeChildGroups(String parentGroupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.removeChildGroups(parentGroupId); 
		return resp;
	}
	*/

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#removeGroup(java.lang.String)
	 */
	public Response removeGroup(String grpId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.removeGroup(grpId); 
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#removeUserFromGroup(java.lang.String, java.lang.String)
	 */
	public Response removeUserFromGroup(String groupId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		groupManager.removeUserFromGroup(groupId, userId); 
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#saveAttributes(org.openiam.idm.srvc.grp.dto.GroupAttribute[])
	 */
	public Response saveAttributes(GroupAttribute[] groupAttr) {
		groupManager.saveAttributes(groupAttr);
		return new Response(ResponseStatus.SUCCESS);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#search(org.openiam.idm.srvc.grp.dto.GroupSearch)
	 */
	public GroupListResponse search(GroupSearch search) {
		final GroupListResponse resp = new GroupListResponse(ResponseStatus.SUCCESS);
		final List<Group> grpList = groupManager.search(search); 
		if (CollectionUtils.isEmpty(grpList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupList(dozerUtils.getDozerDeepMappedGroupList(grpList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#updateAttribute(org.openiam.idm.srvc.grp.dto.GroupAttribute)
	 */
	public Response updateAttribute(GroupAttribute attribute) {
		groupManager.updateAttribute(attribute); 
		return new Response(ResponseStatus.SUCCESS);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.grp.ws.GroupDataWebService#updateGroup(org.openiam.idm.srvc.grp.dto.Group)
	 */
	public GroupResponse updateGroup(Group grp) {
		final GroupResponse resp = new GroupResponse(ResponseStatus.SUCCESS);	
		groupManager.updateGroup(grp); 
		
		if (grp.getGrpId() == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		resp.setGroup(grp);
		return  resp;
	}
}
