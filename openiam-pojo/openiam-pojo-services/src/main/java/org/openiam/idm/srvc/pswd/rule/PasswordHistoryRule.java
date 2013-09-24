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


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.service.service.ServiceDAOImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Validates a password to ensure that is conforms to the history rules.
 * 
 * @author suneet
 *
 */
public class PasswordHistoryRule extends AbstractPasswordRule {

	private static final Log log = LogFactory.getLog(PasswordHistoryRule.class);

	@Override
	public void validate() throws PasswordRuleException {
		
		log.info("PasswordHistoryRule called.");
		
		boolean enabled = false;
						
		PolicyAttribute attribute = policy.getAttribute("PWD_HIST_VER");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			enabled = true;

		}
		
		if (enabled) {			
			log.info("password history rule is enabled.");
			Password pswd = new Password();
			pswd.setDomainId(lg.getDomainId());
			pswd.setManagedSysId(lg.getManagedSysId());
			pswd.setPrincipal(lg.getLogin());
			pswd.setPassword(password);
			
			int version =  Integer.parseInt( attribute.getValue1() );
			List<PasswordHistoryEntity> historyList = passwordHistoryDao.getPasswordHistoryByLoginId(lg.getLoginId(), 0, version);
			if (historyList == null || historyList.isEmpty()) {
				// no history
				return;
			}
			// check the list.
            String userId = (user==null)?lg.getUserId():user.getUserId();

            log.info("Found " + historyList.size() + " passwords in the history");
			for ( PasswordHistoryEntity hist  : historyList) {
				String pwd = hist.getPassword();
				String decrypt = null;
				try {
					decrypt =  cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.password.name()), pwd);
				}catch(Throwable e) {
					log.error("PasswordHistoryRule failed due to decrption error. ", e);
					throw new PasswordRuleException(ResponseCode.FAIL_HISTORY_RULE);
				}
				if (pswd.getPassword().equals(decrypt)) {
					log.info("matching password found.");
					throw new PasswordRuleException(ResponseCode.FAIL_HISTORY_RULE);
				}
			}
		}	
	}
}
