package org.openiam.provision.service;

import groovy.lang.MissingPropertyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.*;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogDispatcher;
import org.openiam.idm.srvc.audit.service.AuditLogProvider;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.*;
import javax.jms.Queue;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("provDispatcher")
public class ProvisionDispatcher implements Sweepable {

    private static final Log log = LogFactory.getLog(ProvisionDispatcher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "provQueue")
    private Queue queue;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;
    @Autowired
    private KeyManagementService keyManagementService;
    @Autowired
    private AttributeMapDozerConverter attributeMapDozerConverter;
    @Autowired
    private ConnectorAdapter connectorAdapter;
    @Autowired
    private ManagedSystemService managedSystemService;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceDozerConverter resourceDozerConverter;
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ProvisionConnectorService connectorService;
    @Autowired
    private AuditLogProvider auditLogProvider;
    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    private final Object mutext = new Object();

    public void sweep() {

        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                synchronized (mutext) {
                    final List<List<ProvisionDataContainer>> batchList = new LinkedList<List<ProvisionDataContainer>>();
                    List<ProvisionDataContainer> list = new ArrayList<ProvisionDataContainer>();
                    Enumeration e = browser.getEnumeration();
                    int count = 0;
                    while (e.hasMoreElements()) {
                        list.add((ProvisionDataContainer) ((ObjectMessage) jmsTemplate.receive(queue)).getObject());
                        if (count++ >= 100) {
                            batchList.add(list);
                            list = new ArrayList<ProvisionDataContainer>();
                        }
                        e.nextElement();
                    }
                    batchList.add(list);

                    if (batchList.size() > 0 && batchList.get(0) != null && batchList.get(0).size() > 0) {
                        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                        Boolean res = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                            @Override
                            public Boolean doInTransaction(TransactionStatus status) {
                                final String parentAuditLogId = batchList.get(0).get(0).getParentAuditLogId();
                                final AuditLogBuilder parentAuditBuilder;
                                if (parentAuditLogId != null) {
                                    parentAuditBuilder = new AuditLogBuilder(auditLogService.findById(parentAuditLogId));
                                } else {
                                    parentAuditBuilder = auditLogProvider.getAuditLogBuilder();
                                    auditLogProvider.persist(parentAuditBuilder);
                                }

                                AuditLogBuilder auditBuilderDispatcherChild = new AuditLogBuilder();
                                auditBuilderDispatcherChild.setRequestorUserId(systemUserId).setTargetUser(null).setAction(AuditAction.PROVISIONING_DISPATCHER);
                                auditLogProvider.persist(auditBuilderDispatcherChild);

                                parentAuditBuilder.addChild(auditBuilderDispatcherChild);
                                auditLogProvider.persist(parentAuditBuilder);

                                for (final List<ProvisionDataContainer> entityList : batchList) {
                                    process(entityList, auditBuilderDispatcherChild);
                                }

                                auditLogProvider.remove(auditBuilderDispatcherChild.getEntity().getId());
                                return true;
                            }
                        });

                    }

