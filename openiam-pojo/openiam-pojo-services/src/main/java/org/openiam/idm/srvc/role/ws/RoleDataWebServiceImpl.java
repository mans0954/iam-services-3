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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.RolePolicyDozerConverter;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.role.ws.RoleDataWebService", 
		targetNamespace = "urn:idm.openiam.org/srvc/role/service", 
		portName = "RoleDataWebServicePort",
		serviceName = "RoleDataWebService")
@Service("roleWS")
public class RoleDataWebServiceImpl extends AbstractBaseService implements RoleDataWebService {
	
	private static Logger LOG = Logger.getLogger(RoleDataWebServiceImpl.class);
	
	@Autowired
    private RoleDataService roleDataService;
    @Autowired
    private UserDataService userDataService;
    
    @Autowired
    private RoleDozerConverter roleDozerConverter;
    
    @Autowired
    private RolePolicyDozerConverter rolePolicyDozerConverter;
    
    @Autowired
    private GroupDataService groupService;

    @Autowired
    protected SysConfiguration sysConfiguration;

	@Override
	public Response validateEdit(Role role) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			validate(role);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public Response validateDelete(String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			validateDeleteInternal(roleId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	private void validate(final Role role) throws BasicDataServiceException {
		if(role == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Role object is null");
		}
		
		final RoleEntity entity = roleDozerConverter.convertToEntity(role, true);
		if(StringUtils.isBlank(entity.getName())) {
			throw new BasicDataServiceException(ResponseCode.NO_NAME, "Role Name is null or empty");
		}
		
		/* check if the name is taken by another entity */
		final RoleEntity nameEntity = roleDataService.getRoleByName(role.getName(), null);
		if(nameEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Role Name is already exists");
			}
		}
		
		entityValidator.isValid(entity);
	}
	
	public void validateDeleteInternal(final String roleId) throws BasicDataServiceException {
		if(roleId == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId  is null or empty");
		}
		
		final RoleEntity entity = roleDataService.getRole(roleId);
		if(entity == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, String.format("No Role is found for roleId: %s", roleId));
		}
	}

