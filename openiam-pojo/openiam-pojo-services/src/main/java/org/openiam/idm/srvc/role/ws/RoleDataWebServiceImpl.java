/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.role.ws;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupArrayResponse;
import org.openiam.idm.srvc.grp.ws.GroupListResponse;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.role.dto.RoleSearch;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserArrayResponse;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.role.ws.RoleDataWebService", 
		targetNamespace = "urn:idm.openiam.org/srvc/role/service", 
		portName = "RoleDataWebServicePort",
		serviceName = "RoleDataWebService")
public class RoleDataWebServiceImpl implements RoleDataWebService {

	private RoleDataService roleDataService;
	private Map<DozerMappingType, DozerBeanMapper> dozerMap;

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addAttribute(org.openiam.idm.srvc.role.dto.RoleAttribute)
	 */
	public RoleAttributeResponse addAttribute(RoleAttribute attribute) {
		final RoleAttributeResponse resp = new RoleAttributeResponse(ResponseStatus.SUCCESS);
		roleDataService.addAttribute(attribute);
		if (attribute.getRoleAttrId() == null || attribute.getRoleAttrId().isEmpty() ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setRoleAttr(attribute);
		}
		return resp;

	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addGroupToRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response addGroupToRole(String serviceId, String roleId,
			String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.addGroupToRole(serviceId, roleId, groupId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addRole(org.openiam.idm.srvc.role.dto.Role)
	 */
	public RoleResponse addRole(Role role) {
		final RoleResponse resp = new RoleResponse(ResponseStatus.SUCCESS);
		roleDataService.addRole(role);
		if (role.getId().getRoleId() != null) {
			resp.setRole(role);
		}else {
			resp.setStatus(ResponseStatus.FAILURE);
		}

		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addUserToRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response addUserToRole(String serviceId, String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.addUserToRole(serviceId, roleId, userId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getAllAttributes(java.lang.String, java.lang.String)
	 */
	public RoleAttributeArrayResponse getAllAttributes(String serviceId,
			String roleId) {
		final RoleAttributeArrayResponse resp = new RoleAttributeArrayResponse(ResponseStatus.SUCCESS);
		final RoleAttribute[] roleAttrAry = roleDataService.getAllAttributes(serviceId, roleId); 
		if (roleAttrAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleAttrAry(roleAttrAry);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getAllRoles()
	 */
	public RoleListResponse getAllRoles() {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getAllRoles(); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getAttribute(java.lang.String)
	 */
	public RoleAttributeResponse getAttribute(String attrId) {
		final RoleAttributeResponse resp = new RoleAttributeResponse(ResponseStatus.SUCCESS);
		final RoleAttribute roleAttr = roleDataService.getAttribute(attrId); 
		if (roleAttr == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleAttr(roleAttr);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getGroupsInRole(java.lang.String, java.lang.String)
	 */
	public GroupArrayResponse getGroupsInRole(String serviceId, String roleId) {
		final GroupArrayResponse resp = new GroupArrayResponse(ResponseStatus.SUCCESS);
		final Group[] groupAry = roleDataService.getGroupsInRole(serviceId, roleId);
		if (groupAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupAry(getDozerMappedGroupArray(groupAry));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getRole(java.lang.String, java.lang.String)
	 */
	public RoleResponse getRole(String serviceId, String roleId) {
		final RoleResponse resp = new RoleResponse(ResponseStatus.SUCCESS);
		final Role role = roleDataService.getRole(serviceId, roleId);
		if (role == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRole(getDozerMappedRole(role));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getRolesInDomain(java.lang.String)
	 */
	public RoleListResponse getRolesInDomain(String domainId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getRolesInDomain(domainId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getRolesInGroup(java.lang.String)
	 */
	public RoleListResponse getRolesInGroup(String groupId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getRolesInGroup(groupId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getUserRoles(java.lang.String)
	 */
	public RoleListResponse getUserRoles(String userId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getUserRoles(userId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}
	
	public RoleListResponse getUserRolesAsFlatList(	String userId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getUserRolesAsFlatList(userId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getUserRolesByDomain(java.lang.String, java.lang.String)
	 */
	public RoleListResponse getUserRolesByDomain(String domainId, String userId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getUserRolesByDomain(domainId, userId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getUserRolesDirect(java.lang.String)
	 */
	public RoleListResponse getUserRolesDirect(String userId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getUserRolesDirect(userId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getUsersInRole(java.lang.String, java.lang.String)
	 */
	public UserArrayResponse getUsersInRole(String serviceId, String roleId) {
		final UserArrayResponse resp = new UserArrayResponse(ResponseStatus.SUCCESS);
		final User[] userAry = roleDataService.getUsersInRole(serviceId, roleId); 
		if (userAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setUserAry(getDozerMappedUserArray(userAry));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#isGroupInRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response isGroupInRole(String serviceId, String roleId, String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.isGroupInRole(serviceId, roleId, groupId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#isUserInRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response isUserInRole(String serviceId, String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		final boolean retval = roleDataService.isUserInRole(serviceId, roleId, userId);
		resp.setResponseValue(new Boolean(retval));
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeAllAttributes(java.lang.String, java.lang.String)
	 */
	public Response removeAllAttributes(String serviceId, String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeAllAttributes(serviceId, roleId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeAllGroupsFromRole(java.lang.String, java.lang.String)
	 */
	public Response removeAllGroupsFromRole(String serviceId, String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeAllGroupsFromRole(serviceId, roleId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeAttribute(org.openiam.idm.srvc.role.dto.RoleAttribute)
	 */
	public Response removeAttribute(RoleAttribute attr) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeAttribute(attr);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeGroupFromRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response removeGroupFromRole(String serviceId, String roleId, String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeGroupFromRole(serviceId, roleId, groupId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeRole(java.lang.String, java.lang.String)
	 */
	public Response removeRole(String serviceId, String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
	    final int retval = roleDataService.removeRole(serviceId, roleId);
        if (retval == 0) {
             resp.setStatus( ResponseStatus.FAILURE);
        }
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeUserFromRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response removeUserFromRole(String serviceId, String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeUserFromRole(serviceId, roleId, userId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#search(org.openiam.idm.srvc.role.dto.RoleSearch)
	 */
	public RoleListResponse search(RoleSearch search) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.search(search); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(getDozerMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#updateAttribute(org.openiam.idm.srvc.role.dto.RoleAttribute)
	 */
	public Response updateAttribute(RoleAttribute attribute) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.updateAttribute(attribute);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#updateRole(org.openiam.idm.srvc.role.dto.Role)
	 */
	public Response updateRole(Role role) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.updateRole(role);
		return resp;
	}


	public RolePolicyResponse addRolePolicy(RolePolicy rPolicy) {
		final RolePolicyResponse resp = new RolePolicyResponse(ResponseStatus.SUCCESS);
		final RolePolicy rp = roleDataService.addRolePolicy(rPolicy);
		if (rp == null || rp.getRolePolicyId() == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setRolePolicy(rp);
		}
		return resp;
	}

	public RolePolicyResponse updateRolePolicy(RolePolicy rolePolicy) {
		final RolePolicyResponse resp = new RolePolicyResponse(ResponseStatus.SUCCESS);
		final RolePolicy rp = roleDataService.updateRolePolicy(rolePolicy);
		if (rp == null || rp.getRolePolicyId() == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setRolePolicy(rp);
		}
		return resp;
	}

	public RolePolicyListResponse getAllRolePolicies(String domainId,String roleId) {
		final RolePolicyListResponse resp = new RolePolicyListResponse(ResponseStatus.SUCCESS);
		final List<RolePolicy> rp = roleDataService.getAllRolePolicies(domainId, roleId);
		if (CollectionUtils.isEmpty(rp)) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setRolePolicy(rp);
		}
		return resp;
	}

	public RolePolicyResponse getRolePolicy(String rolePolicyId) {
		final RolePolicyResponse resp = new RolePolicyResponse(ResponseStatus.SUCCESS);
		final RolePolicy rp = roleDataService.getRolePolicy(rolePolicyId);
		if (rp == null || rp.getRolePolicyId() == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setRolePolicy(rp);
		}
		return resp;
	}

	public Response assocUserToRole(UserRole userRole) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.assocUserToRole(userRole);
		return resp;
	}

	public Response updateUserRoleAssoc(UserRole userRole) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.updateUserRoleAssoc(userRole);
		return resp;
	}

	public UserRoleResponse getUserRoleById(String userRoleId) {
		final UserRoleResponse resp = new UserRoleResponse(ResponseStatus.SUCCESS);
		final UserRole ur =  roleDataService.getUserRoleById(userRoleId);
		if ( ur != null) {
			resp.setUserRole(ur);
		} else {
			resp.setStatus(ResponseStatus.FAILURE);
		}
	 	return resp;
	}

	public UserRoleListResponse getUserRolesForUser(String userId) {
		final UserRoleListResponse resp = new UserRoleListResponse(ResponseStatus.SUCCESS);
		final List<UserRole> ur =  roleDataService.getUserRolesForUser(userId);
		if ( ur != null) {
			resp.setUserRoleList(ur);
		} else {
			resp.setStatus(ResponseStatus.FAILURE);
		}
	 	return resp;
	}
	
	
	public Response removeRolePolicy(RolePolicy rolePolicy) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeRolePolicy(rolePolicy);
		return resp;
	}


	public RoleDataService getRoleDataService() {
		return roleDataService;
	}

	public void setRoleDataService(RoleDataService roleDataService) {
		this.roleDataService = roleDataService;
	}
	
	@Required
	public void setDozerMap(final Map<DozerMappingType, DozerBeanMapper> dozerMap) {
		this.dozerMap = dozerMap;
	}


	private List<Role> getDozerMappedRoleList(final List<Role> roleList) {
		final List<Role> convertedList = new LinkedList<Role>();
		if(CollectionUtils.isNotEmpty(roleList)) {
			for(final Role role : roleList) {
				convertedList.add(dozerMap.get(DozerMappingType.DEEP).map(role, Role.class));
			}
		}
		return convertedList;
	}
	
	private Group[] getDozerMappedGroupArray(final Group[] groupArray) {
		Group[] retVal = null;
		if(groupArray != null) {
			retVal = new Group[groupArray.length];
			for(int i = 0; i < groupArray.length; i++) {
				retVal[i] = dozerMap.get(DozerMappingType.DEEP).map(groupArray[i], Group.class);
			}
		}
		return retVal;
	}
	
	private Role getDozerMappedRole(final Role role) {
		Role retVal = null;
		if(role != null) {
			retVal = dozerMap.get(DozerMappingType.DEEP).map(role, Role.class);
		}
		return retVal;
	}
	
	public User[] getDozerMappedUserArray(final User[] userArray) {
		User[] retVal = null;
		if(userArray != null) {
			retVal = new User[userArray.length];
			for(int i = 0; i < userArray.length; i++) {
				retVal[i] = dozerMap.get(DozerMappingType.DEEP).map(userArray[i], User.class);
			}
		}
		return retVal;
	}
}
