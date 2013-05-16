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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.RoleAttributeDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.RolePolicyDozerConverter;
import org.openiam.idm.searchbeans.MembershipRoleSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.searchbean.converter.RoleSearchBeanConverter;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.role.ws.RoleDataWebService", 
		targetNamespace = "urn:idm.openiam.org/srvc/role/service", 
		portName = "RoleDataWebServicePort",
		serviceName = "RoleDataWebService")
@Service("roleWS")
public class RoleDataWebServiceImpl implements RoleDataWebService {
	
	private static Logger LOG = Logger.getLogger(RoleDataWebServiceImpl.class);
	
	@Autowired
	private RoleDataService roleDataService;
    
    @Autowired
    private RoleDozerConverter roleDozerConverter;
    
    @Autowired
    private RoleAttributeDozerConverter roleAttributeDozerConverter;
    
    @Autowired
    private RolePolicyDozerConverter rolePolicyDozerConverter;
    
    @Autowired
    private RoleSearchBeanConverter roleSearchBeanConverter;
    
    @Autowired
    private GroupDataService groupService;
    
    @Autowired
    private SecurityDomainDAO securityDomainDAO;

	@Override
	public RoleAttributeResponse addAttribute(RoleAttribute attribute) {
		final RoleAttributeResponse response = new RoleAttributeResponse(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(attribute.getName())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(attribute.getValue())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RoleAttributeEntity entity = roleAttributeDozerConverter.convertToEntity(attribute, false);
			roleDataService.saveAttribute(entity);
			final RoleAttribute dto = roleAttributeDozerConverter.convertToDTO(entity, false);
			response.setRoleAttr(dto);
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
	public Response addGroupToRole(String roleId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || groupId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RoleEntity role =  roleDataService.getRole(roleId);
			final GroupEntity group = groupService.getGroup(groupId);
			if(role == null || group == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(role.hasGroup(group.getGrpId())) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			roleDataService.addGroupToRole(roleId, groupId);
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
	public Response addUserToRole(String roleId, String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(roleDataService.getUserRole(userId, roleId) != null) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			roleDataService.addUserToRole(roleId, userId);
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
	public Role getRole(String roleId) {
		Role retVal = null;
		if(roleId != null) {
			final RoleEntity entity = roleDataService.getRole(roleId);
			if(entity != null) {
				retVal = roleDozerConverter.convertToDTO(entity, false);
			}
		}
		return retVal;
	}

//	@Override
//	public List<Role> getRolesInGroup(final String groupId, final int from, final int size) {
//		final List<RoleEntity> entityList = roleDataService.getRolesInGroup(groupId, from, size);
//		final List<Role> roleList = roleDozerConverter.convertToDTOList(entityList, false);
//		return roleList;
//	}
//
//	@Override
//	public List<Role> getRolesForUser(final String userId, final int from, final int size) {
//		final List<RoleEntity> entityList = roleDataService.getRolesForUser(userId, from, size);
//		final List<Role> roleList = roleDozerConverter.convertToDTOList(entityList, false);
//		return roleList;
//	}
	
//	@Override
//	public int getNumOfRolesForUser(final String userId) {
//		return roleDataService.getNumOfRolesForUser(userId);
//	}


	@Override
	public Response removeAttribute(final String attributeId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			roleDataService.removeAttribute(attributeId);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response removeGroupFromRole(String roleId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(groupId == null || roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			roleDataService.removeGroupFromRole(roleId, groupId);
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
	public Response removeRole(String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RoleEntity entity = roleDataService.getRole(roleId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/*
			if(CollectionUtils.isNotEmpty(entity.getChildRoles())) {
				throw new BasicDataServiceException(ResponseCode.ROLE_HANGING_CHILD_ROLES);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getGroups())) {
				throw new BasicDataServiceException(ResponseCode.ROLE_HANGING_GROUPS);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getResourceRoles())) {
				throw new BasicDataServiceException(ResponseCode.ROLE_HANGING_RESOURCES);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getUserRoles())) {
				throw new BasicDataServiceException(ResponseCode.ROLE_HANGING_USERS);
			}
			*/
			
			 roleDataService.removeRole(roleId);
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
	public Response removeUserFromRole(String roleId, String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			roleDataService.removeUserFromRole(roleId, userId);
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
	public Response updateAttribute(RoleAttribute attribute) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RoleAttributeEntity entity = roleAttributeDozerConverter.convertToEntity(attribute, false);
			roleDataService.saveAttribute(entity);
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
	public Response saveRole(Role role) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(role == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			RoleEntity entity = roleDozerConverter.convertToEntity(role, false);
			if(StringUtils.isBlank(entity.getRoleName())) {
				throw new BasicDataServiceException(ResponseCode.NO_NAME);
			}
			
			/* check if the name is taken by another entity */
			final RoleEntity nameEntity = roleDataService.getRoleByName(role.getRoleName());
			if(nameEntity != null) {
				if(StringUtils.isBlank(entity.getRoleId()) || !entity.getRoleId().equals(nameEntity.getRoleId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			
			if(StringUtils.isBlank(entity.getServiceId())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ROLE_DOMAIN);
			}
			
			if(securityDomainDAO.findById(entity.getServiceId()) == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ROLE_DOMAIN);
			}
			
			if(StringUtils.isNotBlank(entity.getRoleId())) {
				final RoleEntity dbObject = roleDataService.getRole(entity.getRoleId());
				if(dbObject == null) {
					throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
				}
				
				/* merge */
				dbObject.setRoleName(entity.getRoleName());
				dbObject.setDescription(entity.getDescription());
				dbObject.setServiceId(entity.getServiceId());
				dbObject.setStatus(entity.getStatus());
				dbObject.setMetadataTypeId(entity.getMetadataTypeId());
				dbObject.setInternalRoleId(entity.getInternalRoleId());
				entity = dbObject;
			}
			
			roleDataService.saveRole(entity);
			response.setResponseValue(entity.getRoleId());
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
	public RolePolicyResponse addRolePolicy(final RolePolicy policy) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(dto);
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
	public RolePolicyResponse updateRolePolicy(final RolePolicy policy) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(dto);
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
	public RolePolicyResponse getRolePolicy(String rolePolicyId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
		try {
			final RolePolicyEntity entity = roleDataService.getRolePolicy(rolePolicyId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final RolePolicy policy = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(policy);
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
	public Response removeRolePolicy(final String rolePolicyId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			roleDataService.removeRolePolicy(rolePolicyId);
		} catch(Throwable e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public List<Role> findBeans(final RoleSearchBean searchBean, final int from, final int size) {
		final List<RoleEntity> found = roleDataService.findBeans(searchBean, from, size);
		return roleDozerConverter.convertToDTOList(found, (searchBean.isDeepCopy()));
	}

	@Override
	@WebMethod
	public int countBeans(final RoleSearchBean searchBean) {
		return roleDataService.countBeans(searchBean);
	}

//	@Override
//	public List<Role> getRolesForResource(final String resourceId, final int from, final int size) {
//		final List<RoleEntity> entityList = roleDataService.getRolesForResource(resourceId, from, size);
//		return roleDozerConverter.convertToDTOList(entityList, false);
//	}
//
//	@Override
//	public int getNumOfRolesForResource(final String resourceId) {
//		return roleDataService.getNumOfRolesForResource(resourceId);
//	}

	@Override
	public List<Role> getChildRoles(final MembershipRoleSearchBean searchBean, final  int from, final int size) {
		final List<RoleEntity> entityList = roleDataService.getChildRoles(searchBean, from, size);
		final List<Role> roleList = roleDozerConverter.convertToDTOList(entityList, false);
		return roleList;
	}

	@Override
	@WebMethod
	public int getNumOfChildRoles(final MembershipRoleSearchBean searchBean) {
		return roleDataService.getNumOfChildRoles(searchBean);
	}

	@Override
	@WebMethod
	public List<Role> getParentRoles(final MembershipRoleSearchBean searchBean, final int from, final int size) {
		final List<RoleEntity> entityList = roleDataService.getParentRoles(searchBean, from, size);
		final List<Role> roleList = roleDozerConverter.convertToDTOList(entityList, false);
		return roleList;
	}

	@Override
	@WebMethod
	public int getNumOfParentRoles(final MembershipRoleSearchBean searchBean) {
		return roleDataService.getNumOfParentRoles(searchBean);
	}

	@Override
	public Response addChildRole(final String roleId, final String childRoleId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			final RoleEntity parent = roleDataService.getRole(roleId);
			final RoleEntity child = roleDataService.getRole(childRoleId);
			if(parent == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			if(parent.hasChildRole(child.getRoleId())) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
			}
			
			if(roleId.equals(childRoleId)) {
				throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
			}
			
			if(causesCircularDependency(parent, child, new HashSet<RoleEntity>())) {
				throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
			}
			
			roleDataService.addChildRole(roleId, childRoleId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			LOG.error("Can't add child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
	
	private boolean causesCircularDependency(final RoleEntity parent, final RoleEntity child, final Set<RoleEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentRoles())) {
					for(final RoleEntity entity : parent.getParentRoles()) {
						retval = entity.getRoleId().equals(child.getRoleId());
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

	@Override
	public Response removeChildRole(final String roleId, final String childRoleId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			final RoleEntity parent = roleDataService.getRole(roleId);
			final RoleEntity child = roleDataService.getRole(childRoleId);
			if(parent == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			roleDataService.removeChildRole(roleId, childRoleId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			LOG.error("Can't remove child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

    public List<Role> getEntitlementRoles(MembershipRoleSearchBean searchBean, int from, int size){
        final List<RoleEntity> roleEntityList = roleDataService.getEntitlementRoles(searchBean, from, size);
        final List<Role> roleList = roleDozerConverter.convertToDTOList(roleEntityList, false);
        return  roleList;
    }
    public int getNumOfEntitlementRoles(MembershipRoleSearchBean searchBean){
        return roleDataService.getNumOfEntitlementRoles(searchBean);
    }

//	@Override
//	public int getNumOfRolesForGroup(final String groupId) {
//		return roleDataService.getNumOfRolesForGroup(groupId);
//	}
}
