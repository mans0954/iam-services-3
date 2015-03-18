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
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenRequest;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenResponse;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.dto.ValidatePasswordResetTokenResponse;
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
 *
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
        Policy pswdPolicy = getPasswordPolicyUsingContentProvider(
                pswd.getPrincipal(), pswd.getManagedSysId(), null);
        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info(String.format("Selected Password policy=%s", pswdPolicy.getId()));
        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd);
            retVal.setRules(rules);
            passwordValidator.validate(pswdPolicy, pswd);
        } catch(PasswordRuleException e) {
            retVal.setErrorCode(e.getCode());
            retVal.setResponseValues(e.getResponseValues());
            retVal.setMinBound(e.getMinBound());
            retVal.setMaxBound(e.getMaxBound());
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
    public PasswordValidationResponse isPasswordValidForUser(Password pswd,
                                                             UserEntity user, LoginEntity lg) throws ObjectNotFoundException {
        PasswordValidationResponse retVal = new PasswordValidationResponse(ResponseStatus.SUCCESS);
        
        final PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
    	searchBean.setUserId(user.getId());
        Policy pswdPolicy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info("Selected Password policy=" + pswdPolicy.getId());
        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd, user, lg);
            retVal.setRules(rules);
            passwordValidator.validateForUser(pswdPolicy, pswd, user, lg);
        } catch(PasswordRuleException e) {
            retVal.setErrorCode(e.getCode());
            retVal.setMinBound(e.getMinBound());
            retVal.setMaxBound(e.getMaxBound());
            retVal.setResponseValues(e.getResponseValues());
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
            final PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        	searchBean.setUserId(user.getId());
            pswdPolicy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
        }

        if (pswdPolicy == null) {
            retVal.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            retVal.fail();
            return retVal;
        }
        log.info(String.format("Selected Password policy=%s",pswdPolicy.getId()));
        try {
            final List<PasswordRule> rules = passwordValidator.getPasswordRules(pswdPolicy, pswd, user, lg);
            retVal.setRules(rules);
            passwordValidator.validateForUser(pswdPolicy, pswd, user, lg);
        } catch(PasswordRuleException e) {
            retVal.setErrorCode(e.getCode());
            retVal.setResponseValues(e.getResponseValues());
            retVal.setMinBound(e.getMinBound());
            retVal.setMaxBound(e.getMaxBound());
            retVal.fail();
        } catch (final Throwable io) {
            log.error("Unknown exception", io);
            retVal.setErrorCode(ResponseCode.FAIL_OTHER);
            retVal.fail();
        }
        return retVal;
    }

    @Override
    public int daysToPasswordExpiration(String principal,
                                        String managedSysId) {
        long DAY = 86400000L;
        long curTime = System.currentTimeMillis();
        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        if (lg == null) {
            return -1;
        }
        if (lg.getPwdExp() == null) {
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

    @Override
    public boolean isPasswordChangeAllowed(String principal, String managedSysId) {
        boolean enabled = false;
        Policy policy = getPasswordPolicyUsingContentProvider(principal, managedSysId, null);
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

    @Override
    public int passwordChangeCount(String principal, String managedSysId) {
        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        if (lg == null) {
            return -1;
        }
        return lg.getPasswordChangeCount();
    }

    @Override
    @Deprecated
    public Policy getPasswordPolicy(String principal, String managedSysId) {
        return getPasswordPolicyUsingContentProvider(principal, managedSysId, null);
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

    @Override
    public PasswordResetTokenResponse generatePasswordResetToken(
            PasswordResetTokenRequest request) {
        PasswordResetTokenResponse resp = new PasswordResetTokenResponse(
                ResponseStatus.SUCCESS);
        int expirationDays = 0;
        if (request == null || request.getPrincipal() == null
                || request.getManagedSysId() == null) {
            log.warn("can't generate password reset token - Either the request, principal, domain ID, or managed system ID were null");
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        Policy pl = getPasswordPolicyUsingContentProvider(request.getPrincipal(), request.getManagedSysId(), request.getContentProviderId());
        if (pl == null) {
            log.warn("can't generate password reset token - can't get password policy");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PASSWORD_POLICY_NOT_FOUND);
            return resp;
        }
        final PolicyAttribute expirationTime = pl.getAttribute("NUM_DAYS_FORGOT_PASSWORD_TOKEN_VALID");
        try {
            expirationDays = Integer.parseInt(expirationTime.getValue1());
        } catch(Throwable e) {
            log.warn("Can't parse the '' policy attribute. Either it's not an integer, or it doesn't exist. Defaulting...", e);
            expirationDays = 3;
        }
        LoginEntity l = loginManager.getLoginByManagedSys(request.getPrincipal(), request.getManagedSysId());
        long expireDate = getExpirationTime(expirationDays);
        Date tokenExpDate = new Date(expireDate);
        String str = request.getPrincipal() + "*" + expireDate;
        String token = DigestUtils.sha256Hex(str);
        resp.setPasswordResetToken(token);
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

        LoginEntity l = loginManager.getPasswordResetToken(token);
        if (l == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        Date expToken = l.getPswdResetTokenExp();
        long expTokenMillis = expToken.getTime();
        long curTime = System.currentTimeMillis();
        if (curTime > expTokenMillis) {
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
    public Policy getPasswordPolicyUsingContentProvider(String principal, String managedSysId, String contentProviderId) {
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSysId);
        final PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
    	searchBean.setUserId(lg.getUserId());
    	searchBean.setContentProviderId(contentProviderId);
        return passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
    }
}
