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

                                    ProvisionDataContainer data = deprovisionResourceDataPrepare(res, userEntity, new ProvisionUser(user), requestorUserId);

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

    private String findResourcePropertyByName(final String resId, final String name) {
        Resource r = resourceDataService.getResource(resId, null);
        if (r != null) {
            Set<ResourceProp> rpSet = r.getResourceProps();
            if (CollectionUtils.isNotEmpty(rpSet)) {
                for (ResourceProp rp : rpSet) {
                    if (StringUtils.equalsIgnoreCase(rp.getName(), name))  {
                        return rp.getValue();
                    }
                }
            }
        }
        return null;
    }

    public ProvisionDataContainer deprovisionResourceDataPrepare(Resource res, UserEntity userEntity, ProvisionUser pUser,
                                                       String requestId) {

        // ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        ManagedSysDto mSys = managedSysService.getManagedSysByResource(res.getId());
        String managedSysId = (mSys != null) ? mSys.getId() : null;
        if (mSys == null || mSys.getConnectorId() == null) {
            return null;
        }

        String onDeleteProp = findResourcePropertyByName(res.getId(), "ON_DELETE");
        if(StringUtils.isEmpty(onDeleteProp)) {
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

        LoginEntity mLg = null;
        for (LoginEntity l : userEntity.getPrincipalList()) {
            if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                l.setStatus(LoginStatusEnum.INACTIVE);
                l.setProvStatus(provLoginStatus);
                mLg = l;
            }
        }

        if (mLg != null) {
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            for (Login l : pUser.getPrincipalList()) { // saving Login
                // properties from pUser
                if (l.getLoginId() != null && l.getLoginId().equals(targetSysLogin.getLoginId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                    targetSysLogin.setInitialStatus(l.getStatus());
                }
            }

            ProvisionDataContainer data = new ProvisionDataContainer();
            data.setRequestId(requestId);
            data.setResourceId(res.getId());
            data.setIdentity(targetSysLogin);

            switch (onDeleteProp) {
                case "DELETE":
                    data.setOperation(ProvOperationEnum.DELETE);
                    break;
                case "DISABLE":
                    data.setOperation(ProvOperationEnum.DISABLE);
                    break;
                default:
                    data.setOperation(ProvOperationEnum.UPDATE);
            }

            return data;
        }
        return null;
    }

    @Deprecated
    public ProvisionUserResponse deprovisionSelectedResources( String userId, String requestorUserId, List<String> resourceList)  {

        log.debug("deprovisionSelectedResources().....for userId=" + userId);

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
                        if (executePreProcess(ppScript, bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                            continue;
                        }
                    }
                }
            }

            log.debug("Resource object = " + res);

            ManagedSysDto managedSys = managedSysService.getManagedSysByResource(res.getId());
            String mSysId = (managedSys != null) ? managedSys.getId() : null;
            if (mSysId != null)  {

                if (!mSysId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {

                    log.debug("Looking up identity for : " + mSysId);

                    LoginEntity l = getLoginForManagedSys(mSysId, principalList);

                    log.debug("Identity for Managedsys =" + l);

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
                        log.debug("Deleting id=" + l.getLogin());
                        log.debug("- delete using managed sys id=" + mSys.getId());

                        boolean connectorSuccess = false;

                        ObjectResponse resp = delete(loginDozerConverter.convertToDTO(l, true), requestId, mSys, matchObj);
                        if (resp.getStatus() == StatusCodeType.SUCCESS) {
                            connectorSuccess = true;
                        }

                        String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                        if (postProcessScript != null && !postProcessScript.isEmpty()) {
                            PostProcessor ppScript = createPostProcessScript(postProcessScript);
                            if (ppScript != null) {
                                executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
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
