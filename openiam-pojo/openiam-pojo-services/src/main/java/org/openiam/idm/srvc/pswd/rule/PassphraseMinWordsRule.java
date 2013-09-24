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


import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;

/**
 * Validates a password to ensure that there are minimum words in passphrase as
 * defined in the password policy
 * @author Ekta
 *
 */
public class PassphraseMinWordsRule extends AbstractPasswordRule {


	@Override
	public void validate() throws PasswordRuleException {
		int minWords = 0;
				
		PolicyAttribute attribute = policy.getAttribute("MIN_WORDS_PASSPHRASE");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			minWords = Integer.parseInt(attribute.getValue1());
		}
		if (password == null) {
			throw new PasswordRuleException(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE);
		}
		
		if (minWords > 0 ) {
			StringTokenizer tokenizer = new StringTokenizer(password);
			
			if (tokenizer.countTokens() < minWords) {
				throw new PasswordRuleException(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE);
			}
		}
	}	
}
