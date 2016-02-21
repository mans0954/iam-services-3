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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.util.encrypt.Cryptor;

import java.util.List;

/**
 * Validates a password to ensure that is conforms to the history rules.
 *
 * @author suneet
 */
public class PasswordHistoryRule extends AbstractPasswordRule {

    private static final Log log = LogFactory.getLog(PasswordHistoryRule.class);
    protected PasswordHistoryDAO passwordHistoryDao;
    protected Cryptor cryptor;
    protected KeyManagementService keyManagementService;

    @Override
    public String getAttributeName() {
        return "PWD_HIST_VER";
    }

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {

        log.info("PasswordHistoryRule called.");

        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = true;
        }

        if (enabled) {
            log.info("password history rule is enabled.");

            if(StringUtils.isBlank(user.getId()) || StringUtils.isBlank(lg.getLogin())){
                // new user skip validation
                return;
            }

            Password pswd = new Password();
            pswd.setManagedSysId(lg.getManagedSysId());
            pswd.setPrincipal(lg.getLogin());
            pswd.setPassword(password);

            int version = Integer.parseInt(attribute.getValue1());
            List<PasswordHistoryEntity> historyList = passwordHistoryDao.getPasswordHistoryByLoginId(lg.getLoginId(), 0, version);
            if (historyList == null || historyList.isEmpty()) {
                // no history
                return;
            }
            // check the list.
            String userId = (user == null) ? lg.getUserId() : user.getId();

            log.info("Found " + historyList.size() + " passwords in the history");
            for (PasswordHistoryEntity hist : historyList) {
                String pwd = hist.getPassword();
                String decrypt = null;
                try {
                    decrypt = cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.password.name()), pwd);
                    if (StringUtils.equals(pswd.getPassword(), decrypt)) {
                        log.info("matching password found.");
                        throw new PasswordRuleException(ResponseCode.FAIL_HISTORY_RULE);
                    }
                } catch (PasswordRuleException e) {
                    throw e;
                } catch (Throwable e) {
                    log.error("PasswordHistoryRule failed due to decryption error. ", e);
                    /*
					 * this is not an error for the user to see - it's a f*ckup in the database
					 * just don't return anything
					 */
                    //throw new PasswordRuleException(ResponseCode.FAIL_HISTORY_RULE);
                }
            }
        }
    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = true;
        }

        if (enabled) {
            int version = Integer.parseInt(attribute.getValue1());
            return new PasswordRuleException(ResponseCode.FAIL_HISTORY_RULE, new Object[]{version});
        } else {
            return null;
        }
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = true;
        }

        if (enabled) {
            int version = Integer.parseInt(attribute.getValue1());
            return new PasswordRule(ResponseCode.FAIL_HISTORY_RULE, new Object[]{version});
        } else {
            return null;
        }
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
}
