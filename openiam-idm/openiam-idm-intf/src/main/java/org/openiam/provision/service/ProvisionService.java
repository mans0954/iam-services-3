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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.connector.type.request.LookupRequest;

import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.AccountLockEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;

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
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

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
     * from the appropriate target systems
     *
     * @param securityDomain -
     * @param managedSystemId - target system
     * @param principal - identity of the user in target system
     * @param status - status od delete operation
     * @param requestorId - requester
     * @return
     */
    @WebMethod
    public ProvisionUserResponse deleteUser(
            @WebParam(name = "securityDomain", targetNamespace = "") String securityDomain,
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "principal", targetNamespace = "") String principal,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requestorId", targetNamespace = "") String requestorId);

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
     * Return all possible attributes for selected managed system
     *
     * @param managedSysId - managed system
     * @param config - LookupRequest
     * @return  List<String> with attributes
     */
    @WebMethod
    public List<String> getAttributesList(
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
            @WebParam(name = "config", targetNamespace = "") LookupRequest config);

}
