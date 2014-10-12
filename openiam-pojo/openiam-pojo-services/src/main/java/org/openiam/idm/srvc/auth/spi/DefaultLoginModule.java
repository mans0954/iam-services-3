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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * DefaultLoginModule provides basic password based authentication using the OpenIAM repository.
 * @author suneet
 *
 */
@Component("defaultLoginModule")
public class DefaultLoginModule extends AbstractLoginModule {
	
	private static final String DEFAULT_TOKEN_LIFE = "30";

    private static final Log log = LogFactory.getLog(DefaultLoginModule.class);

    public DefaultLoginModule() {
    }
    
    /*
    @Override
    public void logout(final LogoutRequest request, final IdmAuditLog auditLog) throws Exception {
    	super.logout(request, auditLog);
    }
    */
    
    @Override
	protected void validate(final AuthenticationContext context) throws Exception {
    	final String principal = context.getPrincipal();
    	final String password = context.getPassword();
    	final IdmAuditLog newLoginEvent = context.getEvent();
    	
    	if (StringUtils.isBlank(principal)) {
            newLoginEvent.setFailureReason("Invalid Principlal");
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }

        if (StringUtils.isBlank(password)) {
            newLoginEvent.setFailureReason("Invalid Password");
            throw new BasicDataServiceException(ResponseCode.INVALID_PASSWORD);
        }
	}

