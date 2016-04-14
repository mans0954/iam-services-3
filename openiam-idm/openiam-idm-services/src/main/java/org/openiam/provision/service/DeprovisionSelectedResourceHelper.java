package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * Helper class that implements functionality required for provisioning a selected set of resources.
 */
@Component
public class DeprovisionSelectedResourceHelper extends BaseProvisioningHelper {


    public ProvisionUserResponse deprovisionSelectedResourcesAsync(final List<String> userIds, final String requestorUserId, final Collection<String> resourceList) {
        final List<ProvisionDataContainer> dataList = new LinkedList<ProvisionDataContainer>();
        ProvisionUserResponse res = new ProvisionUserResponse();
        res.setStatus(ResponseStatus.FAILURE);
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            res = transactionTemplate.execute(new TransactionCallback<ProvisionUserResponse>() {
                @Override
                public ProvisionUserResponse doInTransaction(TransactionStatus status) {

                    ProvisionUserResponse tmpRes = new ProvisionUserResponse(ResponseStatus.FAILURE);
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(requestorUserId);
                    UserEntity requestor = userMgr.getUser(requestorUserId);

                    LoginEntity requestorPrimaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(),
                            requestor.getPrincipalList());
                    auditLog.setRequestorPrincipal(requestorPrimaryIdentity.getLogin());
                    auditLog.setAction(AuditAction.DE_PROVISIONING.value());

                    try {
                        for (String userId : userIds) {
                            UserEntity userEntity = userMgr.getUser(userId);
                            User user = userDozerConverter.convertToDTO(userEntity, true);

                            Login primaryIdentity = UserUtils.getUserManagedSysIdentity(sysConfiguration.getDefaultManagedSysId(),
                                    user.getPrincipalList());

                            final IdmAuditLog auditLogChild = new IdmAuditLog();
                            auditLog.setRequestorPrincipal(requestorPrimaryIdentity.getLogin());
                            auditLog.setAction(AuditAction.DE_PROVISIONING.value());
                            auditLog.addTarget(userEntity.getId(), AuditTarget.USER.value(), primaryIdentity.getLogin());

                            auditLogChild.setAuditDescription("De-Provisioning add user: " + userEntity.getId()
                                    + " with first/last name: " + userEntity.getFirstName() + "/" + userEntity.getLastName());
                            auditLog.addChild(auditLogChild);

                            for (String resId : resourceList) {
                                // skip provisioning for resource if it in NotProvisioning
                                // set
                                Resource res = resourceDataService.getResource(resId, null);
                                try {
                                    Map<String, Object> bindingMap = new HashMap<String, Object>(); //TODO: check if enough bindingMap data for UPDATE
                                    ProvisionDataContainer data = deprovisionResourceDataPrepare(res, userEntity, new ProvisionUser(user), requestorUserId, bindingMap);

                                    auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                            "De-Provisioning for resource: " + res.getName());
                                    if (data != null) {
                                        data.setParentAuditLogId(auditLog.getId());
                                        dataList.add(data);
                                    }
                                } catch (Throwable tw) {
                                    auditLog.fail();
                                    auditLog.setFailureReason(
                                            "De-Provisioning resource : " + res.getName() + " for user with primary identity: "
                                                    + primaryIdentity + ". Exception: " + tw.getMessage());
                                    auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                            "De-Provisioning resource : " + res.getName() + " for user with primary identity: "
                                                    + primaryIdentity + ". Exception: " + tw.getMessage());
                                    log.error(res, tw);
                                }
                            }
                        }
                    } finally {
                        auditLogService.enqueue(auditLog);
                    }
                    tmpRes.setStatus(ResponseStatus.SUCCESS);
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

    public ProvisionDataContainer deprovisionResourceDataPrepare(Resource res, UserEntity userEntity, ProvisionUser pUser,
                                                                 String requestId, Map<String, Object> tmpMap) {

        Map<String, Object> bindingMap = new HashMap<String, Object>(tmpMap); // prevent data rewriting

        // ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        ManagedSysDto mSys = managedSysService.getManagedSysByResource(res.getId());
        if (mSys == null || mSys.getConnectorId() == null) {
            return null;
        }
        String managedSysId = mSys.getId();
        ProvisionUser targetSysProvUser = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
//        ProvisionUser targetSysProvUser = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
        setCurrentSuperiors(targetSysProvUser);
        targetSysProvUser.setStatus(pUser.getStatus());

        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, res.getId());
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
        bindingMap.put(AbstractProvisioningService.USER, targetSysProvUser);
        bindingMap.put(AbstractProvisioningService.USER_ATTRIBUTES, userMgr.getUserAttributesDto(pUser.getId()));

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
        if (matchObjAry != null && matchObjAry.length > 0) {
            matchObj = matchObjAry[0];
            bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
        }
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, AbstractProvisioningService.IDENTITY_EXIST);

        String onDeleteProp = resourceDataService.getResourcePropValueByName(res.getId(), "ON_DELETE");
        if (StringUtils.isEmpty(onDeleteProp)) {
            onDeleteProp = "DELETE";
        }
        ProvLoginStatusEnum provLoginStatus = null;
        switch (onDeleteProp) {
            case "DELETE":
                provLoginStatus = ProvLoginStatusEnum.PENDING_DELETE;
                break;
            case "DISABLE":
                provLoginStatus = ProvLoginStatusEnum.PENDING_DISABLE;
                break;
            default:
                provLoginStatus = ProvLoginStatusEnum.PENDING_UPDATE;
        }

