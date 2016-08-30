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
package org.openiam.srvc.am;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.TreeObjectId;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.RoleAPI;
import org.openiam.srvc.AbstractApiService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.srvc.am.RoleDataWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/role/service",
        portName = "RoleDataWebServicePort",
        serviceName = "RoleDataWebService")
@Service("roleWS")
public class RoleDataWebServiceImpl extends AbstractApiService implements RoleDataWebService {

    private static final Log LOG = LogFactory.getLog(RoleDataWebServiceImpl.class);

    protected RoleDataWebServiceImpl() {
        super(OpenIAMQueue.RoleQueue);
    }

    @Override
    public Response validateEdit(Role role) {
        RoleRequest request = new RoleRequest();
        request.setRole(role);
        return this.manageApiRequest(RoleAPI.ValidateEdit, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response validateDelete(String roleId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        return this.manageApiRequest(RoleAPI.ValidateDelete, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Role getRoleLocalized(String roleId, String requesterId, Language language) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        request.setLanguage(language);
        request.setRequesterId(requesterId);
        RoleGetResponse response = this.manageApiRequest(RoleAPI.GetRoleLocalized, request, RoleGetResponse.class);
        if (response.isSuccess()) {
            return response.getRole();
        } else {
            return null;
        }
    }

    @Override
    public List<RoleAttribute> getRoleAttributes(String roleId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        RoleAttributeGetResponse response = this.manageApiRequest(RoleAPI.GetRoleAttributes, request, RoleAttributeGetResponse.class);
        if (response.isSuccess()) {
            return response.getRoleAttributes();
        } else {
            return null;
        }
    }

    @Override
    public Response saveRole(Role role, String requesterId) {
        RoleRequest request = new RoleRequest();
        request.setRole(role);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.SaveRole, request, Response.class);
    }

    @Override
    public Response removeRole(String roleId, String requesterId) {

        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.RemoveRole, request, Response.class);
    }

    @Override
    public Role getRole(String roleId, String requesterId) {
        return this.getRoleLocalized(roleId, requesterId, null);
    }

    @Override
    public Response addGroupToRole(String roleId, String groupId, String requesterId, Set<String> rightIds, Date startDate, Date endDate) {
        EntitleToRoleRequest request = new EntitleToRoleRequest();
        request.setRequesterId(requesterId);
        request.setRoleId(roleId);
        request.setLinkedObjectId(groupId);
        request.setRightIds(rightIds);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return this.manageApiRequest(RoleAPI.AddGroupToRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response validateGroup2RoleAddition(String roleId, String groupId) {
        EntitleToRoleRequest request = new EntitleToRoleRequest();
        request.setRoleId(roleId);
        request.setLinkedObjectId(groupId);
        return this.manageApiRequest(RoleAPI.ValidateGroup2RoleAddition, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response removeGroupFromRole(String roleId, String groupId, String requesterId) {
        EntitleToRoleRequest request = new EntitleToRoleRequest();
        request.setRoleId(roleId);
        request.setLinkedObjectId(groupId);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.RemoveGroupFromRole, request, BooleanResponse.class).convertToBase();

    }

    @Override
    public Response addUserToRole(String roleId, String userId, String requesterId, Set<String> rightIds, Date startDate, Date endDate) {
        EntitleToRoleRequest request = new EntitleToRoleRequest();
        request.setRoleId(roleId);
        request.setLinkedObjectId(userId);
        request.setRightIds(rightIds);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.AddUserToRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response removeUserFromRole(String roleId, String userId, String requesterId) {
        EntitleToRoleRequest request = new EntitleToRoleRequest();
        request.setRoleId(roleId);
        request.setLinkedObjectId(userId);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.RemoveUserFromRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public List<Role> findBeans(RoleSearchBean searchBean, String requesterId, int from, int size) {
        BaseSearchServiceRequest<RoleSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        RoleFindBeansResponse response = this.manageApiRequest(RoleAPI.FindBeans, request, RoleFindBeansResponse.class);
        if (response.isSuccess()) {
            return response.getRoles();
        } else {
            return null;
        }
    }

    @Override
    public int countBeans(RoleSearchBean searchBean, String requesterId) {
        BaseSearchServiceRequest<RoleSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        CountResponse response = this.manageApiRequest(RoleAPI.CountBeans, request, CountResponse.class);
        return response.getRowCount();
    }

    @Override
    public List<Role> getParentRoles(String roleId, String requesterId, int from, int size) {
        GetParentsRequest request = new GetParentsRequest();
        request.setId(roleId);
        request.setRequesterId(requesterId);
        request.setFrom(from);
        request.setSize(size);
        RoleFindBeansResponse response = this.manageApiRequest(RoleAPI.GetParentRoles, request, RoleFindBeansResponse.class);
        if (response.isSuccess()) {
            return response.getRoles();
        } else {
            return null;
        }


    }

    @Override
    public Response addChildRole(String roleId, String childRoleId, String requesterId, Set<String> rights, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public Response canAddChildRole(String roleId, String childRoleId, Set<String> rights, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public Response removeChildRole(String roleId, String childRoleId, String requesterId) {
        return null;
    }

    @Override
    public Response canAddUserToRole(String userId, String roleId) {
        return null;
    }

    @Override
    public Response canRemoveUserFromRole(String userId, String roleId) {
        return null;
    }

    @Override
    public List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, String requesterId) {
        return null;
    }

    @Override
    public boolean hasChildEntities(String roleId) {
        return false;
    }

//    @Autowired
//    private RoleDataService roleDataService;
//    @Autowired
//    private UserDataService userDataService;
//
//
//    @Autowired
//    private GroupDataService groupService;
//
//    @Autowired
//    protected SysConfiguration sysConfiguration;
//
//    @Autowired
//    private LanguageDozerConverter languageConverter;
//
//
//    @PostConstruct
//    public void dataPreparation() {
//        roleDataService.rebuildRoleHierarchyCache();
//    }
//

//
//    @Override
//    public Response validateDelete(String roleId) {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            validateDeleteInternal(roleId);
//        } catch (BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//            response.setErrorTokenList(e.getErrorTokenList());
//        } catch (Throwable e) {
//            LOG.error("Exception", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    private void validate(final Role role) throws BasicDataServiceException {
//        if (role == null) {
//            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Role object is null");
//        }
//
//        final RoleEntity entity = roleDozerConverter.convertToEntity(role, true);
//        if (StringUtils.isBlank(entity.getName())) {
//            throw new BasicDataServiceException(ResponseCode.NO_NAME, "Role Name is null or empty");
//        }
//
//		/* check if the name is taken by another entity */
//        final RoleEntity nameEntity = roleDataService.getRoleByNameAndManagedSysId(role.getName(), role.getManagedSysId());
//        if (nameEntity != null) {
//            if (StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
//                throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Role Name + Managed Sys combination taken");
//            }
//        }
//
//        entityValidator.isValid(entity);
//    }
//
//    public void validateDeleteInternal(final String roleId) throws BasicDataServiceException {
//        if (roleId == null) {
//            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId  is null or empty");
//        }
//
//        final RoleEntity entity = roleDataService.getRole(roleId);
//        if (entity == null) {
//            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, String.format("No Role is found for roleId: %s", roleId));
//        }
//    }
//
//    @Override
//    public List<RoleAttribute> getRoleAttributes(String roleId) {
//        return roleDataService.getRoleAttributes(roleId);
//    }
//
//    @Override
//    public Response addGroupToRole(final String roleId,
//                                   final String groupId,
//                                   final String requesterId,
//                                   final Set<String> rightIds,
//                                   final Date startDate,
//                                   final Date endDate) {
//        return roleDataService.addGroupToRole(roleId, groupId, requesterId, rightIds, startDate, endDate);
//    }
//
//    @Override
//    public Response addUserToRole(final String roleId,
//                                  final String userId,
//                                  final String requesterId,
//                                  final Set<String> rightIds,
//                                  final Date startDate,
//                                  final Date endDate) {
//        return roleDataService.addUserToRole(roleId, userId, requesterId, rightIds, startDate, endDate);
//    }
//
//    @Override
//    //@LocalizedServiceGet
//    //@Transactional(readOnly=true)
//    public Role getRoleLocalized(final String roleId, final String requesterId, final Language language) {
//        Role retVal = null;
//        if (StringUtils.isNotBlank(roleId)) {
//            final RoleEntity entity = roleDataService.getRoleLocalized(roleId, requesterId, languageConverter.convertToEntity(language, false));
//            retVal = roleDozerConverter.convertToDTO(entity, true);
//        }
//        return retVal;
//    }
//
//    @Override
//    public Role getRole(String roleId, String requesterId) {
//        Role retVal = null;
//        if (StringUtils.isNotBlank(roleId)) {
//            retVal = roleDataService.getRoleDTO(roleId);
//        }
//        return retVal;
//    }
//
//    @Override
//    @Deprecated
//    public List<Role> getRolesInGroup(final String groupId, String requesterId, boolean deepFlag, final int from, final int size) {
//        final RoleSearchBean sb = new RoleSearchBean();
//        sb.addGroupId(groupId);
//        sb.setDeepCopy(deepFlag);
//        return findBeans(sb, requesterId, from, size);
//    }
//
//    @Override
//    @Deprecated
//    public List<Role> getRolesForUser(final String userId, final String requesterId, Boolean deepFlag, final int from, final int size) {
//        final RoleSearchBean sb = new RoleSearchBean();
//        sb.addUserId(userId);
//        sb.setDeepCopy(deepFlag);
//        return findBeans(sb, requesterId, from, size);
//    }
//
//    @Override
//    @Deprecated
//    public int getNumOfRolesForUser(final String userId, String requesterId) {
//        final RoleSearchBean sb = new RoleSearchBean();
//        sb.addUserId(userId);
//        return countBeans(sb, requesterId);
//    }
//
//    @Override
//    public Response removeGroupFromRole(String roleId, String groupId, String requesterId) {
//        return roleDataService.removeGroupFromRole(roleId, groupId, requesterId);
//    }
//
//    @Override
//    public Response removeRole(String roleId, String requesterId) {
//        return roleDataService.removeRole(roleId, requesterId);
//    }
//
//    @Override
//    public Response removeUserFromRole(String roleId, String userId, String requesterId) {
//        return roleDataService.removeUserFromRole(roleId, userId, requesterId);
//    }
//
//    @Override
//    public Response saveRole(Role role, final String requesterId) {
//        return roleDataService.saveRole(role, requesterId);
//    }
//
///*	@Override
//	public List<Role> findBeans(final RoleSearchBean searchBean, String requesterId, final int from, final int size) {
//		final List<RoleEntity> entityList = roleDataService.findBeans(searchBean, requesterId, from, size);
//        final List<Role> dtoList = roleDataService.findBeansDto(searchBean, requesterId, from, size);
//        accessRightProcessor.process(searchBean, dtoList, entityList);
//        return dtoList;
//	}*/
//
//    @Override
//    public List<Role> findBeans(final RoleSearchBean searchBean, String requesterId, final int from, final int size) {
//        final List<RoleEntity> entityList = roleDataService.findBeans(searchBean, requesterId, from, size);
//        final List<Role> dtoList = roleDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
//        accessRightProcessor.process(searchBean, dtoList, entityList);
//        return dtoList;
//    }
//
//    @Override
//    @WebMethod
//    public int countBeans(final RoleSearchBean searchBean, String requesterId) {
//        return roleDataService.countBeans(searchBean, requesterId);
//    }
//
//    @Override
//    @Deprecated
//    public List<Role> getRolesForResource(final String resourceId, String requesterId, boolean deepFlag, final int from, final int size) {
//        return roleDataService.getRolesDtoForResource(resourceId, requesterId, from, size);
//    }
//
//    @Override
//    @Deprecated
//    public int getNumOfRolesForResource(final String resourceId, String requesterId) {
//        return roleDataService.getNumOfRolesForResource(resourceId, requesterId);
//    }
//
//    @Override
//    @Deprecated
//    public List<Role> getChildRoles(final String roleId, String requesterId, Boolean deepFlag, final int from, final int size) {
//        return roleDataService.getChildRolesDto(roleId, requesterId, from, size);
//    }
//
//    @Override
//    @Deprecated
//    public int getNumOfChildRoles(final String roleId, String requesterId) {
//        return roleDataService.getNumOfChildRoles(roleId, requesterId);
//    }
//
//    @Override
//    @WebMethod
//    @Deprecated
//    public List<Role> getParentRoles(final String roleId, String requesterId, final int from, final int size) {
//        return roleDataService.getParentRolesDto(roleId, requesterId, from, size);
//    }
//
//    @Override
//    @WebMethod
//    @Deprecated
//    public int getNumOfParentRoles(final String roleId, String requesterId) {
//        return roleDataService.getNumOfParentRoles(roleId, requesterId);
//    }
//
//    @Override
//    public Response addChildRole(final String roleId,
//                                 final String childRoleId,
//                                 final String requesterId,
//                                 final Set<String> rights,
//                                 final Date startDate,
//                                 final Date endDate) {
//        return roleDataService.addChildRole(roleId, childRoleId, requesterId, rights, startDate, endDate);
//    }
//
//    @Override
//    public Response removeChildRole(final String roleId, final String childRoleId, String requesterId) {
//        return roleDataService.removeChildRole(roleId, childRoleId, requesterId);
//    }
//
//
//    @Override
//    @Deprecated
//    public int getNumOfRolesForGroup(final String groupId, String requesterId) {
//        return roleDataService.getNumOfRolesForGroup(groupId, requesterId);
//    }
//
//    @Override
//    public Response canAddUserToRole(String userId, String roleId) {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if (roleId == null || userId == null) {
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or UserId  is null");
//            }
//
//            //if(userDataService.isRoleInUser(userId, roleId)) {
//            //	throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("User %s has already been added to role: %s", userId, roleId));
//            //}
//        } catch (BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch (Throwable e) {
//            LOG.error("Exception", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response canRemoveUserFromRole(String userId, String roleId) {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if (roleId == null || userId == null) {
//                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or UserId is null");
//            }
//        } catch (BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch (Throwable e) {
//            LOG.error("Exception", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response canAddChildRole(String roleId, String childRoleId, final Set<String> rights, final Date startDate, final Date endDate) {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            roleDataService.validateRole2RoleAddition(roleId, childRoleId, rights, startDate, endDate);
//        } catch (BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch (Throwable e) {
//            LOG.error("Exception", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
//    @Override
//    public Response validateGroup2RoleAddition(String roleId, String groupId) {
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            roleDataService.validateGroup2RoleAddition(roleId, groupId);
//        } catch (BasicDataServiceException e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch (Throwable e) {
//            LOG.error("Exception", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
//    }
//
///*    @Override
//    //@Transactional(readOnly = true)
//    public List<Role> findRolesByAttributeValue(String attrName, String attrValue) {
//        return roleDataService.findRolesDtoByAttributeValue(attrName, attrValue);
//    }*/
//
//    @Override
//    public List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, String requesterId) {
//        return roleDataService.getRolesWithSubRolesIds(roleIds, requesterId);
//    }
//
//    @Override
//    public boolean hasChildEntities(String roleId) {
//        return roleDataService.hasChildEntities(roleId);
//    }
}
