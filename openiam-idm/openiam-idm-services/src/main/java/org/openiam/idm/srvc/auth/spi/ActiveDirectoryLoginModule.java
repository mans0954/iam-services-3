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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
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
        LoginResponse lgResp = loginManager.getLoginByManagedSys(principal, managedSysId);
        Login lg = lgResp.getPrincipal();
        if (lg == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // checking if User is valid
        UserEntity user = userManager.getUser(lg.getUserId());
        if (user == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // Find user in target system
        List<ExtensibleAttribute> attrs = new ArrayList<ExtensibleAttribute>();
        attrs.add(new ExtensibleAttribute("distinguishedName", null));
        LookupUserResponse resp = provisionService.getTargetSystemUser(principal, managedSysId, attrs);
        log.debug("Lookup for user identity =" + principal + " in target system = " + mSys.getName() + ". Result = " + resp.getStatus() + ", " + resp.getErrorCode());

        if (resp.isFailure()) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        principal = lg.getLogin();

        String distinguishedName = null;
        if (CollectionUtils.isNotEmpty(resp.getAttrList())) {
            distinguishedName = resp.getAttrList().get(0).getValue();
        }
        if (StringUtils.isEmpty(distinguishedName)) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // checking password policy
        Policy passwordPolicy = passwordManager.getPasswordPolicy(principal, lg.getManagedSysId());
        if (passwordPolicy == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }

        AuthCredentialsValidator validator = null;
        try {
            if (StringUtils.isNotBlank(authCredentialsValidatorScript)) {
                validator = (AuthCredentialsValidator)scriptRunner.instantiateClass(null, authCredentialsValidatorScript);
                log.debug("Using custom credentials validator " + authCredentialsValidatorScript);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        if (validator == null) {
            validator = defaultAuthCredentialsValidator;
            log.debug("Using default credentials validator");
        }

        AuthenticationException changePassword = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("distinguishedName", distinguishedName);
            validator.execute(user, lg, AuthCredentialsValidator.NEW, params);

        } catch (AuthenticationException ae) {
            // we should validate password before change password
            if (AuthenticationConstants.RESULT_PASSWORD_EXPIRED == ae.getErrorCode() ||
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

        // try to login to AD with this user
        LdapContext ldapCtx = connect(distinguishedName, password, mSys);

        if (ldapCtx == null) {
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
                    loginManager.saveLogin(lg);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);

                    throw new AuthenticationException(AuthenticationConstants.RESULT_LOGIN_LOCKED);

                } else {
                    // update the counter save the record
                    loginManager.saveLogin(lg);

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
        lg.setFirstTimeLogin(0);
        log.debug("-Good Authn: Login object updated.");
        loginManager.saveLogin(lg);

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
