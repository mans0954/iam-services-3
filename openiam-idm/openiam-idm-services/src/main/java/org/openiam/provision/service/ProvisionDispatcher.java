package org.openiam.provision.service;

import groovy.lang.MissingPropertyException;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvOperationEnum;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
    private ConnectorAdapter connectorAdapter;
    @Autowired
    private ManagedSystemWebService managedSystemWebService;

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ProvisionService provisionService;
    @Autowired
    private ResourceDozerConverter resourceDozerConverter;
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ProvisionConnectorService connectorService;

    @Value(",${org.openiam.debug.hidden.attributes},")
    private String hiddenAttributes;

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

    @Override
    //TODO change when Spring 3.2.2 @Scheduled(fixedDelayString = "${org.openiam.prov.threadsweep}")
    @Scheduled(fixedDelay=3000)
    public void sweep() {

        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                synchronized (mutext) {
                    final List<ProvisionDataContainer> list = new ArrayList<ProvisionDataContainer>();
                    Enumeration e = browser.getEnumeration();
                    while (e.hasMoreElements()) {
                        list.add((ProvisionDataContainer) ((ObjectMessage) jmsTemplate.receive(queue)).getObject());
                        e.nextElement();
                    }

                    process(list);

                    return Boolean.TRUE;
                }
            }
        });

    }

    public void process(final List<ProvisionDataContainer> entities) {
        for (final ProvisionDataContainer data : entities) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    process(data);
                }
            });
            try {
                //chance to other threads to be executed
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void process(final ProvisionDataContainer data) {

        Login identity = data.getIdentity();

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_DISPATCHER.value());
        idmAuditLog.setTargetUser(identity.getUserId(), identity.getLogin());
        idmAuditLog.setManagedSysId(identity.getManagedSysId());

        idmAuditLog.succeed();

		try {

			LoginChanges loginChanges = new LoginChanges();

			if (data.getOperation() == ProvOperationEnum.DELETE) {
                try {
                    // update target sys identity
                    // do de-provisioning
                    ObjectResponse response = deprovision(data, idmAuditLog);
                    StatusCodeType statusCodeType = response.getStatus();
                    if (statusCodeType == StatusCodeType.FAILURE
                            && ErrorCode.NO_SUCH_IDENTIFIER.equals(response.getError())) {
                        statusCodeType = StatusCodeType.SUCCESS; // User
                                                                 // doesn't
                                                                 // exist in
                                                                 // target
                                                                 // system
                    }

                    if (StatusCodeType.SUCCESS.equals(statusCodeType)) {
						loginChanges.setProvStatus(ProvLoginStatusEnum.DELETED);
						loginChanges.setAuthFailCount(0);
						loginChanges.setPasswordChangeCount(0);
						loginChanges.setIsLocked(0);

                        String scrambledPassword = PasswordGenerator.generatePassword(10);
                        try {
							loginChanges.setPassword(loginManager.encryptPassword(identity.getUserId(),
                                    scrambledPassword));
                        } catch (EncryptionException ee) {
                            log.error(ee);
                            // put the password in a clean state so that the
                            // operation continues
							loginChanges.setPassword(null);
                        }

                    } else {
						idmAuditLog.fail();
						idmAuditLog.setFailureReason(ProvLoginStatusEnum.FAIL_DELETE.getValue());
						if (CollectionUtils.isNotEmpty(response.getErrorMessage())) {
							for(final String error : response.getErrorMessage()) {
								idmAuditLog.setFailureReason(error);
							}
						}
						loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DELETE);
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DELETE IDENTITY=" + identity
                            + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status=" + th.getMessage());
					loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DELETE);
                }

            } else if (data.getOperation() == ProvOperationEnum.CREATE) {
                try {
                    // do provisioning to target system
                    ProvisionUserResponse response = provision(data, idmAuditLog);
                    if (response.isSuccess()) {
						loginChanges.setProvStatus(ProvLoginStatusEnum.CREATED);
                    } else {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(response.getErrorText());
						loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_CREATE);
                    }

                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ADD IDENTITY=" + identity
                            + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status="
                            + ProvLoginStatusEnum.FAIL_CREATE + " details=" + th.getMessage());
					loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_CREATE);
                }

            } else if (data.getOperation() == ProvOperationEnum.UPDATE) {
                try {
                    // do provisioning to target system
                    ProvisionUserResponse response = provision(data, idmAuditLog);

                    if (response.isSuccess()) {
						loginChanges.setProvStatus(ProvLoginStatusEnum.UPDATED);
                    } else {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(response.getErrorText());

						loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_UPDATE);
                        // if we have changed identity for managed sys when
                        // rename we have to revert it because failed
                        if (StringUtils.isNotEmpty(data.getIdentity().getOrigPrincipalName())) {
							loginChanges.setLogin(data.getIdentity().getOrigPrincipalName());
                        }
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "UPDATE IDENTITY=" + identity
                            + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status="
                            + ProvLoginStatusEnum.FAIL_UPDATE + " details=" + th.getMessage());
					loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_UPDATE);
                    // if we have changed identity for managed sys when
                    // rename we have to revert it because failed
                    if (StringUtils.isNotEmpty(data.getIdentity().getOrigPrincipalName())) {
						loginChanges.setLogin(data.getIdentity().getOrigPrincipalName());
                    }
                }

            } else if (data.getOperation() == ProvOperationEnum.DISABLE) {

                String requestId = data.getRequestId();
                ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
                Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
                ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
                String managedSysId = (mSys != null) ? mSys.getId() : null;

                Login targetSysLogin = data.getIdentity();

                SuspendResumeRequest suspendReq = new SuspendResumeRequest();
                suspendReq.setObjectIdentity(targetSysLogin.getLogin());
                suspendReq.setTargetID(managedSysId);
                suspendReq.setRequestID(requestId);
                suspendReq.setScriptHandler(mSys.getSuspendHandler());
                suspendReq.setHostLoginId(mSys.getUserId());

                ExtensibleUser extUser = buildFromRules(managedSysId, data.getBindingMap());
                suspendReq.setExtensibleObject(extUser);

                String passwordDecoded = mSys.getPswd();
                try {
                    passwordDecoded = getDecryptedPassword(mSys);
                } catch (ConnectorDataException e) {
                    e.printStackTrace();
                }
                suspendReq.setHostLoginPassword(passwordDecoded);
                suspendReq.setHostUrl(mSys.getHostUrl());

                try {
                    ResponseType resp = connectorAdapter.suspendRequest(mSys, suspendReq,
                            MuleContextProvider.getCtx());
                    if (StatusCodeType.SUCCESS.equals(resp.getStatus())) {
						loginChanges.setProvStatus(ProvLoginStatusEnum.DISABLED);
                    } else {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(resp.getErrorMsgAsStr());
						loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DISABLE);
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DISABLE IDENTITY=" + identity
                            + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status="
                            + ProvLoginStatusEnum.FAIL_DISABLE + " details=" + th.getMessage());
					loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DISABLE);
                }

            } else if (data.getOperation() == ProvOperationEnum.ENABLE) {

                String requestId = data.getRequestId();
                ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
                Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
                ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
                String managedSysId = (mSys != null) ? mSys.getId() : null;

                Login targetSysLogin = data.getIdentity();

                SuspendResumeRequest suspendReq = new SuspendResumeRequest();
                suspendReq.setObjectIdentity(targetSysLogin.getLogin());
                suspendReq.setTargetID(managedSysId);
                suspendReq.setRequestID(requestId);
                suspendReq.setScriptHandler(mSys.getSuspendHandler());
                suspendReq.setHostLoginId(mSys.getUserId());

                ExtensibleUser extUser = buildFromRules(managedSysId, data.getBindingMap());
                suspendReq.setExtensibleObject(extUser);

                String passwordDecoded = mSys.getPswd();
                try {
                    passwordDecoded = getDecryptedPassword(mSys);
                } catch (ConnectorDataException e) {
                    e.printStackTrace();
                }
                suspendReq.setHostLoginPassword(passwordDecoded);
                suspendReq.setHostUrl(mSys.getHostUrl());

                try {
                    ResponseType resp = connectorAdapter.resumeRequest(mSys, suspendReq,
                            MuleContextProvider.getCtx());
                    if (StatusCodeType.SUCCESS.equals(resp.getStatus())) {
						loginChanges.setProvStatus(ProvLoginStatusEnum.ENABLED);
                    } else {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(resp.getErrorMsgAsStr());
						loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ENABLE IDENTITY=" + identity
                            + " from MANAGED_SYS_ID=" + identity.getManagedSysId() + " status="
                            + ProvLoginStatusEnum.FAIL_ENABLE + " details=" + th.getMessage());
					loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);
                }
            }

			saveChanges(identity, loginChanges);

		} finally {
            IdmAuditLog parentAuditLog = StringUtils.isNotEmpty(data.getParentAuditLogId()) ? auditLogService.findById(data.getParentAuditLogId()) : null;
            if(parentAuditLog != null) {
                parentAuditLog.addChild(idmAuditLog);
                idmAuditLog.addParent(parentAuditLog);
                auditLogService.save(parentAuditLog);
            } else {
                auditLogService.save(idmAuditLog);
            }
        }
    }

	private void saveChanges(Login identity, LoginChanges changes) {

		LoginEntity loginEntity = null;
		if (StringUtils.isNotBlank(identity.getLoginId())) {
			loginEntity = loginManager.getLoginDetails(identity.getLoginId());
		}
		if (loginEntity == null) {
			loginEntity = loginManager.getLoginByManagedSys(identity.getLogin(), identity.getManagedSysId());
		}
		if (loginEntity == null) {
			loginEntity = loginDozerConverter.convertToEntity(identity, true);
			log.error(String.format("Identity %s for managed sys %s hasn't saved yet to a database",
					identity.getLogin(), identity.getManagedSysId()));
		}

		for (final String field : changes.getFieldsAffected()) {
			switch (field) {
				case "login":
					if (StringUtils.equals(identity.getLogin(), loginEntity.getLogin())) {
						loginEntity.setLogin(changes.getLogin());
					}
					break;
				case "provStatus":
					loginEntity.setProvStatus(changes.getProvStatus());
					break;
				case "password":
					loginEntity.setPassword(changes.getPassword());
					break;
				case "authFailCount":
					loginEntity.setAuthFailCount(changes.getAuthFailCount());
					break;
				case "passwordChangeCount":
					loginEntity.setPasswordChangeCount(changes.getPasswordChangeCount());
					break;
				case "isLocked":
					loginEntity.setIsLocked(changes.getIsLocked());
					break;
			}
		}

	}

    private ObjectResponse deprovision(ProvisionDataContainer data, IdmAuditLog idmAuditLog) {

        String requestId = data.getRequestId();
        Login targetSysLogin = data.getIdentity();
        ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
        Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
        ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
        idmAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());

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
        request.setExtensibleObject(new ExtensibleUser());

        return connectorAdapter.deleteRequest(mSys, request, MuleContextProvider.getCtx());

    }

    private ProvisionUserResponse provision(ProvisionDataContainer data, final IdmAuditLog idmAuditLog) {

        String requestId = data.getRequestId();
        ProvisionUserResponse response = new ProvisionUserResponse();

        ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
        Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
        ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
        String managedSysId = (mSys != null) ? mSys.getId() : null;
        ProvisionUser targetSysProvUser = data.getProvUser();
        idmAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());
        try {
            Login targetSysLogin = data.getIdentity();
            Map<String, Object> bindingMap = data.getBindingMap();
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] objArr = managedSystemWebService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);

            if (objArr != null && objArr.length > 0) {
                matchObj = objArr[0];
            }

            // get the attributes at the target system
            // this lookup only for getting attributes from the
            // system
            Map<String, ExtensibleAttribute> currentValueMap = new HashMap<>();
            boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(requestId, targetSysLogin, provisionService.buildExtensibleUser(managedSysId), mSys,
                    matchObj, currentValueMap);

            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, currentValueMap);
            ExtensibleUser extUser = buildFromRules(managedSysId, bindingMap);
            try {
                idmAuditLog.addCustomRecord("ATTRIBUTES", extUser.getAttributesAsJSON());
            } catch (JsonGenerationException jge) {
                log.error(jge);
            }

            boolean connectorSuccess = false;

            // pre-processing
            ResourceProp preProcessProp = res.getResourceProperty("PRE_PROCESS");
            String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
            if (StringUtils.isNotBlank(preProcessScript)) {
                PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                if (ppScript != null) {
                    int executePreProcessResult = executePreProcess(ppScript, bindingMap, targetSysProvUser, null,
                            isExistedInTargetSystem ? "MODIFY" : "ADD");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "executePreProcessResult: "
                            + (isExistedInTargetSystem ? "[MODIFY]" : "[ADD] = ") + executePreProcessResult);
                    if (executePreProcessResult == ProvisioningConstants.FAIL) {
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                        return response;
                    }
                }
            }

            if (!isExistedInTargetSystem) {
                ObjectResponse resp = provisionService.requestAddModify(extUser, targetSysLogin, true, requestId,
                        idmAuditLog);
                connectorSuccess = resp.getStatus() != StatusCodeType.FAILURE;

            } else { // if user exists in target system

                // updates the attributes with the correct operation codes
                extUser = updateAttributeList(extUser, currentValueMap);

                if (targetSysLogin.getOperation() == AttributeOperationEnum.REPLACE
                        && targetSysLogin.getOrigPrincipalName() != null) {
                    extUser.getAttributes().add(
                            new ExtensibleAttribute("ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(),
                                    AttributeOperationEnum.REPLACE.getValue(), "String"));
                }
                ObjectResponse resp = provisionService.requestAddModify(extUser, targetSysLogin, false, requestId,
                        idmAuditLog);
                connectorSuccess = resp.getStatus() != StatusCodeType.FAILURE;
            }

            // post processing
            ResourceProp postProcessProp = res.getResourceProperty("POST_PROCESS");
            String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
            if (StringUtils.isNotBlank(postProcessScript)) {
                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                if (ppScript != null) {
                    int executePostProcessResult = executePostProcess(ppScript, bindingMap, targetSysProvUser, null,
                            isExistedInTargetSystem ? "MODIFY" : "ADD", connectorSuccess);
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "executePostProcessResult "
                            + (isExistedInTargetSystem ? "[MODIFY]" : "[ADD] =") + executePostProcessResult);
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

    /**
     * Update the list of attributes with the correct operation values so that
     * they can be passed to the connector
     */
    private ExtensibleUser updateAttributeList(org.openiam.provision.type.ExtensibleUser extUser,
            Map<String, ExtensibleAttribute> currentValueMap) {
        if (extUser == null) {
            return null;
        }
        log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        // log.debug("updateAttributeList: Current attributeMap = " +
        // currentValueMap);

        List<ExtensibleAttribute> extAttrList = extUser.getAttributes();
        if (extAttrList == null) {
            log.debug("Extended user attributes is null");
            return null;
        }

        // log.debug("updateAttributeList: New Attribute List = " +
        // extAttrList);
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
                    ExtensibleAttribute curAttr = currentValueMap.get(nm);
                    if (attr.valuesAreEqual(curAttr)) {
                        log.debug("- Op = 0 - AttrName = " + nm);
                        attr.setOperation(0);
                    } else if (curAttr == null || !curAttr.containsAnyValue()) {
                        log.debug("- Op = 1 - AttrName = " + nm);
                        attr.setOperation(1);
                    } else {
                        log.debug("- Op = 2 - AttrName = " + nm);
                        attr.setOperation(2);
                    }
                }
            }
        }
        return extUser;
    }

    private boolean getCurrentObjectAtTargetSystem(String requestId, Login mLg, ExtensibleUser extUser,
            ManagedSysDto mSys, ManagedSystemObjectMatch matchObj, Map<String, ExtensibleAttribute> curValueMap) {

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
        if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
            reqType.setBaseDN(matchObj.getSearchBaseDn());
        }
        reqType.setExtensibleObject(extUser);
        reqType.setScriptHandler(mSys.getLookupHandler());

        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
        if (lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ExtensibleAttribute> extAttrList = lookupSearchResponse.getObjectList().size() > 0 ? lookupSearchResponse
                    .getObjectList().get(0).getAttributeList()
                    : new LinkedList<ExtensibleAttribute>();

            if (extAttrList != null) {
                for (ExtensibleAttribute attr : extAttrList) {
                    curValueMap.put(attr.getName(), attr);
                }
            } else {
                log.debug(" - NO attributes found in target system lookup ");
            }
            return true;
        }

        return false;
    }

    private ExtensibleUser buildFromRules(String managedSysId, Map<String, Object> bindingMap) {

        List<AttributeMap> attrMap = managedSystemWebService.getAttributeMapsByManagedSysId(managedSysId);

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

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
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        ((Integer) output).toString(), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), 1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr.getAttributeName(), (byte[]) output, 1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof List) {
                                // process a list - multi-valued object
                                if (CollectionUtils.isNotEmpty((List)output)) {
                                    newAttr = new ExtensibleAttribute(attr.getAttributeName(), (List) output, 1, attr
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

    private String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if (managedSys.getPswd() != null) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()),
                        managedSys.getPswd());
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

    private int executePreProcess(PreProcessor ppScript, Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync,
            String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        }
        return 0;
    }

    private static int executePostProcess(PostProcessor ppScript, Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync,
            String operation, boolean success) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap, success);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap, success);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap, success);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap, success);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap, success);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap, success);
        }
        return 0;
    }

	private static class LoginChanges extends Login {

		private Set<String> fieldsAffected = new HashSet<>();

		public Set<String> getFieldsAffected() {
			return fieldsAffected;
		}

		@Override
		public void setProvStatus(ProvLoginStatusEnum status) {
			fieldsAffected.add("provStatus");
			super.setProvStatus(status);
		}

		public void setPassword(String password) {
			fieldsAffected.add("password");
			super.setPassword(password);
		}

		public void setLogin(String login) {
			fieldsAffected.add("login");
			super.setLogin(login);
		}

		public void setAuthFailCount(int authFailCount) {
			fieldsAffected.add("authFailCount");
			super.setAuthFailCount(authFailCount);
		}

		public void setPasswordChangeCount(int passwordChangeCount) {
			fieldsAffected.add("passwordChangeCount");
			super.setPasswordChangeCount(passwordChangeCount);
		}

		public void setIsLocked(int isLocked) {
			fieldsAffected.add("isLocked");
			super.setIsLocked(isLocked);
		}
	}
}
