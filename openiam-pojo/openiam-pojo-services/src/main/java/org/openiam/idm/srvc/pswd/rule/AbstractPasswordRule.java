/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.pswd.rule;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.util.encrypt.Cryptor;

/**
 * All password validation rules must extend AbstractPasswordRule
 *
 * @author suneet
 */
public abstract class AbstractPasswordRule {

    protected boolean skipPasswordFrequencyCheck;
    protected Policy policy;
    protected String password;
    protected String principal;
    protected String managedSysId;
    protected UserEntity user;
    protected LoginEntity lg;
    protected PasswordHistoryDAO passwordHistoryDao;
    protected Cryptor cryptor;
    protected KeyManagementService keyManagementService;

    public abstract void validate(PolicyAttribute pe) throws PasswordRuleException;

    public abstract String getAttributeName();

    public void validate() throws PasswordRuleException {
        PasswordRuleException exp = createException();
        if (password == null && exp != null) {
            throw exp;
        }
        PolicyAttribute pa = this.getAttribute(this.getAttributeName());
        if (pa == null || !pa.isRequired()) {
            return;
        } else {
            validate(pa);
        }
    }

    public abstract PasswordRuleException createException(PolicyAttribute pe);

    public PasswordRuleException createException() {
        PolicyAttribute pa = this.getAttribute(this.getAttributeName());
        if (pa == null || !pa.isRequired()) {
            return null;
        } else {
            return createException(pa);
        }
    }

    public PasswordRule createRule() {
        PolicyAttribute pa = this.getAttribute(this.getAttributeName());
        if (pa == null || !pa.isRequired()) {
            return null;
        } else {
            return createRule(pa);
        }
    }

    public abstract PasswordRule createRule(PolicyAttribute pe);

    protected PasswordRule createRule(final ResponseCode code, final int minBound, final int maxBound) {
        final PasswordRule rule = new PasswordRule(code);
        rule.setMinBound((minBound > 0) ? Integer.valueOf(minBound) : null);
        rule.setMaxBound((maxBound > 0) ? Integer.valueOf(maxBound) : null);
        return rule;
    }

    protected PasswordRuleException createException(final ResponseCode code, final int minBound,
                                                    final int maxBound) {
        final PasswordRuleException ex = new PasswordRuleException(code);
        ex.setMinBound((minBound > 0) ? Integer.valueOf(minBound) : null);
        ex.setMaxBound((maxBound > 0) ? Integer.valueOf(maxBound) : null);
        return ex;
    }

    protected PolicyAttribute getAttribute(final String name) {
        return policy.getAttribute(name);
    }

    protected boolean getBoolean(final PolicyAttribute attribute) {
        boolean enabled = false;
        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = Boolean.getBoolean(attribute.getValue1());
        }
        return enabled;
    }

    protected boolean isValue1Present(final PolicyAttribute attribute) {
        return (attribute != null && StringUtils.isNotBlank(attribute.getValue1()));
    }

    protected int getValue1(final PolicyAttribute attribute) {
        int minlen = 0;
        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            minlen = Integer.parseInt(attribute.getValue1());
        }
        return minlen;
    }

    protected int getValue2(final PolicyAttribute attribute) {
        int maxlen = 0;
        if (attribute != null && StringUtils.isNotBlank(attribute.getValue2())) {
            maxlen = Integer.parseInt(attribute.getValue2());
        }
        return maxlen;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LoginEntity getLg() {
        return lg;
    }

    public void setLg(LoginEntity lg) {
        this.lg = lg;
    }

    public PasswordHistoryDAO getPasswordHistoryDao() {
        return passwordHistoryDao;
    }

    public void setPasswordHistoryDao(PasswordHistoryDAO passwordHistoryDao) {
        this.passwordHistoryDao = passwordHistoryDao;
    }

    public Cryptor getCryptor() {
        return cryptor;
    }

    public void setCryptor(Cryptor cryptor) {
        this.cryptor = cryptor;
    }

    public KeyManagementService getKeyManagementService() {
        return keyManagementService;
    }

    public void setKeyManagementService(KeyManagementService keyManagementService) {
        this.keyManagementService = keyManagementService;
    }

    public boolean isSkipPasswordFrequencyCheck() {
        return skipPasswordFrequencyCheck;
    }

    public void setSkipPasswordFrequencyCheck(boolean skipPasswordFrequencyCheck) {
        this.skipPasswordFrequencyCheck = skipPasswordFrequencyCheck;
    }

}
