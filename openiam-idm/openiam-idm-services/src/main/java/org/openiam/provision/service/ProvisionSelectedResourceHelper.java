package org.openiam.provision.service;

import groovy.lang.MissingPropertyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttributeContainer;
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
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.UserUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ProvisionSelectedResourceHelper extends BaseProvisioningHelper {

    public ProvisionUserResponse provisionSelectedResources(final List<String> userIds, final String requestorUserId, final Collection<String> resourceList) {
        final List<ProvisionDataContainer> dataList = new LinkedList<>();
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
                                    Map<String, Object> bindingMap = new HashMap<>();
                                    bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
                                    bindingMap.put("operation", "MODIFY");
                                    bindingMap.put(AbstractProvisioningService.USER, userEntity);
                                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
                                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);
                                    // Protects other resources if one resource failed
                                    Map<String, Object> tmpMap = new HashMap<>(bindingMap); // prevent
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

        Map<String, Object> bindingMap = new HashMap<>(tmpMap); // prevent data rewriting
        log.debug(" - provisionResource started ");
        String managedSysId = managedSysDaoService.getManagedSysIdByResource(res.getId(), "ACTIVE");
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
            bindingMap.put(AbstractProvisioningService.USER_ATTRIBUTES,userMgr.getUserAttributesDto(pUser.getId()));

            List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getId());


            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }

            String onDeleteProp = resourceDataService.getResourcePropValueByName(res.getId(), "ON_DELETE");
            if(StringUtils.isEmpty(onDeleteProp)) {
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
                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, new HashMap<>());
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

            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, mLg != null ? mLg.getLogin() : null);
            if (mLg != null) {
                bindingMap.put("lg", mLg);
                String decPassword = "";
                try {
                    decPassword = loginManager.decryptPassword(mLg.getUserId(), mLg.getPassword());
                    log.debug(" - decryptPassword ");
                } catch (Exception e) {
                    log.debug(" - Failed to decrypt password for " + mLg.getUserId());
                }
                bindingMap.put("password", decPassword);
            }

            // Identity of current target system
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            log.debug(" - targetSysLogin converted ");
            for (Login l : pUser.getPrincipalList()) { // saving Login
                // properties from pUser
                if (l.getLoginId() != null && l.getLoginId().equals(targetSysLogin.getLoginId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                    targetSysLogin.setInitialStatus(l.getStatus());
                    break;
                }
            }

            ProvisionDataContainer data = new ProvisionDataContainer();
            if (isMngSysIdentityExistsInOpeniam) {
                switch (onDeleteProp) {
                    case "DISABLE":
                        data.setOperation(ProvOperationEnum.ENABLE);
                        bindingMap.put("operation", "RESUME");
                        break;
                    default:
                        data.setOperation(ProvOperationEnum.UPDATE);
                        bindingMap.put("operation", "MODIFY");
                }
            } else {
                data.setOperation(ProvOperationEnum.CREATE);
                bindingMap.put("operation", "ADD");
            }
            data.setRequestId(requestId);
            data.setResourceId(res.getId());
            data.setIdentity(targetSysLogin);
            data.setProvUser(targetSysProvUser);
            data.setBindingMap(bindingMap);
            log.debug(" - provisionResource finished ");
            return data;
        }
        return null;
    }

    /**
     * Build ExtensibleUser attributes by PolicyMap groovy scripts
     *
     * @param managedSysId
     * @param bindingMap
     * @return
     */
    public ExtensibleUser buildFromRules(String managedSysId, Map<String, Object> bindingMap) {

        List<AttributeMap> attrMap = managedSysService.getAttributeMapsByManagedSysId(managedSysId);

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            //used just to check on hidden to defin show the value in logs or not
            String[] hiddenAttributesArr = hiddenAttributes.toLowerCase().trim().split(",");
            List<String> hiddenAttributesList = Arrays.asList(hiddenAttributesArr);

            log.debug("buildFromRules: attrMap IS NOT null");

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }

                String objectType = attr.getMapForObjectType();
                if (objectType != null) {

                    if (objectType.equalsIgnoreCase(PolicyMapObjectTypeOptions.USER.name())) {
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
                                (hiddenAttributesList.contains(attr.getAttributeName().toLowerCase())
                                        ? "******" : output));

                        if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), (String) output, -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        ((Integer) output).toString(), -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr.getAttributeName(), (byte[]) output, -1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof ExtensibleAttribute) {
                                newAttr = (ExtensibleAttribute)output;
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof List) {
                                // process a list - multi-valued object
                                if (CollectionUtils.isNotEmpty((List)output)) {
                                    newAttr = new ExtensibleAttribute(attr.getAttributeName(), (List) output, -1, attr
                                            .getDataType().getValue());
                                    newAttr.setObjectType(objectType);
                                    extUser.getAttributes().add(newAttr);
                                    log.debug("buildFromRules: added attribute to extUser:" + attr.getAttributeName());
                                }
                            }
                        }
                    } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {

                        extUser.setPrincipalFieldName(attr.getAttributeName());
                        extUser.setPrincipalFieldDataType(attr.getDataType().getValue());

                    }
                }
            }
        }

        return extUser;
    }

    /**
     * Build ExtensibleUser with attributes without values. We don't need to process groovy scripts in this method.
     * Used for lookup request.
     *
     * @param managedSysId
     * @return
     */
    public ExtensibleUser buildEmptyAttributesExtensibleUser(String managedSysId) {
        List<AttributeMap> attrMap = managedSysService.getAttributeMapsByManagedSysId(managedSysId);
        ExtensibleUser extUser = new ExtensibleUser();
        if (attrMap != null) {
            for (AttributeMap attr : attrMap) {
                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }
                String objectType = attr.getMapForObjectType();
                if (objectType != null) {
                    if (PolicyMapObjectTypeOptions.USER.name().equalsIgnoreCase(objectType)) {
                        ExtensibleAttribute newAttr = new ExtensibleAttribute(attr.getAttributeName(), null);
                        newAttr.setObjectType(objectType);
                        extUser.getAttributes().add(newAttr);

                    } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                        extUser.setPrincipalFieldName(attr.getAttributeName());
                        extUser.setPrincipalFieldDataType(attr.getDataType().getValue());
                    }
                }
            }
        }

        return extUser;
    }



}