	@Override
	public Response addGroupToRole(String roleId, String groupId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_GROUP_TO_ROLE.value());
        GroupEntity groupEntity = groupService.getGroup(groupId);
        idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Add group to  role: %s", roleId));
		try {
			roleDataService.validateGroup2RoleAddition(roleId, groupId);
			roleDataService.addGroupToRole(roleId, groupId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public Response addUserToRole(String roleId, String userId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.ADD_USER_TO_ROLE.value());
        UserEntity user = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getPrimaryIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAuditDescription(String.format("Add user to  role: %s", roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "UserId or RoleId  is null or empty");
			}
			
			roleDataService.addUserToRole(roleId, userId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}  finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public Role getRole(String roleId, String requesterId) {
		Role retVal = null;
        try{
            if(roleId != null) {
                final RoleEntity entity = roleDataService.getRole(roleId, requesterId);
                if(entity != null) {
                    retVal = roleDozerConverter.convertToDTO(entity, true);
                }
            }
        } catch(Throwable e) {
            LOG.error("Exception", e);
        }
		return retVal;
	}

	@Override
	public List<Role> getRolesInGroup(final String groupId, String requesterId, boolean deepFlag, final int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.getRolesInGroup(groupId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public List<Role> getRolesForUser(final String userId, final String requesterId, Boolean deepFlag, final int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.getRolesForUser(userId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public int getNumOfRolesForUser(final String userId, String requesterId) {
        return roleDataService.getNumOfRolesForUser(userId, requesterId);
	}

	@Override
	public Response removeGroupFromRole(String roleId, String groupId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_GROUP_FROM_ROLE.value());
        GroupEntity groupEntity = groupService.getGroup(groupId);
        idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Remove group %s from role: %s", groupId, roleId));
		try {
			if(groupId == null || roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
			}

			roleDataService.removeGroupFromRole(roleId, groupId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public Response removeRole(String roleId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.DELETE_ROLE.value());
        idmAuditLog.setRequestorUserId(requesterId);
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
		try {
			if(roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId  is null or empty");
			}
			
			final RoleEntity entity = roleDataService.getRole(roleId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, String.format("No Role is found for roleId: %s", roleId));
			}
			roleDataService.removeRole(roleId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }

		return response;
	}

	@Override
	public Response removeUserFromRole(String roleId, String userId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.REMOVE_USER_FROM_ROLE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getPrimaryIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAuditDescription(String.format("Remove user %s from role: %s", userId, roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			roleDataService.removeUserFromRole(roleId, userId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		} finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public Response saveRole(Role role, final String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.SAVE_ROLE.value());
		try {
            idmAuditLog.setRequestorUserId(requesterId);

            if(StringUtils.isBlank(role.getId())) {
                idmAuditLog.setAction(AuditAction.ADD_ROLE.value());
            }
            
            validate(role);
			
			final RoleEntity entity = roleDozerConverter.convertToEntity(role, true);
			roleDataService.saveRole(entity, requesterId);
            idmAuditLog.setTargetRole(entity.getId(), entity.getName());
            response.setResponseValue(entity.getId());
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public RolePolicyResponse addRolePolicy(final RolePolicy policy,final String requesterId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.ADD_ROLE_POLICY.value());
        RoleEntity roleEntity = roleDataService.getRole(policy.getRoleId());
        idmAuditLog.setTargetRole(policy.getRoleId(), roleEntity.getName());
        idmAuditLog.setRequestorUserId(requesterId);
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"Role policy object is null");
			}

			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(dto);
            idmAuditLog.setTargetPolicy(policy.getRolePolicyId(), policy.getName());
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		} finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public RolePolicyResponse updateRolePolicy(final RolePolicy policy, final String requesterId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.UPDATE_ROLE_POLICY.value());
        RoleEntity roleEntity = roleDataService.getRole(policy.getRoleId());
        idmAuditLog.setTargetRole(policy.getRoleId(), roleEntity.getName());
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"Role policy object is null");
			}
			
			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
            idmAuditLog.setTargetPolicy(policy.getRolePolicyId(), policy.getName());
			response.setRolePolicy(dto);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public RolePolicyResponse getRolePolicy(String rolePolicyId, final String requesterId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        try {
			final RolePolicyEntity entity = roleDataService.getRolePolicy(rolePolicyId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "No Role Policy is found");
			}
			
			final RolePolicy policy = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(policy);
		} catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            LOG.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
		return response;
	}
	
	@Override
	public Response removeRolePolicy(final String rolePolicyId, final String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_ROLE_POLICY.value());
		try {
			roleDataService.removeRolePolicy(rolePolicyId);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        }finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public List<Role> findBeans(final RoleSearchBean searchBean, String requesterId, final int from, final int size) {
        final List<RoleEntity> found = roleDataService.findBeans(searchBean, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(found, false);
	}

	@Override
	@WebMethod
	public int countBeans(final RoleSearchBean searchBean, String requesterId) {
        return roleDataService.countBeans(searchBean, requesterId);
	}

	@Override
	public List<Role> getRolesForResource(final String resourceId, String requesterId, boolean deepFlag,  final int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.getRolesForResource(resourceId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId, String requesterId) {
        return roleDataService.getNumOfRolesForResource(resourceId, requesterId);
	}

	@Override
	public List<Role> getChildRoles(final String roleId, String requesterId, Boolean deepFlag, final  int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.getChildRoles(roleId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	@WebMethod
	public int getNumOfChildRoles(final String roleId, String requesterId) {
        return roleDataService.getNumOfChildRoles(roleId, requesterId);
	}

	@Override
	@WebMethod
	public List<Role> getParentRoles(final String roleId, String requesterId, final int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.getParentRoles(roleId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
	}

	@Override
	@WebMethod
	public int getNumOfParentRoles(final String roleId, String requesterId) {
        return roleDataService.getNumOfParentRoles(roleId, requesterId);
	}

	@Override
	public Response addChildRole(final String roleId, final String childRoleId, String requesterId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_CHILD_ROLE.value());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        RoleEntity roleEntityChild = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(childRoleId, roleEntityChild.getName());
        idmAuditLog.setAuditDescription(String.format("Add child role: %s to role: %s", childRoleId, roleId));
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or child roleId is null");
			}
			roleDataService.validateRole2RoleAddition(roleId, childRoleId);
			roleDataService.addChildRole(roleId, childRoleId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Can't add child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
		}finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}

	@Override
	public Response removeChildRole(final String roleId, final String childRoleId, String requesterId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_CHILD_ROLE.value());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        RoleEntity roleEntityChild = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(childRoleId, roleEntityChild.getName());
        idmAuditLog.setAuditDescription(String.format("Remove child role: %s from role: %s", childRoleId, roleId));
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or child roleId is null");
			}
			final RoleEntity parent = roleDataService.getRole(roleId, null);
			final RoleEntity child = roleDataService.getRole(childRoleId, null);
			if(parent == null || child == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Parent Role or Child Role are not found");
			}
			
			roleDataService.removeChildRole(roleId, childRoleId);
            idmAuditLog.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
		} catch(Throwable e) {
			LOG.error("Can't remove child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        }finally {
            auditLogService.enqueue(idmAuditLog);
        }
		return response;
	}


	@Override
	public int getNumOfRolesForGroup(final String groupId, String requesterId) {
        return roleDataService.getNumOfRolesForGroup(groupId, requesterId);
	}

	@Override
	public Response canAddUserToRole(String userId, String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or UserId  is null");
			}

			if(userDataService.isRoleInUser(userId, roleId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("User %s has already been added to role: %s", userId, roleId));
			}
        } catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
        } catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
        }
		return response;
	}

	@Override
	public Response canRemoveUserFromRole(String userId, String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
     	try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"RoleId or UserId is null");
			}
     	} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
     	} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
        }
		return response;
	}

	@Override
	public Response canAddChildRole(String roleId, String childRoleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			roleDataService.validateRole2RoleAddition(roleId, childRoleId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
        }
		return response;
	}

	@Override
	public Response validateGroup2RoleAddition(String roleId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			roleDataService.validateGroup2RoleAddition(roleId, groupId);
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
        }
		return response;
	}

    @Override
    public List<Role> findRolesByAttributeValue(String attrName, String attrValue) {
        return roleDozerConverter.convertToDTOList(
                roleDataService.findRolesByAttributeValue(attrName, attrValue), true);
    }
}
