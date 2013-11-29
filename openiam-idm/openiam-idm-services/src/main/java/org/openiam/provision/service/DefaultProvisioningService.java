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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.*;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.AccountLockEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jws.WebService;
import java.util.*;

/**
 * DefaultProvisioningService is responsible for receiving and processing
 * requests that are to be sent to the target system connectors.
 *
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.provision.service.ProvisionService", targetNamespace = "http://www.openiam.org/service/provision", portName = "DefaultProvisionControllerServicePort", serviceName = "ProvisioningService")
@Component("defaultProvision")
public class DefaultProvisioningService extends AbstractProvisioningService {

    @Autowired
    private DeprovisionSelectedResourceHelper deprovisionSelectedResource;

    @Autowired
    @Qualifier("disableUser")
    private DisableUserDelegate disableUser;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    private static final Log log = LogFactory
            .getLog(DefaultProvisioningService.class);

    public Response testConnectionConfig(String managedSysId) {
        final AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        AuditLogBuilder auditBuilderTestChild = new AuditLogBuilder();
        auditBuilderTestChild.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_TEST);
        try{
            Response response = validateConnectionConfig.testConnection(managedSysId,
                    MuleContextProvider.getCtx());
            if(response != null && response.isSuccess()) {
                auditBuilderTestChild.succeed().setAuditDescription("Managed system ID: "+managedSysId);
            } else {
                auditBuilderTestChild.fail().setFailureReason("Managed system ID: "+managedSysId);
            }
            auditBuilder.addChild(auditBuilderTestChild);
            return response;
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#addUser(org.openiam.provision
     * .dto.ProvisionUser)
     */
    @Override
    public ProvisionUserResponse addUser(final ProvisionUser pUser) {
        final List<ProvisionDataContainer> dataList = new LinkedList<ProvisionDataContainer>();
        final AuditLogBuilder auditBuilder=auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        ProvisionUserResponse res = new ProvisionUserResponse();
        res.setStatus(ResponseStatus.FAILURE);
         try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            res = transactionTemplate.execute(new TransactionCallback<ProvisionUserResponse>() {
                @Override
                public ProvisionUserResponse doInTransaction(TransactionStatus status) {
                    auditBuilder.setAuditDescription("Provisioning add new user");
                    AuditLogBuilder auditBuilderAddChild = new AuditLogBuilder();
                    auditBuilderAddChild.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_ADD);
                    ProvisionUserResponse tmpRes = addModifyUser(pUser, true, dataList, auditBuilderAddChild);
                    auditBuilder.addChild(auditBuilderAddChild);
                    return tmpRes;
                }
            });

            if (res.isSuccess()) {
                auditBuilder.succeed().setAuditDescription("User: " + res.getUser().getLogin());
                provQueueService.enqueue(dataList);
            } else {
                auditBuilder.fail().setFailureReason(res.getErrorCode()).setFailureReason(res.getErrorText());
            }
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#modifyUser(org.openiam
     * .provision.dto.ProvisionUser)
     */
    @Override
    public ProvisionUserResponse modifyUser(final ProvisionUser pUser) {
        final List<ProvisionDataContainer> dataList = new LinkedList<ProvisionDataContainer>();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        final AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        ProvisionUserResponse res = new ProvisionUserResponse();
        res.setStatus(ResponseStatus.SUCCESS);
        try {
            res = transactionTemplate.execute(new TransactionCallback<ProvisionUserResponse>() {
                @Override
                public ProvisionUserResponse doInTransaction(TransactionStatus status) {
                    auditBuilder.setAuditDescription("Provisioning modify user: " + pUser.getUserId());
                    AuditLogBuilder auditBuilderModifyChild = new AuditLogBuilder();
                    auditBuilderModifyChild.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_MODIFY);
                    ProvisionUserResponse tmpRes = addModifyUser(pUser, false, dataList, auditBuilderModifyChild);
                    auditBuilder.addChild(auditBuilderModifyChild);
                    return tmpRes;
                }
            });

            if (res.isSuccess()) {
                provQueueService.enqueue(dataList);

            } else {
                auditBuilder.fail().setFailureReason(res.getErrorCode()).setFailureReason(res.getErrorText());
            }
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
        return res;
    }
    /**
     * Determine when we are going to provision a user
     *
     * @param user
     * @return
     */
    private boolean provisionUserNow(ProvisionUser user) {

        // if no start is provided then we can assume that we want to provision
        // the user now. There is no future provisioning date
        // if a date is provided and its in the future, then provision it later

        Date curDate = new Date(System.currentTimeMillis());
        Date startDate = user.getStartDate();

        if (startDate == null) {
            // no startDate specified = assume that we can provision now
            return true;
        }

        if (!user.isProvisionOnStartDate()) {
            return true;
        }

        return !curDate.before(startDate);

    }

    @Override
    @Transactional
    public ProvisionUserResponse deleteByUserId(String userId,
            UserStatusEnum status, String requestorId) {

        log.debug("----deleteByUserId called.------");
        final AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        try {
            List<LoginEntity> loginEntityList = loginManager.getLoginByUser(userId);
            LoginEntity primaryIdentity = getPrimaryIdentity(
                    this.sysConfiguration.getDefaultManagedSysId(),
                    loginDozerConverter.convertToEntityList(loginDozerConverter.convertToDTOList(loginEntityList,false), false));
            AuditLogBuilder auditBuilderAddChild = new AuditLogBuilder();
            auditBuilderAddChild.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_DELETE);
            auditBuilder.addChild(auditBuilderAddChild);

            ProvisionUserResponse response = deleteUser(primaryIdentity.getDomainId(), sysConfiguration
                    .getDefaultManagedSysId(), primaryIdentity.getLogin(),status,requestorId);
            if(response != null && response.isSuccess()) {
                auditBuilder.succeed().setAuditDescription("User primary identity: "+primaryIdentity.getLogin());
            } else {
                auditBuilder.fail().setFailureReason(response.getErrorText());
            }
            return response;
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#deleteUser(java.lang.String
     * , java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ProvisionUserResponse deleteUser(String securityDomain,
            String managedSystemId, String principal, UserStatusEnum status,
            String requestorId) {
        log.debug("----deleteUser called.------");



        ProvisionUserResponse response = new ProvisionUserResponse(
                ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        if (status != UserStatusEnum.DELETED
                && status != UserStatusEnum.REMOVE
                && status != UserStatusEnum.LEAVE
                && status != UserStatusEnum.TERMINATE
                && status != UserStatusEnum.RETIRED) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            return response;
        }

        String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        final LoginEntity login = loginManager.getLoginByManagedSys(
                securityDomain, principal, managedSystemId);
        if (login == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        String userId = login.getUserId();
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        ProvisionUser pUser = new ProvisionUser(usr);
        // SET PRE ATTRIBUTES FOR DEFAULT SYS SCRIPT
        bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSystemId);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
        bindingMap.put(TARGET_SYS_RES_ID, null);
        bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

        if (callPreProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }

        if (status != UserStatusEnum.REMOVE &&  (usr.getStatus() == UserStatusEnum.DELETED
                || usr.getStatus() == UserStatusEnum.TERMINATE)) {
            log.debug("User was already deleted. Nothing more to do.");
            return response;
        }

        if (!managedSystemId.equalsIgnoreCase(sysConfiguration
                .getDefaultManagedSysId())) {
            // managedSysId point to one of the seconardary identities- just
            // terminate that identity

            // call delete on the connector
            ManagedSysDto mSys = managedSysService
                    .getManagedSys(managedSystemId);

            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService
                    .managedSysObjectParam(mSys.getManagedSysId(), "USER");
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(MATCH_PARAM, matchObj);
            }
            // pre-processing

            Resource res = null;
            String resourceId = mSys.getResourceId();

            bindingMap.put("IDENTITY", login);
            bindingMap.put("RESOURCE", res);
            bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
            bindingMap.put(TARGET_SYS_RES_ID, resourceId);
            bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

            if (resourceId != null) {
                res = resourceDataService.getResource(resourceId);
                if (res != null) {
                    String preProcessScript = getResProperty(
                            res.getResourceProps(), "PRE_PROCESS");
                    if (preProcessScript != null && !preProcessScript.isEmpty()) {
                        PreProcessor ppScript = createPreProcessScript(
                                preProcessScript, bindingMap);
                        if (ppScript != null) {
                            executePreProcess(ppScript, bindingMap, pUser,
                                    "DELETE");
                        }
                    }
                }
            }

            boolean connectorSuccess = false;
            ResponseType resp = delete(loginDozerConverter.convertToDTO(login, true),
                    requestId, mSys, matchObj);

            if (resp.getStatus() == StatusCodeType.SUCCESS) {
                connectorSuccess = true;
                // if REMOVE status:  we do physically delete identity for selected managed system after successful provisioning
                // if DELETE status:  we don't delete identity from database only set status to INACTIVE
                if(status == UserStatusEnum.REMOVE) {
                    loginManager.deleteLogin(login.getLoginId());
                } else {
                    login.setStatus(LoginStatusEnum.INACTIVE);
                    login.setAuthFailCount(0);
                    login.setPasswordChangeCount(0);
                    login.setIsLocked(1);
                    loginManager.updateLogin(login);
                }
            } else {
                    login.setStatus(status == UserStatusEnum.REMOVE ? LoginStatusEnum.FAIL_REMOVE : LoginStatusEnum.FAIL_DELETE);
                    loginManager.updateLogin(login);
            }

            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
            String postProcessScript = getResProperty(res.getResourceProps(),
                    "POST_PROCESS");
            if (postProcessScript != null && !postProcessScript.isEmpty()) {
                PostProcessor ppScript = createPostProcessScript(
                        postProcessScript, bindingMap);
                if (ppScript != null) {
                    executePostProcess(ppScript, bindingMap, pUser, "DELETE",
                            connectorSuccess);
                }
            }

        } else {
            // delete user and all its identities.
            LoginEntity lRequestor = loginManager
                    .getPrimaryIdentity(requestorId);
            LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

            if (lRequestor != null && lTargetUser != null) {
                log.debug("Requestor identity=" + lRequestor);
                log.debug("Target identity=" + lTargetUser);
            } else {
                log.debug("Unable to log disable operation. Of of the following is null:");
                log.debug("Requestor identity=" + lRequestor);
                log.debug("Target identity=" + lTargetUser);
            }

            // update the identities and set them to inactive
            List<LoginEntity> principalList = loginManager
                    .getLoginByUser(userId);
            if (principalList != null) {
                for (LoginEntity l : principalList) {
                    // this try-catch block for protection other operations and other resources if one resource was fall with error
                    try {
                        if (!LoginStatusEnum.INACTIVE.equals(l.getStatus())) {
                            // only add the connectors if its a secondary identity.
                            if (!l.getManagedSysId().equalsIgnoreCase(
                                    this.sysConfiguration.getDefaultManagedSysId())) {

                                ManagedSysDto mSys = managedSysService
                                        .getManagedSys(l.getManagedSysId());

                                ManagedSystemObjectMatch matchObj = null;
                                ManagedSystemObjectMatch[] matchObjAry = managedSysService
                                        .managedSysObjectParam(
                                                mSys.getManagedSysId(), "USER");
                                if (matchObjAry != null && matchObjAry.length > 0) {
                                    matchObj = matchObjAry[0];
                                    bindingMap.put(MATCH_PARAM, matchObj);
                                }
                                log.debug("Deleting id=" + l.getLogin());
                                log.debug("- delete using managed sys id="
                                        + mSys.getManagedSysId());

                                // pre-processing
                                bindingMap.put(IDENTITY, l);
                                bindingMap.put(TARGET_SYS_RES, null);

                                Resource resource = null;
                                String resourceId = mSys.getResourceId();

                                // SET PRE ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(TARGET_SYSTEM_IDENTITY, l.getLogin());
                                bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, mSys.getManagedSysId());
                                bindingMap.put(TARGET_SYS_RES_ID, resourceId);
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
                                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, l.getDomainId());

                                if (resourceId != null) {
                                    resource = resourceDataService
                                            .getResource(resourceId);
                                    if (resource != null) {
                                        bindingMap.put(TARGET_SYS_RES, resource);

                                        String preProcessScript = getResProperty(
                                                resource.getResourceProps(),
                                                "PRE_PROCESS");
                                        if (preProcessScript != null
                                                && !preProcessScript.isEmpty()) {
                                            PreProcessor ppScript = createPreProcessScript(
                                                    preProcessScript, bindingMap);
                                            if (ppScript != null) {
                                                if (executePreProcess(ppScript,
                                                        bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }


                                boolean connectorSuccess = false;

                                ObjectResponse resp = delete(
                                            loginDozerConverter.convertToDTO(l,
                                                    true), requestId, mSys,
                                        matchObj);
                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }


                                if (connectorSuccess) {
                                    l.setStatus(LoginStatusEnum.INACTIVE);
                                    l.setAuthFailCount(0);
                                    l.setPasswordChangeCount(0);
                                    l.setIsLocked(1);
                                }
                                // SET POST ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
                                if (resource != null) {
                                    String postProcessScript = getResProperty(
                                            resource.getResourceProps(),
                                            "POST_PROCESS");
                                    if (postProcessScript != null
                                            && !postProcessScript.isEmpty()) {
                                        PostProcessor ppScript = createPostProcessScript(
                                                postProcessScript, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript,
                                                    bindingMap, pUser, "DELETE",
                                                    connectorSuccess);
                                        }
                                    }
                                }
                                if(status == UserStatusEnum.REMOVE) {
                                    loginManager.deleteLogin(login.getLogin());
                                }
                            }

                        }
                    }catch(Throwable tw) {
                       log.error(l,tw);
                    }
                }
            }
        }
        if(status == UserStatusEnum.REMOVE) {
           // loginManager.deleteLogin(login.getLogin());
            try {
                userMgr.removeUser(userId);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_SQL_ERROR);
                return response;
            }
        } else {
            usr.setStatus(status);
            usr.setSecondaryStatus(null);
            usr.setLastUpdatedBy(requestorId);
            usr.setLastUpdate(new Date(System.currentTimeMillis()));
            userMgr.updateUserWithDependent(userDozerConverter.convertToEntity(usr, true), false);
        }
        // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT

        bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYS_RES_ID, null);
        bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

        if (callPostProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return response;
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    @Override
    @Transactional
    public ProvisionUserResponse deprovisionSelectedResources(String userId,
            String requestorUserId, List<String> resourceList) {
        return deprovisionSelectedResource.deprovisionSelectedResources(userId,
                requestorUserId, resourceList);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#disableUser(java.lang.
     * String, boolean)
     */
    @Override
    @Transactional
    public Response disableUser(String userId, boolean operation,
            String requestorId) {

        return disableUser.disableUser(userId, operation, requestorId,
                MuleContextProvider.getCtx());

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#lockUser(java.lang.String,
     * org.openiam.provision.dto.AccountLockEnum)
     */
    @Override
    @Transactional
    public Response lockUser(String userId, AccountLockEnum operation,
            String requestorId) {
        final Response response = new Response();
        String auditReason = null;

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        if (requestorId == null) {
            throw new NullPointerException("requestorId is null");
        }

        if (operation == null) {
            throw new NullPointerException("Operation parameter is null");
        }

        UserEntity user = userMgr.getUser(userId);
        if (user == null) {
            log.error("UserId " + userId + " not found");
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
            return response;

        }
        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        if (operation.equals(AccountLockEnum.LOCKED)) {
            user.setSecondaryStatus(UserStatusEnum.LOCKED);
            if (lg != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Identity flag set to locked.");
                }
                lg.setIsLocked(1);
            }
            auditReason = "LOCKED";
        } else if (operation.equals(AccountLockEnum.LOCKED_ADMIN)) {
            user.setSecondaryStatus(UserStatusEnum.LOCKED_ADMIN);
            if (lg != null) {
                lg.setIsLocked(2);
            }
            auditReason = "LOCKED_ADMIN";
        } else {
            user.setSecondaryStatus(null);
            if (lg == null) {
                log.error(String.format(
                        "Primary identity for UserId %s not found", userId));
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }
            lg.setAuthFailCount(0);
            lg.setIsLocked(0);
            auditReason = "UNLOCK";
        }
        loginManager.updateLogin(lg);
        userMgr.updateUserWithDependent(user, false);

        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);

        /*
        auditHelper.addLog(auditReason, logDomainId, logLoginId, "IDM SERVICE",
                requestorId, "USER", "USER", logUserId, null, "SUCCESS", null,
                null, null, requestId, auditReason, null, null, null, login,
                domain);
		*/
        final List<LoginEntity> loginList = loginManager.getLoginByUser(userId);
        for (final LoginEntity userLogin : loginList) {
            if (userLogin != null) {
                if (userLogin.getManagedSysId() != null
                        && !userLogin.getManagedSysId().equals("0")) {
                    ResponseType responsetype = null;
                    final String managedSysId = userLogin.getManagedSysId();
                    final ManagedSysDto managedSys = managedSysService
                            .getManagedSys(managedSysId);
                    if (AccountLockEnum.LOCKED.equals(operation)
                            || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                        final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                        suspendCommand.setObjectIdentity(userLogin.getLogin());
                        suspendCommand.setTargetID(managedSysId);
                        suspendCommand.setRequestID("R" + System.currentTimeMillis());
                        connectorAdapter.suspendRequest(managedSys,
                                suspendCommand, MuleContextProvider.getCtx());
                    } else {
                        final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                        resumeRequest.setObjectIdentity(userLogin.getLogin());
                        resumeRequest.setTargetID(managedSysId);
                        resumeRequest.setRequestID("R"
                                + System.currentTimeMillis());
                        connectorAdapter.resumeRequest(managedSys,
                                resumeRequest, MuleContextProvider.getCtx());
                    }

                    if (responsetype == null) {
                        log.info("Response object from set password is null");
                        response.setStatus(ResponseStatus.FAILURE);
                        return response;
                    }

                    if (responsetype.getStatus() == null) {
                        log.info("Response status is null");
                        response.setStatus(ResponseStatus.FAILURE);
                        return response;
                    }
                    log.info(String.format("Response status=%s",
                            response.getStatus()));

                    // TODO: process the result of the WS call to resume/suspend
                    // of teh connector
                }
            }
        }
        final List<RoleEntity> roleList = roleDataService.getUserRoles(
                user.getUserId(), null, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (final RoleEntity role : roleList) {
                final List<Resource> resourceList = resourceDataService
                        .getResourcesForRole(role.getRoleId(), 0,
                                Integer.MAX_VALUE);
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (final Resource resource : resourceList) {
                        ManagedSysDto managedSys = managedSysService.getManagedSysByResource(resource.getResourceId());
                        if (managedSys != null) {
                            ResponseType responsetype = null;
                            if(AccountLockEnum.LOCKED.equals(operation) || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                                final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                                suspendCommand.setObjectIdentity(lg.getLogin());
                                suspendCommand.setTargetID(managedSys.getManagedSysId());
                                suspendCommand.setRequestID("R"
                                        + System.currentTimeMillis());
                                connectorAdapter.suspendRequest(managedSys,
                                        suspendCommand, MuleContextProvider.getCtx());
                            } else {
                                final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                                resumeRequest.setObjectIdentity(lg.getLogin());
                                resumeRequest.setTargetID(managedSys.getManagedSysId());
                                resumeRequest.setRequestID("R"
                                        + System.currentTimeMillis());
                                connectorAdapter.resumeRequest(managedSys,
                                        resumeRequest, MuleContextProvider.getCtx());
                            }

                            if (responsetype.getStatus() == null) {
                                log.info("Response status is null");
                                response.setStatus(ResponseStatus.FAILURE);
                                return response;
                            }
                            log.info(String.format("Response status=%s",
                                    response.getStatus()));

                        }
                    }
                }
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private ProvisionUserResponse addModifyUser(ProvisionUser pUser, boolean isAdd, List<ProvisionDataContainer> dataList, final AuditLogBuilder auditLog) {

        if (isAdd) {
            log.debug("--- DEFAULT PROVISIONING SERVICE: addUser called ---");
        } else {
            log.debug("--- DEFAULT PROVISIONING SERVICE: modifyUser called ---");
        }

        UserEntity userEntity = !isAdd ? userMgr.getUser(pUser.getUserId()) : new UserEntity();
        if (userEntity == null) {
            throw new IllegalArgumentException("UserId='" + pUser.getUserId() + "' is not valid");
        }

        ProvisionUserResponse resp = new ProvisionUserResponse();
        String requestId = "R" + UUIDGen.getUUID();

        // flag to determine if we should provision this user in target systems
        boolean provInTargetSystemNow = true;

        // determine if we provision now or in the future
        // if its in the future then we wont put the user in the target systems
        if (isAdd) {
            provInTargetSystemNow = provisionUserNow(pUser);
            if (!provInTargetSystemNow) {
                // start date is in the future flag says that we should prov after the startdate
                pUser.setStatus(UserStatusEnum.PENDING_START_DATE);
            }
        }

        // bind the objects to the scripting engine
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("org", pUser.getPrimaryOrganization());
        bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
        bindingMap.put("user", pUser);
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, null);
        if (!isAdd) {
            ProvisionUser u = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
            setCurrentSuperiors(u); // TODO: Consider the possibility to add and update superiors by cascade from UserEntity
            bindingMap.put("userBeforeModify", u);
        }
        if (callPreProcessor(isAdd ? "ADD" : "MODIFY", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            auditLog.fail().setFailureReason("PreProcessor error.");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return resp;
        }
        // make sure that our object as the attribute set that will be used for audit logging
        checkAuditingAttributes(pUser);

        if (!isAdd) {
            // get the current roles
            List<Role> curRoleList = roleDataService.getUserRolesAsFlatList(pUser.getUserId()); //TODO: do we need children roles?
            // get all groups for user
            List<Group> curGroupList = groupDozerConverter.convertToDTOList(
                    groupManager.getGroupsForUser(pUser.getUserId(), null, -1, -1), false);
            // make the role and group list before these updates available to the
            // attribute policies
            bindingMap.put("currentRoleList", curRoleList);
            bindingMap.put("currentGroupList", curGroupList);
        }

        // dealing with principals
        if (!isAdd) {
            List<LoginEntity> curPrincipalList = userEntity.getPrincipalList();
            // check that a primary identity exists some where
            LoginEntity curPrimaryIdentity = getPrimaryIdentity(sysConfiguration.getDefaultManagedSysId(), curPrincipalList);
            if (curPrimaryIdentity == null && pUser.getPrincipalList() == null) {
                log.debug("Primary identity not found...");
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return resp;
            }
        } else {
            Login primaryLogin;
            boolean customPassword = false;
            if (CollectionUtils.isEmpty(pUser.getPrincipalList())) {
                // build the list
                primaryLogin = buildPrimaryPrincipal(bindingMap, scriptRunner);
                if (primaryLogin != null) {
                    primaryLogin.setOperation(AttributeOperationEnum.ADD);

                } else {
                    log.debug("policyAttrMap IS null...");
                    resp.setStatus(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                    return resp;
                }

            } else {
                primaryLogin = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                if (primaryLogin != null) {
                    // Check if a custom password is set
                    if (StringUtils.isNotBlank(primaryLogin.getPassword())) {
                        customPassword = true;
                    } else {
                        buildPrimaryIDPassword(primaryLogin, bindingMap, scriptRunner);
                    }
                } else {
                    log.debug("Primary identity not found...");
                    resp.setStatus(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                    return resp;
                }
            }
            // check if there is a custom login provided in the request
            if (StringUtils.isNotBlank(pUser.getLogin())) {
                primaryLogin.setLogin(pUser.getLogin());
            }
            // check if there is a custom password provided in the request
            if (StringUtils.isNotBlank(pUser.getPassword())) {
                customPassword = true;
                primaryLogin.setPassword(pUser.getPassword());
            }
            // if the password of the primaryIdentity is custom validate it
            if (customPassword) {
                resp = validatePassword(primaryLogin, pUser, requestId);
                if (resp.isFailure()) {
                    return resp;
                }
            }
            // validate that this identity does not already exist
            LoginEntity dupPrincipal = loginManager.getLoginByManagedSys(
                    primaryLogin.getDomainId(), primaryLogin.getLogin(),
                    primaryLogin.getManagedSysId());

            if (dupPrincipal != null) {
                // identity exists
                auditLog.fail().setFailureReason(ResponseCode.DUPLICATE_PRINCIPAL+": "+dupPrincipal);

                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.DUPLICATE_PRINCIPAL);
                return resp;
            }
            // remove primary id if exists
            if (CollectionUtils.isNotEmpty(pUser.getPrincipalList())) {
                for (Login l : pUser.getPrincipalList()) {
                    if (l.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {
                        pUser.getPrincipalList().remove(l);
                        break;
                    }
                }
            }
            // add primary id
            pUser.getPrincipalList().add(primaryLogin);
        }

        pUser.setObjectState(isAdd ? BaseObject.NEW : BaseObject.UPDATE);

        // filling up user properties
        Date currDate = new Date(System.currentTimeMillis());
        if (isAdd) {
            userEntity.setCreateDate(currDate);
        }
        userEntity.setLastUpdate(currDate);

        // update originalUser from IDM with the new user information
        updateUserProperties(userEntity, pUser);

        if (isAdd) {
            try {
                userMgr.addUser(userEntity); // Need to have userId to encrypt/decrypt password
                pUser.setUserId(userEntity.getUserId());
            } catch (Exception e) {
                auditLog.fail().setFailureReason("Exception while creating user: "+e.getMessage());
                log.error("Exception while creating user", e);
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_OTHER);
                return resp;
            }
        }

        // update addresses
        updateAddresses(userEntity, pUser);

        // update phones
        updatePhones(userEntity, pUser);

        // update emails
        updateEmails(userEntity, pUser);

        // update supervisors
        updateSupervisors(userEntity, pUser);

        // update groups
        updateGroups(userEntity, pUser);

        // update roles
        Set<Role> roleSet = new HashSet<Role>();
        Set<Role> deleteRoleSet = new HashSet<Role>();
        updateRoles(userEntity, pUser, roleSet, deleteRoleSet);
        bindingMap.put("userRole", roleSet);

        // update organization associations
        updateAffiliations(userEntity, pUser);

        // Set of resources that a person should have based on their active roles
        Set<Resource> resourceSet = getResourcesForRoles(roleSet);
        // Set of resources that are to be removed based on roles that are to be deleted
        Set<Resource> deleteResourceSet = getResourcesForRoles(deleteRoleSet);

        // update resources, update resources sets
        updateResources(userEntity, pUser, resourceSet, deleteResourceSet);

        log.debug("Resources to be added ->> " + resourceSet);
        log.debug("Delete the following resources ->> " + deleteResourceSet);

        // update principals
        updatePrincipals(userEntity, pUser);

        // get primary identity and bind it for the groovy scripts
        LoginEntity primaryIdentityEntity = getPrimaryIdentity(sysConfiguration.getDefaultManagedSysId(),
                userEntity.getPrincipalList());
        Login primaryIdentity = (primaryIdentityEntity != null) ?
                loginDozerConverter.convertToDTO(primaryIdentityEntity, false) : null;

        if (primaryIdentity == null) { // Try to generate a new primary identity from scratch
            primaryIdentity = buildPrimaryPrincipal(bindingMap, scriptRunner);
        }

        String decPassword = "";
        if (primaryIdentity != null) {
            if (StringUtils.isEmpty(primaryIdentity.getUserId())) {
                throw new IllegalArgumentException("primaryIdentity userId can not be empty");
            }
            String password = primaryIdentity.getPassword();
            if (password != null) {
                try {
                    decPassword = loginManager.decryptPassword(primaryIdentity.getUserId(), password);
                } catch (EncryptionException e) {
                    auditLog.fail().setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                    resp.setStatus(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                    return resp;
                }
                bindingMap.put("password", decPassword);
            }
            bindingMap.put("lg", primaryIdentity);

        } else {
            auditLog.fail().setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
            log.debug("Primary identity not found for user=" + userEntity.getUserId());
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return resp;
        }

        // update attributes
        updateUserAttributes(userEntity, pUser);

        log.debug("Binding active roles to scripting");
        log.debug("- role set -> " + roleSet);
        log.debug("- Primary Identity : " + primaryIdentity);

        // deprovision resources
        if (!isAdd) {
            if (CollectionUtils.isNotEmpty(deleteResourceSet)) {
                for (Resource res : deleteResourceSet) {
                    //skip provisioning for resource if it in NotProvisioning set
                    if(pUser.getNotProvisioninResourcesIds().contains(res.getResourceId())) {
                         auditLog.succeed().setAuditDescription("Skip De-Provisioning for resource: "+res.getName());
                         continue;
                    }
                    try {
                    // Protects other resources if one resource failed
                        ProvisionDataContainer data = deprovisionResource(res, userEntity, requestId);
                        if (data != null) {
                            dataList.add(data);
                        }
                    } catch (Throwable tw) {
                        auditLog.fail().setFailureReason("Deprovisioning resource : "+res.getName() +" for user with primary identity: "+primaryIdentity+". Exception: "+tw.getMessage());
                        log.error(res, tw);
                    }
                }
            }
        }

        // provision resources
        if (provInTargetSystemNow) {
            if (CollectionUtils.isNotEmpty(resourceSet)) {
                for (Resource res : resourceSet) {
                    //skip provisioning for resource if it in NotProvisioning set
                    if(pUser.getNotProvisioninResourcesIds().contains(res.getResourceId())) {
                        auditLog.succeed().setAuditDescription("Skip Provisioning for resource: "+res.getName());
                        continue;
                    }
                    try {
                       if(pUser.getSrcSystemId() != null) {
                           // do check if provisioning user has source resource => we should skip it from double provisioning
                           // reconciliation case
                           ManagedSysDto managedSys = managedSysService.getManagedSys(pUser.getSrcSystemId());
                           if(res.getResourceId().equals(managedSys.getResourceId())) {
                              continue;
                           }
                       }
                       // Protects other resources if one resource failed
                        Map<String, Object> tmpMap = new HashMap<String, Object>(bindingMap); // prevent bindingMap rewrite in dataList
                        ProvisionDataContainer data = provisionResource(isAdd, res, userEntity, pUser, tmpMap, primaryIdentity, requestId);
                        if (data != null) {
                            dataList.add(data);
                        }
                    } catch (Throwable tw) {
                        auditLog.fail().setFailureReason("Provisioning resource : "+res.getName() +" for user with primary identity: "+primaryIdentity+". Exception: "+tw.getMessage());
                        log.error(res, tw);
                    }
                }
            }
        }

        ProvisionUser finalProvUser = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));

        if (isAdd) { // send email notifications
            if (pUser.isEmailCredentialsToNewUsers()) {
                sendCredentialsToUser(finalProvUser.getUser(), primaryIdentity.getLogin(), decPassword);
            }
            if (pUser.isEmailCredentialsToSupervisor()) {
                if (pUser.getSuperiors() != null) {
                    Set<User> superiors = pUser.getSuperiors();
                    if (CollectionUtils.isNotEmpty(superiors)) {
                        for (User s : superiors) {
                            sendCredentialsToSupervisor(s, primaryIdentity.getLogin(), decPassword, finalProvUser.getFirstName() + " " + finalProvUser.getLastName());
                        }
                    }
                }
            }
        }

        if (callPostProcessor(isAdd ? "ADD" : "MODIFY", finalProvUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            auditLog.fail().setFailureReason("PostProcessor error.");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return resp;
        }
        /* Response object */

        userMgr.updateUser(userEntity);

        if (isAdd) {
            log.debug("DEFAULT PROVISIONING SERVICE: addUser complete");
        } else {
            log.debug("DEFAULT PROVISIONING SERVICE: modifyUser complete");
        }
        auditLog.succeed();
        resp.setStatus(ResponseStatus.SUCCESS);
        resp.setUser(finalProvUser);
        return resp;
    }

    private ProvisionDataContainer provisionResource(boolean isAdd, Resource res, UserEntity userEntity, ProvisionUser pUser,
            Map<String, Object> bindingMap, Login primaryIdentity, String requestId) {
        ManagedSysDto managedSys = managedSysService.getManagedSysByResource(res.getResourceId());
        String managedSysId = (managedSys != null) ? managedSys.getManagedSysId() : null;
        if (managedSysId != null) {
            if (pUser.getSrcSystemId() != null) {
            // we are checking if SrcSystemId is set in ProvisionUser it means we should ignore this resource from provisioning to avoid cyclic. Used in Reconciliation of one managed system to another
                if (res.getResourceId().equalsIgnoreCase(pUser.getSrcSystemId())) {
                    return null;
                }
            }
            // what the new object will look like
            // Provision user that goes to the target system. Derived from userEntity after all changes
            ProvisionUser targetSysProvUser = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
            setCurrentSuperiors(targetSysProvUser); // TODO: Consider the possibility to add and update superiors by cascade from UserEntity
            targetSysProvUser.setStatus(pUser.getStatus()); // copying user status (need to define enable/disable status)

            bindingMap.put(TARGET_SYS_RES_ID, res.getResourceId());
            bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSysId);
            bindingMap.put("user", targetSysProvUser);

            List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getResourceId());
            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
            if (mSys == null || mSys.getConnectorId() == null) {
                return null;
            }

            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, "USER");
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(MATCH_PARAM, matchObj);
            }

            // get the identity linked to this resource / managedsys
            LoginEntity mLg = null;
            for (LoginEntity l : userEntity.getPrincipalList()) {
                if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                    // TODO: check if another props need to be updated
                    l.setStatus(LoginStatusEnum.PENDING_UPDATE);
                    mLg = l;
                }
            }

            // determine if this identity exists in IDM or not
            // if not, do an ADD otherwise, do an UPDATE

            if (mLg != null && mLg.getLoginId() != null) {
                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, mLg.getDomainId());
            } else {
                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, mSys.getDomainId());
            }

            if (mLg != null) {
                log.debug("PROCESSING IDENTITY =" + mLg);
            } else {
                log.debug("BUILDING NEW IDENTITY");
            }

            boolean isMngSysIdentityExistsInOpeniam = (mLg != null);
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, isMngSysIdentityExistsInOpeniam ? IDENTITY_EXIST : IDENTITY_NEW );

            if (!isMngSysIdentityExistsInOpeniam) {
                try {
                    log.debug(" - Building principal Name for: " + managedSysId);
                    String newPrincipalName = ProvisionServiceUtil.buildPrincipalName(attrMap, scriptRunner, bindingMap);
                    if (StringUtils.isBlank(newPrincipalName)) {
                        log.debug("Principal name for managed sys " + managedSysId + " is blank.");
                        return null;
                    }
                    log.debug(" - New principalName = " + newPrincipalName);

                    mLg = new LoginEntity();
                    log.debug(" - PrimaryIdentity for build new identity for target system = " + primaryIdentity);

                    mLg.setLogin(newPrincipalName);
                    mLg.setDomainId(primaryIdentity.getDomainId());
                    mLg.setManagedSysId(managedSysId);
                    mLg.setPassword(primaryIdentity.getPassword());
                    mLg.setUserId(primaryIdentity.getUserId());
                    mLg.setAuthFailCount(0);
                    mLg.setCreateDate(new Date(System.currentTimeMillis()));
                    mLg.setCreatedBy(userEntity.getLastUpdatedBy());
                    mLg.setIsLocked(0);
                    mLg.setFirstTimeLogin(1);
                    mLg.setStatus(LoginStatusEnum.PENDING_CREATE);

                    userEntity.getPrincipalList().add(mLg); // add new identity to user principals

                } catch (ScriptEngineException e) {
                    e.printStackTrace();
                }
            }

            bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, null);
            bindingMap.put(TARGET_SYSTEM_IDENTITY, isMngSysIdentityExistsInOpeniam ? mLg.getLogin() : null);
            bindingMap.put( TARGET_SYS_SECURITY_DOMAIN, isMngSysIdentityExistsInOpeniam ? mLg.getDomainId() : null);

            // Identity of current target system
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            for (Login l : pUser.getPrincipalList()) { // saving Login properties from pUser
                if (l.getLoginId()!=null && l.getLoginId().equals(targetSysLogin.getLoginId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                }
            }

            ProvisionDataContainer data = new ProvisionDataContainer();
            if (isMngSysIdentityExistsInOpeniam) {
                data.setOperation(AttributeOperationEnum.REPLACE);
            } else {
                data.setOperation(AttributeOperationEnum.ADD);
            }
            data.setRequestId(requestId);
            data.setResourceId(res.getResourceId());
            data.setIdentity(targetSysLogin);
            data.setProvUser(targetSysProvUser);
            data.setBindingMap(bindingMap);

            return data;
        }
        return null;
    }

    private ProvisionDataContainer deprovisionResource(Resource res, UserEntity userEntity, String requestId) {

        //ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        ManagedSysDto mSys = managedSysService.getManagedSysByResource(res.getResourceId());
        String managedSysId = (mSys != null) ? mSys.getManagedSysId() : null;
        if (mSys == null || mSys.getConnectorId() == null) {
            return null;
        }

        LoginEntity mLg = null;
        for (LoginEntity l : userEntity.getPrincipalList()) {
            if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                // TODO: check if another props need to be updated
                l.setStatus(LoginStatusEnum.PENDING_UPDATE);
                mLg = l;
            }
        }

        if (mLg != null) {
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            ProvisionDataContainer data = new ProvisionDataContainer();
            data.setRequestId(requestId);
            data.setResourceId(res.getResourceId());
            data.setIdentity(targetSysLogin);
            data.setOperation(AttributeOperationEnum.DELETE);
            return data;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#resetPassword(org.openiam
     * .provision.dto.PasswordSync)
     */
    @Override
    @Transactional
    public PasswordResponse resetPassword(PasswordSync passwordSync) {
        log.debug("----resetPassword called.------");
        final AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        AuditLogBuilder auditBuilderResetPasswdChildLog = new AuditLogBuilder();
        auditBuilderResetPasswdChildLog.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_RESETPASSWORD);
        auditBuilder.addChild(auditBuilderResetPasswdChildLog);
        final PasswordResponse response = new PasswordResponse(
                ResponseStatus.SUCCESS);
        try {
            final String requestId = "R" + UUIDGen.getUUID();

            // get the user object associated with this principal
            final LoginEntity login = loginManager.getLoginByManagedSys(
                    passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                    passwordSync.getManagedSystemId());
            if (login == null) {
                auditBuilderResetPasswdChildLog.fail().setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }
            // check if the user active
            final String userId = login.getUserId();
            if (userId == null) {
                auditBuilderResetPasswdChildLog.fail().setFailureReason(ResponseCode.USER_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.USER_NOT_FOUND);
                return response;
            }
            final UserEntity usr = userMgr.getUser(userId);
            if (usr == null) {
                auditBuilderResetPasswdChildLog.fail().setFailureReason(ResponseCode.USER_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.USER_NOT_FOUND);
                return response;
            }

            String password = passwordSync.getPassword();
            if (StringUtils.isEmpty(password)) {
                // autogenerate the password
                password = String.valueOf(PasswordGenerator.generatePassword(8));
            }
            String encPassword = null;
            try {
                encPassword = loginManager.encryptPassword(userId, password);
            } catch (EncryptionException e) {
                auditBuilderResetPasswdChildLog.fail().setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }
            final boolean retval = loginManager.resetPassword(
                    passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                    passwordSync.getManagedSystemId(), encPassword);

            if (retval) {
                log.debug("-Password changed in openiam repository for user:"
                        + passwordSync.getPrincipal());
                auditBuilderResetPasswdChildLog.succeed();

            /*
             * came with merge from v2.3 //check if password should be sent to
             * the user. if (passwordSync.isSendPasswordToUser()) { //
             * sendPasswordToUser(usr, password); }
             */
                if (passwordSync.getSendPasswordToUser()) {
                    sendResetPasswordToUser(usr, passwordSync.getPrincipal(),
                            password);
                }

            } else {
                auditBuilderResetPasswdChildLog.fail().setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                Response resp = new Response();
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            }

            if (StringUtils.equalsIgnoreCase(passwordSync.getManagedSystemId(),
                    sysConfiguration.getDefaultManagedSysId())) {
                // typical sync
                final List<LoginEntity> principalList = loginManager
                        .getLoginByUser(login.getUserId());
                if (principalList != null) {
                    log.debug("PrincipalList size =" + principalList.size());
                    for (final LoginEntity lg : principalList) {
                        // get the managed system for the identity - ignore the
                        // managed system id that is linked to openiam's repository
                        log.debug("**** Managed System Id in passwordsync object="
                                + passwordSync.getManagedSystemId());

                        if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(),
                                passwordSync.getManagedSystemId())
                                && !StringUtils.equalsIgnoreCase(
                                lg.getManagedSysId(),
                                sysConfiguration.getDefaultManagedSysId())) {
                            // determine if you should sync the password or not
                            final String managedSysId = lg.getManagedSysId();
                            final ManagedSysEntity mSys = managedSystemService
                                    .getManagedSysById(managedSysId);
                            if (mSys != null) {
                                final ResourceEntity res = resourceService
                                        .findResourceById(mSys.getResourceId());
                                log.debug(" - managedsys id = " + managedSysId);
                                log.debug(" - Resource for sysId =" + res);

                                // check the sync flag

                                if (syncAllowed(res)) {

                                    log.debug("Sync allowed for sys="
                                            + managedSysId);
                                    loginManager.resetPassword(lg.getDomainId(),
                                            lg.getLogin(), lg.getManagedSysId(),
                                            encPassword);

                                    ManagedSystemObjectMatchEntity matchObj = null;
                                    final List<ManagedSystemObjectMatchEntity> matcheList = managedSystemService
                                            .managedSysObjectParam(managedSysId,
                                                    "USER");

                                    if (CollectionUtils.isNotEmpty(matcheList)) {
                                        matchObj = matcheList.get(0);
                                    }

                                    ResponseType resp = resetPassword(requestId, loginDozerConverter.convertToDTO(lg, false), password,
                                            managedSysDozerConverter.convertToDTO(mSys, false), objectMatchDozerConverter.convertToDTO(matchObj, false));
                                    if(resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                                        auditBuilderResetPasswdChildLog.succeed().setAuditDescription("Reset password for resource: " + res.getName() + " for user: " + lg.getLogin());
                                    } else {
                                        auditBuilderResetPasswdChildLog.fail().setFailureReason("Reset password for resource: " + mSys.getName() + " for user: " + login.getLogin() + ". " + resp.getErrorMsgAsStr());
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                // update just the system that was specified

                final ManagedSysEntity mSys = managedSystemService
                        .getManagedSysById(passwordSync.getManagedSystemId());

                ManagedSystemObjectMatchEntity matchObj = null;
                final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                        .managedSysObjectParam(mSys.getManagedSysId(), "USER");
                if (CollectionUtils.isNotEmpty(matchList)) {
                    matchObj = matchList.get(0);
                }
                ResponseType resp = resetPassword(requestId, loginDozerConverter.convertToDTO(login, false), passwordSync.getPassword(), managedSysDozerConverter.convertToDTO(mSys, false),
                        objectMatchDozerConverter.convertToDTO(matchObj, false));
                if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                    auditBuilderResetPasswdChildLog.succeed().setAuditDescription("Reset password for resource: " + mSys.getName() + " for user: " + login.getLogin());
                    response.setStatus(ResponseStatus.SUCCESS);
                } else {
                    auditBuilderResetPasswdChildLog.fail().setFailureReason("Reset password for resource: " + mSys.getName() + " for user: " + login.getLogin() + ". " + resp.getErrorMsgAsStr());
                    response.setErrorText(resp.getErrorMsgAsStr());
                    response.setStatus(ResponseStatus.FAILURE);
                }

            }
        } finally {
            auditLogService.enqueue(auditBuilder);
        }

        return response;

    }

    @Override
    @Transactional
    public LookupUserResponse getTargetSystemUser(String principalName,
            String managedSysId, List<ExtensibleAttribute> extensibleAttributes) {

        final AuditLogBuilder auditBuilder = auditLogProvider.getAuditLogBuilder();
        auditBuilder.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING);
        AuditLogBuilder auditBuilderLookupLog = new AuditLogBuilder();
        auditBuilderLookupLog.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_LOOKUP);
        auditBuilder.addChild(auditBuilderLookupLog);

        log.debug("getTargetSystemUser called. for = " + principalName);

        LookupUserResponse response = new LookupUserResponse(
                ResponseStatus.SUCCESS);
        try {
            response.setManagedSysId(managedSysId);
            response.setPrincipalName(principalName);
            // get the connector for the managedSystem

            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);

            // do the lookup

            log.debug("Calling lookupRequest ");

            LookupRequest reqType = new LookupRequest();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);
            reqType.setSearchValue(principalName);
            reqType.setRequestedAttributes(extensibleAttributes);
            reqType.setTargetID(managedSysId);
            reqType.setHostLoginId(mSys.getUserId());
            String passwordDecoded;
            try {
                passwordDecoded = getDecryptedPassword(mSys);
            } catch (ConnectorDataException e) {
                auditBuilderLookupLog.fail().setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }
            reqType.setHostLoginPassword(passwordDecoded);
            reqType.setHostUrl(mSys.getHostUrl());
            reqType.setExtensibleObject(new ExtensibleUser());
            reqType.setScriptHandler(mSys.getLookupHandler());

            SearchResponse responseType = connectorAdapter.lookupRequest(
                    mSys, reqType, MuleContextProvider.getCtx());
            if (responseType.getStatus() == StatusCodeType.FAILURE || responseType.getObjectList().size() == 0) {
                response.setStatus(ResponseStatus.FAILURE);
                return response;
            }

            String targetPrincipalName = responseType.getObjectList().get(0)
                    .getObjectIdentity() != null ? responseType.getObjectList().get(0)
                    .getObjectIdentity() : parseUserPrincipal(responseType
                    .getObjectList().get(0).getAttributeList());
            response.setPrincipalName(targetPrincipalName);
            response.setAttrList(responseType.getObjectList().get(0).getAttributeList());
            response.setResponseValue(responseType.getObjectList().get(0));

            auditBuilderLookupLog.succeed();

        } finally {
            auditLogService.enqueue(auditBuilder);
        }

        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#setPassword(org.openiam
     * .provision.dto.PasswordSync)
     */
    @Override
    @Transactional
    public PasswordValidationResponse setPassword(PasswordSync passwordSync) {
        log.debug("----setPassword called.------");
        final AuditLogBuilder auditLog = auditLogProvider.getAuditLogBuilder();
        auditLog.setBaseObject(passwordSync);
        auditLog.setAction(AuditAction.CHANGE_PASSWORD);
        PasswordValidationResponse response = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        final Map<String, Object> bindingMap = new HashMap<String, Object>();

        try {
	        if (callPreProcessor("SET_PASSWORD", null, bindingMap) != ProvisioningConstants.SUCCESS) {
	            response.fail();
	            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
	            auditLog.fail().setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
	            return response;
	        }

	        final String requestId = "R" + UUIDGen.getUUID();

	        // get the user object associated with this principal
	        final LoginEntity login = loginManager.getLoginByManagedSys(
	                passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
	                passwordSync.getManagedSystemId());
	        if (login == null) {
	        	auditLog.fail().setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
	            response.fail();
	            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
	            return response;
	        }
	        // check if the user active
	        final String userId = login.getUserId();
	        if (userId == null) {
	        	response.fail();
	        	auditLog.fail().setFailureReason(ResponseCode.USER_NOT_FOUND);
	            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
	            return response;
	        }

	        final UserEntity usr = userMgr.getUser(userId);
	        if (usr == null) {
	        	auditLog.fail().setFailureReason(ResponseCode.USER_NOT_FOUND);
	            response.fail();
	            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
	            return response;
	        }
	        auditLog.setTargetUser(userId);

	        // validate the password against password policy
	        final Password pswd = new Password();
	        pswd.setDomainId(passwordSync.getSecurityDomain());
	        pswd.setManagedSysId(passwordSync.getManagedSystemId());
	        pswd.setPrincipal(passwordSync.getPrincipal());
	        pswd.setPassword(passwordSync.getPassword());

	        try {
	            response = passwordManager.isPasswordValid(pswd);
	            if (response.isFailure()) {
	            	auditLog.fail().setFailureReason("Invalid Password");
	                return response;
	            }

	        } catch (ObjectNotFoundException oe) {
	        	auditLog.setException(oe);
	            log.error("Object not found", oe);
	        }

	        String encPassword = null;
	        try {
	            encPassword = loginManager.encryptPassword(usr.getUserId(),
	                    passwordSync.getPassword());
	        } catch (EncryptionException e) {
	        	auditLog.setException(e).fail().setFailureReason(ResponseCode.FAIL_ENCRYPTION);
	            response.fail();
	            response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
	            return response;
	        }

	        // make sure that update all the primary identity records
	        final List<LoginEntity> principalList = loginManager
	                .getLoginByUser(login.getUserId());
	        // List<Login> identityList =
	        // loginManager.getLoginByUser(usr.getUserId()) ;
	        for (final LoginEntity l : principalList) {

	            // find the openiam identity and update it in openiam DB
	            if (StringUtils.equalsIgnoreCase(l.getManagedSysId(),
	                    passwordSync.getManagedSystemId())) {

	                final boolean retval = loginManager.setPassword(
	                        l.getDomainId(), l.getLogin(),
	                        passwordSync.getManagedSystemId(), encPassword,
	                        passwordSync.isPreventChangeCountIncrement());
	                if (retval) {
	                    log.debug("-Password changed in openiam repository for user:"
	                            + passwordSync.getPrincipal());
	                    auditLog.succeed();
	                    // update the user object that the password was changed
	                    usr.setDatePasswordChanged(new Date(System
	                            .currentTimeMillis()));

	                    userMgr.updateUserWithDependent(usr, false);

	                } else {
	                	auditLog.fail().setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
	                    response.setStatus(ResponseStatus.FAILURE);
	                    response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
	                }
	            }
	        }

	        boolean connectorSuccess = false;

	        if (StringUtils.equalsIgnoreCase(passwordSync.getManagedSystemId(),
	                sysConfiguration.getDefaultManagedSysId())) {
	            // typical sync
	            // List<Login> principalList =
	            // loginManager.getLoginByUser(login.getUserId());
	            // if (principalList != null) {

	            // sync the non-openiam identities
	            for (final LoginEntity lg : principalList) {
	                // get the managed system for the identity - ignore the managed
	                // system id that is linked to openiam's repository
	                log.debug("**** Managed System Id in passwordsync object="
	                        + passwordSync.getManagedSystemId());

	                if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(),
	                        sysConfiguration.getDefaultManagedSysId())) {

	                    // determine if you should sync the password or not
	                    final String managedSysId = lg.getManagedSysId();
	                    final ManagedSysEntity mSys = managedSystemService
	                            .getManagedSysById(managedSysId);
	                    if (mSys != null) {
	                        final ResourceEntity resource = resourceService
	                                .findResourceById(mSys.getResourceId());

	                        log.debug(" - managedsys id = " + managedSysId);
	                        log.debug(" - Resource for sysId =" + resource);

	                        // check the sync flag

	                        if (syncAllowed(resource)) {

	                            log.debug("Sync allowed for sys=" + managedSysId);

	                            // pre-process

	                            bindingMap.put("IDENTITY", lg);
	                            bindingMap.put("RESOURCE", resource);
	                            bindingMap.put("PASSWORD_SYNC", passwordSync);

	                            if (resource != null) {
	                                final String preProcessScript = getResourceProperty(
	                                        resource, "PRE_PROCESS");
	                                if (preProcessScript != null
	                                        && !preProcessScript.isEmpty()) {
	                                    final PreProcessor ppScript = createPreProcessScript(
	                                            preProcessScript, bindingMap);
	                                    if (ppScript != null) {
	                                        if (executePreProcess(ppScript,
	                                                bindingMap, null,
	                                                "SET_PASSWORD") == ProvisioningConstants.FAIL) {
	                                            continue;
	                                        }
	                                    }
	                                }
	                            }

	                            // update the password in openiam
	                            loginManager.setPassword(lg.getDomainId(), lg
	                                    .getLogin(), lg.getManagedSysId(),
	                                    encPassword, passwordSync
	                                            .isPreventChangeCountIncrement());

	                            if (StringUtils.isNotEmpty(mSys.getConnectorId())) {
	                                final ProvisionConnectorEntity connector = connectorService
	                                        .getProvisionConnectorsById(mSys
	                                                .getConnectorId());

	                                ManagedSystemObjectMatchEntity matchObj = null;
	                                final List<ManagedSystemObjectMatchEntity> matchObjects = managedSystemService
	                                        .managedSysObjectParam(
	                                                mSys.getManagedSysId(), "USER");
	                                if (CollectionUtils.isNotEmpty(matchObjects)) {
	                                    matchObj = matchObjects.get(0);
	                                }

                                    ResponseType resp = setPassword(requestId, loginDozerConverter.convertToDTO(lg,false), passwordSync.getPassword(), managedSysDozerConverter.convertToDTO(mSys, false),
                                            objectMatchDozerConverter.convertToDTO(matchObj,false));

	                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
	                                        connectorSuccess = true;
	                                    }

	                                // post-process
	                                if (resource != null) {
	                                    final String postProcessScript = getResourceProperty(
	                                            resource, "POST_PROCESS");
	                                    if (postProcessScript != null
	                                            && !postProcessScript.isEmpty()) {
	                                        final PostProcessor ppScript = createPostProcessScript(
	                                                postProcessScript, bindingMap);
	                                        if (ppScript != null) {
	                                            executePostProcess(ppScript,
	                                                    bindingMap, null,
	                                                    "SET_PASSWORD",
	                                                    connectorSuccess);
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    } else {
	                        log.debug("Sync not allowed for sys=" + managedSysId);
	                    }
	                }
	            }
	            // }
	        } else {
	            // just the update the managed system that was specified.
	            final ManagedSysEntity mSys = managedSystemService
	                    .getManagedSysById(passwordSync.getManagedSystemId());
	            final ProvisionConnectorEntity connector = connectorService
	                    .getProvisionConnectorsById(mSys.getConnectorId());

	            ManagedSystemObjectMatchEntity matchObj = null;
	            final List<ManagedSystemObjectMatchEntity> matchObjects = managedSystemService
	                    .managedSysObjectParam(mSys.getManagedSysId(), "USER");
	            if (CollectionUtils.isNotEmpty(matchObjects)) {
	                matchObj = matchObjects.get(0);
	            }

	            // pre-process
	            final ResourceEntity resource = resourceService
	                    .findResourceById(mSys.getResourceId());

	            bindingMap.put("IDENTITY", login);
	            bindingMap.put("PASSWORD_SYNC", passwordSync);

	            if (resource != null) {
	                bindingMap.put("RESOURCE", resource);

	                final String preProcessScript = getResourceProperty(resource,
	                        "PRE_PROCESS");
	                if (preProcessScript != null && !preProcessScript.isEmpty()) {
	                    final PreProcessor ppScript = createPreProcessScript(
	                            preProcessScript, bindingMap);
	                    if (ppScript != null) {
	                        executePreProcess(ppScript, bindingMap, null,
	                                "SET_PASSWORD");
	                    }
	                }
	            }

                setPassword(requestId, loginDozerConverter.convertToDTO(login,false), passwordSync.getPassword(), managedSysDozerConverter.convertToDTO(mSys, false),
                        objectMatchDozerConverter.convertToDTO(matchObj,false));
	            // post-process
	            if (resource != null) {
	                String postProcessScript = getResourceProperty(resource,
	                        "POST_PROCESS");
	                if (postProcessScript != null && !postProcessScript.isEmpty()) {
	                    PostProcessor ppScript = createPostProcessScript(
	                            postProcessScript, bindingMap);
	                    if (ppScript != null) {
	                        executePostProcess(ppScript, bindingMap, null,
	                                "SET_PASSWORD", connectorSuccess);
	                    }
	                }
	            }

	        }

	        if (callPostProcessor("SET_PASSWORD", null, bindingMap) != ProvisioningConstants.SUCCESS) {
	            response.setStatus(ResponseStatus.FAILURE);
	            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
	            return response;
	        }

	        response.setStatus(ResponseStatus.SUCCESS);
	        return response;
        } finally {
        	auditLogService.enqueue(auditLog);
        }
    }

    /* ********* Helper Methods --------------- */

    private boolean syncAllowed(final ResourceEntity resource) {
        boolean retVal = true;
        if (resource != null) {
            retVal = !StringUtils.equalsIgnoreCase(
                    getResourceProperty(resource, "INCLUDE_IN_PASSWORD_SYNC"),
                    "N");
        }
        return retVal;
    }

    private String getResourceProperty(final ResourceEntity resource,
            final String propertyName) {
        String retVal = null;
        if (resource != null && StringUtils.isNotBlank(propertyName)) {
            final ResourcePropEntity property = resource
                    .getResourceProperty(propertyName);
            if (property != null) {
                retVal = property.getPropValue();
            }
        }
        return retVal;
    }

    private String getResProperty(Set<ResourceProp> resPropSet,
            String propertyName) {
        String value = null;

        if (resPropSet == null) {
            return null;
        }
        Iterator<ResourceProp> propIt = resPropSet.iterator();
        while (propIt.hasNext()) {
            ResourceProp prop = propIt.next();
            if (prop.getName().equalsIgnoreCase(propertyName)) {
                return prop.getPropValue();
            }
        }

        return value;
    }

    private Set<Resource> getResourcesForRoles(Set<Role> roleSet) {
        log.debug("GetResourcesForRole().....");
        final Set<Resource> resourceList = new HashSet<Resource>();
        if (CollectionUtils.isNotEmpty(roleSet)) {
            for (Role rl : roleSet) {
                if (rl.getRoleId() != null) {
                    List<ResourceEntity> resources = resourceService.getResourcesForRole(rl.getRoleId(), -1, -1);
                    if (CollectionUtils.isNotEmpty(resources)) {
                        resourceList.addAll(resourceDozerConverter.convertToDTOList(resources, false));
                    }
                }
            }
        }
        return resourceList;
    }

    @Override
    @Transactional
    public List<String> getAttributesList(String mSysId,
                                          LookupRequest config) {
        if (mSysId == null)
            return null;
        ManagedSysDto msys = managedSysService.getManagedSys(mSysId);
        if (msys == null)
            return null;
        LookupAttributeResponse response = connectorAdapter
                .lookupAttributes(msys.getConnectorId(), config, MuleContextProvider.getCtx());
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            List<String> attributeNames = new LinkedList<String>();
            for(ExtensibleAttribute attr : response.getAttributes()) {
                attributeNames.add(attr.getName());
            }
            return attributeNames;
        } else  {
            return null;
        }
    }
}
