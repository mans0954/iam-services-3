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
import org.openiam.idm.srvc.pswd.dto.PasswordRule;

/**
 * Validates a password to ensure that it contains the appropriate number of numeric characters in 
 * the password.
 * @author suneet
 *
 */
public class LowerCaseRule extends AbstractPasswordRule {


	@Override
	public void validate() throws PasswordRuleException {
	
		PolicyAttribute attribute = getAttribute("LOWERCASE_CHARS");
		int minChar = getValue1(attribute);
		int maxChar = getValue2(attribute);
		final PasswordRuleException ex = createException();
		
		// count the number of characters in the password
		if (password == null) {
			throw ex;
		}
		int charCtr = 0;
		for (int i=0; i < password.length(); i++) {
			int ch = password.charAt(i);
			if (ch >= 97 && ch <= 122) {
				charCtr++;
			}
		}
		
		if (minChar > 0 ) {
			if (charCtr  < minChar) {
				throw ex;
			}
		}
		if (maxChar > 0 ) {
			if (charCtr > maxChar ) {
				throw ex;
			}
		}
	}

	@Override
	public PasswordRuleException createException() {
		PolicyAttribute attribute = getAttribute("LOWERCASE_CHARS");
		int minChar = getValue1(attribute);
		int maxChar = getValue2(attribute);
		if (minChar > 0 || maxChar > 0) {
			return createException(ResponseCode.FAIL_LOWER_CASE_RULE, minChar, maxChar);
		} else {
			return null;
		}
	}

	@Override
	public PasswordRule createRule() {
		PolicyAttribute attribute = getAttribute("LOWERCASE_CHARS");
		int minChar = getValue1(attribute);
		int maxChar = getValue2(attribute);
		if (minChar > 0 || maxChar > 0) {
			return createRule(ResponseCode.FAIL_LOWER_CASE_RULE, minChar, maxChar);
		} else {
			return null;
		}
	}	
}
