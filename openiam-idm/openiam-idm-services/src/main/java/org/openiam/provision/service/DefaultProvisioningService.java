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

import groovy.lang.MissingPropertyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
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
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationEnum;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.idm.srvc.prov.request.dto.OperationBean;
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
import org.openiam.provision.resp.ManagedSystemViewerResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.provision.type.ManagedSystemViewerBean;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private ProvisionSelectedResourceHelper provisionSelectedResourceHelper;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Value(",${org.openiam.debug.hidden.attributes},")
    private String hiddenAttributes;

    private static final Log log = LogFactory.getLog(DefaultProvisioningService.class);
	private String errorDescription;

	public Response testConnectionConfig(String managedSysId, String requesterId) {
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_TEST.value());
        try {
            Response response = validateConnectionConfig.testConnection(managedSysId, MuleContextProvider.getCtx());
            if (response != null && response.isSuccess()) {
                idmAuditLog.succeed();
                idmAuditLog.setAuditDescription("Managed system ID: " + managedSysId);
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason("Managed system ID: " + managedSysId);
            }
            return response;
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
    }

    @Override
    public ProvisionUserResponse provisionUsersToResource(final List<String> usersIds, final String requestorUserId, final List<String> resourceList) {
        return provisionSelectedResourceHelper.provisionSelectedResources(usersIds, requestorUserId, resourceList);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResource(@WebParam(name = "usersIds", targetNamespace = "") List<String> users, @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId, @WebParam(name = "resourcesIds", targetNamespace = "") List<String> resources) {
        return deprovisionSelectedResource.deprovisionSelectedResourcesAsync(users,requestorUserId,resources);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResourceByRole(@WebParam(name = "usersIds", targetNamespace = "") List<String> users, @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId, @WebParam(name = "rolesIds", targetNamespace = "") List<String> roles) {
        Set<String> resourceIds = new HashSet<String>();
        for(String roleId : roles) {
            ResourceSearchBean rsb = new ResourceSearchBean();
            rsb.setDeepCopy(false);
            List<org.openiam.idm.srvc.res.dto.Resource> resources = resourceDataService.getResourcesForRole(roleId, -1, -1, rsb, null);
            for(Resource res : resources) {
                resourceIds.add(res.getId());
            }
        }
        return deprovisionSelectedResource.deprovisionSelectedResourcesAsync(users,requestorUserId,resourceIds);
    }

    @Override
    public ProvisionUserResponse deProvisionUsersToResourceByGroup(@WebParam(name = "usersIds", targetNamespace = "") List<String> users, @WebParam(name = "requestorUserId", targetNamespace = "") String requestorUserId, @WebParam(name = "groupsIds", targetNamespace = "") List<String> groups) {
        Set<String> resourceIds = new HashSet<String>();
        for(String groupId : groups) {
            ResourceSearchBean rsb = new ResourceSearchBean();
            rsb.setDeepCopy(false);
            List<org.openiam.idm.srvc.res.dto.Resource> resources = resourceDataService.getResourcesForGroup(groupId, -1, -1, rsb, null);
            for(Resource res : resources) {
                resourceIds.add(res.getId());
            }
        }
        return deprovisionSelectedResource.deprovisionSelectedResourcesAsync(users,requestorUserId,resourceIds);
    }

    @Override
    public ProvisionUserResponse provisionUsersToResourceByRole(final List<String> usersIds, final String requestorUserId, final List<String> roleList) {
        Set<String> resourceIds = new HashSet<String>();
        for(String roleId : roleList) {
            ResourceSearchBean rsb = new ResourceSearchBean();
            rsb.setDeepCopy(false);
            rsb.setResourceTypeId(ResourceSearchBean.TYPE_MANAGED_SYS);
            List<org.openiam.idm.srvc.res.dto.Resource> resources = resourceDataService.getResourcesForRole(roleId, -1, -1, rsb, null);
            for(Resource res : resources) {
                resourceIds.add(res.getId());
            }
        }
        return provisionSelectedResourceHelper.provisionSelectedResources(usersIds, requestorUserId, resourceIds);
    }

    @Override
    public ProvisionUserResponse provisionUsersToResourceByGroup(final List<String> usersIds, final String requestorUserId, final List<String> groupList) {
        Set<String> resourceIds = new HashSet<String>();
        for(String groupId : groupList) {
            ResourceSearchBean rsb = new ResourceSearchBean();
            rsb.setDeepCopy(false);
            List<org.openiam.idm.srvc.res.dto.Resource> resources = resourceDataService.getResourcesForGroup(groupId, -1, -1, rsb, null);
            for(Resource res : resources) {
                resourceIds.add(res.getId());
            }
        }
        return provisionSelectedResourceHelper.provisionSelectedResources(usersIds, requestorUserId, resourceIds);
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
        return addUser(pUser, null);
    }

    private ProvisionUserResponse addUser(final ProvisionUser pUser, final IdmAuditLog auditLog) {
        final List<ProvisionDataContainer> dataList = new LinkedList<ProvisionDataContainer>();

        ProvisionUserResponse res = new ProvisionUserResponse();
        res.setStatus(ResponseStatus.FAILURE);
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            res = transactionTemplate.execute(new TransactionCallback<ProvisionUserResponse>() {
                @Override
                public ProvisionUserResponse doInTransaction(TransactionStatus status) {
                    IdmAuditLog idmAuditLog = new IdmAuditLog();

                    idmAuditLog.setRequestorUserId(pUser.getRequestorUserId());
                    idmAuditLog.setRequestorPrincipal(pUser.getRequestorLogin());
                    idmAuditLog.setAction(AuditAction.CREATE_USER.value());
                    idmAuditLog.setAuditDescription("Provisioning add user: " + pUser.getId()
                            + " with first/last name: " + pUser.getFirstName() + "/" + pUser.getLastName());

                    if (auditLog != null) {
                        auditLog.addChild(idmAuditLog);
                        idmAuditLog.addParent(auditLog);
                        idmAuditLog = auditLogService.save(idmAuditLog);
                    }
                    idmAuditLog = auditLogService.save(idmAuditLog);


                    ProvisionUserResponse tmpRes = addModifyUser(pUser, true, dataList, idmAuditLog);

                    idmAuditLog = auditLogService.save(idmAuditLog);
                    return tmpRes;
                }
            });

            if (res.isSuccess()) {
                provQueueService.enqueue(dataList);
            }
        } catch (Throwable t) {
            log.error("Can't add user", t);
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
        return modifyUser(pUser, null);
    }

    private ProvisionUserResponse modifyUser(final ProvisionUser pUser, final IdmAuditLog auditLog) {
        final List<ProvisionDataContainer> dataList = new LinkedList<ProvisionDataContainer>();
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        ProvisionUserResponse res = new ProvisionUserResponse();
        res.setStatus(ResponseStatus.SUCCESS);

        try {
            res = transactionTemplate.execute(new TransactionCallback<ProvisionUserResponse>() {
                @Override
                public ProvisionUserResponse doInTransaction(TransactionStatus status) {
                    IdmAuditLog idmAuditLog = new IdmAuditLog();
                    idmAuditLog.setRequestorUserId(pUser.getRequestorUserId());
                    idmAuditLog.setRequestorPrincipal(pUser.getRequestorLogin());
                    idmAuditLog.setAction(AuditAction.MODIFY_USER.value());
                    LoginEntity loginEntity = loginManager.getByUserIdManagedSys(pUser.getId(),sysConfiguration.getDefaultManagedSysId());
                    idmAuditLog.setTargetUser(pUser.getId(),loginEntity.getLogin());
                    idmAuditLog.setAuditDescription("Provisioning modify user: " + pUser.getId()
                            + " with primary identity: " + loginEntity);
                    if (auditLog != null) {
                        auditLog.addChild(idmAuditLog);
                        idmAuditLog.addParent(auditLog);
                        auditLogService.save(auditLog);
                    }
                    idmAuditLog = auditLogService.save(idmAuditLog);
                    ProvisionUserResponse tmpRes = addModifyUser(pUser, false, dataList, idmAuditLog);
                    idmAuditLog = auditLogService.save(idmAuditLog);
                    return tmpRes;
                }
            });

            if (res.isSuccess()) {
                provQueueService.enqueue(dataList);

            }
        } catch (Throwable t) {
            t.printStackTrace();
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
            // no startDate++ specified = assume that we can provision now
            return true;
        }

        if (!user.isProvisionOnStartDate()) {
            return true;
        }

        return !curDate.before(startDate);

    }

    @Override
    @Transactional
    public ProvisionUserResponse deleteByUserWithSkipManagedSysList(String userId, UserStatusEnum status, String requestorId, List<String> skipManagedSysList) {
        return deleteByUserWithSkipManagedSysList(userId, status, requestorId, skipManagedSysList, null);
    }

    private ProvisionUserResponse deleteByUserWithSkipManagedSysList(String userId, UserStatusEnum status, String requestorId, List<String> skipManagedSysList, IdmAuditLog auditLog) {
        log.debug("----deleteByUserId called.------");

        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), loginEntityList);

        return deleteUserWithSkipManagedSysList(sysConfiguration.getDefaultManagedSysId(),
                primaryIdentity.getLogin(), status, requestorId, skipManagedSysList, auditLog);
     }

    @Override
    @Transactional
    public ProvisionUserResponse deleteByUserId(String userId, UserStatusEnum status, String requestorId) {
        return deleteByUserWithSkipManagedSysList(userId, status, requestorId, null);
    }

    @Override
    @Transactional
    public ProvisionUserResponse deleteUser(String managedSystemId, String principal, UserStatusEnum status,
                                            String requestorId) {
        return deleteUserWithSkipManagedSysList(managedSystemId, principal, status, requestorId, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#deleteUserWithSkipManagedSysList(java.lang.String
     * , java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ProvisionUserResponse deleteUserWithSkipManagedSysList(String managedSystemId, String principal, UserStatusEnum status,
                                            String requestorId, List<String> skipManagedSysList) {
        return deleteUserWithSkipManagedSysList(managedSystemId, principal, status, requestorId, skipManagedSysList, null);
    }

    private ProvisionUserResponse deleteUserWithSkipManagedSysList(String managedSystemId, String principal, UserStatusEnum status,
                String requestorId, List<String> skipManagedSysList, IdmAuditLog auditLog) {
        log.debug("----deleteUser called.------");

        ProvisionUserResponse response = new ProvisionUserResponse(ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requestorId);
        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);
        idmAuditLog.setRequestorPrincipal(lRequestor.getLogin());
        final String action = (status == UserStatusEnum.DELETED)
                ? AuditAction.USER_DEACTIVATE.value()
                : AuditAction.PROVISIONING_DELETE.value();
        idmAuditLog.setAction(action);

        if (auditLog != null) {
            auditLog.addChild(idmAuditLog);
        }

        try {
            if (status != UserStatusEnum.DELETED && status != UserStatusEnum.REMOVE && status != UserStatusEnum.LEAVE
                    && status != UserStatusEnum.TERMINATED && status != UserStatusEnum.RETIRED) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.USER_STATUS);
                return response;
            }

            String requestId = "R" + UUIDGen.getUUID();

            // get the user object associated with this principal
            final LoginEntity login = loginManager.getLoginByManagedSys(principal, managedSystemId);
            if (login == null) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorText());
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

            idmAuditLog.setTargetUser(usr.getId(), principal);

            ProvisionUser pUser = new ProvisionUser(usr);
            // SET PRE ATTRIBUTES FOR DEFAULT SYS SCRIPT
            bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSystemId);
            bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
            bindingMap.put(TARGET_SYS_RES_ID, null);

            if (callPreProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                return response;
            }

            if (status != UserStatusEnum.REMOVE
                    && (usr.getStatus() == UserStatusEnum.DELETED || usr.getStatus() == UserStatusEnum.TERMINATED)) {
                log.debug("User was already deleted. Nothing more to do.");
                return response;
            }

            Set<String> processedResources = new HashSet<String>();
            if (!managedSystemId.equals(sysConfiguration.getDefaultManagedSysId())) {
                final IdmAuditLog idmAuditLogChild = new IdmAuditLog();
                idmAuditLogChild.setRequestorUserId(requestorId);
                idmAuditLogChild.setRequestorPrincipal(lRequestor.getLogin());
                idmAuditLogChild.setAction(AuditAction.PROVISIONING_DELETE_IDENTITY.value());
                idmAuditLogChild.setManagedSysId(managedSystemId);
                idmAuditLogChild.setTargetUser(login.getUserId(), login.getLogin());
                // managedSysId point to one of the seconardary identities- just
                // terminate that identity

                // call delete on the connector
                ManagedSysDto mSys = managedSysService.getManagedSys(managedSystemId);

                idmAuditLogChild.setTargetManagedSys(mSys.getId(), mSys.getName());

                ManagedSystemObjectMatch matchObj = null;
                ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(mSys.getId(), ManagedSystemObjectMatch.USER);
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

                if (resourceId != null) {
                    processedResources.add(resourceId);
                    res = resourceDataService.getResource(resourceId, null);
                    if (res != null) {
                        String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                        if (preProcessScript != null && !preProcessScript.isEmpty()) {
                            PreProcessor<ProvisionUser> ppScript = createPreProcessScript(preProcessScript, bindingMap);
                            if (ppScript != null) {
                                executePreProcess(ppScript, bindingMap, pUser, "DELETE");
                            }
                        }
                    }
                }

                ResponseType resp = new ResponseType();
                resp.setStatus(StatusCodeType.SUCCESS);
                if (CollectionUtils.isEmpty(skipManagedSysList) || !skipManagedSysList.contains(managedSystemId)) {
                    resp = delete(loginDozerConverter.convertToDTO(login, true), requestId, mSys, matchObj);
                }

                boolean connectorSuccess = false;
                if (resp.getStatus() == StatusCodeType.SUCCESS) {
                    connectorSuccess = true;
                    // if REMOVE status: we do physically delete identity for
                    // selected managed system after successful provisioning
                    // if DELETE status: we don't delete identity from database only
                    // set status to INACTIVE
                    if (status == UserStatusEnum.REMOVE) {
                        loginManager.deleteLogin(login.getLoginId());
                    } else {
                        login.setStatus(LoginStatusEnum.INACTIVE);
                        login.setProvStatus(ProvLoginStatusEnum.DELETED);
                        login.setAuthFailCount(0);
                        login.setPasswordChangeCount(0);
                        login.setIsLocked(1);
                        loginManager.updateLogin(login);
                    }

                    idmAuditLogChild.succeed();
                } else {
                    login.setStatus(LoginStatusEnum.INACTIVE);
                    login.setProvStatus(ProvLoginStatusEnum.FAIL_DELETE);
                    loginManager.updateLogin(login);

                    idmAuditLogChild.fail();
                    idmAuditLogChild.setFailureReason(resp.getErrorMsgAsStr());
                }

                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
                String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                if (postProcessScript != null && !postProcessScript.isEmpty()) {
                    PostProcessor<ProvisionUser> ppScript = createPostProcessScript(postProcessScript, bindingMap);
                    if (ppScript != null) {
                        executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
                    }
                }
                idmAuditLog.addChild(idmAuditLogChild);

            } else {
                // delete user and all its identities.
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
                List<LoginEntity> principalList = loginManager.getLoginByUser(userId);
                if (principalList != null) {
                    for (LoginEntity l : principalList) {
                        final IdmAuditLog idmAuditLogChild = new IdmAuditLog();
                        idmAuditLogChild.setRequestorUserId(requestorId);
                        idmAuditLogChild.setRequestorPrincipal(lRequestor.getLogin());
                        idmAuditLogChild.setAction(AuditAction.PROVISIONING_DELETE_IDENTITY.value());
                        idmAuditLogChild.setTargetUser(l.getUserId(), l.getLogin());
                        idmAuditLogChild.setManagedSysId(managedSystemId);

                        // this try-catch block for protection other operations and
                        // other resources if one resource was fall with error
                        try {
                            // only add the connectors if its a secondary
                            // identity.
                            if (!l.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {

                                ManagedSysDto mSys = managedSysService.getManagedSys(l.getManagedSysId());
                                idmAuditLogChild.setTargetManagedSys(mSys.getId(), mSys.getName());

                                ManagedSystemObjectMatch matchObj = null;
                                ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(
                                        mSys.getId(), ManagedSystemObjectMatch.USER);
                                if (matchObjAry != null && matchObjAry.length > 0) {
                                    matchObj = matchObjAry[0];
                                    bindingMap.put(MATCH_PARAM, matchObj);
                                }
                                log.debug("Deleting id=" + l.getLogin());
                                log.debug("- delete using managed sys id=" + mSys.getId());

                                // pre-processing
                                bindingMap.put(IDENTITY, l);
                                bindingMap.put(TARGET_SYS_RES, null);

                                Resource resource = null;
                                String resourceId = mSys.getResourceId();

                                // SET PRE ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(TARGET_SYSTEM_IDENTITY, l.getLogin());
                                bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
                                bindingMap.put(TARGET_SYS_RES_ID, resourceId);
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);

                                if (resourceId != null) {
                                    processedResources.add(resourceId);
                                    resource = resourceDataService.getResource(resourceId, null);
                                    if (resource != null) {
                                        bindingMap.put(TARGET_SYS_RES, resource);

                                        String preProcessScript = getResProperty(resource.getResourceProps(),
                                                "PRE_PROCESS");
                                        if (preProcessScript != null && !preProcessScript.isEmpty()) {
                                            PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                            if (ppScript != null) {
                                                if (executePreProcess(ppScript, bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }

                                ResponseType resp = new ResponseType();
                                resp.setStatus(StatusCodeType.SUCCESS);
                                if (CollectionUtils.isEmpty(skipManagedSysList) || !skipManagedSysList.contains(l.getManagedSysId())) {
                                    resp = delete(loginDozerConverter.convertToDTO(l, true), requestId, mSys, matchObj);
                                }

                                boolean connectorSuccess = false;
                                if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                    connectorSuccess = true;
                                    l.setStatus(LoginStatusEnum.INACTIVE);
                                    l.setProvStatus(ProvLoginStatusEnum.DELETED);
                                    l.setAuthFailCount(0);
                                    l.setPasswordChangeCount(0);
                                    l.setIsLocked(1);

                                    idmAuditLogChild.succeed();
                                } else {
                                    l.setStatus(LoginStatusEnum.INACTIVE);
                                    l.setProvStatus(ProvLoginStatusEnum.FAIL_DELETE);

                                    idmAuditLogChild.fail();
                                    idmAuditLogChild.setFailureReason(resp.getErrorMsgAsStr());
                                }
                                // SET POST ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
                                if (resource != null) {
                                    String postProcessScript = getResProperty(resource.getResourceProps(),
                                            "POST_PROCESS");
                                    if (postProcessScript != null && !postProcessScript.isEmpty()) {
                                        PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
                                        }
                                    }
                                }
                                if (status == UserStatusEnum.REMOVE) {
                                    loginManager.deleteLogin(login.getLogin());
                                }
                            }

                        } catch (Throwable tw) {
                            log.error(l, tw);
                        } finally {
                            idmAuditLog.addChild(idmAuditLogChild);
                        }
                    }
                }
            }
            // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT
            bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
            bindingMap.put(TARGET_SYS_RES_ID, null);

            if (callPostProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorText());
                return response;
            } else {
                idmAuditLog.succeed();
            }

            if (status == UserStatusEnum.REMOVE) {
                try {
                    userMgr.removeUser(userId);
                } catch (Throwable e) {
                    log.error("Can't remove user", e);
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.FAIL_SQL_ERROR);
                    return response;
                }
            } else {
                pUser.setStatus(status);
                pUser.setSecondaryStatus(UserStatusEnum.INACTIVE);
                pUser.setLastUpdatedBy(requestorId);
                pUser.setLastUpdate(new Date());
                pUser.setNotProvisioninResourcesIds(processedResources);
                modifyUser(pUser);
            }

        } finally {
            if (auditLog == null) {
                auditLogService.enqueue(idmAuditLog);
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    @Override
    @Transactional
    public ProvisionUserResponse deprovisionSelectedResources(String userId, String requestorUserId,
            List<String> resourceList) {
        return deprovisionSelectedResource.deprovisionSelectedResources(userId, requestorUserId, resourceList);
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
    public Response lockUser(String userId, AccountLockEnum operation, String requestorId) {
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
                log.error(String.format("Primary identity for UserId %s not found", userId));
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
         * auditHelper.addLog(auditReason, logDomainId, logLoginId,
         * "IDM SERVICE", requestorId, "USER", "USER", logUserId, null,
         * "SUCCESS", null, null, null, requestId, auditReason, null, null,
         * null, login, domain);
         */
        final List<LoginEntity> loginList = loginManager.getLoginByUser(userId);
        for (final LoginEntity userLogin : loginList) {
            if (userLogin != null) {
                if (userLogin.getManagedSysId() != null && !userLogin.getManagedSysId().equals("0")) {
                    ResponseType responsetype = null;
                    final String managedSysId = userLogin.getManagedSysId();
                    final ManagedSysDto managedSys = managedSysService.getManagedSys(managedSysId);
                    final Login login = loginDozerConverter.convertToDTO(userLogin, false);
                    if (AccountLockEnum.LOCKED.equals(operation) || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                        final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                        suspendCommand.setObjectIdentity(userLogin.getLogin());
                        suspendCommand.setTargetID(managedSysId);
                        suspendCommand.setRequestID("R" + System.currentTimeMillis());
                        suspendCommand.setExtensibleObject(buildMngSysAttributes(login, "SUSPEND"));
                        connectorAdapter.suspendRequest(managedSys, suspendCommand, MuleContextProvider.getCtx());
                    } else {
                        final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                        resumeRequest.setObjectIdentity(userLogin.getLogin());
                        resumeRequest.setTargetID(managedSysId);
                        resumeRequest.setRequestID("R" + System.currentTimeMillis());
                        resumeRequest.setExtensibleObject(buildMngSysAttributes(login, "RESUME"));
                        connectorAdapter.resumeRequest(managedSys, resumeRequest, MuleContextProvider.getCtx());
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
                    log.info(String.format("Response status=%s", response.getStatus()));

                    // TODO: process the result of the WS call to resume/suspend
                    // of teh connector
                }
            }
        }
        final List<RoleEntity> roleList = roleDataService.getUserRoles(user.getId(), null, 0, Integer.MAX_VALUE);
        final Login primLogin = loginDozerConverter.convertToDTO(lg, false);
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (final RoleEntity role : roleList) {
                final List<Resource> resourceList = resourceDataService.getResourcesForRole(role.getId(), 0,
                        Integer.MAX_VALUE, null, null);
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (final Resource resource : resourceList) {
                        ManagedSysDto managedSys = managedSysService.getManagedSysByResource(resource.getId());
                        if (managedSys != null) {
                            ResponseType responsetype = null;
                            if (AccountLockEnum.LOCKED.equals(operation)
                                    || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                                final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                                suspendCommand.setObjectIdentity(lg.getLogin());
                                suspendCommand.setTargetID(managedSys.getId());
                                suspendCommand.setRequestID("R" + System.currentTimeMillis());
                                suspendCommand.setExtensibleObject(buildMngSysAttributes(primLogin, "SUSPEND"));
                                connectorAdapter.suspendRequest(managedSys, suspendCommand,
                                        MuleContextProvider.getCtx());
                            } else {
                                final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                                resumeRequest.setObjectIdentity(lg.getLogin());
                                resumeRequest.setTargetID(managedSys.getId());
                                resumeRequest.setRequestID("R" + System.currentTimeMillis());
                                resumeRequest.setExtensibleObject(buildMngSysAttributes(primLogin, "RESUME"));
                                connectorAdapter.resumeRequest(managedSys, resumeRequest, MuleContextProvider.getCtx());
                            }

                            if (responsetype.getStatus() == null) {
                                log.info("Response status is null");
                                response.setStatus(ResponseStatus.FAILURE);
                                return response;
                            }
                            log.info(String.format("Response status=%s", response.getStatus()));

                        }
                    }
                }
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    @Override
    @Transactional
    public void updateResources(UserEntity userEntity, ProvisionUser pUser, Set<Resource> resourceSet, Set<Resource> deleteResourceSet, IdmAuditLog parentLog) {
        super.updateResources(userEntity, pUser, resourceSet, deleteResourceSet, parentLog);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private ProvisionUserResponse addModifyUser(ProvisionUser pUser, boolean isAdd,
            List<ProvisionDataContainer> dataList, final IdmAuditLog auditLog) {

        if (isAdd) {
            log.debug("--- DEFAULT PROVISIONING SERVICE: addUser called ---");
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "--- DEFAULT PROVISIONING SERVICE: addUser called ---");
        } else {
            log.debug("--- DEFAULT PROVISIONING SERVICE: modifyUser called ---");
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "--- DEFAULT PROVISIONING SERVICE: modifyUser called ---");
        }

        UserEntity userEntity = !isAdd ? userMgr.getUser(pUser.getId()) : new UserEntity();
        if (userEntity == null) {
            throw new IllegalArgumentException("UserId='" + pUser.getId() + "' is not valid");
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
                // start date is in the future flag says that we should prov
                // after the startdate
                pUser.setStatus(UserStatusEnum.PENDING_START_DATE);
            }
        }

        // bind the objects to the scripting engine
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("org", pUser.getPrimaryOrganization());
        bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
        bindingMap.put(USER, pUser);
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, null);
        if (!isAdd) {
            ProvisionUser u = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
            provisionSelectedResourceHelper.setCurrentSuperiors(u);
            bindingMap.put("userBeforeModify", u);
        }
        int callPreProcessor = callPreProcessor(isAdd ? "ADD" : "MODIFY", pUser, bindingMap);
        auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "callPreProcessor result="
                + (callPreProcessor == 1 ? "SUCCESS" : "FAIL"));
        if (callPreProcessor != ProvisioningConstants.SUCCESS) {
            auditLog.fail();
            auditLog.setFailureReason("PreProcessor error.");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return resp;
        }
        // make sure that our object as the attribute set that will be used for
        // audit logging
        checkAuditingAttributes(pUser);

        if (!isAdd) {
            // get the current roles
            List<Role> curRoleList = roleDataService.getUserRolesAsFlatList(pUser.getId());

            // get all groups for user
            List<Group> curGroupList = groupDozerConverter.convertToDTOList(
                    groupManager.getGroupsForUser(pUser.getId(), null, -1, -1), false);
            // make the role and group list before these updates available to
            // the
            // attribute policies
            bindingMap.put("currentRoleList", curRoleList);
            bindingMap.put("currentGroupList", curGroupList);
        }

        // dealing with principals
        if (!isAdd) {
            List<LoginEntity> curPrincipalList = userEntity.getPrincipalList();
            // check that a primary identity exists some where
            LoginEntity curPrimaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(),
                    curPrincipalList);
            if (curPrimaryIdentity == null && pUser.getPrincipalList() == null) {
                log.debug("Primary identity not found...");
                auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Primary identity not found...");
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
            LoginEntity dupPrincipal = loginManager.getLoginByManagedSys(primaryLogin.getLogin(),
                    primaryLogin.getManagedSysId());

            if (dupPrincipal != null) {
                // identity exists
                auditLog.fail();
                auditLog.setFailureReason(ResponseCode.DUPLICATE_PRINCIPAL + ": " + dupPrincipal);
                auditLog.addAttribute(AuditAttributeName.DESCRIPTION, ResponseCode.DUPLICATE_PRINCIPAL + ": "
                        + dupPrincipal.getLogin());
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
        updateUserProperties(userEntity, pUser, auditLog);

        if (isAdd) {
            try {
                userMgr.addUser(userEntity); // Need to have userId to
                                           // encrypt/decrypt password
                pUser.setId(userEntity.getId());
                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                auditLog.setTargetUser(userEntity.getId(), login.getLogin());
            } catch (Exception e) {
                auditLog.fail();
                auditLog.setFailureReason("Exception while creating user: " + e.getMessage());
                auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                        "Exception while creating user: " + e.getMessage());
                log.error("Exception while creating user", e);
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_OTHER);
                return resp;
            }
        }

        // update addresses
        updateAddresses(userEntity, pUser, auditLog);

        // update phones
        updatePhones(userEntity, pUser, auditLog);

        // update emails
        updateEmails(userEntity, pUser, auditLog);

        // update supervisors
        updateSupervisors(userEntity, pUser, auditLog);

        // update groups
        updateGroups(userEntity, pUser, auditLog);

        // update roles
        Set<Role> roleSet = new HashSet<Role>();
        Set<Role> deleteRoleSet = new HashSet<Role>();
        updateRoles(userEntity, pUser, roleSet, deleteRoleSet, auditLog);
        bindingMap.put("userRole", roleSet);

        // update organization associations
        updateAffiliations(userEntity, pUser, auditLog);

        // Set of resources that a person should have based on their active
        // roles
        Set<Resource> resourceSet = getResourcesForRoles(roleSet);

        List<Organization> orgs = orgManager.getOrganizationsForUserLocalized(pUser.getId(), null, 0, 100, null);
        for(Organization org : orgs) {
            Resource res = resourceDataService.getResource(org.getAdminResourceId(), null);
            if(res != null) {
                resourceSet.add(res);
            }
        }


        // Set of resources that are to be removed based on roles that are to be
        // deleted
        Set<Resource> deleteResourceSet = getResourcesForRoles(deleteRoleSet);

        // update resources, update resources sets
        updateResources(userEntity, pUser, resourceSet, deleteResourceSet, auditLog);

        // update principals
        updatePrincipals(userEntity, pUser, auditLog);

        // get primary identity and bind it for the groovy scripts
        LoginEntity primaryIdentityEntity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(),
                userEntity.getPrincipalList());
        Login primaryIdentity = (primaryIdentityEntity != null) ? loginDozerConverter.convertToDTO(
                primaryIdentityEntity, false) : null;

        if (primaryIdentity == null) { // Try to generate a new primary identity
                                       // from scratch
            LoginEntity entity = loginDozerConverter.convertToEntity(buildPrimaryPrincipal(bindingMap, scriptRunner),
                    false);
            try {
                entity.setUserId(userEntity.getId());
                userEntity.getPrincipalList().add(entity);
                entity.setPassword(loginManager.encryptPassword(entity.getUserId(), entity.getPassword()));
                primaryIdentity = loginDozerConverter.convertToDTO(entity, false);
            } catch (Exception ee) {
                log.error(ee);
                ee.printStackTrace();
            }
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
                } catch (Exception e) {
                    auditLog.fail();
                    auditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                    resp.setStatus(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                    return resp;
                }
                bindingMap.put("password", decPassword);
            }
            bindingMap.put("lg", primaryIdentity);

        } else {
            auditLog.fail();
            auditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "Primary identity not found for user=" + userEntity.getId());
            log.debug("Primary identity not found for user=" + userEntity.getId());
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return resp;
        }

        // update attributes
        updateUserAttributes(userEntity, pUser, auditLog);

        log.debug("Binding active roles to scripting");
        log.debug("- role set -> " + roleSet);
        log.debug("- Primary Identity : " + primaryIdentity);
        auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "- Primary Identity: " + primaryIdentity.getLogin());
        ProvisionUser finalProvUser = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));

        if (!isAdd) {
            //If identity for resource exists and it's status is 'INACTIVE' user should be deprovisioned from target system
            Set<Resource> inactiveResources = new HashSet<Resource>();
            for (Resource res : resourceSet) {
                String managedSysId = managedSysDaoService.getManagedSysIdByResource(res.getId(),"ACTIVE");

                if (AttributeOperationEnum.NO_CHANGE.equals(res.getOperation())) { // if not adding resource
                    for (LoginEntity l : userEntity.getPrincipalList()) {
                        if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                            if (LoginStatusEnum.INACTIVE.equals(l.getStatus())) {
                                inactiveResources.add(res);
                            }
                            break;
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(inactiveResources)) {
                resourceSet.removeAll(inactiveResources);
                deleteResourceSet.addAll(inactiveResources); // inactive resources should be marked for deletion
            }
        }

        log.debug("Resources to be added ->> " + resourceSet);
        log.debug("Delete the following resources ->> " + deleteResourceSet);

        // deprovision resources
        if (!isAdd) {
            if (CollectionUtils.isNotEmpty(deleteResourceSet)) {

                List<Resource> resources = orderResources("DELETE", finalProvUser, deleteResourceSet, bindingMap);

                for (Resource res : resources) {
                    // skip provisioning for resource if it in NotProvisioning
                    // set
                    if (pUser.getNotProvisioninResourcesIds().contains(res.getId())) {
                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Skip De-Provisioning for resource: "
                                + res.getName());
                        continue;
                    }
                    try {
                        // Protects other resources if one resource failed

                        ProvisionDataContainer data = deprovisionSelectedResource.deprovisionResourceDataPrepare(res, userEntity, pUser, requestId, bindingMap);

                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "De-Provisioning for resource: " + res.getName());
                        if (data != null) {
                            data.setParentAuditLogId(auditLog.getId());
                            dataList.add(data);
                        }
                    } catch (Throwable tw) {
                        auditLog.fail();
                        auditLog.setFailureReason(
                                "Deprovisioning resource : " + res.getName() + " for user with primary identity: "
                                        + primaryIdentity + ". Exception: " + tw.getMessage());
                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "Deprovisioning resource : " + res.getName() + " for user with primary identity: "
                                        + primaryIdentity + ". Exception: " + tw.getMessage());
                        log.error(res, tw);
                    }
                }
            }
        }

        // provision resources
        if (provInTargetSystemNow) {
            if (CollectionUtils.isNotEmpty(resourceSet)) {

                List<Resource> resources = orderResources("ADD", finalProvUser, resourceSet, bindingMap);

                for (Resource res : resources) {
                    // skip provisioning for resource if it in NotProvisioning
                    // set
                    if (pUser.getNotProvisioninResourcesIds().contains(res.getId())) {
                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "Skip Provisioning to resource: " + res.getName());
                        continue;
                    }
                    try {
                        if (pUser.getSrcSystemId() != null) {
                            // do check if provisioning user has source resource
                            // => we should skip it from double provisioning
                            // reconciliation case
                            ManagedSysDto managedSys = managedSysService.getManagedSys(pUser.getSrcSystemId());
                            if (res.getId().equalsIgnoreCase(managedSys.getResourceId())) {
                                continue;
                            }
                        }
                        // Protects other resources if one resource failed
                        ProvisionDataContainer data = provisionSelectedResourceHelper.provisionResource(res, userEntity, pUser, bindingMap,
                                primaryIdentity, requestId);
                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "Provisioning for resource: " + res.getName());
                        if (data != null) {
                            data.setParentAuditLogId(auditLog.getId());
                            dataList.add(data);
                        }
                    } catch (Throwable tw) {
                        auditLog.fail();
                        auditLog.setFailureReason(
                                "Provisioning resource : " + res.getName() + " for user with primary identity: "
                                        + primaryIdentity + ". Exception: " + tw.getMessage());
                        auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "Provisioning resource : " + res.getName() + " for user with primary identity: "
                                        + primaryIdentity + ". Exception: " + tw.getMessage());
                        log.error(res, tw);
                    }
                }
            }
        }

        if (isAdd) { // send email notifications
            if (pUser.isEmailCredentialsToNewUsers()) {
                sendCredentialsToUser(finalProvUser.getUser(), primaryIdentity.getLogin(), decPassword);
            }
            if (pUser.isEmailCredentialsToSupervisor()) {
                if (pUser.getSuperiors() != null) {
                    Set<User> superiors = pUser.getSuperiors();
                    if (CollectionUtils.isNotEmpty(superiors)) {
                        for (User s : superiors) {
                            sendCredentialsToSupervisor(s, primaryIdentity.getLogin(), decPassword,
                                    finalProvUser.getFirstName() + " " + finalProvUser.getLastName());
                        }
                    }
                }
            }
        }
        int callPostProcessorResult = callPostProcessor(isAdd ? "ADD" : "MODIFY", finalProvUser, bindingMap);
        auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "callPostProcessor result="
                + (callPostProcessorResult == 1 ? "SUCCESS" : "FAIL"));
        if (callPostProcessorResult != ProvisioningConstants.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "PostProcessor error.");
            return resp;
        }
        /* Response object */

        userMgr.updateUser(userEntity);

        if (isAdd) {
            log.debug("DEFAULT PROVISIONING SERVICE: addUser complete");
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DEFAULT PROVISIONING SERVICE: addUser complete");
        } else {
            log.debug("DEFAULT PROVISIONING SERVICE: modifyUser complete");
            auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DEFAULT PROVISIONING SERVICE: modifyUser complete");
        }
        auditLog.succeed();
        resp.setStatus(ResponseStatus.SUCCESS);
        resp.setUser(finalProvUser);
        return resp;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.provision.service.ProvisionService#resetPassword(org.openiam
     * .provision.dto.PasswordSync)
     */
    @Override
    public PasswordResponse resetPassword(PasswordSync passwordSync) {
        return resetPassword(passwordSync, null);
    }

    @Transactional
    public PasswordResponse resetPassword(PasswordSync passwordSync, IdmAuditLog auditLog) {
        log.debug("----resetPassword called.------");
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(passwordSync.getRequestorId());
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(this.sysConfiguration.getDefaultManagedSysId(), loginEntityList);
        idmAuditLog.setRequestorPrincipal(primaryIdentity.getLogin());
        idmAuditLog.setRequestorUserId(passwordSync.getRequestorId());
        idmAuditLog.setAction(AuditAction.PROVISIONING_RESETPASSWORD.value());

        if (auditLog != null) {
            auditLog.addChild(idmAuditLog);
        }

        final PasswordResponse response = new PasswordResponse(ResponseStatus.SUCCESS);
        try {
            final String requestId = "R" + UUIDGen.getUUID();

            // get the user object associated with this principal
            List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());

            idmAuditLog.setUserId(passwordSync.getUserId());
            LoginEntity identity = null;
            if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                for (LoginEntity le : identities) {
                    if (passwordSync.getManagedSystemId().equals(le.getManagedSysId())) {
                        identity = le;
                        break;
                    }
                }
            } else {
                identity = loginManager.getPrimaryIdentity(passwordSync.getUserId());
            }

            if (identity != null) {
                idmAuditLog.setTargetUser(identity.getUserId(), identity.getLogin());

            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }

            String password = passwordSync.getPassword();
            if (StringUtils.isEmpty(password)) {
                // autogenerate the password
                password = String.valueOf(PasswordGenerator.generatePassword(8));
            }
            String encPassword = null;
            try {
                encPassword = loginManager.encryptPassword(identity.getUserId(), password);
            } catch (Exception e) {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }

            List<LoginEntity> principalList = new ArrayList<LoginEntity>();
            if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                principalList.add(identity);
            } else {
                principalList.addAll(identities);
            }

            // reset passwords for all identities with the same password
            for (final LoginEntity lg : principalList) {
                final String managedSysId = lg.getManagedSysId();
                final ManagedSysEntity mSys = managedSystemService.getManagedSysById(managedSysId);
                if (mSys != null) {
                    final ResourceEntity res = resourceService.findResourceById(mSys.getResourceId());
                    log.debug(" - Managed System Id = " + managedSysId);
                    log.debug(" - Resource Id = " + res.getId());

                    final boolean retval = loginManager.resetPassword(lg.getLogin(), lg.getManagedSysId(), encPassword);

                    if (retval) {
                        log.debug(String.format("- Password changed for principal: %s, user: %s, managed sys: %s -",
                                identity.getLogin(), identity.getUserId(), identity.getManagedSysId()));
                        idmAuditLog.succeed();

                        /*
                         * came with merge from v2.3 //check if password should be sent
                         * to the user. if (passwordSync.isSendPasswordToUser()) { //
                         * sendPasswordToUser(usr, password); }
                         */
                        if (passwordSync.getSendPasswordToUser()) {
                            sendResetPasswordToUser(identity, password);
                        }

                    } else {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                        return response;
                    }

                    if (!lg.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {
                        if (syncAllowed(res)) { // check the sync flag
                            log.debug("Sync allowed for managed sys = " + managedSysId);

                            ManagedSystemObjectMatchEntity matchObj = null;
                            final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                                    .managedSysObjectParam(managedSysId, "USER");

                            if (CollectionUtils.isNotEmpty(matchList)) {
                                matchObj = matchList.get(0);
                            }

                            log.info("============== Connector Reset Password call: " + new Date());
                            Login login = loginDozerConverter.convertToDTO(lg, false);
                            ResponseType resp = resetPassword(requestId,
                                    login, password,
                                    managedSysDozerConverter.convertToDTO(mSys, false),
                                    objectMatchDozerConverter.convertToDTO(matchObj, false),
                                    buildMngSysAttributes(login, "RESET_PASSWORD"));
                            log.info("============== Connector Reset Password get : " + new Date());
                            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                                idmAuditLog.succeed();
                                idmAuditLog.setAuditDescription(
                                        "Reset password for resource: " + res.getName() + " for user: "
                                                + lg.getLogin());
                            } else {
                                idmAuditLog.fail();
                                String reason = "";
                                if(resp != null) {
                                    if (resp.getError() != null) {
                                        reason = resp.getError().value();
                                    } else if (StringUtils.isNotBlank(resp.getErrorMsgAsStr())) {
                                        reason = resp.getErrorMsgAsStr();
                                    }
                                }
                                idmAuditLog.setFailureReason(String.format("Reset password for resource %s user %s failed: %s",
                                        mSys.getName(), lg.getLogin(), reason));

                            }
                        }
                    }
                }
            }
        } finally {
            if (auditLog == null) {
                auditLogService.enqueue(idmAuditLog);
            }
        }

        return response;

    }

    private ExtensibleUser buildMngSysAttributes(Login login, String operation) {
        String userId = login.getUserId();
        String managedSysId = login.getManagedSysId();

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            return null;
        }
        ProvisionUser pUser = new ProvisionUser(usr);
        List<ExtensibleAttribute> idmAttrs = buildMngSysAttributesForIDMUser(pUser, managedSysId, operation);

        ExtensibleUser extUser = new ExtensibleUser();
        extUser.setAttributes(idmAttrs);
        return extUser;
    }

    @Override
    @Transactional
    public LookupUserResponse getTargetSystemUser(String principalName, String managedSysId,
            List<ExtensibleAttribute> extensibleAttributes) {
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_LOOKUP.value());

        log.debug("getTargetSystemUser called. for = " + principalName);

        LookupUserResponse response = new LookupUserResponse(ResponseStatus.SUCCESS);
        try {
            response.setManagedSysId(managedSysId);
            response.setPrincipalName(principalName);
            // get the connector for the managedSystem

            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
            ManagedSystemObjectMatchEntity matchObj = null;
            List<ManagedSystemObjectMatchEntity> objList = managedSystemService.managedSysObjectParam(managedSysId,
                    ManagedSystemObjectMatch.USER);
            if (CollectionUtils.isNotEmpty(objList)) {
                matchObj = objList.get(0);
            }

            // do the lookup

            log.debug("Calling lookupRequest ");

            LookupRequest<ExtensibleUser> reqType = new LookupRequest<>();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);
            reqType.setSearchValue(principalName);

            ExtensibleUser extensibleUser = new ExtensibleUser();
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getKeyField())) {
                extensibleUser.setPrincipalFieldName(matchObj.getKeyField());
            }
            extensibleUser.setPrincipalFieldDataType("string");
            extensibleUser.setAttributes(extensibleAttributes);
            reqType.setExtensibleObject(extensibleUser);
            reqType.setTargetID(managedSysId);
            reqType.setHostLoginId(mSys.getUserId());
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
                reqType.setBaseDN(matchObj.getSearchBaseDn());
            }
            String passwordDecoded;
            try {
                passwordDecoded = getDecryptedPassword(mSys);
            } catch (ConnectorDataException e) {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }
            reqType.setHostLoginPassword(passwordDecoded);
            reqType.setHostUrl(mSys.getHostUrl());
            reqType.setScriptHandler(mSys.getLookupHandler());

            SearchResponse responseType = connectorAdapter.lookupRequest(mSys, reqType, MuleContextProvider.getCtx());
            if (responseType.getStatus() == StatusCodeType.FAILURE || responseType.getObjectList().size() == 0) {
                response.setStatus(ResponseStatus.FAILURE);
                return response;
            }

            String targetPrincipalName = responseType.getObjectList().get(0).getObjectIdentity() != null
                    ? responseType.getObjectList().get(0).getObjectIdentity()
                    : parseUserPrincipal(responseType.getObjectList().get(0).getAttributeList());
            response.setPrincipalName(targetPrincipalName);
            response.setAttrList(responseType.getObjectList().get(0).getAttributeList());
            response.setResponseValue(responseType.getObjectList().get(0));

            idmAuditLog.succeed();

        } finally {
            auditLogService.enqueue(idmAuditLog);
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
        final IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setBaseObject(passwordSync);
        auditLog.setAction(AuditAction.CHANGE_PASSWORD.value());
        PasswordValidationResponse response = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        final Map<String, Object> bindingMap = new HashMap<String, Object>();

        try {
            if (callPreProcessor("SET_PASSWORD", null, bindingMap) != ProvisioningConstants.SUCCESS) {
                response.fail();
                response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                auditLog.fail();
                auditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                return response;
            }

            final String requestId = "R" + UUIDGen.getUUID();

            // get the user identities
            List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());

            auditLog.setUserId(passwordSync.getUserId());
            LoginEntity identity = null;
            if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                for (LoginEntity le : identities) {
                    if (passwordSync.getManagedSystemId().equals(le.getManagedSysId())) {
                        identity = le;
                        break;
                    }
                }
            } else {
                identity = loginManager.getPrimaryIdentity(passwordSync.getUserId());
            }

            if (identity != null) {
                auditLog.setTargetUser(identity.getUserId(), identity.getLogin());

            } else {
                auditLog.fail();
                auditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }

            // validate the password against password policy
            final Password pswd = new Password();
            pswd.setManagedSysId(identity.getManagedSysId());
            pswd.setPrincipal(identity.getLogin());
            pswd.setPassword(passwordSync.getPassword());

            try {
                response = passwordManager.isPasswordValid(pswd);
                if (response.isFailure()) {
                    auditLog.fail();
                    auditLog.setFailureReason("Invalid Password");
                    return response;
                }
            } catch (ObjectNotFoundException oe) {
                auditLog.setException(oe);
                log.error("Object not found", oe);
            }

            String encPassword = null;
            try {
                encPassword = loginManager.encryptPassword(identity.getUserId(), passwordSync.getPassword());
            } catch (Exception e) {
                auditLog.fail();
                auditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }

            List<LoginEntity> principalList = new ArrayList<LoginEntity>();
            if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                principalList.add(identity);
            } else {
                principalList.addAll(identities);
            }

            for (final LoginEntity lg : principalList) {

                final String managedSysId = lg.getManagedSysId();
                final ManagedSysEntity mSys = managedSystemService.getManagedSysById(managedSysId);

                if (mSys != null) {
                    final ResourceEntity res = resourceService.findResourceById(mSys.getResourceId());
                    log.debug(" - Managed System Id = " + managedSysId);
                    log.debug(" - Resource Id = " + res.getId());

                    final boolean retval = loginManager.setPassword(lg.getLogin(), managedSysId,
                            encPassword, passwordSync.isPreventChangeCountIncrement());

                    if (retval) {
                        log.debug(String.format("- Password changed for principal: %s, user: %s, managed sys: %s -",
                                identity.getLogin(), identity.getUserId(), identity.getManagedSysId()));
                        auditLog.succeed();

                        /*
                         * came with merge from v2.3 //check if password should be sent
                         * to the user. if (passwordSync.isSendPasswordToUser()) { //
                         * sendPasswordToUser(usr, password); }
                         */
                        if (passwordSync.getSendPasswordToUser()) {
                            sendResetPasswordToUser(identity, passwordSync.getPassword());
                        }

                    } else {
                        auditLog.fail();
                        auditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                        return response;
                    }

                    if (!managedSysId.equals(sysConfiguration.getDefaultManagedSysId())) {
                        if (syncAllowed(res)) { // check the sync flag
                            log.debug("Sync allowed for managed sys = " + managedSysId);

                            // pre-process

                            bindingMap.put("IDENTITY", lg);
                            bindingMap.put("RESOURCE", res);
                            bindingMap.put("PASSWORD_SYNC", passwordSync);

                            if (res != null) {
                                final String preProcessScript = getResourceProperty(res, "PRE_PROCESS");
                                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                                    final PreProcessor ppScript = createPreProcessScript(preProcessScript,
                                            bindingMap);
                                    if (ppScript != null) {
                                        if (executePreProcess(ppScript, bindingMap, null, "SET_PASSWORD") == ProvisioningConstants.FAIL) {
                                            continue;
                                        }
                                    }
                                }
                            }

                            String prevDecodedPassword = getDecryptedPassword(lg.getUserId(), lg.getPassword());
                            // update the password in openiam
                            loginManager.setPassword(lg.getLogin(), lg.getManagedSysId(), encPassword,
                                    passwordSync.isPreventChangeCountIncrement());

                            ManagedSystemObjectMatchEntity matchObj = null;
                            final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                                    .managedSysObjectParam(managedSysId, "USER");

                            if (CollectionUtils.isNotEmpty(matchList)) {
                                matchObj = matchList.get(0);
                            }

                            Login login = loginDozerConverter.convertToDTO(lg, false);
                            ResponseType resp = setPassword(requestId,
                                    login, prevDecodedPassword,
                                    passwordSync.getPassword(),
                                    managedSysDozerConverter.convertToDTO(mSys, false),
                                    objectMatchDozerConverter.convertToDTO(matchObj, false),
                                    buildMngSysAttributes(login, "SET_PASSWORD"));

                            boolean connectorSuccess = false;
                            log.info("============== Connector Set Password get : " + new Date());
                            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                                connectorSuccess = true;
                                auditLog.succeed();
                                auditLog.setAuditDescription(
                                        "Set password for resource: " + res.getName() + " for user: "
                                                + lg.getLogin());
                            } else {
                                auditLog.fail();
                                String reason = "";
                                if(resp != null) {
                                    if (resp.getError() != null) {
                                        reason = resp.getError().value();
                                    } else if (StringUtils.isNotBlank(resp.getErrorMsgAsStr())) {
                                        reason = resp.getErrorMsgAsStr();
                                    }
                                }
                                auditLog.setFailureReason(String.format("Set password for resource %s user %s failed: %s",
                                        mSys.getName(), lg.getLogin(), reason));

                            }

                            // post-process
                            if (res != null) {
                                final String postProcessScript = getResourceProperty(res, "POST_PROCESS");
                                if (postProcessScript != null && !postProcessScript.isEmpty()) {
                                    final PostProcessor ppScript = createPostProcessScript(postProcessScript,
                                            bindingMap);
                                    if (ppScript != null) {
                                        executePostProcess(ppScript, bindingMap, null, "SET_PASSWORD",
                                                connectorSuccess);
                                    }
                                }
                            }
                        } else {
                            log.debug("Sync not allowed for sys=" + managedSysId);
                        }
                    }
                }
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
            retVal = !StringUtils.equalsIgnoreCase(getResourceProperty(resource, "INCLUDE_IN_PASSWORD_SYNC"), "N");
        }
        return retVal;
    }

    private String getResourceProperty(final ResourceEntity resource, final String propertyName) {
        String retVal = null;
        if (resource != null && StringUtils.isNotBlank(propertyName)) {
            final ResourcePropEntity property = resource.getResourceProperty(propertyName);
            if (property != null) {
                retVal = property.getValue();
            }
        }
        return retVal;
    }

    private String getResProperty(Set<ResourceProp> resPropSet, String propertyName) {
        String value = null;

        if (resPropSet == null) {
            return null;
        }
        Iterator<ResourceProp> propIt = resPropSet.iterator();
        while (propIt.hasNext()) {
            ResourceProp prop = propIt.next();
            if (prop.getName().equalsIgnoreCase(propertyName)) {
                return prop.getValue();
            }
        }

        return value;
    }

    private Set<Resource> getResourcesForRoles(Set<Role> roleSet) {
        log.debug("GetResourcesForRole().....");
        final Set<Resource> resourceList = new HashSet<Resource>();
        if (CollectionUtils.isNotEmpty(roleSet)) {
            for (Role rl : roleSet) {
                if (rl.getId() != null) {
                    ResourceSearchBean resourceSearchBean = new ResourceSearchBean();
                    resourceSearchBean.setDeepCopy(false);
                    resourceSearchBean.setResourceTypeId(ResourceSearchBean.TYPE_MANAGED_SYS);
                    List<ResourceEntity> resources = resourceService.getResourcesForRole(rl.getId(), 0, Integer.MAX_VALUE, resourceSearchBean);
                    if (CollectionUtils.isNotEmpty(resources)) {
                        List<Resource> list = resourceDozerConverter.convertToDTOList(resources, true);
                        for (Resource r : list) {
                            r.setOperation(rl.getOperation()); // get operation value from role
                        }
                        resourceList.addAll(list);
                    }
                }
            }
        }
        return resourceList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPolicyMapAttributesList(String mSysId) {
        if (mSysId == null)
            return null;
        LookupAttributeResponse response = lookupAttributes(mSysId, "POLICY_MAP");
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            List<String> attributeNames = new LinkedList<String>();
            for (ExtensibleAttribute attr : response.getAttributes()) {
                if (!"READ_ONLY".equals(attr.getMetadataElementId())) {
                    attributeNames.add(attr.getName());
                }
            }
            return attributeNames;
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getManagedSystemAttributesList(String mSysId) {
        if (mSysId == null)
            return null;
        LookupAttributeResponse response = lookupAttributes(mSysId, "MANAGED_SYSTEM");
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            List<String> attributeNames = new LinkedList<String>();
            for (ExtensibleAttribute attr : response.getAttributes()) {
                attributeNames.add(attr.getName());
            }
            return attributeNames;
        } else {
            return null;
        }
    }

    private LookupAttributeResponse lookupAttributes(String mSysId, String execMode) {
        ManagedSysDto mSys = managedSysService.getManagedSys(mSysId);
        if (mSys != null) {
            LookupRequest lookupRequest = new LookupRequest();
            lookupRequest.setExecutionMode(execMode);
            lookupRequest.setTargetID(mSys.getId());
            lookupRequest.setRequestID(mSys.getResourceId());
            lookupRequest.setHostUrl(mSys.getHostUrl());
            lookupRequest.setHostLoginId(mSys.getUserId());
            lookupRequest.setHostLoginPassword(mSys.getDecryptPassword());
            lookupRequest.setScriptHandler(mSys.getAttributeNamesHandler());
            return connectorAdapter.lookupAttributes(mSys.getConnectorId(), lookupRequest,
                    MuleContextProvider.getCtx());
        }
        return null;
    }

    public Response syncPasswordFromSrc(PasswordSync passwordSync) {
        // ManagedSystemId where this event originated.
        // Ensure that we dont send the event back to this system

        log.debug("----syncPasswordFromSrc called.------");

		final IdmAuditLog auditLog = new IdmAuditLog();
		auditLog.setBaseObject(passwordSync);
		auditLog.setAction(AuditAction.PASSWORD_INTERCEPTOR.value());
		if (StringUtils.isNotBlank(passwordSync.getRequestorId())) {
			auditLog.setRequestorUserId(passwordSync.getRequestorId());
		}
		auditLog.setManagedSysId(passwordSync.getManagedSystemId());

		long curTime = System.currentTimeMillis();

        Response response = new Response(ResponseStatus.SUCCESS);

        String requestId = "R" + UUIDGen.getUUID();

		try {

			// get the user object associated with this principal
			LoginEntity login = loginManager.getLoginByManagedSys(passwordSync.getPrincipal(),
					passwordSync.getManagedSystemId());

			if (login == null) {
				auditLog.fail();
				auditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
				errorDescription = String.format("Principal: %s", passwordSync.getPrincipal());
				auditLog.addAttribute(AuditAttributeName.DESCRIPTION, errorDescription);

				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
				return response;
			}

			String userId = login.getUserId();

			auditLog.setTargetUser(userId, login.getLogin());

			// check if the user active
			if (userId == null) {
				auditLog.fail();
				auditLog.setFailureReason(ResponseCode.USER_NOT_FOUND);

				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.USER_NOT_FOUND);
				return response;
			}
			UserEntity usr = userMgr.getUser(userId);
			if (usr == null) {
				auditLog.fail();
				auditLog.setFailureReason(ResponseCode.USER_NOT_FOUND);

				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.USER_NOT_FOUND);
				return response;
			}

			// do not check the password policy
			// assume that the system that accepted the password already checked
			// this.

			String encPassword = null;

			try {
				encPassword = loginManager.encryptPassword(userId, passwordSync.getPassword());
			} catch (Exception e) {
				auditLog.fail();
				auditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);

				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
				return response;
			}

			// make sure all primary identity records were updated
			List<LoginEntity> principalList = loginManager.getLoginByUser(login.getUserId());
			for (LoginEntity l : principalList) {
				// if managedsysId is equal to the source or the openiam default
				// ID, then only update the database
				// otherwise do a sync
				if (l.getManagedSysId().equalsIgnoreCase(passwordSync.getManagedSystemId())
						|| l.getManagedSysId().equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {

					log.debug("Updating password for " + l.getLogin());

					auditLog.setManagedSysId(l.getManagedSysId());
					auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Updating password for " + l.getLogin());

					boolean retval = loginManager.setPassword(l.getLogin(), l.getManagedSysId(), encPassword,
							passwordSync.isPreventChangeCountIncrement());
					if (retval) {
						auditLog.succeed();
						log.debug("-Password changed in openiam repository for user:" + passwordSync.getPrincipal());
						// update the user object that the password was changed
						usr.setDatePasswordChanged(new Date(curTime));
						// reset any locks that may be in place
						if (usr.getSecondaryStatus() == UserStatusEnum.LOCKED) {
							usr.setSecondaryStatus(null);
						}
						userMgr.updateUserWithDependent(usr, false);

					} else {
						auditLog.fail();
						auditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);

						Response resp = new Response();
						resp.setStatus(ResponseStatus.FAILURE);
						resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
					}
				} else {

					log.debug("Synchronizing password from: " + l);

					// determine if you should sync the password or not
					String managedSysId = l.getManagedSysId();
					final ManagedSysEntity mSys = managedSystemService.getManagedSysById(managedSysId);
					final ResourceEntity res = resourceService.findResourceById(mSys.getResourceId());

					// check the sync flag

					if (syncAllowed(res)) {

						log.debug("Sync allowed for sys=" + managedSysId);

						// update the password in openiam
						loginManager.setPassword(l.getLogin(), l.getManagedSysId(), encPassword,
								passwordSync.isPreventChangeCountIncrement());

						// update the target system

						final ProvisionConnectorEntity connector = connectorService.getProvisionConnectorsById(mSys
								.getConnectorId());

						ManagedSystemObjectMatchEntity matchObj = null;
						final List<ManagedSystemObjectMatchEntity> matcheList = managedSystemService.managedSysObjectParam(
								managedSysId, "USER");

						if (CollectionUtils.isNotEmpty(matcheList)) {
							matchObj = matcheList.get(0);
						}

						// exclude the system where this event occured.
						Login loginDTO = loginDozerConverter.convertToDTO(login, false);
						ResponseType resp = resetPassword(requestId, loginDTO,
								passwordSync.getPassword(), managedSysDozerConverter.convertToDTO(mSys, false),
								objectMatchDozerConverter.convertToDTO(matchObj, false),
								buildMngSysAttributes(loginDTO, "SYNC_PASSWORD"));
						if (resp.getStatus() == StatusCodeType.SUCCESS) {
							auditLog.succeed();
							auditLog.setAuditDescription("Set password for resource: " + res.getName() + " for user: " + l.getLogin());

							response.setStatus(ResponseStatus.SUCCESS);
						} else {
							final String reason =
									(resp.getError() != null)
											? resp.getError().value()
									: (StringUtils.isNotBlank(resp.getErrorMsgAsStr()))
											? resp.getErrorMsgAsStr()
											: "";

							auditLog.fail();
							auditLog.setFailureReason(String.format("Set password for resource %s user %s failed: %s",
									mSys.getName(), l.getLogin(), reason));

							response.setErrorText(resp.getErrorMsgAsStr());
							response.setStatus(ResponseStatus.FAILURE);
						}

					} else {
						auditLog.fail();
						auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Sync not allowed for resource: " + res.getName());

						log.debug("Sync not allowed for sys=" + managedSysId);
					}
				}
			}
			auditLog.succeed();
			response.setStatus(ResponseStatus.SUCCESS);
			return response;
		} finally {
			auditLogService.save(auditLog);
		}
    }

    @Override
    @Transactional
    public Response startBulkOperation(final BulkOperationRequest bulkRequest) {
        if (CollectionUtils.isNotEmpty(bulkRequest.getUserIds()) &&
                CollectionUtils.isNotEmpty(bulkRequest.getOperations())) {

            final IdmAuditLog idmAuditLog = new IdmAuditLog();
            idmAuditLog.setAction(AuditAction.BULK_OPERATION.value());
            String requestorId = bulkRequest.getRequesterId();
            LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);
            idmAuditLog.setRequestorUserId(requestorId);
            idmAuditLog.setRequestorPrincipal(lRequestor.getLogin());

            List<String> failedUserIds = new ArrayList<String>();
            try {

                for (String userId : bulkRequest.getUserIds()) {
                    User user = userMgr.getUserDto(userId);

                    if (user != null) {

                        ProvisionUser pUser = new ProvisionUser(user);
                        pUser.setRequestorUserId(requestorId);
                        pUser.setRequestorLogin(lRequestor.getLogin());

                        boolean isEntitlementModified = false;

                        Set<Group> existingGroups = pUser.getGroups();
                        pUser.setGroups(new HashSet<Group>());

                        Set<Role> existingRoles = pUser.getRoles();
                        pUser.setRoles(new HashSet<Role>());

                        Set<Organization> existingOrganizations = pUser.getAffiliations();
                        pUser.setAffiliations(new HashSet<Organization>());

                        Set<Resource> existingResources = pUser.getResources();
                        pUser.setResources(new HashSet<Resource>());

                        Response res = new Response(ResponseStatus.FAILURE);
                        for (OperationBean ob : bulkRequest.getOperations()) {
                            switch (ob.getObjectType()) {
                                case USER:
                                    switch(ob.getOperation()) {
                                        case ACTIVATE_USER:
                                            pUser.setStatus(UserStatusEnum.ACTIVE);
                                            res = modifyUser(pUser, idmAuditLog);
                                            break;
                                        case DEACTIVATE_USER:
                                            res = deleteByUserWithSkipManagedSysList(
                                                    userId, UserStatusEnum.DELETED, requestorId, null, idmAuditLog);
                                            break;
                                        case DELETE_USER:
                                            res = deleteByUserWithSkipManagedSysList(
                                                    userId, UserStatusEnum.REMOVE, requestorId, null, idmAuditLog);
                                            break;
                                        case ENABLE_USER:
                                            res = disableUser(userId, false, requestorId, idmAuditLog);
                                            break;
                                        case DISABLE_USER:
                                            res = disableUser(userId, true, requestorId, idmAuditLog);
                                            break;
                                        case RESET_USER_PASSWORD:
                                            final PasswordSync pswdSync = new PasswordSync();
                                            pswdSync.setManagedSystemId(null);
                                            if (ob.getProperties() != null) {
                                                if (ob.getProperties().containsKey("password")) {
                                                    pswdSync.setPassword((String)ob.getProperties().get("password"));
                                                } else {
                                                    pswdSync.setPassword(PasswordGenerator.generatePassword(16));
                                                }
                                                if (ob.getProperties().containsKey("sendPasswordToUser")) {
                                                    pswdSync.setSendPasswordToUser((Boolean)ob.getProperties().get("sendPasswordToUser"));
                                                }
                                            }
                                            pswdSync.setUserId(userId);
                                            pswdSync.setRequestorLogin(lRequestor.getLogin());
                                            pswdSync.setRequestorId(requestorId);
                                            res = resetPassword(pswdSync, idmAuditLog);
                                            break;
                                        case NOTIFY_USER:
                                            Map<String, Object> bindingMap = new HashMap<String, Object>();
                                            bindingMap.put("firstName", pUser.getFirstName());
                                            bindingMap.put("lastName", pUser.getLastName());
                                            Login primaryIdentity = UserUtils.getUserManagedSysIdentity(sysConfiguration.getDefaultManagedSysId(), pUser.getPrincipalList());
                                            bindingMap.put("login", primaryIdentity.getLogin());
                                            if (primaryIdentity != null) {
                                                String decPassword = null;
                                                String password = primaryIdentity.getPassword();
                                                if (password != null) {
                                                    try {
                                                        decPassword = loginManager.decryptPassword(primaryIdentity.getUserId(), password);
                                                    } catch (Exception e) {
                                                    }
                                                    bindingMap.put("password", decPassword);
                                                }
                                            }

                                            String subject = null;
                                            String text = null;
                                            if (ob.getProperties().containsKey("subject")) {
                                                try {
                                                    subject = scriptRunner.evaluate(bindingMap, (String)ob.getProperties().get("subject"));
                                                } catch (IOException ioe) {
                                                    log.error("Error in subject string = '", ioe);
                                                }
                                            }
                                            if (ob.getProperties().containsKey("text")) {
                                                try {
                                                    text = scriptRunner.evaluate(bindingMap, (String)ob.getProperties().get("text"));
                                                } catch (IOException ioe) {
                                                    log.error("Error in text string = '", ioe);
                                                }
                                            }

                                            final IdmAuditLog childAuditLog = new IdmAuditLog();
                                            childAuditLog.setRequestorUserId(requestorId);
                                            childAuditLog.setRequestorPrincipal(lRequestor.getLogin());
                                            childAuditLog.setAction(AuditAction.USER_NOTIFY.value());
                                            childAuditLog.setTargetUser(pUser.getId(), primaryIdentity.getLogin());

                                            EmailAddress emailAddress = pUser.getPrimaryEmailAddress();
                                            if (emailAddress != null && StringUtils.isNotBlank(emailAddress.getEmailAddress())) {
                                                mailService.sendEmail(null, emailAddress.getEmailAddress(), null, subject, text, null, false);
                                                res = new Response(ResponseStatus.SUCCESS);
                                                childAuditLog.setAuditDescription("Notification sent to " + emailAddress.getEmailAddress());
                                                childAuditLog.succeed();
                                            } else {
                                                res = new Response(ResponseStatus.FAILURE);
                                                childAuditLog.setFailureReason("Email address wasn't found for user " + primaryIdentity.getLogin());
                                                childAuditLog.fail();
                                            }
                                            idmAuditLog.addChild(childAuditLog);
                                            break;
                                    }
                                    if (res.isFailure()) {
                                        failedUserIds.add(userId);
                                    }
                                    break;
                                case GROUP:
                                    boolean isModifiedGroup = false;
                                    Group group = groupDozerConverter.convertToDTO(
                                            groupManager.getGroup(ob.getObjectId(), requestorId), false);
                                    if (existingGroups.contains(group)) {
                                        if (BulkOperationEnum.DELETE_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingGroups.remove(group);
                                            group.setOperation(AttributeOperationEnum.DELETE);
                                            isModifiedGroup = true;
                                        }
                                    } else {
                                        if (BulkOperationEnum.ADD_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingGroups.add(group);
                                            group.setOperation(AttributeOperationEnum.ADD);
                                            isModifiedGroup = true;
                                        }
                                    }
                                    if (isModifiedGroup) {
                                        pUser.getGroups().add(group);
                                        isEntitlementModified = true;
                                    }
                                    break;
                                case ROLE:
                                    boolean isModifiedRole = false;
                                    Role role = roleDozerConverter.convertToDTO(
                                            roleDataService.getRole(ob.getObjectId(), requestorId), false);
                                    if (existingRoles.contains(role)) {
                                        if (BulkOperationEnum.DELETE_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingRoles.remove(role);
                                            role.setOperation(AttributeOperationEnum.DELETE);
                                            isModifiedRole = true;
                                        }
                                    } else {
                                        if (BulkOperationEnum.ADD_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingRoles.add(role);
                                            role.setOperation(AttributeOperationEnum.ADD);
                                            isModifiedRole = true;
                                        }
                                    }
                                    if (isModifiedRole) {
                                        pUser.getRoles().add(role);
                                        isEntitlementModified = true;
                                    }
                                    break;
                                case RESOURCE:
                                    boolean isModifiedResource = false;
                                    Resource resource = resourceService.getResourceDTO(ob.getObjectId());
                                    if (existingResources.contains(resource)) {
                                        if (BulkOperationEnum.DELETE_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingResources.remove(resource);
                                            resource.setOperation(AttributeOperationEnum.DELETE);
                                            isModifiedResource = true;
                                        }
                                    } else {
                                        if (BulkOperationEnum.ADD_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingResources.add(resource);
                                            resource.setOperation(AttributeOperationEnum.ADD);
                                            isModifiedResource = true;
                                        }
                                    }
                                    if (isModifiedResource) {
                                        pUser.getResources().add(resource);
                                        isEntitlementModified = true;
                                    }
                                    break;
                                case ORGANIZATION:
                                    boolean isModifiedOrg = false;
                                    Organization organization = organizationService.getOrganizationDTO(ob.getObjectId(), null);
                                    if (existingOrganizations.contains(organization)) {
                                        if (BulkOperationEnum.DELETE_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingOrganizations.remove(organization);
                                            organization.setOperation(AttributeOperationEnum.DELETE);
                                            isModifiedOrg = true;
                                        }
                                    } else {
                                        if (BulkOperationEnum.ADD_ENTITLEMENT.equals(ob.getOperation())) {
                                            existingOrganizations.add(organization);
                                            organization.setOperation(AttributeOperationEnum.ADD);
                                            isModifiedOrg = true;
                                        }
                                    }
                                    if (isModifiedOrg) {
                                        pUser.getAffiliations().add(organization);
                                        isEntitlementModified = true;
                                    }
                                    break;
                            }
                        }
                        if (isEntitlementModified) {
                            res = modifyUser(pUser, idmAuditLog);
                            if (res.isFailure()) {
                                failedUserIds.add(userId);
                            }
                        }

                    }
                }

            } finally {
                if (CollectionUtils.isNotEmpty(failedUserIds)) {
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Successfully processed " + (bulkRequest.getUserIds().size() - failedUserIds.size()) + " users");
                    idmAuditLog.addAttribute(AuditAttributeName.FAILURE_REASON, "Failed to process " + failedUserIds.size() + " users");
                    for (String id : failedUserIds) {
                        idmAuditLog.addAttribute(AuditAttributeName.FAILURE_REASON, id);
                    }
                } else {
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Successfully processed " + bulkRequest.getUserIds().size() + " users");
                }
                auditLogService.enqueue(idmAuditLog);
            }
        }

        return new Response(ResponseStatus.SUCCESS);
    }

    public ManagedSystemViewerResponse buildManagedSystemViewer(String userId, String managedSysId) {
        ManagedSystemViewerResponse res = new ManagedSystemViewerResponse();

        if (StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("userId can not be empty");
        }
        if (StringUtils.isEmpty(managedSysId)) {
            throw new IllegalArgumentException("managedSysId can not be empty");
        }
        res.setUserId(userId);
        res.setManagedSysId(managedSysId);

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            res.setStatus(ResponseStatus.FAILURE);
            res.setErrorText(String.format("User with id=%s doesn't exist", userId));
            return res;
        }

        Login login = null;
        List<Login> principals = usr.getPrincipalList();
        if (CollectionUtils.isNotEmpty(principals)) {
            for (Login l : principals) {
                if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                    login = l;
                    break;
                }
            }
        }
        if (login == null) {
            res.setStatus(ResponseStatus.FAILURE);
            res.setErrorText(String.format("User with id=%s doesn't have identity for managed system with id=%s",
                    userId, managedSysId));
            return res;
        }

        List<AttributeMapEntity> attrMapEntities = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();
        for (AttributeMapEntity ame : attrMapEntities) {
            if ("USER".equalsIgnoreCase(ame.getMapForObjectType()) && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getAttributeName(), null));
            }
        }

        LookupAttributeResponse response = lookupAttributes(managedSysId, "POLICY_MAP");
        List<ExtensibleAttribute> hiddenAttrs = new ArrayList<ExtensibleAttribute>();
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            for (ExtensibleAttribute attr : response.getAttributes()) {
                if ("READ_ONLY".equals(attr.getMetadataElementId())) { //Adding readOnly attributes
                    requestedExtensibleAttributes.add(new ExtensibleAttribute(attr.getName(), null, attr.getMetadataElementId()));

                } else if ("HIDDEN".equals(attr.getMetadataElementId())) { //Removing hidden attributes
                    for (ExtensibleAttribute a : requestedExtensibleAttributes) {
                        if (attr.getName().equals(a.getName())) {
                            hiddenAttrs.add(a);
                            break;
                        }
                    }
                } else {
                    for (ExtensibleAttribute ea : requestedExtensibleAttributes) {
                        if (StringUtils.equals(attr.getName(), ea.getName())) {
                            ea.setMetadataElementId(attr.getMetadataElementId());
                        }
                    }

                }
            }
            if (CollectionUtils.isNotEmpty(hiddenAttrs)) {
                requestedExtensibleAttributes.removeAll(hiddenAttrs);
            }
        }

        ProvisionUser pUser = new ProvisionUser(usr);
        List<ExtensibleAttribute> idmAttrs = buildMngSysAttributesForIDMUser(pUser, managedSysId, "VIEW");

        List<ExtensibleAttribute> mngSysAttrs = new ArrayList<ExtensibleAttribute>();
        LookupUserResponse lookupUserResponse = getTargetSystemUser(login.getLogin(), managedSysId, requestedExtensibleAttributes);
        boolean targetSysUserExists = false;
        if (ResponseStatus.SUCCESS.equals(lookupUserResponse.getStatus())) {
            mngSysAttrs = lookupUserResponse.getAttrList();
            targetSysUserExists = true;
        }

        List<ManagedSystemViewerBean> viewerList = new ArrayList<ManagedSystemViewerBean>();
        if (CollectionUtils.isNotEmpty(requestedExtensibleAttributes)) {
            for (ExtensibleAttribute a : requestedExtensibleAttributes) {
                ManagedSystemViewerBean viewerBean = new ManagedSystemViewerBean();
                viewerBean.setAttributeName(a.getName());
                viewerBean.setIdmAttribute(findExtAttrByName(a.getName(), idmAttrs));
                viewerBean.setMngSysAttribute(findExtAttrByName(a.getName(), mngSysAttrs));
                viewerBean.setReadOnly("READ_ONLY".equals(a.getMetadataElementId()) ||
                        "NON_EDITABLE".equals(a.getMetadataElementId()));
                viewerList.add(viewerBean);
            }
        }
        res.setStatus(ResponseStatus.SUCCESS);
        res.setViewerBeanList(viewerList);
        res.setExist(targetSysUserExists);

        return res;
    }

    private List<ExtensibleAttribute> buildMngSysAttributesForIDMUser(ProvisionUser pUser, String managedSysId,
                                                                      String operation) {

        Map bindingMap = new HashMap<String, Object>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("org", pUser.getPrimaryOrganization());
        bindingMap.put("operation", operation);
        bindingMap.put("user", pUser);

        UserEntity userEntity = null;
        userEntity = userMgr.getUser(pUser.getId());

        LoginEntity identityEntity = UserUtils.getUserManagedSysIdentityEntity(managedSysId,
                userEntity.getPrincipalList());
        Login identity = (identityEntity != null) ? loginDozerConverter.convertToDTO(
                identityEntity, false) : null;
        if (identity != null) {
            String decPassword = null;
            String password = identity.getPassword();
            if (password != null) {
                try {
                    decPassword = loginManager.decryptPassword(identity.getUserId(), password);
                } catch (Exception e) {
                }
                bindingMap.put("password", decPassword);
            }
            bindingMap.put("lg", identity);
        }

        ProvisionUser u = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
        provisionSelectedResourceHelper.setCurrentSuperiors(u);
        bindingMap.put("userBeforeModify", u);

        List<Role> curRoleList = roleDataService.getUserRolesAsFlatList(pUser.getId());
        List<Group> curGroupList = groupDozerConverter.convertToDTOList(
                groupManager.getGroupsForUser(pUser.getId(), null, -1, -1), false);
        bindingMap.put("currentRoleList", curRoleList);
        bindingMap.put("currentGroupList", curGroupList);

        bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSysId);
        ManagedSysDto managedSys = managedSysService.getManagedSys(managedSysId);
        bindingMap.put(TARGET_SYS_RES_ID, managedSys.getResourceId());

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
        if (matchObjAry != null && matchObjAry.length > 0) {
            matchObj = matchObjAry[0];
            bindingMap.put(MATCH_PARAM, matchObj);
        }

        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);

        LoginEntity mLg = null;
        for (LoginEntity l : userEntity.getPrincipalList()) {
            if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                mLg = l;
                break;
            }
        }
        bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, null);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, mLg != null ? mLg.getLogin() : null);

        List<AttributeMapEntity> attrMapEntities = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
        List<ExtensibleAttribute> idmExtensibleAttributes = new ArrayList<ExtensibleAttribute>();

        for (AttributeMapEntity attr : attrMapEntities) {

            if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                continue;
            }

            String objectType = attr.getMapForObjectType();
            if (objectType != null) {

                if (objectType.equalsIgnoreCase("USER")
                        || objectType.equalsIgnoreCase("PASSWORD")) {
                    Object output = "";
                    try {
                        output = ProvisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, scriptRunner);
                    } catch (ScriptEngineException see) {
                        log.error("Error in script = '", see);
                        continue;
                    } catch (MissingPropertyException mpe) {
                        log.error("Error in script = '", mpe);
                        continue;
                    }

                    log.debug("buildFromRules: OBJECTTYPE="+objectType+", ATTRIBUTE=" + attr.getAttributeName() +
                              ", SCRIPT OUTPUT=" +
                              (hiddenAttributes.toLowerCase().contains(","+attr.getAttributeName().toLowerCase()+",")
                                      ? "******" : output));

                    if (output != null) {
                        ExtensibleAttribute newAttr;
                        if (output instanceof String) {

                            // if its memberOf object than dont add it to
                            // the list
                            // the connectors can detect a delete if an
                            // attribute is not in the list

                            newAttr = new ExtensibleAttribute(attr.getAttributeName(), (String) output, 1, attr
                                    .getDataType().getValue());
                            newAttr.setObjectType(objectType);
                            idmExtensibleAttributes.add(newAttr);

                        } else if (output instanceof Integer) {

                            // if its memberOf object than dont add it to
                            // the list
                            // the connectors can detect a delete if an
                            // attribute is not in the list

                            newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                    ((Integer) output).toString(), 1, attr.getDataType().getValue());
                            newAttr.setObjectType(objectType);
                            idmExtensibleAttributes.add(newAttr);

                        } else if (output instanceof Date) {
                            // date
                            Date d = (Date) output;
                            String DATE_FORMAT = "MM/dd/yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                            newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), 1, attr
                                    .getDataType().getValue());
                            newAttr.setObjectType(objectType);
                            idmExtensibleAttributes.add(newAttr);

                        } else if (output instanceof byte[]) {
                            idmExtensibleAttributes.add(
                                    new ExtensibleAttribute(attr.getAttributeName(), (byte[]) output, 1, attr
                                            .getDataType().getValue()));

                        } else if (output instanceof BaseAttributeContainer) {
                            // process a complex object which can be passed
                            // to the connector
                            newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                    (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                            newAttr.setObjectType(objectType);
                            idmExtensibleAttributes.add(newAttr);

                        } else {
                            // process a list - multi-valued object

                            newAttr = new ExtensibleAttribute(attr.getAttributeName(), (List) output, 1, attr
                                    .getDataType().getValue());
                            newAttr.setObjectType(objectType);
                            idmExtensibleAttributes.add(newAttr);

                        }
                    }
                }
            }
        }
        return idmExtensibleAttributes;
    }

    private ExtensibleAttribute findExtAttrByName(String name, List<ExtensibleAttribute> attrs) {
        if (CollectionUtils.isNotEmpty(attrs)) {
            for (ExtensibleAttribute ea: attrs) {
                if (ea.getName().equals(name)) {
                    return ea;
                }
            }
        }
        return null;
    }

    public Response requestAdd(ExtensibleUser extUser, Login login, String requestorId) {
        final String requestId = "R" + UUIDGen.getUUID();
        ProvisionUserResponse response = new ProvisionUserResponse(ResponseStatus.SUCCESS);

        log.debug("----addModify called.------");
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requestorId != null ? requestorId : systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_ADD.value());
        idmAuditLog.setTargetUser(login.getUserId(), login.getLogin());

        try {

            List<ExtensibleAttribute> hiddenAttrs = buildHiddenMngSysAttributes(login);
            extUser.getAttributes().addAll(hiddenAttrs);

            ObjectResponse resp = requestAddModify(extUser, login, true, requestId, idmAuditLog);
            if (resp.getStatus() != StatusCodeType.SUCCESS) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorText(resp.getErrorMsgAsStr());
                idmAuditLog.fail();
            } else {
                idmAuditLog.succeed();
            }
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }

        return response;
    }

    public Response requestModify(ExtensibleUser extUser, Login login, String requestorId) {
        final String requestId = "R" + UUIDGen.getUUID();
        ProvisionUserResponse response = new ProvisionUserResponse(ResponseStatus.SUCCESS);

        log.debug("----requestModify called.------");
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requestorId != null ? requestorId : systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_MODIFY.value());
        idmAuditLog.setTargetUser(login.getUserId(), login.getLogin());

        try {

            List<ExtensibleAttribute> hiddenAttrs = buildHiddenMngSysAttributes(login);
            extUser.getAttributes().addAll(hiddenAttrs);

            ObjectResponse resp = requestAddModify(extUser, login, false, requestId, idmAuditLog);
            if (resp.getStatus() != StatusCodeType.SUCCESS) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorText(resp.getErrorMsgAsStr());
                idmAuditLog.fail();
            } else {
                idmAuditLog.succeed();
            }
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }

        return response;
    }

    private List<ExtensibleAttribute> buildHiddenMngSysAttributes(Login login) {

        String userId = login.getUserId();
        String managedSysId = login.getManagedSysId();

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            return null;
        }
        ProvisionUser pUser = new ProvisionUser(usr);
        List<ExtensibleAttribute> idmAttrs = buildMngSysAttributesForIDMUser(pUser, managedSysId, "VIEW");

        List<AttributeMapEntity> attrMapEntities = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();
        for (AttributeMapEntity ame : attrMapEntities) {
            if ("USER".equalsIgnoreCase(ame.getMapForObjectType()) && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getAttributeName(), null));
            }
        }

        LookupAttributeResponse response = lookupAttributes(managedSysId, "POLICY_MAP");
        List<ExtensibleAttribute> hiddenAttrs = new ArrayList<ExtensibleAttribute>();
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            for (ExtensibleAttribute attr : response.getAttributes()) {
                if ("HIDDEN".equals(attr.getMetadataElementId()) ||
                        "NON_EDITABLE".equals(attr.getMetadataElementId())) {
                    for (ExtensibleAttribute a : requestedExtensibleAttributes) {
                        if (attr.getName().equals(a.getName())) {
                            hiddenAttrs.add(a);
                            break;
                        }
                    }
                }
            }
        }

        LookupUserResponse lookupUserResponse = getTargetSystemUser(login.getLogin(), managedSysId, hiddenAttrs);
        boolean targetSysUserExists = false;
        List<ExtensibleAttribute> mngSysAttrs = new ArrayList<ExtensibleAttribute>();
        if (ResponseStatus.SUCCESS.equals(lookupUserResponse.getStatus())) {
            if (CollectionUtils.isNotEmpty(hiddenAttrs)) {
                mngSysAttrs = lookupUserResponse.getAttrList();
            }
            targetSysUserExists = true;
        }

        List<ExtensibleAttribute> idmAttrsToDelete = new ArrayList<ExtensibleAttribute>();
        for (ExtensibleAttribute idma : idmAttrs) {
            idma.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
            if (targetSysUserExists) {
                boolean exists = false;
                for (ExtensibleAttribute msa : mngSysAttrs) {
                    if (StringUtils.equals(idma.getName(), msa.getName())) {
                        if (!StringUtils.equals(idma.getValue(), msa.getValue())) {
                            idma.setOperation(AttributeOperationEnum.REPLACE.getValue());
                        }
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    idmAttrsToDelete.add(idma);
                }
            } else {
                idma.setOperation(AttributeOperationEnum.ADD.getValue());
            }
        }

        if (CollectionUtils.isNotEmpty(idmAttrsToDelete)) {
            idmAttrs.removeAll(idmAttrsToDelete);
        }

        return idmAttrs;

    }

    /*
 * (non-Javadoc)
 *
 * @see
 * org.openiam.provision.service.ProvisionService#disableUser(java.lang.
 * String, boolean)
 */
    @Override
    public Response disableUser(String userId, boolean operation, String requestorId) {
        return disableUser(userId, operation, requestorId, null);
    }

    @Transactional
    public Response disableUser(String userId, boolean operation, String requestorId, IdmAuditLog auditLog) {

        log.debug("----disableUser called.------");
        log.debug("operation code=" + operation);

        Response response = new Response(ResponseStatus.SUCCESS);
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        UserEntity usr = this.userMgr.getUser(userId);

        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), usr.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        idmAuditLog.setRequestorUserId(requestorId);
        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(requestorId);
        LoginEntity requestorIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), loginEntityList);
        idmAuditLog.setRequestorPrincipal(requestorIdentity.getLogin());

        if (auditLog != null) {
            auditLog.addChild(idmAuditLog);
        }

        try {

            String requestId = "R" + UUIDGen.getUUID();

            if (usr == null) {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.USER_NOT_FOUND);

                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.USER_NOT_FOUND);
                return response;
            }
            // disable the user in OpenIAM
            if (operation) {
                idmAuditLog.setAction(AuditAction.PROVISIONING_DISABLE.value());
                usr.setSecondaryStatus(UserStatusEnum.DISABLED);
            } else {
                // enable an account that was previously disabled.
                idmAuditLog.setAction(AuditAction.PROVISIONING_ENABLE.value());
                usr.setSecondaryStatus(null);
            }
            userMgr.updateUserWithDependent(usr, false);

            LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);
            LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

            if (lRequestor == null || lTargetUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug(String
                            .format("Unable to log disable operation.  Requestor: %s, Target: %s",
                                    lRequestor, lTargetUser));
                }

                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.OBJECT_NOT_FOUND);

                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
                response.setErrorText(String.format(
                        "Requestor: '%s' or User: '%s' not found", requestorId,
                        userId));

                return response;
            }
            // disable the user in the managed systems

            // typical sync
            List<LoginEntity> principalList = loginManager
                    .getLoginByUser(usr.getId());
            if (principalList != null) {
                log.debug("PrincipalList size =" + principalList.size());
                for (LoginEntity lg : principalList) {

                    final IdmAuditLog idmAuditLogChild = new IdmAuditLog();
                    idmAuditLogChild.setRequestorUserId(requestorId);
                    idmAuditLogChild.setRequestorPrincipal(lRequestor.getLogin());
                    idmAuditLogChild.setAction(operation ? AuditAction.PROVISIONING_DISABLE_IDENTITY.value() :
                            AuditAction.PROVISIONING_ENABLE_IDENTITY.value());
                    idmAuditLogChild.setTargetUser(lg.getUserId(), lg.getLogin());
                    idmAuditLogChild.setManagedSysId(lg.getManagedSysId());
                    idmAuditLog.addChild(idmAuditLogChild);

                    // get the managed system for the identity - ignore the managed
                    // system id that is linked to openiam's repository
                    log.debug("-diabling managed system=" + lg.getLogin()
                            + " - " + lg.getManagedSysId());

                    if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(), sysConfiguration.getDefaultManagedSysId())) {
                        String managedSysId = lg.getManagedSysId();
                        // update the target system
                        ManagedSysDto mSys = managedSysService
                                .getManagedSys(managedSysId);

                        idmAuditLogChild.setTargetManagedSys(mSys.getId(), mSys.getName());

                        final Login login = loginDozerConverter.convertToDTO(lg, false);
                        if (operation) {
                            // suspend
                            log.debug("preparing suspendRequest object");
                            lg.setStatus(LoginStatusEnum.INACTIVE);

                            SuspendResumeRequest suspendReq = new SuspendResumeRequest();
                            suspendReq.setObjectIdentity(lg.getLogin());
                            suspendReq.setTargetID(managedSysId);
                            suspendReq.setRequestID(requestId);
                            suspendReq.setScriptHandler(mSys
                                    .getSuspendHandler());

                            suspendReq.setHostLoginId(mSys.getUserId());
                            String passwordDecoded = mSys.getPswd();
                            try {
                                passwordDecoded = getDecryptedPassword(mSys);
                            } catch (ConnectorDataException e) {
                                e.printStackTrace();
                            }
                            suspendReq.setHostLoginPassword(passwordDecoded);
                            suspendReq.setHostUrl(mSys.getHostUrl());
                            suspendReq.setExtensibleObject(buildMngSysAttributes(login, "SUSPEND"));


                            ResponseType resp = connectorAdapter.suspendRequest(mSys, suspendReq,
                                    MuleContextProvider.getCtx());

                            if (StatusCodeType.SUCCESS.equals(resp.getStatus())) {
                                lg.setProvStatus(ProvLoginStatusEnum.DISABLED);
                                idmAuditLogChild.succeed();
                                idmAuditLogChild.setAuditDescription("Login: " + lg.getLogin() + " for Managed system ID: " + managedSysId + " is disabled");

                            } else {
                                lg.setProvStatus(ProvLoginStatusEnum.FAIL_DISABLE);
                                idmAuditLogChild.fail();
                                idmAuditLogChild.setFailureReason("Login: " + lg.getLogin() + " for Managed system ID: " + managedSysId + " failed to disable");
                            }
                            loginManager.updateLogin(lg);


                        } else {
                            // resume - re-enable
                            log.debug("preparing resumeRequest object");

                            // reset flags that go with this identiy
                            lg.setAuthFailCount(0);
                            lg.setIsLocked(0);
                            lg.setPasswordChangeCount(0);
                            lg.setStatus(LoginStatusEnum.ACTIVE);

                            SuspendResumeRequest resumeReq = new SuspendResumeRequest();
                            resumeReq.setObjectIdentity(lg.getLogin());
                            resumeReq.setTargetID(managedSysId);
                            resumeReq.setRequestID(requestId);
                            resumeReq.setScriptHandler(mSys
                                    .getSuspendHandler());
                            resumeReq.setHostLoginId(mSys.getUserId());
                            resumeReq.setExtensibleObject(buildMngSysAttributes(login, "RESUME"));

                            String passwordDecoded = mSys.getPswd();
                            try {
                                passwordDecoded = getDecryptedPassword(mSys);
                            } catch (ConnectorDataException e) {
                                e.printStackTrace();
                            }
                            resumeReq.setHostLoginPassword(passwordDecoded);
                            resumeReq.setHostUrl(mSys.getHostUrl());

                            ResponseType resp = connectorAdapter.resumeRequest(mSys,
                                    resumeReq, MuleContextProvider.getCtx());

                            if (StatusCodeType.SUCCESS.equals(resp.getStatus())) {
                                lg.setProvStatus(ProvLoginStatusEnum.ENABLED);
                                idmAuditLogChild.succeed();
                                idmAuditLogChild.setAuditDescription("Login: " + lg.getLogin() + " for Managed system ID: " + managedSysId + " is enabled");

                            } else {
                                lg.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);
                                idmAuditLogChild.fail();
                                idmAuditLogChild.setFailureReason("Login: " + lg.getLogin() + " for Managed system ID: " + managedSysId + " failed to enable");
                            }
                            loginManager.updateLogin(lg);
                        }

                    } else {
                        lg.setAuthFailCount(0);
                        lg.setIsLocked(0);
                        lg.setPasswordChangeCount(0);
                        loginManager.updateLogin(lg);
                    }
                }
            }
        } finally {
            if (auditLog == null) {
                auditLogService.enqueue(idmAuditLog);
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

}
