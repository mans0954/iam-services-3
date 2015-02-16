package org.openiam.idm.srvc.auth.spi;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionSelectedResourceHelper;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vitaly on 2/9/2015.
 */
@Component("mngSysLoginModule")
public class MngSysLoginModule extends AbstractLoginModule {

    @Autowired
    private ProvisionSelectedResourceHelper provisionSelectedResourceHelper;

    @Autowired
    protected ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("managedSysService")
    protected ManagedSystemWebService managedSystemWebService;

    private static final Log log = LogFactory.getLog(MngSysLoginModule.class);

    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {
        Subject sub = new Subject();
        log.debug("login() in MngSysLoginModule called");
        String clientIP = authContext.getClientIP();
        String nodeIP = authContext.getNodeIP();

        String authPolicyId = (String)authContext.getAuthParam().get(AuthenticationRequest.AUTH_POLICY_ID);
        if(StringUtils.isEmpty(authPolicyId)) {
            authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
        }
        Policy authPolicy = policyDataService.getPolicy(authPolicyId);
        PolicyAttribute policyAttribute = authPolicy
                .getAttribute("MANAGED_SYS_ID");
        if (policyAttribute == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        ManagedSysDto mSys = managedSystemWebService.getManagedSys(policyAttribute.getValue1());
        if (mSys == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        String managedSysId = mSys.getId();
        // current date
        Date curDate = new Date(System.currentTimeMillis());
        PasswordCredential cred = (PasswordCredential) authContext
                .getCredential();

        String principal = cred.getPrincipal();
        String password = cred.getPassword();

        //lookup by "adminPassword"
        MuleContext muleContext = MuleContextProvider.getCtx();
        LookupRequest<ExtensibleUser> reqType = new LookupRequest<ExtensibleUser>();
        final String requestId = "R" + UUIDGen.getUUID();
        reqType.setRequestID(requestId);
        reqType.setSearchValue(principal);
        reqType.setTargetID(managedSysId);
        reqType.setHostLoginId(mSys.getUserId());
        reqType.setHostLoginPassword(mSys.getDecryptPassword());
        reqType.setHostUrl(mSys.getHostUrl());

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] objArr = managedSystemWebService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);

        if (objArr != null && objArr.length > 0) {
            matchObj = objArr[0];
        }
        if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
            reqType.setBaseDN(matchObj.getSearchBaseDn());
        }
        ExtensibleUser extUser = provisionSelectedResourceHelper.buildEmptyAttributesExtensibleUser(managedSysId);
        reqType.setExtensibleObject(extUser);
        reqType.setScriptHandler(mSys.getLookupHandler());
        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);

       log.debug("Lookup for user identity =" + principal + " in target system = " +mSys.getName() + ". Result = "+lookupSearchResponse.getStatus()+", "+lookupSearchResponse.getErrorMsgAsStr());

        if (lookupSearchResponse.getStatus() == StatusCodeType.FAILURE) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        log.debug("Authentication policyid="
                + authPolicyId);
        // get the authentication lock out policy
        Policy plcy = policyDataService.getPolicy(authPolicyId);
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

        String tokenType = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

        LoginResponse lgResp = loginManager.getLoginByManagedSys(principal, managedSysId);

        if (lgResp.getStatus() == ResponseStatus.FAILURE) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "MATCHING IDENTITY NOT FOUND", domainId, null, principal,
//                    null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        Login lg = lgResp.getPrincipal();
        UserEntity user = this.userManager.getUser(lg.getUserId());

        // try to login to ManagedSystem with this user

        PasswordRequest passwRequest = new PasswordRequest(password, null, principal);
        passwRequest.setRequestID(requestId);
        passwRequest.setTargetID(managedSysId);
        passwRequest.setHostLoginId(mSys.getUserId());
        passwRequest.setHostLoginPassword(mSys.getDecryptPassword());
        passwRequest.setHostUrl(mSys.getHostUrl());
        passwRequest.setBaseDN((matchObj != null) ? matchObj.getBaseDn() : null);
        passwRequest.setOperation("TEST_PASSWORD");
        passwRequest.setScriptHandler(mSys.getPasswordHandler());
        passwRequest.setExtensibleObject(new ExtensibleUser());
        ResponseType responseType = connectorAdapter.validatePassword(mSys, passwRequest, muleContext);

        if (responseType.getStatus() == StatusCodeType.FAILURE) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "RESULT_INVALID_PASSWORD", domainId, null, principal, null,
//                    null, clientIP, nodeIP);
            log.warn("Authentication failed for "
                    + mSys.getName() + " code= " + AuthenticationConstants.RESULT_INVALID_PASSWORD);
            // update the auth fail count
            if (attrValue != null && attrValue.length() > 0) {

                int authFailCount = Integer.parseInt(attrValue);
                // increment the auth fail counter
                int failCount = 0;
                if (lg.getAuthFailCount() != null) {
                    failCount = lg.getAuthFailCount().intValue();
                }
                failCount++;
                lg.setAuthFailCount(failCount);
                lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
                if (failCount >= authFailCount) {
                    // lock the record and save the record.
                    lg.setIsLocked(1);
                    loginManager.saveLogin(lg);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);

//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "ACCOUNT_LOCKED", domainId, null, principal, null,
//                            null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_LOGIN_LOCKED);
                } else {
                    // update the counter save the record
                    loginManager.saveLogin(lg);
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID_PASSWORD", domainId, null, principal,
//                            null, null, clientIP, nodeIP);

                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }
            } else {
                log.debug("No auth fail password policy value found");
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_PASSWORD);

            }

        }


        if (user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID USER STATUS", domainId, null, principal,
//                            null, null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(
                    UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                // invalid status
//                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                        "INVALID USER STATUS", domainId, null, principal, null,
//                        null, clientIP, nodeIP);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
            this.checkSecondaryStatus(user);
        }

        int pswdResult = passwordExpired(lg, curDate);
        if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "PASSWORD_EXPIRED",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
        }
        Integer daysToExp = setDaysToPassworExpiration(lg, curDate, sub, null);
        if (daysToExp != null) {
            sub.setDaysToPwdExp(0);
            if (daysToExp > -1)
                sub.setDaysToPwdExp(daysToExp);
        }

        // update the login and user records to show this authentication
        lg.setLastAuthAttempt(curDate);

        // move the current login to prev login fields
        lg.setPrevLogin(lg.getLastLogin());
        lg.setPrevLoginIP(lg.getLastLoginIP());

        // assign values to the current login
        lg.setLastLogin(curDate);
        lg.setLastLoginIP(clientIP);

        // lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
        // lg.setLastLogin(new Date(System.currentTimeMillis()));

        lg.setAuthFailCount(0);
        lg.setFirstTimeLogin(0);

        log.info("Good Authn: Login object updated.");

        loginManager.saveLogin(lg);

        // check the user status
        if (user.getStatus() != null) {

            if (user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN) ||
                    // after the start date
                    user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {

                user.setStatus(UserStatusEnum.ACTIVE);
                userManager.updateUser(user);
            }
        }

        // Successful login
        sub.setUserId(lg.getUserId());
        sub.setPrincipal(principal);
        sub.setSsoToken(token(lg.getUserId(), tokenParam));
        setResultCode(lg, sub, curDate, null);

        // send message into to audit log

//        log("AUTHENTICATION", "AUTHENTICATION", "SUCCESS", null, domainId,
//                user.getId(), distinguishedName, null, null, clientIP,
//                nodeIP);

        return sub;
    }
}
