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
package org.openiam.srvc.idm;

import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.constants.AccountLockEnum;
import org.openiam.constants.ProvisionActionTypeEnum;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.*;
import org.openiam.provision.service.ProvisioningDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.*;

/**
 * DefaultProvisioningService is responsible for receiving and processing
 * requests that are to be sent to the target system connectors.
 *
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.srvc.idm.ProvisionService", targetNamespace = "http://www.openiam.org/service/provision", portName = "DefaultProvisionControllerServicePort", serviceName = "ProvisioningService")
@Component("defaultProvision")
public class DefaultProvisioningService implements ProvisionService {

    @Autowired
    private ProvisioningDataService provisioningDataService;

    @Override
    public Response testConnectionConfig(String managedSysId) {
        return provisioningDataService.testConnectionConfig(managedSysId);
    }

    @Override
    public ProvisionUserResponse addUser(ProvisionUser user) throws Exception {
        return provisioningDataService.addUser(user);
    }

    @Override
    public ProvisionUserResponse modifyUser(ProvisionUser user) {
        return provisioningDataService.modifyUser(user);
    }

    @Override
    public ProvisionUserResponse deleteUserWithSkipManagedSysList(String managedSystemId, String principal, UserStatusEnum status, List<String> skipManagedSysList) {
        return provisioningDataService.deleteUserWithSkipManagedSysList(managedSystemId, principal, status, skipManagedSysList);
    }

    @Override
    public ProvisionUserResponse deleteUser(String managedSystemId, String principal, UserStatusEnum status) {
        return provisioningDataService.deleteUser(managedSystemId, principal, status);
    }

    @Override
    public ProvisionUserResponse deleteByUserWithSkipManagedSysList(String userId, UserStatusEnum status, List<String> skipManagedSysList) {
        return provisioningDataService.deleteByUserWithSkipManagedSysList(userId, status, skipManagedSysList);
    }

    @Override
    public ProvisionUserResponse deleteByUserId(String userId, UserStatusEnum status) {
        return provisioningDataService.deleteByUserId(userId, status);
    }

    @Override
    public ProvisionUserResponse deprovisionSelectedResources(String userId, List<String> resourceList) {
        return provisioningDataService.deprovisionSelectedResources(userId, resourceList);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResource(List<String> users, List<String> resources) {
        return provisioningDataService.deProvisionUsersToResource(users, resources);
    }

    @Override
    public ProvisionUserResponse provisionUsersToResource(List<String> users, List<String> resources) {
        return provisioningDataService.provisionUsersToResource(users, resources);
    }

    @Override
    public ProvisionUserResponse provisionUsersToResourceByRole(List<String> users, List<String> roles) {
        return provisioningDataService.provisionUsersToResourceByRole(users, roles);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResourceByRole(List<String> users, List<String> roles) {
        return provisioningDataService.deProvisionUsersToResourceByRole(users, roles);
    }

    @Override
    public ProvisionUserResponse provisionUsersToResourceByGroup(List<String> users, List<String> groups) {
        return provisioningDataService.provisionUsersToResourceByGroup(users, groups);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResourceByGroup(List<String> users, List<String> groups) {
        return provisioningDataService.deProvisionUsersToResourceByGroup(users, groups);
    }

    @Override
    public PasswordValidationResponse setPassword(PasswordSync passwordSync) {
        return provisioningDataService.setPassword(passwordSync);
    }

    @Override
    public PasswordResponse resetPassword(PasswordSync passwordSync) {
        return provisioningDataService.resetPassword(passwordSync);
    }

    @Override
    public Response lockUser(String userId, AccountLockEnum operation) {
        return provisioningDataService.lockUser(userId, operation);
    }

    @Override
    public Response disableUser(String userId, boolean operation) {
        return provisioningDataService.disableUser(userId, operation);
    }

    @Override
    public LookupUserResponse getTargetSystemUser(String principalName, String managedSysId, List<ExtensibleAttribute> attributes) {
        return provisioningDataService.getTargetSystemUser(principalName, managedSysId, attributes);
    }

    @Override
    public List<String> getPolicyMapAttributesList(String managedSysId) {
        return provisioningDataService.getPolicyMapAttributesList(managedSysId);
    }

    @Override
    public List<String> getManagedSystemAttributesList(String managedSysId) {
        return provisioningDataService.getManagedSystemAttributesList(managedSysId);
    }

    @Override
    public Response syncPasswordFromSrc(PasswordSync passwordSync) {
        return provisioningDataService.syncPasswordFromSrc(passwordSync);
    }

    @Override
    public Response startBulkOperation(BulkOperationRequest bulkRequest) {
        return provisioningDataService.startBulkOperation(bulkRequest);
    }

    @Override
    public ManagedSystemViewerResponse buildManagedSystemViewer(String userId, String managedSysId) {
        return provisioningDataService.buildManagedSystemViewer(userId, managedSysId);
    }

    @Override
    public Response requestAdd(ExtensibleUser extUser, Login login) {
        return provisioningDataService.requestAdd(extUser, login);
    }

    @Override
    public Response requestModify(ExtensibleUser extUser, Login login) {
        return provisioningDataService.requestModify(extUser, login);
    }

    @Override
    public ObjectResponse requestAddModify(ExtensibleUser extUser, Login login, boolean isAdd, IdmAuditLogEntity idmAuditLog) {
        return provisioningDataService.requestAddModify(extUser, login, isAdd, idmAuditLog);
    }

    @Override
    public Response addEvent(ProvisionActionEvent event, ProvisionActionTypeEnum type) {
        return provisioningDataService.addEvent(event, type);
    }
}