	@Override
	protected LoginEntity getLogin(final AuthenticationContext context) throws Exception {
		final IdmAuditLog newLoginEvent = context.getEvent();
		final String principal = context.getPrincipal();
        final ManagedSysEntity managedSystem = getManagedSystem(context);
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());
        if (lg == null) {
            newLoginEvent.setFailureReason(String.format("Cannot find login for principal '%s' and managedSystem '%s'", principal, managedSystem.getId()));
            throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN);
        }
        return lg;
	}

	@Override
	protected UserEntity getUser(final AuthenticationContext context, final LoginEntity login) throws Exception {
		final IdmAuditLog newLoginEvent = context.getEvent();
		final String userId = login.getUserId();
        newLoginEvent.setRequestorUserId(userId);
        newLoginEvent.setTargetUser(userId, login.getLogin());
        final UserEntity user = userDAO.findById(userId);
        return user;
	}

    @Override
    @Transactional
    protected Subject doLogin(final AuthenticationContext context, final UserEntity user, final LoginEntity login) throws Exception {
    	final String principal = context.getPrincipal();
    	final String password = context.getPassword();
    	final IdmAuditLog newLoginEvent = context.getEvent();

        final Subject sub = new Subject();

        final String clientIP = context.getClientIP();
        final String nodeIP = context.getNodeIP();

        // current date
        final Date curDate = new Date();

        if (user != null && user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
                    throw new AuthenticationException(AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
            // check the secondary status
            checkSecondaryStatus(user);

        }
        
        final PolicyEntity policy = getPolicy(context);
        final String attrValue = getPolicyAttribute(policy, "FAILED_AUTH_COUNT");
        final String tokenType = getPolicyAttribute(policy, "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(policy, "TOKEN_LIFE");
        final String tokenIssuer = getPolicyAttribute(policy, "TOKEN_ISSUER");
        
        if(StringUtils.isBlank(tokenType)) {
        	final String warning = String.format("Property %s not valid for policy key %s for policy %s", tokenType, "TOKEN_TYPE", policy);
        	newLoginEvent.addWarning(warning);
        	log.warn(warning);
        }
        
        if(StringUtils.isBlank(tokenLife) || !NumberUtils.isNumber(tokenLife)) {
        	final String warning = String.format("Property %s not valid for policy key %s for policy %s.  Defaulting to %s", tokenLife, "TOKEN_LIFE", policy, DEFAULT_TOKEN_LIFE);
        	newLoginEvent.addWarning(warning);
        	log.error(warning);
        	tokenLife = DEFAULT_TOKEN_LIFE;
        }

        final Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

        // check the password
        final String decryptPswd = this.decryptPassword(login.getUserId(), login.getPassword());
        if (decryptPswd != null && !decryptPswd.equals(password)) {

            // if failed auth count is part of the polices, then do the
            // following processing
            if (StringUtils.isNotBlank(attrValue)) {

                int authFailCount = Integer.parseInt(attrValue);
                // increment the auth fail counter
                int failCount = 0;
                if (login.getAuthFailCount() != null) {
                    failCount = login.getAuthFailCount().intValue();
                }
                failCount++;
                login.setAuthFailCount(failCount);
                login.setLastAuthAttempt(new Date(System.currentTimeMillis()));
                if (failCount >= authFailCount) {
                    // lock the record and save the record.
                	login.setIsLocked(1);
                    loginManager.updateLogin(login);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);
                    newLoginEvent.addAttribute(AuditAttributeName.FAIL_COUNT, Integer.valueOf(failCount).toString());
                    newLoginEvent.addWarning(String.format("User %s has fail count %s.  Setting secondary status to locked, and login record to locked", user.getId(), failCount));
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_LOGIN_LOCKED);
                } else {
                    // update the counter save the record
                    loginManager.updateLogin(login);
                    newLoginEvent.addAttribute(AuditAttributeName.FAIL_COUNT, Integer.valueOf(authFailCount).toString());
                    newLoginEvent.addWarning(String.format("User %s has fail count %s", user.getId(), failCount));
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }
            } else {
                final String warning = String.format("No '%s' policy attribute found on policy %s", "FAILED_AUTH_COUNT", policy);
                newLoginEvent.addWarning(warning);
                log.warn(warning);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_PASSWORD);

            }
        } else {
            // validate the password expiration rules
            log.debug("Validating the state of the password - expired or not");
            int pswdResult = passwordExpired(login, curDate, policy);
            if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
            	newLoginEvent.addWarning(String.format("Password Expired"));
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
            }
            Integer daysToExp = setDaysToPassworExpiration(login, curDate, sub, policy);
            if (daysToExp!=null) {
                sub.setDaysToPwdExp(0);
                if(daysToExp > -1)
                    sub.setDaysToPwdExp(daysToExp);
            }
            // check password policy if it is necessary to change it after reset

            if(login.getResetPassword()>0){
                String chngPwdAttr = getPolicyAttribute(policy, "CHNG_PSWD_ON_RESET");
                if (StringUtils.isNotBlank(chngPwdAttr) && Integer.parseInt(chngPwdAttr) > 0) {
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET);
                }
            }

            log.debug("-login successful");
            // good login - reset the counters
            Date curTime = new Date(System.currentTimeMillis());

            login.setLastAuthAttempt(curDate);

            // move the current login to prev login fields
            login.setPrevLogin(login.getLastLogin());
            login.setPrevLoginIP(login.getLastLoginIP());

            // assign values to the current login
            login.setLastLogin(curDate);
            login.setLastLoginIP(clientIP);

            login.setAuthFailCount(0);
            login.setFirstTimeLogin(0);
            log.debug("-Good Authn: Login object updated.");
            loginManager.updateLogin(login);

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

        sub.setUserId(login.getUserId());
        sub.setPrincipal(principal);
        sub.setSsoToken(token(login.getUserId(), tokenType, tokenLife, tokenParam));
        setResultCode(login, sub, curDate, policy);

        newLoginEvent.setSuccessReason("Succssfull authentication into Default Login Module");
        return sub;
    }

    /**
     * If the password has expired, but its before the grace period then its a good login
     * If the password has expired and after the grace period, then its an exception.
     * You should also set the days to expiration
     * @param lg
     * @return
     */
    private int passwordExpired(final LoginEntity lg, final Date curDate, final PolicyEntity policy) {
        log.debug("passwordExpired Called.");
        log.debug("- Password Exp =" + lg.getPwdExp());
        log.debug("- Password Grace Period =" + lg.getGracePeriod());

        if (lg.getGracePeriod() == null) {
            // set an early date
            Date gracePeriodDate = getGracePeriodDate(lg, curDate, policy);
            log.debug("Calculated the gracePeriod Date to be: "
                    + gracePeriodDate);

            if (gracePeriodDate == null) {
                lg.setGracePeriod(new Date(0));
            } else {
                lg.setGracePeriod(gracePeriodDate);
            }
        }
        if (lg.getPwdExp() != null) {
            if (curDate.after(lg.getPwdExp())
                    && curDate.after(lg.getGracePeriod())) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_PASSWORD_EXPIRED;
            }
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg
                    .getGracePeriod()))) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
            }
        }
        return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
    }

    private Date getGracePeriodDate(LoginEntity lg, Date curDate, final PolicyEntity policy) {

    	final Date pwdExpDate = lg.getPwdExp();

        if (pwdExpDate == null) {
            return null;
        }

        final String gracePeriod = getPolicyAttribute(policy, "PWD_EXP_GRACE");
        final Calendar cal = Calendar.getInstance();
        cal.setTime(pwdExpDate);
        if(StringUtils.isNotEmpty(gracePeriod)) {
            cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
            log.debug(String.format("Calculated grace period date=%s",cal.getTime()));
            return cal.getTime();
        }
        return null;

    }

    private SSOToken token(final String userId, final String tokenType, final String tokenLife, final Map tokenParam) throws Exception {

        log.debug("Generating Security Token");

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        return tkModule.createToken(tokenParam);
    }
}
