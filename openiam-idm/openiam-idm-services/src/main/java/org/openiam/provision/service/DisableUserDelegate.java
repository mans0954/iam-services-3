package org.openiam.provision.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class to handle the Disable User operation
 * @author suneet shah
 *
 */
@Component("disableUser")
public class DisableUserDelegate {
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ManagedSystemWebService managedSysService;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected ProvisionConnectorWebService provisionConnectorWebService;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;
    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;

    protected static final Log log = LogFactory
            .getLog(DisableUserDelegate.class);

    protected String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if( managedSys.getPswd()!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }
    public Response disableUser(String userId, boolean operation,
            String requestorId, MuleContext muleContext) {
        log.debug("----disableUser called.------");
        log.debug("operation code=" + operation);

        Response response = new Response(ResponseStatus.SUCCESS);

        String requestId = "R" + UUIDGen.getUUID();
        String strOperation = null;

        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        UserEntity usr = this.userMgr.getUser(userId);

        if (usr == null) {
        	/*
            auditHelper.addLog((operation) ? "DISABLE" : "ENABLE",
                    sysConfiguration.getDefaultSecurityDomain(), null,
                    "IDM SERVICE", requestorId, "IDM", "USER", userId, null,
                    "FAILURE", null, null, null, requestId, null, null, null);
			*/
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        // disable the user in OpenIAM
        if (operation) {
            usr.setSecondaryStatus(UserStatusEnum.DISABLED);
            strOperation = "DISABLE";
        } else {
            // enable an account that was previously disabled.
            usr.setSecondaryStatus(null);
            strOperation = "ENABLE";
        }
        userMgr.updateUserWithDependent(usr, false);

        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);
        LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

        if (lRequestor != null && lTargetUser != null) {
        	/*
            auditHelper.addLog(strOperation, lRequestor.getDomainId(),
                    lRequestor.getLogin(), "IDM SERVICE", requestorId,
                    "IDM", "USER", usr.getUserId(), null, "SUCCESS", null,
                    null, null, requestId, null, null, null, null, lTargetUser
                            .getLogin(), lTargetUser.getDomainId());
			*/
        } else {
            if (log.isDebugEnabled()) {
                log.debug(String
                        .format("Unable to log disable operation.  Requestor: %s, Target: %s",
                                lRequestor, lTargetUser));
            }

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
                .getLoginByUser(usr.getUserId());
        if (principalList != null) {
            log.debug("PrincipalList size =" + principalList.size());
            for (LoginEntity lg : principalList) {
                // get the managed system for the identity - ignore the managed
                // system id that is linked to openiam's repository
                log.debug("-diabling managed system=" + lg.getLogin()
                        + " - " + lg.getManagedSysId());

                if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(), sysConfiguration.getDefaultManagedSysId())) {
                    String managedSysId = lg.getManagedSysId();
                    // update the target system
                    ManagedSysDto mSys = managedSysService
                            .getManagedSys(managedSysId);

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


                        connectorAdapter.suspendRequest(mSys, suspendReq,
                                muleContext);

                        lg.setStatus(LoginStatusEnum.INACTIVE);
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
                        String passwordDecoded = mSys.getPswd();
                        try {
                            passwordDecoded = getDecryptedPassword(mSys);
                        } catch (ConnectorDataException e) {
                            e.printStackTrace();
                        }
                        resumeReq.setHostLoginPassword(passwordDecoded);
                        resumeReq.setHostUrl(mSys.getHostUrl());

                        connectorAdapter.resumeRequest(mSys,
                                resumeReq, MuleContextProvider.getCtx());
                    }

                    String domainId = null;
                    String loginId = null;
                    if (lRequestor != null) {
                        domainId = lRequestor.getDomainId();
                        loginId = lRequestor.getLogin();
                    }
                    /*
                    auditHelper.addLog(strOperation + " IDENTITY", domainId,
                            loginId, "IDM SERVICE", requestorId, "IDM", "USER",
                            null, null, "SUCCESS", requestId, null, null,
                            requestId, null, null, null, null, lg.getLogin(), lg.getDomainId());
					*/
                } else {
                    lg.setAuthFailCount(0);
                    lg.setIsLocked(0);
                    lg.setPasswordChangeCount(0);
                    loginManager.updateLogin(lg);
                }
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }
}
