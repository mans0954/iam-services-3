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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * DefaultLoginModule provides basic password based authentication using the OpenIAM repository.
 * @author suneet
 *
 */

@Component("defaultLoginModule")
public class DefaultLoginModule extends AbstractLoginModule {

    private static final Log log = LogFactory.getLog(DefaultLoginModule.class);

    public DefaultLoginModule() {
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
        // TODO Auto-generated method stub

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

        Subject sub = new Subject();

        String clientIP = authContext.getClientIP();
        String nodeIP = authContext.getNodeIP();

        log.debug("login() in DefaultLoginModule called");

        // current date
        Date curDate = new Date(System.currentTimeMillis());
        PasswordCredential cred = (PasswordCredential) authContext
                .getCredential();

        String principal = cred.getPrincipal();
        String password = cred.getPassword();
        LoginResponse lgResp = loginManager.getLoginByManagedSys(principal, sysConfiguration.getDefaultManagedSysId());
        Login lg = lgResp.getPrincipal();
        UserEntity user = userManager.getUser(lg.getUserId());

        if (user != null && user.getStatus() != null) {
            log.debug("-User Status=" + user.getStatus());
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID_USER_STATUS", domainId, null, principal,
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
//                        "INVALID_USER_STATUS", domainId, null, principal, null,
//                        null, clientIP, nodeIP);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
            // check the secondary status
            log.debug("-Secondary status=" + user.getSecondaryStatus());
            checkSecondaryStatus(user);

        }

        log.debug("Authentication policyid="
                + sysConfiguration.getDefaultAuthPolicyId());
        // get the authentication lock out policy
        Policy plcy = policyDataService.getPolicy(sysConfiguration.getDefaultAuthPolicyId());
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

        String tokenType = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

        // check the password
        String decryptPswd = this.decryptPassword(lg.getUserId(), lg.getPassword());
        Policy pwdPlcy = passwordManager.getPasswordPolicy(lg.getLogin(), lg.getManagedSysId());
        if (StringUtils.isEmpty(password) || !StringUtils.equals(decryptPswd, password)) {

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

//                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                        "INVALID_PASSWORD", domainId, null, principal, null,
//                        null, clientIP, nodeIP);

                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_PASSWORD);

            }
        } else {
            // validate the password expiration rules
            log.debug("Validating the state of the password - expired or not");
            int pswdResult = passwordExpired(lg, curDate);
            if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
//                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                        "PASSWORD_EXPIRED", domainId, null, principal, null,
//                        null, clientIP, nodeIP);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
            }
            Integer daysToExp = setDaysToPassworExpiration(lg, curDate, sub, pwdPlcy);
            if (daysToExp!=null) {
                sub.setDaysToPwdExp(0);
                if(daysToExp > -1)
                    sub.setDaysToPwdExp(daysToExp);
            }
            // check password policy if it is necessary to change it after reset

            if(lg.getResetPassword()>0){
                String chngPwdAttr = getPolicyAttribute(pwdPlcy.getPolicyAttributes(),"CHNG_PSWD_ON_RESET");
                if (StringUtils.isNotBlank(chngPwdAttr) && StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), chngPwdAttr)) {
                    throw new AuthenticationException(AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET);
                }
            }

            log.debug("-login successful");
            // good login - reset the counters
            Date curTime = new Date(System.currentTimeMillis());

            lg.setLastAuthAttempt(curDate);

            // move the current login to prev login fields
            lg.setPrevLogin(lg.getLastLogin());
            lg.setPrevLoginIP(lg.getLastLoginIP());

            // assign values to the current login
            lg.setLastLogin(curDate);
            lg.setLastLoginIP(clientIP);

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
        }

        // Successful login
        log.debug("-Populating subject after authentication");

        sub.setUserId(lg.getUserId());
        sub.setPrincipal(principal);
        sub.setSsoToken(token(lg.getUserId(), tokenParam));
        setResultCode(lg, sub, curDate, pwdPlcy);



        // send message into to audit log

//        log("AUTHENTICATION", "AUTHENTICATION", "SUCCESS", null, domainId,
//                user.getId(), principal, null, null, clientIP, nodeIP);

        return sub;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.auth.spi.LoginModule#logout(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    /*
    public void logout(String securityDomain, String principal,
            String managedSysId) {

        log("AUTHENTICATION", "LOGOUT", "SUCCESS", null, securityDomain, null,
                principal, null, null, null, null);

    }
    */


}
