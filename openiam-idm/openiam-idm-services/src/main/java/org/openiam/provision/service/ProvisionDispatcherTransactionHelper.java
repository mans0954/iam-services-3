package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
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
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Vitaly on 4/27/2015.
 */
@Component
public class ProvisionDispatcherTransactionHelper {

    private static final Log log = LogFactory.getLog(ProvisionDispatcherTransactionHelper.class);

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected LoginDataService loginManager;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ProvisionService provisionService;

    @Autowired
    private ResourceDozerConverter resourceDozerConverter;

    @Autowired
    private ManagedSystemWebService managedSystemWebService;

    @Autowired
    private LoginDozerConverter loginDozerConverter;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    protected ProvisionConnectorService connectorService;

    @Autowired
    private ProvisionSelectedResourceHelper provisionSelectedResourceHelper;

    @Autowired
    private BuildUserPolicyMapHelper buildPolicyMapHelper;

    @Value("${org.openiam.debug.hidden.attributes}")
    protected String hiddenAttributes;

    private String[] hiddenAttrs = null;

    @Autowired
    @Qualifier("userManager")
    private UserDataService userDataService;

    @Transactional
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
                        loginChanges.setChallengeResponseFailCount(0);
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
                            for (final String error : response.getErrorMessage()) {
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
                String managedSysId = mSys.getId();
                idmAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());

                Login targetSysLogin = data.getIdentity();

                SuspendResumeRequest suspendReq = new SuspendResumeRequest();
                suspendReq.setObjectIdentity(targetSysLogin.getLogin());
                suspendReq.setTargetID(managedSysId);
                suspendReq.setRequestID(requestId);
                suspendReq.setScriptHandler(mSys.getSuspendHandler());
                suspendReq.setHostLoginId(mSys.getUserId());

