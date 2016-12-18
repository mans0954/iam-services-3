package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.base.response.ObjectResponse;
import org.openiam.constants.AccountLockEnum;
import org.openiam.constants.ProvisionActionTypeEnum;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.base.response.PasswordValidationResponse;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.*;
import org.openiam.base.response.LookupUserResponse;
import org.openiam.base.response.ManagedSystemViewerResponse;
import org.openiam.base.response.PasswordResponse;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;

import java.util.List;

/**
 * Created by anton on 13.08.15.
 */
public interface ProvisioningDataService {

    public ProvisionUserResponse provisionUsersToResource(final List<String> usersIds, final List<String> resourceList);

    public ProvisionUserResponse deProvisionUsersToResource(List<String> users, List<String> resources);

    public ProvisionUserResponse deProvisionUsersToResourceByRole(List<String> users, List<String> roles);

    public ProvisionUserResponse deProvisionUsersToResourceByGroup(List<String> users, List<String> groups);

    public ProvisionUserResponse provisionUsersToResourceByRole(final List<String> usersIds, final List<String> roleList);

    public ProvisionUserResponse provisionUsersToResourceByGroup(final List<String> usersIds, final List<String> groupList);

    public ProvisionUserResponse addUser(final ProvisionUser pUser);

    public ProvisionUserResponse modifyUser(final ProvisionUser pUser);

    public ProvisionUserResponse deleteByUserWithSkipManagedSysList(String userId, UserStatusEnum status, List<String> skipManagedSysList);

    public ProvisionUserResponse deleteByUserId(String userId, UserStatusEnum status);

    public ProvisionUserResponse deleteUser(String managedSystemId, String principal, UserStatusEnum status);

    public ProvisionUserResponse deleteUserWithSkipManagedSysList(String managedSystemId, String principal, UserStatusEnum status, List<String> skipManagedSysList);

    public ProvisionUserResponse deprovisionSelectedResources(String userId, List<String> resourceList);

    public Response lockUser(String userId, AccountLockEnum operation);

    //public void updateResources(UserEntity userEntity, ProvisionUser pUser, Set<Resource> resourceSet, Set<Resource> deleteResourceSet, IdmAuditLog parentLog);

    public PasswordResponse resetPassword(PasswordSync passwordSync);

    public PasswordResponse resetPassword(PasswordSync passwordSync, IdmAuditLogEntity auditLog);

    public LookupUserResponse getTargetSystemUser(String principalName, String managedSysId, List<ExtensibleAttribute> extensibleAttributes);

    public PasswordValidationResponse setPassword(PasswordSync passwordSync);

    public List<String> getPolicyMapAttributesList(String mSysId);

    public List<String> getManagedSystemAttributesList(String mSysId);

    public Response startBulkOperation(final BulkOperationRequest bulkRequest);

    public Response disableUser(String userId, boolean operation);

    public Response testConnectionConfig(String managedSysId);

    public Response syncPasswordFromSrc(PasswordSync passwordSync);

    public ManagedSystemViewerResponse buildManagedSystemViewer(String userId, String managedSysId);

    public Response requestAdd(ExtensibleUser extUser, Login login);

    public Response requestModify(ExtensibleUser extUser, Login login);

    public Response disableUser(String userId, boolean operation, IdmAuditLogEntity auditLog);

    public Response addEvent(ProvisionActionEvent event, ProvisionActionTypeEnum type);

    public ObjectResponse requestAddModify(ExtensibleUser extUser, Login mLg, boolean isAdd, final IdmAuditLogEntity idmAuditLog);


}
