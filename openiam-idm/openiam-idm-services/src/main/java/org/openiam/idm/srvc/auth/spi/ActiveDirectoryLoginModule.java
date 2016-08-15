/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.auth.spi;

import com.sun.jndi.ldap.LdapCtx;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthCredentialsValidator;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.naming.ldap.LdapContext;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DefaultLoginModule provides basic password based authentication using the OpenIAM repository.
 *
 * @author suneet
 */
@Scope("prototype")
@Component("activeDirectoryLoginModule")
public class ActiveDirectoryLoginModule extends AbstractLoginModule {

    private static final Log log = LogFactory
            .getLog(ActiveDirectoryLoginModule.class);


    public ActiveDirectoryLoginModule() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.spi.LoginModule#globalLogout(java.lang.String,
     * java.lang.String)
     */
    /*
    public void globalLogout(String securityDomain, String principal) {

    }
    */

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.auth.spi.LoginModule#login(org.openiam.idm.srvc.
     * auth.context.AuthenticationContext)
     */
    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {

        Date curDate = new Date(System.currentTimeMillis());
        Subject subj = new Subject();

        PasswordCredential cred = (PasswordCredential) authContext.getCredential();
        String principal = cred.getPrincipal();
        String password = cred.getPassword();

        String authPolicyId = (String) authContext.getAuthParam().get(AuthenticationRequest.AUTH_POLICY_ID);
        if (StringUtils.isEmpty(authPolicyId)) {
            authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
        }
        if (log.isDebugEnabled()) {
            log.debug("Authentication policyid=" + authPolicyId);
        }
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
        if (lg.getIsLocked() != 0) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_LOGIN_LOCKED);
        }
        // checking if User is valid
        UserEntity user = userManager.getUser(lg.getUserId());
        if (user == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        if (UserStatusEnum.DISABLED.equals(user.getSecondaryStatus())) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_LOGIN_DISABLED);
        }

        if (UserStatusEnum.LEAVE.equals(user.getStatus())) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_USER_STATUS);
        }

        // Find user in target system
        List<ExtensibleAttribute> attrs = new ArrayList<ExtensibleAttribute>();
        attrs.add(new ExtensibleAttribute("distinguishedName", null));
        attrs.add(new ExtensibleAttribute("Enabled", null));
        attrs.add(new ExtensibleAttribute("AccountExpirationDate", null));
        attrs.add(new ExtensibleAttribute("ChangePasswordAtLogon", null));
        attrs.add(new ExtensibleAttribute("msDS-UserPasswordExpiryTimeComputed", null));
        if (log.isDebugEnabled()) {
            log.debug("AD_LOGIN_MODULE. Find in AD. Start");
        }
        LookupUserResponse resp = provisionService.getTargetSystemUser(principal, managedSysId, attrs);
        if (log.isDebugEnabled()) {
            log.debug("AD_LOGIN_MODULE. Lookup for user identity =" + principal + " in target system = " + mSys.getName() + ". Result = " + resp.getStatus() + ", " + resp.getErrorCode());
        }
        if (resp.isFailure()) {
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE throws=" + AuthenticationConstants.RESULT_INVALID_LOGIN);
            }
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        principal = lg.getLogin();

        String distinguishedName = null;
        if (CollectionUtils.isNotEmpty(resp.getAttrList())) {
            distinguishedName = resp.getAttrList().get(0).getValue();

        }
        if (StringUtils.isEmpty(distinguishedName)) {
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE throws=" + AuthenticationConstants.RESULT_INVALID_LOGIN);
            }
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        if (log.isDebugEnabled()) {
            log.debug("AD_LOGIN_MODULE Find in AD distiguihed name=" + distinguishedName);
        }
        // checking password policy
        Policy passwordPolicy = passwordManager.getPasswordPolicy(principal, lg.getManagedSysId());
        if (passwordPolicy == null) {
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE throws=" + AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
            }
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }


        AuthenticationException changePassword = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("distinguishedName", distinguishedName);
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE. Validator stars");
            }
            validateFromAD(resp, lg, AuthCredentialsValidator.NEW, params);
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE. Validator finish successfully");
            }
        } catch (AuthenticationException ae) {
            if (log.isDebugEnabled()) {
                log.debug("AD_LOGIN_MODULE Validator throws=" + ae);
            }
            // we should validate password before change password
            if (AuthenticationConstants.RESULT_PASSWORD_EXPIRED == ae.getErrorCode() ||
                    AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP == ae.getErrorCode()) {
                changePassword = ae;

            } else {
                throw ae;
            }

        }

        if (!authContext.isSkipPasswordCheck()) {
            // checking if provided Password is not empty
            if (StringUtils.isEmpty(password)) {
                if (log.isDebugEnabled()) {
                    log.debug("AD_LOGIN_MODULE checking if provided Password is not empty throws=" + AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }
                throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);
            }
            // try to login to AD with this user
            LdapContext ldapCtx = null;
            Boolean isContextNotInitialized = true;
            try {
                ldapCtx = connect(distinguishedName, password, mSys);
            } catch (AuthenticationException ae) {
                if (AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET == ae.getErrorCode()) {
                    isContextNotInitialized = false;
                }
            }

            if (ldapCtx == null && isContextNotInitialized) {
                if (log.isDebugEnabled()) {
                    log.debug("AD_LOGIN_MODULE. COntext is null for dn=" + distinguishedName);
                }
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

            for (ExtensibleAttribute extensibleAttributes : resp.getAttrList()) {
                switch (extensibleAttributes.getName()) {
                    case "msDS-UserPasswordExpiryTimeComputed":
                        String pwdExp = extensibleAttributes.getValue();
                        if (StringUtils.isNotBlank(pwdExp)) {
                            Date pwdExpDate = converADdateToOIMdate(pwdExp);
                            lg.setPwdExp(pwdExpDate);
                        }
                        break;
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

            if (log.isDebugEnabled()) {
                log.debug("-login successful");
            }
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
            if (log.isDebugEnabled()) {
                log.debug("-Good Authn: Login object updated.");
            }
            loginManager.updateLogin(lg);

            // check the user status
            if (UserStatusEnum.PENDING_INITIAL_LOGIN.equals(user.getStatus()) ||
                    // after the start date
                    UserStatusEnum.PENDING_START_DATE.equals(user.getStatus())) {
                user.setStatus(UserStatusEnum.ACTIVE);
                userManager.updateUser(user);
            }
        }
        // Successful login
        if (log.isDebugEnabled()) {
            log.debug("-Populating subject after authentication");
        }

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
        setResultCode(lg, subj, curDate, passwordPolicy, false);

        return subj;
    }


    private void validateFromAD(LookupUserResponse resp, LoginEntity login, int operation, Map<String, Object> bindingMap) throws AuthenticationException {
        boolean enabled = false;
        Date accExpDate = null;
        boolean changePsswdAtLogon = false;
        if (resp.isSuccess()) {
            for (ExtensibleAttribute a : resp.getAttrList()) {
                switch (a.getName()) {
                    case "Enabled":
                        enabled = StringUtils.equalsIgnoreCase("True", a.getValue());
                        break;
                    case "AccountExpirationDate":
                        if (StringUtils.isNotBlank(a.getValue())) {
                            Date maxDate = new Date(221876910000000L); // Dec, 31, 9999
                            if ("0".equals(a.getValue())) { // never expires
                                accExpDate = maxDate;
                            } else {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
                                    accExpDate = sdf.parse(a.getValue());
                                    if (accExpDate.after(maxDate)) {
                                        accExpDate = maxDate;
                                    }
                                } catch (Exception e) {
                                    log.error(e);
                                }
                            }
                        }
                        break;
                    case "ChangePasswordAtLogon":
                        changePsswdAtLogon = StringUtils.equalsIgnoreCase("True", a.getValue());
                        break;
                }
            }
        } else {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_SERVICE_NOT_FOUND);
        }

        if (!enabled) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_LOGIN_DISABLED);
        }

        if (accExpDate != null) {
            if (operation == AuthCredentialsValidator.NEW) {
                login.setPwdExp(accExpDate);
            }
            Date curDate = new Date();
            if (curDate.after(accExpDate)) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_LOGIN_DISABLED);
            }
        }
        if (operation == AuthCredentialsValidator.NEW) {
            if (changePsswdAtLogon) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
            }
        }

    }

}