                ExtensibleUser extUser = provisionSelectedResourceHelper.buildFromRules(managedSysId, data.getBindingMap());
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
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DISABLE IDENTITY = FAILURE, details:" + resp.getErrorMsgAsStr());
                        loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DISABLE);
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DISABLE IDENTITY = FAILURE, details:" + th.getMessage());
                    loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_DISABLE);
                } finally {
                    if (!"FAILURE".equals(idmAuditLog.getResult())) {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "DISABLE IDENTITY = SUCCESS");
                    }
                }

            } else if (data.getOperation() == ProvOperationEnum.ENABLE) {

                String requestId = data.getRequestId();
                ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
                Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
                ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
                String managedSysId = mSys.getId();
                idmAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());

                Login targetSysLogin = data.getIdentity();

                SuspendResumeRequest suspendReq = new SuspendResumeRequest();
                suspendReq.setObjectIdentity(targetSysLogin.getLogin());
                suspendReq.setTargetID(managedSysId);
                suspendReq.setRequestID(requestId);
                suspendReq.setScriptHandler(mSys.getSuspendHandler());
                suspendReq.setHostLoginId(mSys.getUserId());

                ExtensibleUser extUser = provisionSelectedResourceHelper.buildFromRules(managedSysId, data.getBindingMap());
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
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ENABLE IDENTITY = FAILURE, details:" + resp.getErrorMsgAsStr());
                        loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);
                    }
                } catch (Throwable th) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(th.getMessage());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ENABLE IDENTITY = FAILURE, details:" + th.getMessage());
                    loginChanges.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);
                } finally {
                    if (!"FAILURE".equals(idmAuditLog.getResult())) {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ENABLE IDENTITY = SUCCESS");
                    }
                }
            }

            saveChanges(identity, loginChanges);

        } finally {
            IdmAuditLog parentAuditLog = StringUtils.isNotEmpty(data.getParentAuditLogId()) ? auditLogService.findById(data.getParentAuditLogId()) : null;
            if (parentAuditLog != null) {
                parentAuditLog.addChild(idmAuditLog);
                idmAuditLog.addParent(parentAuditLog);
                auditLogService.save(parentAuditLog);
            } else {
                auditLogService.save(idmAuditLog);
            }
        }
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


    private ObjectResponse deprovision(ProvisionDataContainer data, IdmAuditLog idmAuditLog) {

        String requestId = data.getRequestId();
        Login targetSysLogin = data.getIdentity();
        ResourceEntity resEntity = resourceService.findResourceById(data.getResourceId());
        Resource res = resourceDozerConverter.convertToDTO(resEntity, true);
        ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
        idmAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());
        ExtensibleUser extensibleUser = buildPolicyMapHelper.buildMngSysAttributes(targetSysLogin, data.getOperation().name());

        if (mSys.getConnectorId() == null) {
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
        request.setExtensibleObject(extensibleUser);

        return connectorAdapter.deleteRequest(mSys, request, MuleContextProvider.getCtx());

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
            boolean targetSystemUserExists = getCurrentObjectAtTargetSystem(requestId, targetSysLogin, provisionSelectedResourceHelper.buildEmptyAttributesExtensibleUser(managedSysId), mSys,
                    matchObj, currentValueMap, res);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_USER_EXISTS, targetSystemUserExists);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, currentValueMap);
            Map<String, UserAttribute> attributes = userDataService.getUserAttributesDto(targetSysProvUser.getId());
            bindingMap.put(AbstractProvisioningService.USER_ATTRIBUTES, attributes);
            ExtensibleObject extUser = provisionSelectedResourceHelper.buildFromRules(managedSysId, bindingMap);
            try {
                idmAuditLog.addCustomRecord("ATTRIBUTES", extUser.getAttributesAsJSON(hiddenAttrs));
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
                    int executePreProcessResult = AbstractProvisioningService.executePreProcess(ppScript, bindingMap, targetSysProvUser, null, null,
                            targetSystemUserExists ? "MODIFY" : "ADD");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "executePreProcessResult: "
                            + (targetSystemUserExists ? "[MODIFY]" : "[ADD] = ") + executePreProcessResult);
                    if (executePreProcessResult == ProvisioningConstants.FAIL) {
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                        return response;
                    }
                }
            }

            if (!targetSystemUserExists) {

                // updates the attributes with the correct operation codes
                extUser = ProvisionDispatcher.updateAttributeList(extUser, null);

                ObjectResponse resp = provisionService.requestAddModify((ExtensibleUser) extUser, targetSysLogin, true, requestId,
                        idmAuditLog);
                connectorSuccess = resp.getStatus() != StatusCodeType.FAILURE;

            } else { // if user exists in target system

                // updates the attributes with the correct operation codes
                extUser = ProvisionDispatcher.updateAttributeList(extUser, currentValueMap);

                if (targetSysLogin.getOperation() == AttributeOperationEnum.REPLACE
                        && targetSysLogin.getOrigPrincipalName() != null) {
                    extUser.getAttributes().add(
                            new ExtensibleAttribute("ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(),
                                    AttributeOperationEnum.REPLACE.getValue(), "String"));
                }
                ObjectResponse resp = provisionService.requestAddModify((ExtensibleUser) extUser, targetSysLogin, false, requestId,
                        idmAuditLog);
                connectorSuccess = resp.getStatus() != StatusCodeType.FAILURE;
            }

            // post processing
            ResourceProp postProcessProp = res.getResourceProperty("POST_PROCESS");
            String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
            if (StringUtils.isNotBlank(postProcessScript)) {
                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                if (ppScript != null) {
                    int executePostProcessResult = AbstractProvisioningService.executePostProcess(ppScript, bindingMap, targetSysProvUser, null, null,
                            targetSystemUserExists ? "MODIFY" : "ADD", connectorSuccess);
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "executePostProcessResult "
                            + (targetSystemUserExists ? "[MODIFY]" : "[ADD] =") + executePostProcessResult);
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

    private boolean getCurrentObjectAtTargetSystem(String requestId, Login mLg, ExtensibleUser extUser,
                                                   ManagedSysDto mSys, ManagedSystemObjectMatch matchObj, Map<String, ExtensibleAttribute> curValueMap, Resource res) {

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
//PRE processor
        Map<String, Object> bindingMap = new HashMap<>();
        ResourceProp preProcessProp = res.getResourceProperty("PRE_PROCESS");
        String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(preProcessScript)) {
            PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
            if (ppScript != null) {
                int executePreProcessResult = AbstractProvisioningService.executePreProcess(ppScript, bindingMap, null, null, reqType,
                        "LOOKUP");
                if (executePreProcessResult == ProvisioningConstants.FAIL) {
                    return false;
                }
            }
        }

        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
//POST processor
        ResourceProp postProcessProp = res.getResourceProperty("POST_PROCESS");
        String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(postProcessScript)) {
            PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
            if (ppScript != null) {
                AbstractProvisioningService.executePostProcess(ppScript, bindingMap, null, null, lookupSearchResponse,
                        "LOOKUP", lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS);
            }
        }

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

    @PostConstruct
    private void initDispatcher() {
        if (StringUtils.isNotBlank(hiddenAttributes)) {
            hiddenAttrs = hiddenAttributes.trim().toLowerCase().split("\\s*,\\s*");
        }
    }

}
