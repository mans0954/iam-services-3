package org.openiam.idm.srvc.auth.spi;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthCredentialsValidator;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.service.AuthenticationUtils;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Vitaly on 2/9/2015.
 */
@Component("mngSysLoginModule")
public class MngSysLoginModule extends AbstractLoginModule {

    @Autowired
    protected ConnectorAdapter connectorAdapter;

    private static final Log log = LogFactory.getLog(MngSysLoginModule.class);

    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {

        Date curDate = new Date(System.currentTimeMillis());
        Subject subj = new Subject();

        PasswordCredential cred = (PasswordCredential) authContext.getCredential();
        String principal = cred.getPrincipal();
        String password = cred.getPassword();

        String authPolicyId = (String)authContext.getAuthParam().get(AuthenticationRequest.AUTH_POLICY_ID);
        if(StringUtils.isEmpty(authPolicyId)) {
            authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
        }
        log.debug("Authentication policyid=" + authPolicyId);
        Policy authPolicy = policyDataService.getPolicy(authPolicyId);
        if (authPolicy == null) {
            log.error("No auth policy found");
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }

        PolicyAttribute policyAttribute = authPolicy.getAttribute("MANAGED_SYS_ID");
        if (policyAttribute == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        ManagedSysDto mSys = managedSystemWebService.getManagedSys(policyAttribute.getValue1());
        if (mSys == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        String managedSysId = mSys.getId();

        // checking if Login exists in OpenIAM
        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        if (lg == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // checking if User is valid
        UserEntity user = userManager.getUser(lg.getUserId());
        if (user == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // Find user in target system
        LookupUserResponse resp = provisionService.getTargetSystemUser(principal, managedSysId, new ArrayList<ExtensibleAttribute>());
        log.debug("Lookup for user identity =" + principal + " in target system = " + mSys.getName() + ". Result = " + resp.getStatus() + ", " + resp.getErrorCode());

        if (resp.isFailure()) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        principal = lg.getLogin();

        // checking password policy
        Policy passwordPolicy = passwordManager.getPasswordPolicy(principal, lg.getManagedSysId());
        if (passwordPolicy == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }


        AuthenticationException changePassword = null;
        try {
            authenticationUtils.getCredentialsValidator().execute(user, lg, AuthCredentialsValidator.NEW, new HashMap<String, Object>());

        } catch (AuthenticationException ae) {
            // we should validate password before change password
            if (AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET == ae.getErrorCode() ||
                    AuthenticationConstants.RESULT_PASSWORD_EXPIRED == ae.getErrorCode() ||
                    AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP == ae.getErrorCode()) {
                changePassword = ae;

            } else {
                throw ae;
            }
        }

        // checking if provided Password is not empty
        if (StringUtils.isEmpty(password)) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);
        }

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] objArr = managedSystemWebService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
        if (objArr != null && objArr.length > 0) {
            matchObj = objArr[0];
        }

        // checking password is valid at target system
        // Try to login to ManagedSystem with this user
        PasswordRequest passwRequest = new PasswordRequest(password, null, principal);
        passwRequest.setRequestID(UUIDGen.getUUID());
        passwRequest.setTargetID(managedSysId);
        passwRequest.setHostLoginId(mSys.getUserId());
        passwRequest.setHostLoginPassword(mSys.getDecryptPassword());
        passwRequest.setHostUrl(mSys.getHostUrl());
        passwRequest.setBaseDN((matchObj != null) ? matchObj.getBaseDn() : null);
        passwRequest.setOperation("TEST_PASSWORD");
        passwRequest.setScriptHandler(mSys.getPasswordHandler());
        passwRequest.setExtensibleObject(new ExtensibleUser());
        ResponseType responseType = connectorAdapter.validatePassword(mSys, passwRequest, MuleContextProvider.getCtx());

        if (StatusCodeType.FAILURE.equals(responseType.getStatus())) {
            // get the authentication lock out policy
            String attrValue = getPolicyAttribute(authPolicy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

            // if failed auth count is part of the polices, then do the
            // following processing
            if (StringUtils.isNotBlank(attrValue)) {

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
                    loginManager.updateLogin(lg);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);

                    throw new AuthenticationException(AuthenticationConstants.RESULT_LOGIN_LOCKED);

                } else {
                    // update the counter save the record
                    loginManager.updateLogin(lg);

                    throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }

            } else {
                log.error("No auth fail password policy value found");
                throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);

            }
        }

        // now we can change password
        if (changePassword != null) {
            throw changePassword;
        }

        Integer daysToExp = getDaysToPasswordExpiration(lg, curDate, passwordPolicy);
        if (daysToExp != null) {
            subj.setDaysToPwdExp(0);
            if (daysToExp > -1) {
                subj.setDaysToPwdExp(daysToExp);
            }
        }

        log.debug("-login successful");
        // good login - reset the counters

        lg.setLastAuthAttempt(curDate);

        // move the current login to prev login fields
        lg.setPrevLogin(lg.getLastLogin());
        lg.setPrevLoginIP(lg.getLastLoginIP());

        // assign values to the current login
        lg.setLastLogin(curDate);
        lg.setLastLoginIP(authContext.getClientIP());

        lg.setAuthFailCount(0);
        lg.setChallengeResponseFailCount(0);
        lg.setFirstTimeLogin(0);
        log.debug("-Good Authn: Login object updated.");
        loginManager.updateLogin(lg);

        // check the user status
        if (UserStatusEnum.PENDING_INITIAL_LOGIN.equals(user.getStatus()) ||
                // after the start date
                UserStatusEnum.PENDING_START_DATE.equals(user.getStatus())) {
            user.setStatus(UserStatusEnum.ACTIVE);
            userManager.updateUser(user);
        }

        // Successful login
        log.debug("-Populating subject after authentication");

        String tokenType = getPolicyAttribute(authPolicy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(authPolicy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(authPolicy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

        subj.setUserId(lg.getUserId());
        subj.setPrincipal(principal);
        subj.setSsoToken(token(lg.getUserId(), tokenParam));
        setResultCode(lg, subj, curDate, passwordPolicy);

        return subj;
    }

}