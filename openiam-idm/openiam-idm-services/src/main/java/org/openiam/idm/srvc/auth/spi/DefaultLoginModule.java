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
import org.openiam.dozer.converter.PasswordHistoryDozerConverter;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthCredentialsValidator;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
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

        Date curDate = new Date(System.currentTimeMillis());
        Subject subj = new Subject();

        PasswordCredential cred = (PasswordCredential) authContext.getCredential();
        String principal = cred.getPrincipal();
        String password = cred.getPassword();
        
        final boolean skipPasswordCheck = authContext.isSkipPasswordCheck();

        // checking if Login exists in OpenIAM
       LoginEntity lg = loginManager.getLoginByManagedSys(principal, sysConfiguration.getDefaultManagedSysId());
        if (lg == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        principal = lg.getLogin();

        // checking if User is valid
        UserEntity user = userManager.getUser(lg.getUserId());
        if (user == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        // checking password policy
        Policy passwordPolicy = passwordManager.getPasswordPolicy(lg);
        if (passwordPolicy == null) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }

        if(log.isDebugEnabled()) {
        	log.debug("Authentication policyid=" + sysConfiguration.getDefaultAuthPolicyId());
        }
        Policy authPolicy = policyDataService.getPolicy(sysConfiguration.getDefaultAuthPolicyId());
        if (authPolicy == null) {
            log.error("No auth policy found");
            throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }

        // checking passwords are equal
        if(!skipPasswordCheck) {
        	
            AuthenticationException changePassword = null;
            try {
            	if(!authContext.isSkipUserStatusCheck()) {
            		authenticationUtils.getCredentialsValidator().execute(user, lg, AuthCredentialsValidator.NEW, new HashMap<String, Object>());
            	}

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
        	
	        String encryptPswd = encryptPassword(lg.getUserId(), password);
	        if (!StringUtils.equals(lg.getPassword(), encryptPswd)) {
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
	                    throw new AuthenticationException(
	                            AuthenticationConstants.RESULT_LOGIN_LOCKED);
	
	                } else {
	                    // update the counter save the record
	                    loginManager.updateLogin(lg);
	                    throw new AuthenticationException(
	                            AuthenticationConstants.RESULT_INVALID_PASSWORD);
	                }
	
	            } else {
	                log.error("No auth fail password policy value found");
	                throw new AuthenticationException(
	                        AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
	
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
        }

        if(log.isDebugEnabled()) {
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
        if(log.isDebugEnabled()) {
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

        // Successful login
        if(log.isDebugEnabled()) {
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
        setResultCode(lg, subj, curDate, passwordPolicy, skipPasswordCheck);

        return subj;
    }

}