                    return Boolean.TRUE;
                }
            }
        }
        );

    }

    private void process(List<ProvisionDataContainer> entities, final AuditLogBuilder auditBuilderDispatcherChild) {
        for (ProvisionDataContainer data : entities) {
            Login identity = data.getIdentity();

            LoginEntity loginEntity = loginDozerConverter.convertToEntity(identity, true);

            if (data.getOperation() == AttributeOperationEnum.DELETE) {

                try {
                    // udate target sys identity
                    loginEntity.setStatus(LoginStatusEnum.INACTIVE);
                    // do de-provisioning
                    StatusCodeType statusCodeType = deprovision(data).getStatus();
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "DELETE IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + statusCodeType);
                    if (StatusCodeType.SUCCESS.equals(statusCodeType)) {
                        loginEntity.setStatus(LoginStatusEnum.INACTIVE);
                        loginEntity.setAuthFailCount(0);
                        loginEntity.setPasswordChangeCount(0);
                        loginEntity.setIsLocked(0);

                        String scrambledPassword = PasswordGenerator.generatePassword(10);
                        try {
                            loginEntity.setPassword(loginManager.encryptPassword(loginEntity.getUserId(), scrambledPassword));
                        } catch (EncryptionException ee) {
                            log.error(ee);
                            // put the password in a clean state so that the
                            // operation continues
                            loginEntity.setPassword(null);
                        }
                    } else {
                        loginEntity.setStatus(LoginStatusEnum.FAIL_UPDATE);
                    }
                } catch (Throwable th) {
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "DELETE IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + th.getMessage());
                    loginEntity.setStatus(LoginStatusEnum.FAIL_UPDATE);
                }
            } else if (data.getOperation() == AttributeOperationEnum.ADD) {
                try {
                    // update target identity status
                    if (data.getProvUser().getSecondaryStatus() == UserStatusEnum.DISABLED
                            || data.getProvUser().getSecondaryStatus() == UserStatusEnum.DELETED
                            || data.getIdentity().getInitialStatus() == LoginStatusEnum.INACTIVE) {
                        loginEntity.setStatus(LoginStatusEnum.INACTIVE);
                    } else {
                        loginEntity.setStatus(data.getIdentity().getStatus() != null && data.getIdentity().getStatus() != LoginStatusEnum.PENDING_CREATE ? data.getIdentity().getStatus() : LoginStatusEnum.ACTIVE);
                    }

                    data.getIdentity().setStatus(loginEntity.getStatus());
                    // do provisioning to target system
                    ProvisionUserResponse response = provision(data, auditBuilderDispatcherChild);
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "ADD IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + response.getStatus() + " details=" + response.getErrorText());
                    if (!response.isSuccess()) {
                        loginEntity.setStatus(LoginStatusEnum.FAIL_CREATE);
                    }
                } catch (Throwable th) {
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "ADD IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + LoginStatusEnum.FAIL_CREATE + " details=" + th.getMessage());
                    loginEntity.setStatus(LoginStatusEnum.FAIL_CREATE);
                }
            } else if (data.getOperation() == AttributeOperationEnum.REPLACE) {
                try {
                    // update target identity status
                    if (data.getProvUser().getSecondaryStatus() == UserStatusEnum.DISABLED
                            || data.getProvUser().getSecondaryStatus() == UserStatusEnum.DELETED
                            || data.getIdentity().getInitialStatus() == LoginStatusEnum.INACTIVE) {
                        loginEntity.setStatus(LoginStatusEnum.INACTIVE);
                    } else {
                        loginEntity.setStatus(data.getIdentity().getStatus() != null && data.getIdentity().getStatus() != LoginStatusEnum.PENDING_UPDATE ? data.getIdentity().getStatus() : LoginStatusEnum.ACTIVE);
                    }
                    data.getIdentity().setStatus(loginEntity.getStatus());
                    // do provisioning to target system
                    ProvisionUserResponse response = provision(data, auditBuilderDispatcherChild);
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "UPDATE IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + response.getStatus() + " details=" + response.getErrorText());
                    if (!response.isSuccess()) {
                        loginEntity.setStatus(LoginStatusEnum.FAIL_UPDATE);
                        // if we have changed identity for managed sys when rename we have to revert it because failed
                        if (StringUtils.isNotEmpty(data.getIdentity().getOrigPrincipalName())) {
                            loginEntity.setLogin(data.getIdentity().getOrigPrincipalName());
                        }
                    }
                } catch (Throwable th) {
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "UPDATE IDENTITY=" + identity + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + LoginStatusEnum.FAIL_UPDATE + " details=" + th.getMessage());
                    loginEntity.setStatus(LoginStatusEnum.FAIL_UPDATE);
                }
            }
            loginManager.updateLogin(loginEntity);
        }
    }

    private ObjectResponse deprovision(ProvisionDataContainer data) {

        String requestId = data.getRequestId();
        Login targetSysLogin = data.getIdentity();
        ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
        Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
        ManagedSysDto mSys = managedSysDozerConverter.convertToDTO(
                managedSystemService.getManagedSysByResource(res.getResourceId(), "ACTIVE"), true);
        ProvisionConnectorEntity connectorEntity = connectorService.getProvisionConnectorsById(mSys.getConnectorId());
        if (connectorEntity == null) {
            return null;
        }

        CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
        request.setObjectIdentity(targetSysLogin.getLogin());
        request.setRequestID(requestId);
        request.setTargetID(targetSysLogin.getManagedSysId());
        request.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        request.setHostLoginPassword(passwordDecoded);
        request.setHostUrl(mSys.getHostUrl());
        request.setOperation("DELETE");
        request.setScriptHandler(mSys.getDeleteHandler());

        return connectorAdapter.deleteRequest(mSys, request, MuleContextProvider.getCtx());

    }

    private ProvisionUserResponse provision(ProvisionDataContainer data, final AuditLogBuilder auditBuilderDispatcherChild) {

        String requestId = data.getRequestId();
        ProvisionUserResponse response = new ProvisionUserResponse();

        ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
        Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
        ManagedSysDto mSys = managedSysDozerConverter.convertToDTO(
                managedSystemService.getManagedSysByResource(res.getResourceId(), "ACTIVE"), true);
        String managedSysId = (mSys != null) ? mSys.getManagedSysId() : null;
        ProvisionUser targetSysProvUser = data.getProvUser();

        try {
            Login targetSysLogin = data.getIdentity();
            Map<String, Object> bindingMap = data.getBindingMap();
            List<AttributeMapEntity> attrMapEntities = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
            List<AttributeMap> attrMap = attributeMapDozerConverter.convertToDTOList(attrMapEntities, true);
            ManagedSystemObjectMatch matchObj = null;
            List<ManagedSystemObjectMatchEntity> objList = managedSystemService.managedSysObjectParam(managedSysId, "USER");
            if (CollectionUtils.isNotEmpty(objList)) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(objList.get(0), false);
            }

            ExtensibleUser extUser = buildFromRules(targetSysProvUser, attrMap, bindingMap);

            // get the attributes at the target system
            // this lookup only for getting attributes from the
            // system
            Map<String, String> currentValueMap = new HashMap<String, String>();
            boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(
                    requestId, targetSysLogin, extUser, mSys, matchObj, currentValueMap);
            boolean connectorSuccess = false;

            // pre-processing
            bindingMap.put("targetSystemAttributes", currentValueMap);

            ResourceProp preProcessProp = res.getResourceProperty("PRE_PROCESS");
            String preProcessScript = preProcessProp != null ? preProcessProp.getPropValue() : null;
            if (StringUtils.isNotBlank(preProcessScript)) {
                PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                if (ppScript != null) {
                    int executePreProcessResult = executePreProcess(ppScript, bindingMap, targetSysProvUser,
                            isExistedInTargetSystem ? "MODIFY" : "ADD");
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "executePreProcessResult: " + (isExistedInTargetSystem ? "[MODIFY]" : "[ADD] = ") + executePreProcessResult);
                    if (executePreProcessResult == ProvisioningConstants.FAIL) {
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                        return response;
                    }
                }
            }

            if (!isExistedInTargetSystem) {

                connectorSuccess = requestAddModify(targetSysLogin, requestId, mSys, matchObj, extUser, true, auditBuilderDispatcherChild);

            } else { // if user exists in target system

                // updates the attributes with the correct operation codes
                extUser = updateAttributeList(extUser, currentValueMap);

                if (targetSysLogin.getOperation() == AttributeOperationEnum.REPLACE
                        && targetSysLogin.getOrigPrincipalName() != null) {
                    extUser.getAttributes().add(new ExtensibleAttribute(
                            "ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(),
                            AttributeOperationEnum.REPLACE.getValue(), "String"));
                }
                connectorSuccess = requestAddModify(targetSysLogin, requestId, mSys, matchObj, extUser, false, auditBuilderDispatcherChild);
            }

            // post processing
            ResourceProp postProcessProp = res.getResourceProperty("POST_PROCESS");
            String postProcessScript = postProcessProp != null ? postProcessProp.getPropValue() : null;
            if (StringUtils.isNotBlank(postProcessScript)) {
                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                if (ppScript != null) {
                    int executePostProcessResult = executePostProcess(ppScript, bindingMap, targetSysProvUser,
                            isExistedInTargetSystem ? "MODIFY" : "ADD", connectorSuccess);
                    auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, "executePostProcessResult " + (isExistedInTargetSystem ? "[MODIFY]" : "[ADD] =") + executePostProcessResult);
                    if (executePostProcessResult == ProvisioningConstants.FAIL) {
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                        return response;
                    }
                }
            }

            if (connectorSuccess) {
                response.setStatus(ResponseStatus.SUCCESS);
                response.setErrorCode(ResponseCode.SUCCESS);
                return response;
            }

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_CONNECTOR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    private boolean requestAddModify(Login mLg, String requestId, ManagedSysDto mSys,
                                     ManagedSystemObjectMatch matchObj, ExtensibleUser extUser, boolean isAdd, final AuditLogBuilder auditBuilderDispatcherChild) {

        CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
        userReq.setObjectIdentity(mLg.getLogin());
        userReq.setRequestID(requestId);
        userReq.setTargetID(mLg.getManagedSysId());
        userReq.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        userReq.setHostLoginPassword(passwordDecoded);
        userReq.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            userReq.setBaseDN(matchObj.getBaseDn());
        }
        userReq.setOperation(isAdd ? "ADD" : "MODIFY");
        userReq.setExtensibleObject(extUser);
        userReq.setScriptHandler(mSys.getAddHandler());

        ObjectResponse resp = isAdd ? connectorAdapter.addRequest(mSys, userReq, MuleContextProvider.getCtx()) :
                connectorAdapter.modifyRequest(mSys, userReq, MuleContextProvider.getCtx());
        auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, (isAdd ? "ADD IDENTITY = " : "MODIFY IDENTITY = ") + resp.getStatus() + " details:" + resp.getErrorMsgAsStr());
        return resp.getStatus() != StatusCodeType.FAILURE;
    }

    /**
     * Update the list of attributes with the correct operation values so that they can be
     * passed to the connector
     */
    private ExtensibleUser updateAttributeList(org.openiam.provision.type.ExtensibleUser extUser,
                                               Map<String, String> currentValueMap) {
        if (extUser == null) {
            return null;
        }
        log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        log.debug("updateAttributeList: Current attributeMap = " + currentValueMap);

        List<ExtensibleAttribute> extAttrList = extUser.getAttributes();
        if (extAttrList == null) {
            log.debug("Extended user attributes is null");
            return null;
        }

        log.debug("updateAttributeList: New Attribute List = " + extAttrList);
        if (extAttrList != null && currentValueMap == null) {
            for (ExtensibleAttribute attr : extAttrList) {
                attr.setOperation(1);
            }
        } else {

            for (ExtensibleAttribute attr : extAttrList) {
                String nm = attr.getName();
                if (currentValueMap == null) {
                    attr.setOperation(1);
                } else {
                    String curVal = currentValueMap.get(nm);
                    if (curVal == null) {
                        // temp hack
                        if (nm.equalsIgnoreCase("objectclass")) {
                            attr.setOperation(2);
                        } else {
                            log.debug("- Op = 1 - AttrName = " + nm);
                            attr.setOperation(1);
                        }
                    } else {
                        if (curVal.equalsIgnoreCase(attr.getValue())) {
                            log.debug("- Op = 0 - AttrName = " + nm);
                            attr.setOperation(0);
                        } else {
                            log.debug("- Op = 2 - AttrName = " + nm);
                            attr.setOperation(2);
                        }
                    }
                }
            }
        }
        return extUser;
    }

    private boolean getCurrentObjectAtTargetSystem(String requestId, Login mLg, ExtensibleUser extUser, ManagedSysDto mSys,
                                                   ManagedSystemObjectMatch matchObj,
                                                   Map<String, String> curValueMap) {

        String identity = mLg.getLogin();
        MuleContext muleContext = MuleContextProvider.getCtx();
        log.debug("Getting the current attributes in the target system for =" + identity);

        log.debug("- IsRename: " + mLg.getOrigPrincipalName());

        if (mLg.getOrigPrincipalName() != null && !mLg.getOrigPrincipalName().isEmpty()) {
            identity = mLg.getOrigPrincipalName();
        }

        LookupRequest<ExtensibleUser> reqType = new LookupRequest<ExtensibleUser>();
        reqType.setRequestID(requestId);
        reqType.setSearchValue(identity);

        reqType.setTargetID(mLg.getManagedSysId());
        reqType.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        reqType.setHostLoginPassword(passwordDecoded);
        reqType.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            reqType.setBaseDN(matchObj.getBaseDn());
        }
        reqType.setExtensibleObject(extUser);
        reqType.setScriptHandler(mSys.getLookupHandler());


        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
        if (lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ExtensibleAttribute> extAttrList = lookupSearchResponse.getObjectList().size() > 0 ? lookupSearchResponse.getObjectList().get(0).getAttributeList() : new LinkedList<ExtensibleAttribute>();

            if (extAttrList != null) {
                for (ExtensibleAttribute obj : extAttrList) {
                    String name = obj.getName();
                    String value = obj.getValue();
                    curValueMap.put(name, value);
                }
            } else {
                log.debug(" - NO attributes found in target system lookup ");
            }
            return true;
        }

        return false;
    }

    private ExtensibleUser buildFromRules(ProvisionUser pUser,
                                          List<AttributeMap> attrMap,
                                          Map<String, Object> bindingMap) {

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            log.debug("buildFromRules: attrMap IS NOT null");

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }

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

                String objectType = attr.getMapForObjectType();
                if (objectType != null) {

                    log.debug("buildFromRules: OBJECTTYPE="
                            + objectType + " SCRIPT OUTPUT=" + output
                            + " attribute name=" + attr.getAttributeName());

                    if (objectType.equalsIgnoreCase("USER") || objectType.equalsIgnoreCase("PASSWORD")) {
                        if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to the list
                                // the connectors can detect a delete if an attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (String) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);


                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to the list
                                // the connectors can detect a delete if an attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        ((Integer) output).toString(), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);


                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        sdf.format(d), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extUser.getAttributes().add(new ExtensibleAttribute(attr.getAttributeName(),
                                        (byte[]) output, 1, attr.getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (List) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);

                                log.debug("buildFromRules: added attribute to extUser:" + attr.getAttributeName());
                            }
                        }
                    } else if (objectType.equalsIgnoreCase("PRINCIPAL")) {

                        extUser.setPrincipalFieldName(attr.getAttributeName());
                        extUser.setPrincipalFieldDataType(attr.getDataType().getValue());

                    }
                }
            }
        }

        return extUser;
    }
    private String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if (managedSys.getPswd() != null) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId,
                        KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

    private PreProcessor createPreProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PreProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    private PostProcessor createPostProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PostProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    private int executePreProcess(PreProcessor ppScript,
                                  Map<String, Object> bindingMap, ProvisionUser user, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap);
        }
        return 0;
    }

    private int executePostProcess(PostProcessor ppScript,
                                   Map<String, Object> bindingMap, ProvisionUser user, String operation, boolean success) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap, success);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap, success);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap, success);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap, success);
        }
        return 0;
    }

}
