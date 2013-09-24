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
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;

/**
 * Validates a password to ensure the lenght is consistent with the lenght defined in the password policy
 * @author suneet
 *
 */
public class PasswordLengthRule extends AbstractPasswordRule {


	@Override
	public void validate() throws PasswordRuleException {
		int minlen = 0;
		int maxlen = 0;
				
		PolicyAttribute attribute = policy.getAttribute("PWD_LEN");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			minlen = Integer.parseInt(attribute.getValue1());
		}
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue2())) {
			maxlen = Integer.parseInt(attribute.getValue2());
		}
		if (password == null) {
			throw new PasswordRuleException(ResponseCode.FAIL_LENGTH_RULE);
		}
		
		if (minlen > 0 ) {
			if (password.length() < minlen) {
				throw new PasswordRuleException(ResponseCode.FAIL_LENGTH_RULE);
			}
		}
		if (maxlen > 0 ) {
			if (password.length() > maxlen ) {
				throw new PasswordRuleException(ResponseCode.FAIL_LENGTH_RULE);
			}
		}
	}
}