        Login mLg = null;
        for (Login l : targetSysProvUser.getPrincipalList()) {
            if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                l.setStatus(LoginStatusEnum.INACTIVE);
                l.setProvStatus(provLoginStatus);
                mLg = l;
            }
        }

        if (mLg != null) {
            Login targetSysLogin = mLg;
            for (Login l : pUser.getPrincipalList()) { // saving Login
                // properties from pUser
                if (l.getLoginId() != null && l.getLoginId().equals(targetSysLogin.getLoginId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                    targetSysLogin.setInitialStatus(l.getStatus());
                }
            }

            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, mLg.getLogin());
            bindingMap.put("lg", mLg);
            String decPassword = "";
            try {
                decPassword = loginManager.decryptPassword(mLg.getUserId(), mLg.getPassword());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(" - Failed to decrypt password for " + mLg.getUserId());
                }
            }
            bindingMap.put("password", decPassword);

            ProvisionDataContainer data = new ProvisionDataContainer();
            data.setRequestId(requestId);
            data.setResourceId(res.getId());
            data.setIdentity(targetSysLogin);
            data.setProvUser(targetSysProvUser);

            switch (onDeleteProp) {
                case "DELETE":
                    data.setOperation(ProvOperationEnum.DELETE);
                    bindingMap.put("operation", "DELETE");
                    break;
                case "DISABLE":
                    data.setOperation(ProvOperationEnum.DISABLE);
                    bindingMap.put("operation", "SUSPEND");
                    break;
                default:
                    data.setOperation(ProvOperationEnum.UPDATE);
                    bindingMap.put("operation", "MODIFY");
            }
            data.setBindingMap(bindingMap);

            return data;
        }
        return null;
    }

    @Deprecated
    public ProvisionUserResponse deprovisionSelectedResources(String userId, String requestorUserId, List<String> resourceList) {
        if (log.isDebugEnabled()) {
            log.debug("deprovisionSelectedResources().....for userId=" + userId);
        }

        ProvisionUserResponse response = new ProvisionUserResponse(ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        String requestId = "R" + UUIDGen.getUUID();

        if (resourceList == null || resourceList.isEmpty()) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
            return response;
        }

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        ProvisionUser pUser = new ProvisionUser(usr);

        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        List<LoginEntity> principalList = loginManager.getLoginByUser(userId);

        // setup audit information

        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorUserId);
        LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

        if (lRequestor != null && lTargetUser != null) {
            /*
            auditLog = auditHelper.addLog("DEPROVISION RESOURCE", lRequestor.getDomainId(), lRequestor.getLogin(),
                    "IDM SERVICE", usr.getCreatedBy(), "0", "USER", usr.getUserId(),
                    null, "SUCCESS", null, "USER_STATUS",
                    usr.getStatus().toString(),
                    requestId, null, null, null,
                    null, lTargetUser.getLogin(), lTargetUser.getDomainId());
			*/
        }


        for (String resourceId : resourceList) {

            bindingMap.put("IDENTITY", lg);
            //bindingMap.put("RESOURCE", res);

            Resource res = resourceDataService.getResource(resourceId, null);
            if (res != null) {
                String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                    PreProcessor ppScript = createPreProcessScript(preProcessScript);
                    if (ppScript != null) {
                        if (executePreProcess(ppScript, bindingMap, pUser, null, "DELETE") == ProvisioningConstants.FAIL) {
                            continue;
                        }
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Resource object = " + res);
            }

            ManagedSysDto managedSys = managedSysService.getManagedSysByResource(res.getId());
            String mSysId = (managedSys != null) ? managedSys.getId() : null;
            if (mSysId != null) {

                if (!mSysId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {
                    if (log.isDebugEnabled()) {
                        log.debug("Looking up identity for : " + mSysId);
                    }

                    LoginEntity l = getLoginForManagedSys(mSysId, principalList);

                    if (log.isDebugEnabled()) {
                        log.debug("Identity for Managedsys =" + l);
                    }

                    if (l != null) {

                        l.setStatus(LoginStatusEnum.INACTIVE);
                        l.setAuthFailCount(0);
                        l.setPasswordChangeCount(0);
                        l.setIsLocked(0);
                        loginManager.updateLogin(l);

                        ManagedSysDto mSys = managedSysService.getManagedSys(l.getManagedSysId());

                        ManagedSystemObjectMatch matchObj = null;
                        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(mSys.getId(), ManagedSystemObjectMatch.USER);
                        if (matchObjAry != null && matchObjAry.length > 0) {
                            matchObj = matchObjAry[0];
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Deleting id=" + l.getLogin());
                            log.debug("- delete using managed sys id=" + mSys.getId());
                        }

                        boolean connectorSuccess = false;

                        ObjectResponse resp = delete(loginDozerConverter.convertToDTO(l, true), requestId, mSys, matchObj);
                        if (resp.getStatus() == StatusCodeType.SUCCESS) {
                            connectorSuccess = true;
                        }

                        String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                        if (postProcessScript != null && !postProcessScript.isEmpty()) {
                            PostProcessor ppScript = createPostProcessScript(postProcessScript);
                            if (ppScript != null) {
                                executePostProcess(ppScript, bindingMap, pUser, null, "DELETE", connectorSuccess);
                            }
                        }

                    }

                }
            }

        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }


    private LoginEntity getLoginForManagedSys(String managedSysId, List<LoginEntity> principalList) {
        for (LoginEntity l : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                return l;
            }

        }
        return null;
    }


}
