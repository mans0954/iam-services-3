package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.util.UserUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Component
public class ProvisionSelectedResourceHelper extends BaseProvisioningHelper {

    public ProvisionUserResponse provisionSelectedResources(final List<String> userIds, final String requestorUserId, final Collection<String> resourceList) {
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
                    auditLog.setAction(AuditAction.PROVISIONING_MODIFY.value());

                    try {
                        for (String userId : userIds) {
                            UserEntity userEntity = userMgr.getUser(userId);
                            User user = userDozerConverter.convertToDTO(userEntity, true);

                            Login primaryIdentity = UserUtils.getUserManagedSysIdentity(sysConfiguration.getDefaultManagedSysId(),
                                    user.getPrincipalList());

                            final IdmAuditLog auditLogChild = new IdmAuditLog();
                            auditLog.setRequestorPrincipal(requestorPrimaryIdentity.getLogin());
                            auditLog.setAction(AuditAction.PROVISIONING_MODIFY.value());
                            auditLog.addTarget(userEntity.getId(), AuditTarget.USER.value(), primaryIdentity.getLogin());

                            auditLogChild.setAuditDescription("Provisioning add user: " + userEntity.getId()
                                    + " with first/last name: " + userEntity.getFirstName() + "/" + userEntity.getLastName());
                            auditLog.addChild(auditLogChild);

                            for (String resId : resourceList) {
                                // skip provisioning for resource if it in NotProvisioning
                                // set
                                Resource res = resourceDataService.getResource(resId, null);
                                try {
                                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                                    bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
                                    bindingMap.put("operation", "MODIFY");
                                    bindingMap.put(AbstractProvisioningService.USER, userEntity);
                                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
                                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);
                                    // Protects other resources if one resource failed
                                    Map<String, Object> tmpMap = new HashMap<String, Object>(bindingMap); // prevent
                                    // bindingMap
                                    // rewrite
                                    // in
                                    // dataList
                                    ProvisionDataContainer data = provisionResource(res, userEntity, new ProvisionUser(user), tmpMap,
                                            primaryIdentity, requestorUserId);

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

    public ProvisionDataContainer provisionResource(final Resource res,
                                                    final UserEntity userEntity,
                                                    final ProvisionUser pUser,
                                                    final Map<String, Object> tmpMap,
                                                    final Login primaryIdentity,
                                                    final String requestId) {

        Map<String, Object> bindingMap = new HashMap<String, Object>(tmpMap); // prevent data rewriting

        ManagedSysDto managedSys = managedSysService.getManagedSysByResource(res.getId());
        String managedSysId = (managedSys != null) ? managedSys.getId() : null;
        if (managedSysId != null) {
            // we are checking if SrcSystemId is set in ProvisionUser it
            // means we should ignore this resource from provisioning to
            // avoid cyclic. Used in Reconciliation of one managed system to
            // another
            if (pUser.getSrcSystemId() != null && managedSysId.equalsIgnoreCase(pUser.getSrcSystemId())) {
                return null;
            }
            // what the new object will look like
            // Provision user that goes to the target system. Derived from
            // initial ProvisionUser after all changes
            ProvisionUser targetSysProvUser = new ProvisionUser(pUser);
            setCurrentSuperiors(targetSysProvUser); // TODO: Consider the
            // possibility to add and
            // update superiors by
            // cascade from UserEntity
            targetSysProvUser.setStatus(pUser.getStatus()); // copying user
            // status (need to
            // define
            // enable/disable
            // status)

            bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, res.getId());
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
            bindingMap.put(AbstractProvisioningService.USER, targetSysProvUser);

            List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getId());


            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }

            String onDeleteProp = findResourcePropertyByName(res.getId(), "ON_DELETE");
            if (StringUtils.isEmpty(onDeleteProp)) {
                onDeleteProp = "DELETE";
            }
            ProvLoginStatusEnum provLoginStatus;
            switch (onDeleteProp) {
                case "DISABLE":
                    provLoginStatus = ProvLoginStatusEnum.PENDING_ENABLE;
                    break;
                default:
                    provLoginStatus = ProvLoginStatusEnum.PENDING_UPDATE;
            }

            // get the identity linked to this resource / managedsys
            LoginEntity mLg = null;
            for (LoginEntity l : userEntity.getPrincipalList()) {
                if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                    l.setStatus(LoginStatusEnum.ACTIVE);
                    l.setProvStatus(provLoginStatus);
                    mLg = l;
                }
            }

            if (mLg != null) {
                log.debug("PROCESSING IDENTITY =");
            } else {
                log.debug("BUILDING NEW IDENTITY");
            }

            boolean isMngSysIdentityExistsInOpeniam = (mLg != null);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, isMngSysIdentityExistsInOpeniam ? AbstractProvisioningService.IDENTITY_EXIST
                    : AbstractProvisioningService.IDENTITY_NEW);

            if (!isMngSysIdentityExistsInOpeniam) {
                try {
                    log.debug(" - Building principal Name for: " + managedSysId);
                    String newPrincipalName = ProvisionServiceUtil
                            .buildUserPrincipalName(attrMap, scriptRunner, bindingMap);
                    if (StringUtils.isBlank(newPrincipalName)) {
                        log.debug("Principal name for managed sys " + managedSysId + " is blank.");
                        return null;
                    }
                    log.debug(" - New principalName = " + newPrincipalName);

                    mLg = new LoginEntity();
                    log.debug(" - PrimaryIdentity for build new identity for target system");

                    mLg.setLogin(newPrincipalName);
                    mLg.setManagedSysId(managedSysId);
                    mLg.setPassword(primaryIdentity.getPassword());
                    mLg.setUserId(primaryIdentity.getUserId());
                    mLg.setAuthFailCount(0);
                    mLg.setCreateDate(new Date(System.currentTimeMillis()));
                    mLg.setCreatedBy(userEntity.getLastUpdatedBy());
                    mLg.setIsLocked(0);
                    mLg.setFirstTimeLogin(1);
                    mLg.setStatus(LoginStatusEnum.ACTIVE);
                    mLg.setProvStatus(ProvLoginStatusEnum.PENDING_CREATE);

                    userEntity.getPrincipalList().add(mLg); // add new identity
                    // to user
                    // principals

                } catch (ScriptEngineException e) {
                    e.printStackTrace();
                }
            }

            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, null);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, mLg != null ? mLg.getLogin() : null);

            if (mLg != null) {
                bindingMap.put("lg", mLg);
                String decPassword = "";
                try {
                    decPassword = loginManager.decryptPassword(mLg.getUserId(), mLg.getPassword());
                } catch (Exception e) {
                    log.debug(" - Failed to decrypt password for " + mLg.getUserId());
                }
                bindingMap.put("password", decPassword);
            }

            // Identity of current target system
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            for (Login l : pUser.getPrincipalList()) { // saving Login
                // properties from pUser
                if (l.getId() != null && l.getId().equals(targetSysLogin.getId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                    targetSysLogin.setInitialStatus(l.getStatus());
                }
            }

            ProvisionDataContainer data = new ProvisionDataContainer();
            if (isMngSysIdentityExistsInOpeniam) {
                switch (onDeleteProp) {
                    case "DISABLE":
                        data.setOperation(ProvOperationEnum.ENABLE);
                        break;
                    default:
                        data.setOperation(ProvOperationEnum.UPDATE);
                }
            } else {
                data.setOperation(ProvOperationEnum.CREATE);
            }
            data.setRequestId(requestId);
            data.setResourceId(res.getId());
            data.setIdentity(targetSysLogin);
            data.setProvUser(targetSysProvUser);
            data.setBindingMap(bindingMap);

            return data;
        }
        return null;
    }

}
