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
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.RoleAttributeDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.RolePolicyDozerConverter;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.base.AbstractBaseService;
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
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
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
    private SecurityDomainDAO securityDomainDAO;


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
		
		if(StringUtils.isBlank(entity.getServiceId())) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ROLE_DOMAIN, "Security Domain for Role is not set");
		}
		
		if(securityDomainDAO.findById(entity.getServiceId()) == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ROLE_DOMAIN, "Security Domain for Role is not found");
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
	public Response addGroupToRole(String roleId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_GROUP_TO_ROLE).setTargetGroup(groupId).setAuditDescription(String.format("Add group to  role: %s", roleId));
		try {
			roleDataService.validateGroup2RoleAddition(roleId, groupId);
			roleDataService.addGroupToRole(roleId, groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response addUserToRole(String roleId, String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_USER_TO_ROLE).setTargetUser(userId).setAuditDescription(String.format("Add user to  role: %s", roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "UserId or RoleId  is null or empty");
			}
			
			roleDataService.addUserToRole(roleId, userId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}  finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Role getRole(String roleId, String requesterId) {
		Role retVal = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            if(roleId != null) {
                final RoleEntity entity = roleDataService.getRole(roleId, requesterId);
                if(entity != null) {
                    retVal = roleDozerConverter.convertToDTO(entity, true);
                }
            }
            auditBuilder.succeed();
        } catch(Throwable e) {
            LOG.error("Exception", e);
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return retVal;
	}

	@Override
	public List<Role> getRolesInGroup(final String groupId, String requesterId, boolean deepFlag, final int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_IN_GROUP).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            final List<RoleEntity> entityList = roleDataService.getRolesInGroup(groupId, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(entityList, deepFlag);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	public List<Role> getRolesForUser(final String userId, final String requesterId, Boolean deepFlag, final int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_FOR_USER).setRequestorUserId(requesterId).setTargetUser(userId);
        try{
            final List<RoleEntity> entityList = roleDataService.getRolesForUser(userId, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(entityList, deepFlag);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	public int getNumOfRolesForUser(final String userId, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_NUM_FOR_USER).setRequestorUserId(requesterId).setTargetUser(userId);
        try{
            count = roleDataService.getNumOfRolesForUser(userId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public Response removeGroupFromRole(String roleId, String groupId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_GROUP_FROM_ROLE).setTargetGroup(groupId).setAuditDescription(String.format("Remove group %s from role: %s", groupId, roleId));
		try {
			if(groupId == null || roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
			}
			
			roleDataService.removeGroupFromRole(roleId, groupId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeRole(String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_ROLE).setTargetRole(roleId);
		try {
			if(roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId  is null or empty");
			}
			
			final RoleEntity entity = roleDataService.getRole(roleId);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, String.format("No Role is found for roleId: %s", roleId));
			}
			 roleDataService.removeRole(roleId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }

		return response;
	}

	@Override
	public Response removeUserFromRole(String roleId, String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_USER_FROM_ROLE).setTargetUser(userId).setAuditDescription(String.format("Remove user %s from role: %s", userId, roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			roleDataService.removeUserFromRole(roleId, userId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	/*
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
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}
	*/

	@Override
	public Response saveRole(Role role, final String requestorId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SAVE_ROLE);
		try {
            auditBuilder.setRequestorUserId(role.getRequestorUserId()).setTargetRole(role.getId());
            if(StringUtils.isBlank(role.getId())) {
                auditBuilder.setAction(AuditAction.ADD_ROLE);
            }
            
            validate(role);
			
			final RoleEntity entity = roleDozerConverter.convertToEntity(role, true);
			roleDataService.saveRole(entity, requestorId);
			response.setResponseValue(entity.getId());
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public RolePolicyResponse addRolePolicy(final RolePolicy policy) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_ROLE_POLICY).setTargetRole(policy.getRoleId());
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"Role policy object is null");
			}
			
			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(dto);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		} finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public RolePolicyResponse updateRolePolicy(final RolePolicy policy) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.UPDATE_ROLE_POLICY).setTargetRole(policy.getRoleId());
		try {
			if(policy == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"Role policy object is null");
			}
			
			final RolePolicyEntity entity = rolePolicyDozerConverter.convertToEntity(policy, false);
			roleDataService.savePolicy(entity);
			final RolePolicy dto = rolePolicyDozerConverter.convertToDTO(entity, false);
			response.setRolePolicy(dto);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public RolePolicyResponse getRolePolicy(String rolePolicyId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_POLICY);
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
            auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
        } catch(Throwable e) {
            LOG.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}
	
	@Override
	public Response removeRolePolicy(final String rolePolicyId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_ROLE_POLICY);
		try {
			roleDataService.removeRolePolicy(rolePolicyId);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public List<Role> findBeans(final RoleSearchBean searchBean, String requesterId, final int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.SEARCH_ROLE).setRequestorUserId(requesterId);
        try{
            final List<RoleEntity> found = roleDataService.findBeans(searchBean, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(found, (searchBean.isDeepCopy()));
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	@WebMethod
	public int countBeans(final RoleSearchBean searchBean, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_NUM).setRequestorUserId(requesterId);
        try{
            count = roleDataService.countBeans(searchBean, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Role> getRolesForResource(final String resourceId, String requesterId, boolean deepFlag,  final int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_FOR_RESOURCE).setRequestorUserId(requesterId).setTargetResource(resourceId);
        try{
            final List<RoleEntity> entityList = roleDataService.getRolesForResource(resourceId, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(entityList, deepFlag);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_NUM_FOR_RESOURCE).setRequestorUserId(requesterId).setTargetResource(resourceId);
        try{
            count = roleDataService.getNumOfRolesForResource(resourceId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public List<Role> getChildRoles(final String roleId, String requesterId, Boolean deepFlag, final  int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_ROLE).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            final List<RoleEntity> entityList = roleDataService.getChildRoles(roleId, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(entityList, (deepFlag!=null)?deepFlag:false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	@WebMethod
	public int getNumOfChildRoles(final String roleId, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_CHILD_ROLE_NUM).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            count = roleDataService.getNumOfChildRoles(roleId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	@WebMethod
	public List<Role> getParentRoles(final String roleId, String requesterId, final int from, final int size) {
        List<Role> roleList = null;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_ROLE).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            final List<RoleEntity> entityList = roleDataService.getParentRoles(roleId, requesterId, from, size);
            roleList = roleDozerConverter.convertToDTOList(entityList, false);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return  roleList;
	}

	@Override
	@WebMethod
	public int getNumOfParentRoles(final String roleId, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_PARENT_ROLE_NUM).setRequestorUserId(requesterId).setTargetRole(roleId);
        try{
            count = roleDataService.getNumOfParentRoles(roleId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public Response addChildRole(final String roleId, final String childRoleId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.ADD_CHILD_ROLE).setTargetRole(roleId).setAuditDescription(String.format("Add child role: %s to role: %s", childRoleId, roleId));
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or child roleId is null");
			}
			roleDataService.validateRole2RoleAddition(roleId, childRoleId);
			roleDataService.addChildRole(roleId, childRoleId);
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Can't add child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
		}finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response removeChildRole(final String roleId, final String childRoleId) {
		final RolePolicyResponse response = new RolePolicyResponse(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.REMOVE_CHILD_ROLE).setTargetRole(roleId).setAuditDescription(String.format("Remove child role: %s from role: %s", childRoleId, roleId));
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
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Can't remove child role", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}


	@Override
	public int getNumOfRolesForGroup(final String groupId, String requesterId) {
        int count =0;
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.GET_ROLE_NUM_FOR_GROUP).setRequestorUserId(requesterId).setTargetGroup(groupId);
        try{
            count = roleDataService.getNumOfRolesForGroup(groupId, requesterId);
            auditBuilder.succeed();
        } catch(Throwable e) {
            auditBuilder.fail().setException(e);
        }finally {
            auditLogService.enqueue(auditBuilder);
        }
        return count;
	}

	@Override
	public Response canAddUserToRole(String userId, String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_ADD_USER_TO_ROLE).setTargetUser(userId).setAuditDescription(String.format("Check if user can be added to role: %s", roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or UserId  is null");
			}

			if(userDataService.isRoleInUser(userId, roleId)) {
				throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("User %s has already been added to role: %s", userId, roleId));
			}
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
		return response;
	}

	@Override
	public Response canRemoveUserFromRole(String userId, String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setAction(AuditAction.CAN_REMOVE_USER_FROM_ROLE).setTargetUser(userId).setAuditDescription(String.format("Check if user can be removed from role: %s", roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,"RoleId or UserId is null");
			}
            auditBuilder.succeed();
		} catch(BasicDataServiceException e) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
			auditBuilder.fail().setFailureReason(e.getCode()).setException(e);
		} catch(Throwable e) {
			LOG.error("Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
            auditBuilder.fail().setException(e);
        } finally {
            auditLogService.enqueue(auditBuilder);
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
}
