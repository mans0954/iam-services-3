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

import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.util.encrypt.Cryptor;

/**
 * All password validation rules must extend AbstractPasswordRule
 * @author suneet
 *
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

	public abstract void validate() throws PasswordRuleException; 
	
	protected PasswordRuleException createException(final ResponseCode code, final int minBound, final int maxBound) {
		final PasswordRuleException ex = new PasswordRuleException(code);
		ex.setMinBound((minBound > 0) ? Integer.valueOf(minBound) : null);
		ex.setMaxBound((maxBound > 0) ? Integer.valueOf(maxBound) : null);
		return ex;
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
