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

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeObjectId;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	private static final Log LOG = LogFactory.getLog(RoleDataWebServiceImpl.class);
	
	@Autowired
    private RoleDataService roleDataService;
    @Autowired
    private UserDataService userDataService;
    
    @Autowired
    private RoleDozerConverter roleDozerConverter;
    
    @Autowired
    private GroupDataService groupService;

    @Autowired
    protected SysConfiguration sysConfiguration;
    
    @Autowired
    private LanguageDozerConverter languageConverter;
    
    @Autowired
    private AccessRightProcessor accessRightProcessor;

    @PostConstruct
    public void dataPreparation(){
        roleDataService.rebuildRoleHierarchyCache();
    }

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
		final RoleEntity nameEntity = roleDataService.getRoleByNameAndManagedSysId(role.getName(), role.getManagedSysId());
		if(nameEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
				throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Role Name + Managed Sys combination taken");
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
    public List<RoleAttribute> getRoleAttributes(String roleId) {
        return roleDataService.getRoleAttributes(roleId);
    }

    @Override
	public Response addGroupToRole(final String roleId, 
								   final String groupId, 
								   final String requesterId, 
								   final Set<String> rightIds,
								   final Date startDate,
								   final Date endDate) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_GROUP_TO_ROLE.value());
        GroupEntity groupEntity = groupService.getGroup(groupId);
        idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
        RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Add group to  role: %s", roleId));
		try {
			if(startDate != null && endDate != null && startDate.after(endDate)) {
            	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }
			
			roleDataService.validateGroup2RoleAddition(roleId, groupId);
			roleDataService.addGroupToRole(roleId, groupId, rightIds, startDate, endDate);
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
	public Response addUserToRole(final String roleId, 
								  final String userId, 
								  final String requesterId, 
								  final Set<String> rightIds,
								  final Date startDate,
								  final Date endDate) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        final IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.ADD_USER_TO_ROLE.value());
        final UserEntity user = userDataService.getUser(userId);
        final LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        final RoleEntity roleEntity = roleDataService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAuditDescription(String.format("Add user to  role: %s", roleId));
		try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "UserId or RoleId  is null or empty");
			}
			
			if(startDate != null && endDate != null && startDate.after(endDate)) {
            	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }
			
			roleDataService.addUserToRole(roleId, userId, rightIds, startDate, endDate);
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
	@LocalizedServiceGet
	@Transactional(readOnly=true)
	public Role getRoleLocalized(final String roleId, final String requesterId, final Language language) {
		Role retVal = null;
		 if (StringUtils.isNotBlank(roleId)) {
			 final RoleEntity entity = roleDataService.getRoleLocalized(roleId, requesterId, languageConverter.convertToEntity(language, false));
			 retVal = roleDozerConverter.convertToDTO(entity, true);
		 }
		 return retVal;
	}

	@Override
	@Deprecated
	public List<Role> getRolesInGroup(final String groupId, String requesterId, boolean deepFlag, final int from, final int size) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addGroupId(groupId);
		sb.setDeepCopy(deepFlag);
		return findBeans(sb, requesterId, from, size);
	}

	@Override
	@Deprecated
	public List<Role> getRolesForUser(final String userId, final String requesterId, Boolean deepFlag, final int from, final int size) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addUserId(userId);
		sb.setDeepCopy(deepFlag);
		return findBeans(sb, requesterId, from, size);
	}

	@Override
	@Deprecated
	public int getNumOfRolesForUser(final String userId, String requesterId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addUserId(userId);
		return countBeans(sb, requesterId);
	}

	@Override
	public Response removeGroupFromRole(String roleId, String groupId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
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
		try {
			if(roleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId  is null or empty");
			}
			
			final RoleEntity entity = roleDataService.getRoleLocalized(roleId, requesterId, null);
			if(entity == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, String.format("No Role is found for roleId: %s", roleId));
			}
			roleDataService.removeRole(roleId);
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
	public Response removeUserFromRole(String roleId, String userId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.REMOVE_USER_FROM_ROLE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
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
		try {
            validate(role);
			final RoleEntity entity = roleDozerConverter.convertToEntity(role, true);
			roleDataService.saveRole(entity, requesterId);
            response.setResponseValue(entity.getId());
		} catch(BasicDataServiceException e) {
			LOG.warn(String.format("Could not save role", e));
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
	public List<Role> findBeans(final RoleSearchBean searchBean, String requesterId, final int from, final int size) {
        final List<RoleEntity> entityList = roleDataService.findBeans(searchBean, requesterId, from, size);
        final List<Role> dtoList = roleDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
        accessRightProcessor.process(searchBean, dtoList, entityList);
        return dtoList;
	}

	@Override
	@WebMethod
	public int countBeans(final RoleSearchBean searchBean, String requesterId) {
        return roleDataService.countBeans(searchBean, requesterId);
	}

	@Override
	@Deprecated
	public List<Role> getRolesForResource(final String resourceId, String requesterId, boolean deepFlag,  final int from, final int size) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addResourceId(resourceId);
		sb.setDeepCopy(deepFlag);
		return findBeans(sb, requesterId, from, size);
	}

	@Override
	@Deprecated
	public int getNumOfRolesForResource(final String resourceId, String requesterId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addResourceId(resourceId);
		return countBeans(sb, requesterId);
	}

	@Override
	@Deprecated
	public List<Role> getChildRoles(final String roleId, String requesterId, Boolean deepFlag, final  int from, final int size) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addParentId(roleId);
		sb.setDeepCopy(deepFlag);
		return findBeans(sb, requesterId, from, size);
	}

	@Override
	@Deprecated
	public int getNumOfChildRoles(final String roleId, String requesterId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addParentId(roleId);
		return countBeans(sb, requesterId);
	}

	@Override
	@WebMethod
	@Deprecated
	public List<Role> getParentRoles(final String roleId, String requesterId, final int from, final int size) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addChildId(roleId);
		sb.setDeepCopy(false);
		return findBeans(sb, requesterId, from, size);
	}

	@Override
	@WebMethod
	@Deprecated
	public int getNumOfParentRoles(final String roleId, String requesterId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addChildId(roleId);
		return countBeans(sb, requesterId);
	}

	@Override
	public Response addChildRole(final String roleId, 
								final String childRoleId, 
								final String requesterId, 
								final Set<String> rights,
								final Date startDate,
								final Date endDate) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(roleId == null || childRoleId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or child roleId is null");
			}
			if(startDate != null && endDate != null && startDate.after(endDate)) {
            	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }
			
			roleDataService.validateRole2RoleAddition(roleId, childRoleId, rights, startDate, endDate);
			roleDataService.addChildRole(roleId, childRoleId, rights, startDate, endDate);
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

	@Override
	public Response removeChildRole(final String roleId, final String childRoleId, String requesterId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
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


	@Override
	@Deprecated
	public int getNumOfRolesForGroup(final String groupId, String requesterId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.addGroupId(groupId);
		return countBeans(sb, requesterId);
	}

	@Override
	public Response canAddUserToRole(String userId, String roleId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
			if(roleId == null || userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or UserId  is null");
			}

			//if(userDataService.isRoleInUser(userId, roleId)) {
			//	throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("User %s has already been added to role: %s", userId, roleId));
			//}
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
	public Response canAddChildRole(String roleId, String childRoleId, final Set<String> rights, final Date startDate, final Date endDate) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			roleDataService.validateRole2RoleAddition(roleId, childRoleId, rights, startDate, endDate);
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
    @Transactional(readOnly = true)
    public List<Role> findRolesByAttributeValue(String attrName, String attrValue) {
        return roleDozerConverter.convertToDTOList(
                roleDataService.findRolesByAttributeValue(attrName, attrValue), true);
    }

    @Override
    public List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, String requesterId) {
        return roleDataService.getRolesWithSubRolesIds(roleIds, requesterId);
    }

	@Override
	public boolean hasChildEntities(String roleId) {
		return roleDataService.hasChildEntities(roleId);
	}
}
