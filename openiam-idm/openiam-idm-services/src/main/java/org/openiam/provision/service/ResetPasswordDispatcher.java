package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.thread.Sweepable;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.*;
import javax.jms.Queue;

import java.util.*;

/**
 * Created by anton on 28.12.15.
 */
@Component("resetPasswordDispatcher")
public class ResetPasswordDispatcher extends AbstractProvisioningService implements Sweepable {

    @Autowired
    private DeprovisionSelectedResourceHelper deprovisionSelectedResource;

    @Autowired
    private ProvisionSelectedResourceHelper provisionSelectedResourceHelper;

    @Autowired
    private BuildUserPolicyMapHelper buildPolicyMapHelper;

    @Value("${org.openiam.debug.hidden.attributes}")
    private String hiddenAttributes;

    @Value("${org.openiam.send.user.activation.link}")
    private Boolean sendActivationLink;

    @Value("${org.openiam.send.admin.reset.password.link}")
    private Boolean sendAdminResetPasswordLink;


    @Autowired
    private JmsTemplate jmsTemplate;


    @Autowired
    @Qualifier(value = "resetPassQueue")
    private Queue queue;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    private final Object mutex = new Object();

    @Override
    @Scheduled(fixedDelay = 1000)
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                synchronized (mutex) {

                    final StopWatch sw = new StopWatch();
                    sw.start();
                    try {
                        log.info("Starting SourceAdapterRequest sweeper thread");

                        Enumeration e = browser.getEnumeration();

                        while (e.hasMoreElements()) {
                            final PasswordSync passwordSync = (PasswordSync) ((ObjectMessage) jmsTemplate.receive(queue)).getObject();

                            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                                @Override
                                public Boolean doInTransaction(TransactionStatus status) {
                                    if (passwordSync.isResetPassword()) {
                                        resetPassword(passwordSync, null);
                                    } else {
                                        setPassword(passwordSync);
                                    }
                                    try {
                                        // to give other threads chance to be executed
                                        Thread.sleep(100);
                                    } catch (InterruptedException e1) {
                                        log.warn(e1.getMessage());
                                    }

                                    return true;
                                }
                            });

                            e.nextElement();
                        }

                    } finally {
                        log.info(String.format("Done with metadataElement sweeper thread.  Took %s ms", sw.getTime()));
                    }
                    return null;
                }
            }
        });
    }

    public PasswordResponse resetPassword(PasswordSync passwordSync, IdmAuditLog auditLog) {
        log.debug("----resetPassword called.------");
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(passwordSync.getRequestorId());
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(this.sysConfiguration.getDefaultManagedSysId(), loginEntityList);
        idmAuditLog.setRequestorPrincipal(primaryIdentity.getLogin());
        idmAuditLog.setRequestorUserId(passwordSync.getRequestorId());
        idmAuditLog.setAction(AuditAction.USER_RESETPASSWORD.value());

        if (auditLog != null) {
            auditLog.addChild(idmAuditLog);
        }
        boolean allResetOK = true;
        final PasswordResponse response = new PasswordResponse(ResponseStatus.SUCCESS);
        try {
            if (passwordSync.getUserActivateFlag()) {

                List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());
                LoginEntity identity = null;
                if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                    identity = UserUtils.getUserManagedSysIdentityEntity(passwordSync.getManagedSystemId(), identities);

                } else {
                    identity = loginManager.getPrimaryIdentity(passwordSync.getUserId());
                }

                if (identity != null) {
                    idmAuditLog.setTargetUser(identity.getUserId(), identity.getLogin());

                } else {
                    log.debug(ResponseCode.PRINCIPAL_NOT_FOUND); //SIA 2015-08-01
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                    return response;
                }

                if (this.sysConfiguration.getDefaultManagedSysId().equals(passwordSync.getManagedSystemId())) {
                    User u = userMgr.getUserDto(passwordSync.getUserId());
                    if (u == null) {
                        allResetOK = false;
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(ResponseCode.USER_NOT_FOUND);
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.USER_NOT_FOUND);
                    }
                    //List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());
                    LoginEntity activetionPrimaryLogin = UserUtils.getUserManagedSysIdentityEntity(this.sysConfiguration.getDefaultManagedSysId(), identities);
                    if (activetionPrimaryLogin == null) {
                        allResetOK = false;
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                    }
                    Login activationLogin = new Login();
                    activationLogin.setLogin(activetionPrimaryLogin.getLogin());
                    activationLogin.setManagedSysId(activetionPrimaryLogin.getManagedSysId());
                    sendResetActivationLink(u, activationLogin);

                }

            } else {
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                if (callPreProcessor("RESET_PASSWORD", null, bindingMap, passwordSync) != ProvisioningConstants.SUCCESS) {
                    response.fail();
                    response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                    auditLog.fail();
                    auditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                    return response;
                }
                final String requestId = "R" + UUIDGen.getUUID();

                // get the user object associated with this principal
                List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());

                //idmAuditLog.setUserId(passwordSync.getUserId());
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
                    log.debug(ResponseCode.FAIL_ENCRYPTION); //SIA 2015-08-01
                    log.error(e.getStackTrace()); //SIA 2015-08-01
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

                        final boolean retval = loginManager.resetPassword(lg.getLogin(), lg.getManagedSysId(), encPassword, passwordSync.getUserActivateFlag());

                        if (retval) {
                            log.debug(String.format("- Password changed for principal: %s, user: %s, managed sys: %s -",
                                    identity.getLogin(), identity.getUserId(), identity.getManagedSysId()));
                            idmAuditLog.addCustomRecord("Password changed success for principal", "ManagedSysId='" + identity.getManagedSysId() + "'");
                            idmAuditLog.succeed();

                        } else {
                            idmAuditLog.fail();
                            idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                            response.setStatus(ResponseStatus.FAILURE);
                            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                            return response;
                        }
                        final IdmAuditLog childAuditLog = new IdmAuditLog();
                        if (!lg.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {
                            childAuditLog.setRequestorPrincipal(primaryIdentity.getLogin());
                            childAuditLog.setRequestorUserId(passwordSync.getRequestorId());
                            childAuditLog.setAction(AuditAction.PROVISIONING_RESETPASSWORD.value());
                            childAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());

                            log.debug("Sync allowed for managed sys = " + managedSysId);

                            bindingMap.put(TARGET_SYSTEM_IDENTITY, lg.getLogin());
                            bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSysId);
                            bindingMap.put(TARGET_SYS_RES, res);
                            bindingMap.put("PASSWORD_SYNC", passwordSync);

                            // Pre processor script
                            final String preProcessScript = getResourceProperty(res, "PRE_PROCESS");
                            if (preProcessScript != null && !preProcessScript.isEmpty()) {
                                final PreProcessor ppScript = createPreProcessScript(preProcessScript);
                                if (ppScript != null) {
                                    if (executePreProcess(ppScript, bindingMap, null, passwordSync, null, "RESET_PASSWORD") == ProvisioningConstants.FAIL) {
                                        continue;
                                    }
                                }
                            }

                            ManagedSystemObjectMatchEntity matchObj = null;
                            final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                                    .managedSysObjectParam(managedSysId, "USER");

                            if (CollectionUtils.isNotEmpty(matchList)) {
                                matchObj = matchList.get(0);
                            }

                            log.info("============== Connector Reset Password call: " + new Date());
                            Login login = loginDozerConverter.convertToDTO(lg, false);
                            ManagedSysDto managedSysDto = managedSysDozerConverter.convertToDTO(mSys, false);
                            ResponseType resp = resetPassword(requestId,
                                    login, password, managedSysDto,
                                    objectMatchDozerConverter.convertToDTO(matchObj, false),
                                    buildPolicyMapHelper.buildMngSysAttributes(login, "RESET_PASSWORD"), "RESET_PASSWORD");
                            log.info("============== Connector Reset Password get : " + new Date());
                            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                                if (enableOnPassReset(res)) {
                                    // reset flags that go with this identity
                                    lg.setAuthFailCount(0);
                                    lg.setIsLocked(0);
                                    lg.setPasswordChangeCount(0);
                                    lg.setStatus(LoginStatusEnum.ACTIVE);

                                    resp = suspend(requestId, login, managedSysDto, buildPolicyMapHelper.buildMngSysAttributes(login, "RESUME"), false);

                                    if (StatusCodeType.SUCCESS.equals(resp.getStatus())) {
                                        lg.setProvStatus(ProvLoginStatusEnum.ENABLED);

                                        childAuditLog.succeed();
                                        childAuditLog.setAuditDescription("Enabling account after password reset for resource: " + res.getName() + " for user: " + lg.getLogin());
                                        idmAuditLog.addChild(childAuditLog);

                                    } else {
                                        lg.setProvStatus(ProvLoginStatusEnum.FAIL_ENABLE);

                                        allResetOK = false;
                                        String reason = "";
                                        if (resp != null) {
                                            if (StringUtils.isNotBlank(resp.getErrorMsgAsStr())) {
                                                reason = resp.getErrorMsgAsStr();
                                            } else if (resp.getError() != null) {
                                                reason = resp.getError().value();
                                            }
                                            if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                                                // if single target system - let's return error reason
                                                response.setErrorText(reason);
                                            }
                                        }

                                        childAuditLog.fail();
                                        childAuditLog.setFailureReason(String.format("Enabling account after password reset for resource %s user %s failed: %s", mSys.getName(), lg.getLogin(), reason));
                                        idmAuditLog.addChild(childAuditLog);

                                    }
                                    loginManager.updateLogin(lg);

                                } else {
                                    childAuditLog.succeed();
                                    childAuditLog.setAuditDescription("Reset password for resource: " + res.getName() + " for user: " + lg.getLogin());
                                    idmAuditLog.addChild(childAuditLog);
                                }

                            } else {
                                allResetOK = false;
                                String reason = "";
                                if (resp != null) {
                                    if (StringUtils.isNotBlank(resp.getErrorMsgAsStr())) {
                                        reason = resp.getErrorMsgAsStr();
                                    } else if (resp.getError() != null) {
                                        reason = resp.getError().value();
                                    }
                                    if (StringUtils.isNotBlank(passwordSync.getManagedSystemId())) {
                                        // if single target system - let's return error reason
                                        response.setErrorText(reason);
                                    }
                                }

                                childAuditLog.fail();
                                childAuditLog.setFailureReason(String.format("Reset password for resource %s user %s failed: %s", mSys.getName(), lg.getLogin(), reason));
                                idmAuditLog.addChild(childAuditLog);
                            }
                            // Post processor script
                            final String postProcessScript = getResourceProperty(res, "POST_PROCESS");
                            if (postProcessScript != null && !postProcessScript.isEmpty()) {
                                final PostProcessor ppScript = createPostProcessScript(postProcessScript);
                                if (ppScript != null) {
                                    executePostProcess(ppScript, bindingMap, null, passwordSync, null, "RESET_PASSWORD", resp.getStatus() == StatusCodeType.SUCCESS);
                                }
                            }
                        } else {
                            // SIA - 20150702: Audit sync not allowed as child
                            log.debug("Sync not allowed for sys=" + managedSysId);
                            childAuditLog.succeed();
                            childAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Sync not allowed for resource: " + res.getName());
                            idmAuditLog.addChild(childAuditLog);
                        }
                    }
                }

                if (passwordSync.getSendPasswordToUser() && sysConfiguration.getDefaultManagedSysId().equals(identity.getManagedSysId())) {
                    sendResetPasswordToUser(identity, password);
                }

                // Provision post processor script
                if (callPostProcessor("RESET_PASSWORD", null, bindingMap, passwordSync) != ProvisioningConstants.SUCCESS) {
                    response.fail();
                    response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                    auditLog.fail();
                    auditLog.setFailureReason(ResponseCode.FAIL_POSTPROCESSOR);
                    return response;
                }
            }
        } finally {
            if (auditLog == null) {
                if (!allResetOK) {
                    idmAuditLog.fail();
                }
                auditLogService.save(idmAuditLog);
            }
        }
        if (!allResetOK) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;

    }

    public PasswordValidationResponse setPassword(PasswordSync passwordSync) {
        log.debug("----setPassword called.------");
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(passwordSync.getRequestorId());
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(this.sysConfiguration.getDefaultManagedSysId(), loginEntityList);
        idmAuditLog.setRequestorPrincipal(primaryIdentity.getLogin());
        idmAuditLog.setRequestorUserId(passwordSync.getRequestorId());
        idmAuditLog.setAction(AuditAction.CHANGE_PASSWORD.value());
        idmAuditLog.setBaseObject(passwordSync);
        idmAuditLog.setUserId(passwordSync.getUserId());
//        final IdmAuditLog auditLog = new IdmAuditLog();
//        auditLog.setBaseObject(passwordSync);
//        auditLog.setAction(AuditAction.CHANGE_PASSWORD.value());

        boolean allSetOK = true;
        PasswordValidationResponse response = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        final Map<String, Object> bindingMap = new HashMap<String, Object>();

        try {
            if (callPreProcessor("SET_PASSWORD", null, bindingMap, passwordSync) != ProvisioningConstants.SUCCESS) {
                response.fail();
                response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                return response;
            }

            final String requestId = "R" + UUIDGen.getUUID();

            // get the user identities
            List<LoginEntity> identities = loginManager.getLoginByUser(passwordSync.getUserId());

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
                log.debug(ResponseCode.PRINCIPAL_NOT_FOUND); //SIA 2015-08-01
                log.error("Identity not found. " + ResponseCode.PRINCIPAL_NOT_FOUND);
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }

            // validate the password against password policy
            final Password pswd = new Password();
            pswd.setManagedSysId(identity.getManagedSysId());
            pswd.setPrincipal(identity.getLogin());
            pswd.setPassword(passwordSync.getPassword());

            if (!passwordSync.getResyncMode() && !identity.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {
                try {
                    response = passwordManager.isPasswordValid(pswd);
                    if (response.isFailure()) {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason("Invalid Password");
                        return response;
                    }
                } catch (ObjectNotFoundException oe) {
                    log.debug("Object not found", oe); //SIA 2015-08-01
                    log.error(oe.getStackTrace()); //SIA 2015-08-01
                    idmAuditLog.setException(oe);
                    idmAuditLog.setFailureReason(oe.getMessage());
                    idmAuditLog.fail();
                    return response;
                }
            } else {
                log.warn("Password Validation Skipped. In Resync Mode system pushes the same passwords!");
                idmAuditLog.addAttribute(AuditAttributeName.WARNING, "Password Validation Skipped. " +
                        "In Resync Mode system pushes the same passwords!");
            }

            String encPassword = null;
            try {
                encPassword = loginManager.encryptPassword(identity.getUserId(), passwordSync.getPassword());
            } catch (Exception e) {
                log.error("Exception:" + e.getMessage());
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

            for (final LoginEntity lg : principalList) {

                final String managedSysId = lg.getManagedSysId();
                final ManagedSysEntity mSys = managedSystemService.getManagedSysById(managedSysId);

                if (mSys != null && !managedSysId.equals(sysConfiguration.getDefaultManagedSysId())) {
                    final ResourceEntity res = resourceService.findResourceById(mSys.getResourceId());
                    log.debug(" - Managed System Id = " + managedSysId);
                    log.debug(" - Resource Id = " + res.getId());

                    final boolean retval = loginManager.setPassword(lg.getLogin(), managedSysId,
                            encPassword, passwordSync.isPreventChangeCountIncrement());

                    if (retval) {
                        log.debug(String.format("- Password changed for principal: %s, user: %s, managed sys: %s -",
                                identity.getLogin(), identity.getUserId(), identity.getManagedSysId()));
                        idmAuditLog.addCustomRecord("Password changed success for principal", "ManagedSysId='" + identity.getManagedSysId() + "'");
//                        idmAuditLog.succeed();

                        /*
                         * came with merge from v2.3 //check if password should be sent
                         * to the user. if (passwordSync.isSendPasswordToUser()) { //
                         * sendPasswordToUser(usr, password); }
                         */
                        if (passwordSync.getSendPasswordToUser()) {
                            sendResetPasswordToUser(identity, passwordSync.getPassword());
                        }

                    } else {
                        idmAuditLog.fail();
                        log.debug(ResponseCode.PRINCIPAL_NOT_FOUND); //SIA 2015-08-01
                        idmAuditLog.setFailureReason(ResponseCode.PRINCIPAL_NOT_FOUND);
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                        return response;
                    }

                    if (!managedSysId.equals(sysConfiguration.getDefaultManagedSysId())) {

                        final IdmAuditLog childAuditLog = new IdmAuditLog();
                        childAuditLog.setRequestorPrincipal(primaryIdentity.getLogin());
                        childAuditLog.setRequestorUserId(passwordSync.getRequestorId());
                        childAuditLog.setAction(AuditAction.PROVISIONING_SETPASSWORD.value());
                        childAuditLog.setTargetManagedSys(mSys.getId(), mSys.getName());

                        if (includedInPasswordSync(res)) { // check the sync flag


                            log.debug("Sync allowed for managed sys = " + managedSysId);

                            // pre-process

                            bindingMap.put(IDENTITY, lg);
                            bindingMap.put(TARGET_SYS_RES, res);
                            bindingMap.put("PASSWORD_SYNC", passwordSync);


                            final String preProcessScript = getResourceProperty(res, "PRE_PROCESS");
                            if (preProcessScript != null && !preProcessScript.isEmpty()) {
                                final PreProcessor ppScript = createPreProcessScript(preProcessScript);
                                if (ppScript != null) {
                                    if (executePreProcess(ppScript, bindingMap, null, passwordSync, null, "SET_PASSWORD") == ProvisioningConstants.FAIL) {
                                        continue;
                                    }
                                }
                            }

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
                            //TODO Add change status if needed.
                            ResponseType resp = resetPassword(requestId,
                                    login,
                                    passwordSync.getPassword(),
                                    managedSysDozerConverter.convertToDTO(mSys, false),
                                    objectMatchDozerConverter.convertToDTO(matchObj, false),
                                    buildPolicyMapHelper.buildMngSysAttributes(login, "SET_PASSWORD"), "SET_PASSWORD");

                            boolean connectorSuccess = false;
                            log.info("============== Connector Set Password get : " + new Date());
                            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                                connectorSuccess = true;
                                childAuditLog.succeed();
                                childAuditLog.setAuditDescription(
                                        "Set password for resource: " + res.getName() + " for user: "
                                                + lg.getLogin());
                                idmAuditLog.addChild(childAuditLog);
                            } else {
                                allSetOK = false;
                                childAuditLog.fail();
                                String reason = "";
                                if (resp != null) {
                                    if (resp.getError() != null) {
                                        reason = resp.getError().value();
                                    } else if (StringUtils.isNotBlank(resp.getErrorMsgAsStr())) {
                                        reason = resp.getErrorMsgAsStr();
                                    }
                                }

                                log.error(String.format("Set password for resource %s user %s failed: %s", mSys.getName(), lg.getLogin(), reason));
                                childAuditLog.fail();
                                childAuditLog.setFailureReason(String.format("Set password for resource %s user %s failed: %s",
                                        mSys.getName(), lg.getLogin(), reason));
                                idmAuditLog.addChild(childAuditLog); // SIA - 20150702
                            }

                            // post-process
                            if (res != null) {
                                final String postProcessScript = getResourceProperty(res, "POST_PROCESS");
                                if (postProcessScript != null && !postProcessScript.isEmpty()) {
                                    final PostProcessor ppScript = createPostProcessScript(postProcessScript);
                                    if (ppScript != null) {
                                        executePostProcess(ppScript, bindingMap, null, passwordSync, null, "SET_PASSWORD",
                                                connectorSuccess);
                                    }
                                }
                            }
                        } else {
                            log.debug("Sync not allowed for sys=" + managedSysId);
                            childAuditLog.succeed();
                            childAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Sync not allowed for resource: " + res.getName());
                            idmAuditLog.addChild(childAuditLog);
                        }
                    }
                }
            }
            if (callPostProcessor("SET_PASSWORD", null, bindingMap, passwordSync) != ProvisioningConstants.SUCCESS) {
                response.fail();
                response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.FAIL_POSTPROCESSOR);
                return response;
            }
            response.setStatus(ResponseStatus.SUCCESS);
            return response;

        } finally {
            if (!allSetOK) {
                idmAuditLog.fail();
            }
            auditLogService.save(idmAuditLog); //SIA 2015-08-01
        }
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

    private boolean enableOnPassReset(final ResourceEntity resource) {
        boolean retVal = true;
        if (resource != null) {
            retVal = !StringUtils.equalsIgnoreCase(getResourceProperty(resource, "ENABLE_ON_PASSWORD_RESET"), "N");
        }
        return retVal;
    }

    private boolean includedInPasswordSync(final ResourceEntity resource) {
        boolean retVal = true;
        if (resource != null) {
            retVal = !StringUtils.equalsIgnoreCase(getResourceProperty(resource, "INCLUDE_IN_PASSWORD_SYNC"), "N");
        }
        return retVal;
    }

}
