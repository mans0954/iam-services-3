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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.DozerUtils;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
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

	private DozerUtils dozerUtils;
	private RoleDataService roleDataService;
	
    @Autowired
    private GroupDozerConverter groupDozerConverter;

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
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addGroupToRole(java.lang.String, java.lang.String)
	 */
	public Response addGroupToRole(String roleId, String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.addGroupToRole(roleId, groupId);
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
		if (role.getRoleId() != null) {
			resp.setRole(role);
		}else {
			resp.setStatus(ResponseStatus.FAILURE);
		}

		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#addUserToRole(java.lang.String, java.lang.String)
	 */
	public Response addUserToRole(String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.addUserToRole(roleId, userId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getAllAttributes(java.lang.String)
	 */
	public RoleAttributeArrayResponse getAllAttributes(String roleId) {
		final RoleAttributeArrayResponse resp = new RoleAttributeArrayResponse(ResponseStatus.SUCCESS);
		final RoleAttribute[] roleAttrAry = roleDataService.getAllAttributes(roleId); 
		if (roleAttrAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleAttrAry(roleAttrAry);
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
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getGroupsInRole(java.lang.String)
	 */
	public GroupArrayResponse getGroupsInRole(String roleId) {
		final GroupArrayResponse resp = new GroupArrayResponse(ResponseStatus.SUCCESS);
		final GroupEntity[] groupAry = roleDataService.getGroupsInRole(roleId);
		if (groupAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setGroupAry((Group[])groupDozerConverter.convertToDTOList(Arrays.asList(groupAry), true).toArray());
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getRole(java.lang.String, java.lang.String)
	 */
	public RoleResponse getRole(String roleId) {
		final RoleResponse resp = new RoleResponse(ResponseStatus.SUCCESS);
		final Role role = roleDataService.getRole(roleId);
		if (role == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRole(dozerUtils.getDozerDeepMappedRole(role));
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
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
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
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
		}
		return resp;
	}
	
	public RoleListResponse getUserRolesAsFlatList(	String userId) {
		final RoleListResponse resp = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getUserRolesAsFlatList(userId); 
		if (roleList == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
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
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
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
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#getUsersInRole(java.lang.String)
	 */
	public UserArrayResponse getUsersInRole(String roleId) {
		final UserArrayResponse resp = new UserArrayResponse(ResponseStatus.SUCCESS);
		final User[] userAry = roleDataService.getUsersInRole(roleId); 
		if (userAry == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		} else {
			resp.setUserAry(dozerUtils.getDozerDeepMappedUserArray(userAry));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#isGroupInRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response isGroupInRole(String roleId, String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.isGroupInRole(roleId, groupId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#isUserInRole(java.lang.String, java.lang.String)
	 */
	public Response isUserInRole(String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		final boolean retval = roleDataService.isUserInRole(roleId, userId);
		resp.setResponseValue(new Boolean(retval));
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeAllAttributes(java.lang.String)
	 */
	public Response removeAllAttributes(String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeAllAttributes(roleId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeAllGroupsFromRole(java.lang.String)
	 */
	public Response removeAllGroupsFromRole(String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeAllGroupsFromRole(roleId);
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
	public Response removeGroupFromRole(String roleId, String groupId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeGroupFromRole(roleId, groupId);
		//if (retval == 0) {
		//	resp.setStatus(ResponseStatus.FAILURE);
		//}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeRole(java.lang.String, java.lang.String)
	 */
	public Response removeRole(String roleId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
	    final int retval = roleDataService.removeRole(roleId);
        if (retval == 0) {
             resp.setStatus( ResponseStatus.FAILURE);
        }
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.role.ws.RoleDataWebService#removeUserFromRole(java.lang.String, java.lang.String)
	 */
	public Response removeUserFromRole(String roleId, String userId) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		roleDataService.removeUserFromRole(roleId, userId);
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
			resp.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
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

	public RolePolicyListResponse getAllRolePolicies(String roleId) {
		final RolePolicyListResponse resp = new RolePolicyListResponse(ResponseStatus.SUCCESS);
		final List<RolePolicy> rp = roleDataService.getAllRolePolicies(roleId);
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
	
	@Override
	@WebMethod
	public RoleListResponse getRolesInDomain(String domainId) {
		final RoleListResponse response = new RoleListResponse(ResponseStatus.SUCCESS);
		final List<Role> roleList = roleDataService.getRolesInDomain(domainId);
		if(CollectionUtils.isNotEmpty(roleList)) {
			response.setRoleList(dozerUtils.getDozerDeepMappedRoleList(roleList));
		}
		return response;
	}
	
	@Override
	@WebMethod
	public RoleResponse getRoleByName( @WebParam(name = "roleName", targetNamespace = "") String roleName) {
		final RoleResponse response = new RoleResponse(ResponseStatus.SUCCESS);
		final Role retVal = roleDataService.getRoleByName(roleName);
		if(retVal != null) {
			response.setRole(dozerUtils.getDozerDeepMappedRole(retVal));
		}
		return response;
	}


	public RoleDataService getRoleDataService() {
		return roleDataService;
	}

	public void setRoleDataService(RoleDataService roleDataService) {
		this.roleDataService = roleDataService;
	}

	@Required
	public void setDozerUtils(final DozerUtils dozerUtils) {
		this.dozerUtils = dozerUtils;
	}
}
