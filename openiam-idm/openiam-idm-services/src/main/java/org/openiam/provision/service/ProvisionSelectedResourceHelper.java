package org.openiam.provision.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.ResourcePropSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.SpringSecurityHelper;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ProvisionSelectedResourceHelper extends BaseProvisioningHelper {
    @Autowired
    ProvisionServiceUtil provisionServiceUtil;

    @Autowired
    protected ManagedSystemService managedSystemService;

    public ProvisionUserResponse provisionSelectedResources(final List<String> userIds, final Collection<String> resourceList) {
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
                    final IdmAuditLogEntity auditLog = auditLogHelper.newInstance();
                    UserEntity requestor = userMgr.getUser(SpringSecurityHelper.getRequestorUserId());

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

                            final IdmAuditLogEntity auditLogChild = auditLogHelper.newInstance();
                            auditLog.setRequestorPrincipal(requestorPrimaryIdentity.getLogin());
                            auditLog.setAction(AuditAction.PROVISIONING_MODIFY.value());
                            auditLog.addTarget(userEntity.getId(), AuditTarget.USER.value(), primaryIdentity.getLogin());

                            auditLogChild.setAuditDescription("Provisioning add user: " + userEntity.getId()
                                    + " with first/last name: " + userEntity.getFirstName() + "/" + userEntity.getLastName());
                            auditLog.addChild(auditLogChild);

                            for (String resId : resourceList) {
                                // skip provisioning for resource if it in NotProvisioning
                                // set
                                Resource res = resourceService.findResourceDtoById(resId, null);
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
                                            primaryIdentity, SpringSecurityHelper.getRequestorUserId());

                                    auditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                            "Provisioning for resource: " + res.getName());
                                    if (data != null) {
                                        data.setParentAuditLogId(auditLog.getId());
                                        dataList.add(data);
                                    }

                                    // Additional operation is required for managed system with property ON_DELETE = DISABLE
                                    final ResourcePropSearchBean sb = new ResourcePropSearchBean();
                                    sb.setFindInCache(true);
                                    sb.setResourceId(res.getId());
                                    sb.setNameToken(new SearchParam("ON_DELETE", MatchType.EXACT));
                                    final List<ResourceProp> props = resourceService.findBeansDTO(sb, 0, Integer.MAX_VALUE);
                                    String onDeleteProp = (CollectionUtils.isNotEmpty(props)) ? props.get(0).getValue() : null;
                                    if (onDeleteProp != null && "DISABLE".equalsIgnoreCase(onDeleteProp)) {
                                        ProvisionDataContainer enableData = provisionResource(res, userEntity, new ProvisionUser(user), bindingMap,
                                                primaryIdentity, SpringSecurityHelper.getRequestorUserId());
                                        if (enableData != null) {
                                            enableData.setParentAuditLogId(auditLog.getId());
                                            dataList.add(enableData);
                                        }
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
                        auditLogHelper.enqueue(auditLog);
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

        ManagedSysDto managedSys = managedSystemService.getManagedSysDtoByResource(res.getId());
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
            bindingMap.put(AbstractProvisioningService.USER_ATTRIBUTES, userMgr.getUserAttributesDto(pUser.getId()));

            List<AttributeMap> attrMap = managedSystemService.getResourceAttributeMapsDTO(res.getId());


            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParamDTO(managedSysId, ManagedSystemObjectMatch.USER);
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }

            final ResourcePropSearchBean sb = new ResourcePropSearchBean();
            sb.setFindInCache(true);
            sb.setResourceId(res.getId());
            sb.setNameToken(new SearchParam("ON_DELETE", MatchType.EXACT));
            final List<ResourceProp> props = resourceService.findBeansDTO(sb, 0, Integer.MAX_VALUE);
            String onDeleteProp = (CollectionUtils.isNotEmpty(props)) ? props.get(0).getValue() : null;
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

            if(log.isDebugEnabled()) {
	            if (mLg != null) {
	                log.debug("PROCESSING IDENTITY ="+mLg.getLogin()); //SIA 2015-08-01
	            } else {
	                log.debug("BUILDING NEW IDENTITY");
	            }
            }

            boolean isMngSysIdentityExistsInOpeniam = (mLg != null);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, isMngSysIdentityExistsInOpeniam ? AbstractProvisioningService.IDENTITY_EXIST
                    : AbstractProvisioningService.IDENTITY_NEW);

            if (!isMngSysIdentityExistsInOpeniam) {
                try {
                	if(log.isDebugEnabled()) {
                		log.debug(" - Building principal Name for: " + managedSysId);
                	}
                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, new HashMap<>());
                    String newPrincipalName = provisionServiceUtil
                            .buildUserPrincipalName(attrMap, scriptRunner, bindingMap);
                    if (StringUtils.isBlank(newPrincipalName)) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("Principal name for managed sys " + managedSysId + " is blank.");
                    	}
                        return null;
                    }
                    if(log.isDebugEnabled()) {
                    	log.debug(" - New principalName = " + newPrincipalName);
                    }

                    mLg = new LoginEntity();
                    if(log.isDebugEnabled()) {
                    	log.debug(" - PrimaryIdentity for build new identity for target system");
                    }

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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, mLg != null ? mLg.getLogin() : null);
            if (mLg != null) {
                bindingMap.put("lg", mLg);
                String decPassword = "";
                try {
                    decPassword = loginManager.decryptPassword(mLg.getUserId(), mLg.getPassword());
                    if(log.isDebugEnabled()) {
                    	log.debug(" - decryptPassword ");
                    }
                } catch (Exception e) {
                	if(log.isDebugEnabled()) {
                		log.debug(" - Failed to decrypt password for " + mLg.getUserId());
                	}
                }
                bindingMap.put("password", decPassword);
            }

            // Identity of current target system
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            if(log.isDebugEnabled()) {
            	log.debug(" - targetSysLogin converted ");
            }
            for (Login l : pUser.getPrincipalList()) { // saving Login
                // properties from pUser
                if (l.getId() != null && l.getId().equals(targetSysLogin.getId())) {
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
            if(log.isDebugEnabled()) {
            	log.debug(" - provisionResource finished ");
            }
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
        MngSysPolicyDto mngSysPolicy = managedSystemService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSysId, "USER_OBJECT");
        List<AttributeMap> attrMap = managedSystemService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            //used just to check on hidden to defin show the value in logs or not
            String[] hiddenAttributesArr = hiddenAttributes.toLowerCase().trim().split(",");
            List<String> hiddenAttributesList = Arrays.asList(hiddenAttributesArr);
            if(log.isDebugEnabled()) {
            	log.debug("buildFromRules: attrMap IS NOT null");
            }

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }

                String objectType = attr.getMapForObjectType();
                if (objectType != null) {

                    if (objectType.equalsIgnoreCase(PolicyMapObjectTypeOptions.USER.name())) {
                        Object output = "";
                        try {
                            output = provisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, scriptRunner);
                        } catch (ScriptEngineException see) {
                            log.error("Error in script = '", see);
                            continue;
                        } catch (Exception mpe) {
                            log.error("Error in script = '", mpe);
                            continue;
                        }

                        if(log.isDebugEnabled()) {
	                        log.debug("buildFromRules: OBJECTTYPE="+objectType+", ATTRIBUTE=" + attr.getName() +
	                                ", SCRIPT OUTPUT=" +
	                                (hiddenAttributesList.contains(attr.getName().toLowerCase())
	                                        ? "******" : output));
                        }

                        if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getName(), (String) output, -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getName(),
                                        ((Integer) output).toString(), -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getName(), sdf.format(d), -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr.getName(), (byte[]) output, -1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getName(),
                                        (BaseAttributeContainer) output, -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof ExtensibleAttribute) {
                                newAttr = (ExtensibleAttribute) output;
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof List) {
                                // process a list - multi-valued object
                                if (CollectionUtils.isNotEmpty((List) output)) {
                                    newAttr = new ExtensibleAttribute(attr.getName(), (List) output, -1, attr
                                            .getDataType().getValue());
                                    newAttr.setObjectType(objectType);
                                    extUser.getAttributes().add(newAttr);
                                    if(log.isDebugEnabled()) {
                                    	log.debug("buildFromRules: added attribute to extUser:" + attr.getName());
                                    }
                                }
                            }
                        }
                    } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {

                        extUser.setPrincipalFieldName(attr.getName());
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
        MngSysPolicyDto mngSysPolicy = managedSystemService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSysId, "USER_OBJECT");
        List<AttributeMap> attrMap = managedSystemService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());
        ExtensibleUser extUser = new ExtensibleUser();
        if (attrMap != null) {
            for (AttributeMap attr : attrMap) {
                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }
                String objectType = attr.getMapForObjectType();
                if (objectType != null) {
                    if (PolicyMapObjectTypeOptions.USER.name().equalsIgnoreCase(objectType)) {
                        ExtensibleAttribute newAttr = new ExtensibleAttribute(attr.getName(), null);
                        newAttr.setObjectType(objectType);
                        extUser.getAttributes().add(newAttr);

                    } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                        extUser.setPrincipalFieldName(attr.getName());
                        extUser.setPrincipalFieldDataType(attr.getDataType().getValue());
                    }
                }
            }
        }

        return extUser;
    }



}
