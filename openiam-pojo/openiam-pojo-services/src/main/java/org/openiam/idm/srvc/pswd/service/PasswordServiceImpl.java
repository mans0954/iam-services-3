/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
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
package org.openiam.idm.srvc.pswd.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.PasswordHistoryDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.policy.service.PolicyObjectAssocDAO;
import org.openiam.idm.srvc.pswd.dto.*;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.rule.PasswordRuleException;
import org.openiam.idm.srvc.pswd.rule.PasswordRuleViolation;
import org.openiam.idm.srvc.pswd.rule.PasswordValidator;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 */

@Service("passwordManager")
@Transactional
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    protected PasswordValidator passwordValidator;

    @Autowired
    protected LoginDataService loginManager;

    @Autowired
    protected UserDataService userManager;

    @Autowired
    private LoginDozerConverter loginDozerConverter;

//    @Autowired
//    private PolicyDataService policyDataService;
//
//    @Autowired
//    private PolicyObjectAssocDAO policyObjectAssocDao;
//
//    @Autowired
//    private PolicyDAO policyDAO;

    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;

    @Autowired
    protected PasswordHistoryDAO passwordHistoryDao;

    @Autowired
    protected OrganizationDAO organizationDAO;

    @Autowired
    protected KeyManagementService keyManagementService;

    @Autowired
    protected UserDozerConverter userDozerConverter;

    @Autowired
    protected PasswordPolicyProvider passwordPolicyProvider;

    @Autowired
    private PasswordHistoryDozerConverter passwordHistoryDozerConverter;

    private static final Log log = LogFactory.getLog(PasswordServiceImpl.class);
    private static final long DAY_AS_MILLIS = 86400000l;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.policy.pswd.PasswordService#isPasswordValid(org.
     * openiam.idm.srvc.policy.dto.Password)
     */
    @Override
    public PasswordValidationResponse isPasswordValid(Password pswd)
            throws ObjectNotFoundException {
        PasswordValidationResponse retVal = new PasswordValidationResponse(ResponseStatus.SUCCESS);

        Policy pswdPolicy = getPasswordPolicy(
                pswd.getPrincipal(), pswd.getManagedSysId());

        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info(String.format("Selected Password policy=%s", pswdPolicy.getPolicyId()));

        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd);
            retVal.setRules(rules);
            final List<PasswordRuleException> violatingRules = passwordValidator.getAllViolatingRules(pswdPolicy, pswd);
            /* for backwards compatability - the old code just threw the first violation that came back */
            if (CollectionUtils.isNotEmpty(violatingRules)) {
                retVal.fail();
                for (final PasswordRuleException exception : violatingRules) {
                    retVal.addViolation(new PasswordRuleViolation(exception));
                }
            }
        } catch (Throwable io) {
            log.error("Can't validate password", io);
            retVal.setErrorCode(ResponseCode.FAIL_OTHER);
            retVal.fail();
            return retVal;
        }
        return retVal;
    }

    @Override
    public PasswordValidationResponse isPasswordValidForUser(Password pswd,
                                                             UserEntity user, LoginEntity lg) throws ObjectNotFoundException {
        PasswordValidationResponse retVal = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        String managedSystemId = null;
        if (lg != null) {
            managedSystemId = lg.getManagedSysId();
        }
        Policy pswdPolicy = passwordPolicyProvider.getPasswordPolicyByUser(user, managedSystemId);

        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info("Selected Password policy=" + pswdPolicy.getPolicyId());

        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd, user, lg);
            retVal.setRules(rules);
            passwordValidator.validateForUser(pswdPolicy, pswd, user, lg);
        } catch (PasswordRuleException e) {
            retVal.setErrorCode(e.getCode());
            retVal.fail();
        } catch (Throwable io) {
            log.error("Can't validate password", io);
            retVal.setErrorCode(ResponseCode.FAIL_OTHER);
            retVal.fail();
            return retVal;
        }
        return retVal;
    }

    @Override
    public PasswordValidationResponse isPasswordValidForUserAndPolicy(
            Password pswd, UserEntity user, LoginEntity lg, Policy policy)
            throws ObjectNotFoundException {
        final PasswordValidationResponse retVal = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        Policy pswdPolicy = policy;
        if (pswdPolicy == null) {
            String managedSystemId = null;
            if (lg != null) {
                managedSystemId = lg.getManagedSysId();
            }
            pswdPolicy = passwordPolicyProvider.getPasswordPolicyByUser(user, managedSystemId);
        }

        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info(String.format("Selected Password policy=%s", pswdPolicy.getPolicyId()));

        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd, user, lg);
            retVal.setRules(rules);
            passwordValidator.validateForUser(pswdPolicy, pswd, user, lg);
        } catch (PasswordRuleException e) {
            retVal.setErrorCode(e.getCode());
            retVal.fail();
        } catch (final Throwable io) {
            log.error("Unknown exception", io);
            retVal.setErrorCode(ResponseCode.FAIL_OTHER);
            retVal.fail();
        }
        return retVal;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.pswd.service.PasswordService#daysToPasswordExpiration
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int daysToPasswordExpiration(String principal,
                                        String managedSysId) {

        long DAY = 86400000L;

        long curTime = System.currentTimeMillis();

        // Date curDate = new Date(System.currentTimeMillis());

        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        if (lg == null) {
            return -1;
        }
        if (lg.getPwdExp() == null) {
            // no expiration date
            return 9999;
        }

        long endTime = lg.getPwdExp().getTime();

        long diffInMilliseconds = endTime - curTime;
        long diffInDays = diffInMilliseconds / DAY;
        if (diffInDays < 1) {
            return 0;
        }
        return (int) diffInDays;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.pswd.service.PasswordService#isPasswordChangeAllowed
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean isPasswordChangeAllowed(String principal, String managedSysId) {

        boolean enabled = false;
        // get the policy
        Policy policy = getPasswordPolicy(principal, managedSysId);

        log.info("Password policy=" + policy);

        PolicyAttribute changeAttr = policy
                .getAttribute("PASSWORD_CHANGE_ALLOWED");
        if (changeAttr != null) {
            if (changeAttr.getValue1() != null
                    && changeAttr.getValue1().equalsIgnoreCase("0")) {
                return false;
            }
        }

        PolicyAttribute attribute = policy.getAttribute("RESET_PER_TIME");
        if (attribute != null && attribute.getValue1() != null) {
            enabled = true;

        }
        if (enabled) {
            int changeCount = passwordChangeCount(principal,
                    managedSysId);
            int changesAllowed = Integer.parseInt(attribute.getValue1());

            if (changeCount >= changesAllowed) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.pswd.service.PasswordService#passwordChangeCountByDate
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int passwordChangeCount(String principal, String managedSysId) {

        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        if (lg == null) {
            return -1;
        }
        return lg.getPasswordChangeCount();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openiam.idm.srvc.pswd.service.PasswordService#getPasswordPolicy(org
     * .openiam.idm.srvc.user.dto.User)
     */
    @Override
    public Policy getPasswordPolicy(String principal, String managedSysId) {
        // Find a password policy for this user
        // order of search, type, classification, domain, global

        // get the user for this principal
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        return this.getPasswordPolicy(lg);
    }

    @Override
    public Policy getPasswordPolicy(LoginEntity lg) {
        // Find a password policy for this user
        // order of search, type, classification, domain, global

        // get the user for this principal
        log.info(String.format("login=%s", lg));
//		final UserEntity user = userManager.getUser(lg.getUserId());
        PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        if (lg != null) {
            searchBean.setUserId(lg.getUserId());
            searchBean.setManagedSystemId(lg.getManagedSysId());
            return passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
        } else {
            return passwordPolicyProvider.getGlobalPasswordPolicy();
        }
    }

    @Override
    public Policy getPasswordPolicyForUser(final PasswordPolicyAssocSearchBean passwordPolicyAssocSearchBean) {
        return passwordPolicyProvider.getPasswordPolicyByUser(passwordPolicyAssocSearchBean);
    }

    /**
     * Returns the global password policy
     *
     * @return
     */
    @Override
    public Policy getGlobalPasswordPolicy() {
        return passwordPolicyProvider.getGlobalPasswordPolicy();
    }

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.PasswordService#passwordInHistory(org
	 * .openiam.idm.srvc.pswd.dto.Password,
	 * org.openiam.idm.srvc.policy.dto.Policy) 1 - In History, 0 - Not in
	 * history, -1 No policy defined
	 */
    /*
     * public int passwordInHistory(Password pswd, Policy policy) { // get the
	 * list of passwords for this user. String decrypt = null;
	 * 
	 * PolicyAttribute attr = policy.getAttribute("PWD_HIST_VER"); if (attr ==
	 * null || attr.getValue1() == null) { // no policy defined return -1; } int
	 * version = Integer.parseInt(attr.getValue1());
	 * 
	 * final LoginEntity loginEntity =
	 * loginManager.getLoginByManagedSys(pswd.getDomainId(),
	 * pswd.getPrincipal(), pswd.getManagedSysId());
	 * 
	 * int retVal = 0; if(loginEntity != null) { final
	 * List<PasswordHistoryEntity> historyList =
	 * passwordHistoryDao.getPasswordHistoryByLoginId(loginEntity.getLoginId(),
	 * 0, version); if (CollectionUtils.isEmpty(historyList)) { // no history
	 * retVal = 0; } else { log.info("Found " + historyList.size() +
	 * " passwords in the history"); for (PasswordHistoryEntity hist :
	 * historyList) { String pwd = hist.getPassword(); try { LoginEntity login =
	 * loginManager.getLoginDetails(hist.getLogin()); decrypt =
	 * cryptor.decrypt(keyManagementService.getUserKey( login.getUserId(),
	 * KeyName.password.name()), pwd); } catch (Exception e) {
	 * log.error("Unable to decrypt password in history: " + pwd); throw new
	 * IllegalArgumentException(
	 * "Unable to decrypt password in password history list"); } if
	 * (pswd.getPassword().equals(decrypt)) {
	 * log.info("matching password found."); retVal = 1; } } } } else { retVal =
	 * 0; } return retVal; }
	 */

    @Override
    public PasswordResetTokenResponse generatePasswordResetToken(
            PasswordResetTokenRequest request) {

        PasswordResetTokenResponse resp = new PasswordResetTokenResponse(
                ResponseStatus.SUCCESS);

        // number of days in which the password token will expire
        int expirationDays = 0;

        if (request == null || request.getPrincipal() == null
                || request.getManagedSysId() == null) {
            log.warn("can't generate password reset token - Either the request, principal, domain ID, or managed system ID were null");
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }

        Policy pl = getPasswordPolicy(
                request.getPrincipal(), request.getManagedSysId());
        if (pl == null) {
            log.warn("can't generate password reset token - can't get password policy");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            return resp;
        }

        final PolicyAttribute expirationTime = pl.getAttribute("NUM_DAYS_FORGET_PWD_TOKEN_VALID");
        try {
            expirationDays = Integer.parseInt(expirationTime.getValue1());
        } catch (Throwable e) {
            log.warn("Can't parse the '' policy attribute.  Either it's not an integer, or it doesn't exist.  Defaulting...", e.getCause());
            expirationDays = 3;
        }

        LoginEntity l = loginManager.getLoginByManagedSys(request.getPrincipal(), request.getManagedSysId());

        long expireDate = getExpirationTime(expirationDays);

        Date tokenExpDate = new Date(expireDate);

        String str = request.getPrincipal() + "*" + expireDate;

        String token = DigestUtils.sha256Hex(str);

        resp.setPasswordResetToken(token);

        // update our database
        l.setPswdResetToken(token);
        l.setPswdResetTokenExp(tokenExpDate);
        loginManager.updateLogin(l);

        return resp;
    }

    @Override
    public ValidatePasswordResetTokenResponse validatePasswordResetToken(
            String token) {

        ValidatePasswordResetTokenResponse resp = new ValidatePasswordResetTokenResponse(
                ResponseStatus.SUCCESS);

        // look up the token
        LoginEntity l = loginManager.getPasswordResetToken(token);
        if (l == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;

        }

        // check if the token is still valid
        Date expToken = l.getPswdResetTokenExp();
        long expTokenMillis = expToken.getTime();

        long curTime = System.currentTimeMillis();

        if (curTime > expTokenMillis) {

            // token is old - fails validation
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;

        }

        resp.setPrincipal(loginDozerConverter.convertToDTO(l, false));

        return resp;
    }

    protected long getExpirationTime(int numberOfDays) {

        long curTime = System.currentTimeMillis();
        long tokenLife = numberOfDays * DAY_AS_MILLIS;

        return (curTime + tokenLife);

    }

    @Override
    @Transactional(readOnly = true)
    public Set<PasswordHistory> getPasswordHistory(String id, Integer from, Integer count) {
        Set<PasswordHistoryEntity> phESet = new HashSet<PasswordHistoryEntity>(passwordHistoryDao.getPasswordHistoryByLoginId(id, 0, count));
        return passwordHistoryDozerConverter.convertToDTOSet(phESet, false);
    }
}
