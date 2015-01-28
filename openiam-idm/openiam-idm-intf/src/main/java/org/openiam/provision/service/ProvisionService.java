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
package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.*;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.ManagedSystemViewerResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * <code>ProvisionService</code> Interface for the Provisioning service which is
 * used for provisioning users.
 * 
 * @author suneet
 * 
 */
@WebService(targetNamespace = "http://www.openiam.org/service/provision", name = "ProvisionControllerService")
public interface ProvisionService {

    /**
     * Operation validates the connection information that was supplied for this
     * managed system
     * 
     * @param managedSysId
     * @return
     */
    @WebMethod
    public Response testConnectionConfig(
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     *  The addUser operation enables a requester to create a new user on the
     *  target systems.
     *  Also this operation can do modify if this user has existed in one of the target systems.
     *
     * @param user - new provisioning user
     * @return ProvisionUserResponse
     * @throws Exception
     */
    @WebMethod
    public ProvisionUserResponse addUser(
            @WebParam(name = "user", targetNamespace = "") ProvisionUser user)
            throws Exception;

    /**
     * The modifyUser operation enables the requester to modify an existing user
     * in appropriate target systems
     *
     * @param user - provision user for modify
     * @return ProvisionUserResponse
     */
    @WebMethod
    public ProvisionUserResponse modifyUser (
            @WebParam(name = "user", targetNamespace = "") ProvisionUser user);

    /**
     * The deleteUser operation enables the requester to delete an existing user
     * from the appropriate target systems except 'skipManagedSysList' target systems
     *
     * @param managedSystemId - target system
     * @param principal - identity of the user in target system
     * @param status - status od delete operation
     * @param requesterId - requester
     * @param skipManagedSysList - the operations will not applied for this exception list of target systems
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deleteUserWithSkipManagedSysList(
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "principal", targetNamespace = "") String principal,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
            @WebParam(name = "skipManagedSysList", targetNamespace = "") List<String> skipManagedSysList);
    /**
     * The deleteUser operation enables the requester to delete an existing user
     * from the appropriate target systems
     *
     * @param managedSystemId - target system
     * @param principal - identity of the user in target system
     * @param status - status od delete operation
     * @param requesterId - requester
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deleteUser(
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "principal", targetNamespace = "") String principal,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     *
     * Delete user from target systems  except target systems that 'skipManagedSysList' by user id
     *
     * @param userId - deleted user ID
     * @param status - delete status
     * @param requestorId - requester
     * @param skipManagedSysList - the operations will not applied for this exception list of target systems
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deleteByUserWithSkipManagedSysList(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requestorId", targetNamespace = "") String requestorId,
            @WebParam(name = "skipManagedSysList", targetNamespace = "") List<String> skipManagedSysList);

    /**
     * Delete user from target system  by user id
     *
     * @param userId - deleted user ID
     * @param status - delete status
     * @param requestorId - requestor
     * @return  ProvisionUserResponse
     */
    @WebMethod
    public ProvisionUserResponse deleteByUserId(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requestorId", targetNamespace = "") String requestorId);

    /**
     * De-provisioning User only from selected resources
     *
     * @param userId - user id
     * @param requestorUserId - requestor
     * @param resourceList - selected resources
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deprovisionSelectedResources(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList);

    /**
     * De Provisioning Users from selected resources only
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param resources    - selected resources
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deProvisionUsersToResource(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "resourcesIds", targetNamespace = "") List<String> resources);

    /**
     * Provisioning User only to selected resources
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param resources    - selected resources
     * @return
     */
    @WebMethod
    public ProvisionUserResponse provisionUsersToResource(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "resourcesIds", targetNamespace = "") List<String> resources);

    /**
     * Provisioning User only to selected resources by roles
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param roles    - selected roles
     * @return
     */
    @WebMethod
    public ProvisionUserResponse provisionUsersToResourceByRole(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "rolesIds", targetNamespace = "") List<String> roles);

