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

import org.openiam.base.TreeObjectId;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.mq.constants.queue.am.AMQueue;
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

    public RoleDataWebServiceImpl() {
        super(AMQueue.RoleQueue);
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
        return this.manageApiRequest(RoleAPI.GetRoleLocalized, request, RoleGetResponse.class).getRole();
    }

    @Override
    public List<RoleAttribute> getRoleAttributes(String roleId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        return this.manageApiRequest(RoleAPI.GetRoleAttributes, request, RoleAttributeGetResponse.class).getRoleAttributes();
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
        MembershipRequest request = new MembershipRequest();
        request.setRequesterId(requesterId);
        request.setObjectId(roleId);
        request.setLinkedObjectId(groupId);
        request.setRightIds(rightIds);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return this.manageApiRequest(RoleAPI.AddGroupToRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response validateGroup2RoleAddition(String roleId, String groupId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(groupId);
        return this.manageApiRequest(RoleAPI.ValidateGroup2RoleAddition, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response removeGroupFromRole(String roleId, String groupId, String requesterId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(groupId);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.RemoveGroupFromRole, request, BooleanResponse.class).convertToBase();

    }

    @Override
    public Response addUserToRole(String roleId, String userId, String requesterId, Set<String> rightIds, Date startDate, Date endDate) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(userId);
        request.setRightIds(rightIds);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.AddUserToRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response removeUserFromRole(String roleId, String userId, String requesterId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(userId);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.RemoveUserFromRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public List<Role> findBeans(RoleSearchBean searchBean, String requesterId, int from, int size) {
        return this.manageApiRequest(RoleAPI.FindBeans, new BaseSearchServiceRequest<>(searchBean, from, size),
                RoleFindBeansResponse.class).getRoles();
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
        return this.manageApiRequest(RoleAPI.GetParentRoles, request, RoleFindBeansResponse.class).getRoles();
    }

    @Override
    public Response addChildRole(String roleId, String childRoleId, String requesterId, Set<String> rights, Date startDate, Date endDate) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(childRoleId);
        request.setRightIds(rights);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.AddChildRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response canAddChildRole(String roleId, String childRoleId, Set<String> rights, Date startDate, Date endDate) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(childRoleId);
        request.setRightIds(rights);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return this.manageApiRequest(RoleAPI.CanAddChildRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response removeChildRole(String roleId, String childRoleId, String requesterId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(childRoleId);
        return this.manageApiRequest(RoleAPI.RemoveChildRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response canAddUserToRole(String userId, String roleId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(userId);
        return this.manageApiRequest(RoleAPI.CanAddUserToRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public Response canRemoveUserFromRole(String userId, String roleId) {
        MembershipRequest request = new MembershipRequest();
        request.setObjectId(roleId);
        request.setLinkedObjectId(userId);
        return this.manageApiRequest(RoleAPI.CanRemoveUserFromRole, request, BooleanResponse.class).convertToBase();
    }

    @Override
    public List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, String requesterId) {
        IdsServiceRequest request = new IdsServiceRequest();
        request.setIds(roleIds);
        request.setRequesterId(requesterId);
        return this.manageApiRequest(RoleAPI.GetRolesWithSubRolesIds, request, TreeObjectIdListServiceResponse.class).getTreeObjectIds();
    }

    @Override
    public boolean hasChildEntities(String roleId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(roleId);
        return this.manageApiRequest(RoleAPI.HasChildEntities, request, BooleanResponse.class).getValue();
    }
}