    /**
     * De Provisioning Users from selected resources by roles
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param roles    - selected roles
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deProvisionUsersToResourceByRole(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "rolesIds", targetNamespace = "") List<String> roles);


    /**
     * Provisioning User only to selected resources by groups
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param groups    - selected groups
     * @return
     */
    @WebMethod
    public ProvisionUserResponse provisionUsersToResourceByGroup(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "groupsIds", targetNamespace = "") List<String> groups);

    /**
     * DeProvisioning User only to selected resources by groups
     *
     * @param users          - users id list
     * @param requestorUserId - requestor
     * @param groups    - selected groups
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deProvisionUsersToResourceByGroup(
            @WebParam(name = "usersIds", targetNamespace = "") List<String> users,
            @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId,
            @WebParam(name = "groupsIds", targetNamespace = "") List<String> groups);

    /**
     * The setPassword operation enables a requestor to specify a new password
     * for an user across target systems
     * 
     * @param passwordSync
     * @return
     */
    @WebMethod
    public PasswordValidationResponse setPassword(
            @WebParam(name = "passwordSync", targetNamespace = "") PasswordSync passwordSync);

    /**
     * Reset password in target systems
     *
     * @param passwordSync
     * @return PasswordResponse
     */
    @WebMethod
    public PasswordResponse resetPassword(
            @WebParam(name = "passwordSync", targetNamespace = "") PasswordSync passwordSync);

    /**
     * Operation locks or unlocks an account. If the operation flag is true,
     * then the user is locked. Otherwise its is unlocked.
     * 
     * @param userId
     * @param operation
     * @return
     */
    @WebMethod
    Response lockUser(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "operation", targetNamespace = "") AccountLockEnum operation,
            @WebParam(name = "requestorId", targetNamespace = "") String requestorId);

    /**
     * Operation disables or un-disables an account. If the operation flag is
     * true, then the user is disabled. Otherwise its is disabled.
     * 
     * @param userId
     * @param operation
     * @return
     */
    @WebMethod
    Response disableUser(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "operation", targetNamespace = "") boolean operation,
            @WebParam(name = "requestor", targetNamespace = "") String requestorId);

    /**
     * Lookup user by principal name in target system
     *
     * @param principalName - login of user for selected target system
     * @param managedSysId  - selected managed system
     * @return  LookupUserResponse
     */
    @WebMethod
    LookupUserResponse getTargetSystemUser(
            @WebParam(name = "principalName", targetNamespace = "") String principalName,
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
            @WebParam(name = "attributes", targetNamespace = "") List<ExtensibleAttribute> attributes);

    /**
     * Return all possible policy map attributes for selected managed system
     *
     * @param managedSysId - managed system
     * @return  List<String> with attributes
     */
    @WebMethod
    public List<String> getPolicyMapAttributesList(
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    /**
     * Return all possible managed system attributes for selected managed system
     *
     * @param managedSysId - managed sys id
     * @return  List<String> with attributes
     */
    @WebMethod
    public List<String> getManagedSystemAttributesList(
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    @WebMethod
    public Response syncPasswordFromSrc(
            @WebParam(name = "passwordSync", targetNamespace = "")
            PasswordSync passwordSync);

    @WebMethod
    public Response startBulkOperation(
            @WebParam(name = "bulkRequest", targetNamespace = "") BulkOperationRequest bulkRequest);

    @WebMethod
    public ManagedSystemViewerResponse buildManagedSystemViewer(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    /**
     * Adds user directly to a given target system bypassing policy map attributes script logic
     * @param extUser
     * @param login
     * @param requestorId
     * @return
     */
    @WebMethod
    public Response requestAdd(@WebParam(name = "extUser", targetNamespace = "") ExtensibleUser extUser,
            @WebParam(name = "login", targetNamespace = "") Login login,
            @WebParam(name = "requestorId", targetNamespace = "") String requestorId);

    /**
     * Modifies user directly in a given target system bypassing policy map attributes script logic
     * @param extUser
     * @param login
     * @param requestorId
     * @return
     */
    @WebMethod
    public Response requestModify(@WebParam(name = "extUser", targetNamespace = "") ExtensibleUser extUser,
                                  @WebParam(name = "login", targetNamespace = "") Login login,
                                  @WebParam(name = "requestorId", targetNamespace = "") String requestorId);

    @WebMethod
    public ObjectResponse requestAddModify(@WebParam(name = "extUser", targetNamespace = "") ExtensibleUser extUser,
            @WebParam(name = "login", targetNamespace = "") Login login,
            @WebParam(name = "isAdd", targetNamespace = "") boolean isAdd,
            @WebParam(name = "requestId", targetNamespace = "") String requestId,
            @WebParam(name = "idmAuditLog", targetNamespace = "") final IdmAuditLog idmAuditLog);

    @WebMethod
    public ExtensibleUser buildExtensibleUser(@WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    @WebMethod
    Response addEvent(@WebParam(name = "event", targetNamespace = "") ProvisionActionEvent event,
                      @WebParam(name = "type", targetNamespace = "") ProvisionActionTypeEnum type);

}
