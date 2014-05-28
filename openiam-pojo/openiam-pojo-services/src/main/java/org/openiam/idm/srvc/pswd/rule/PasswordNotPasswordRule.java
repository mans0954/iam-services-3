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
 * Validates a password to ensure the password is not equal to the principal
 * @author suneet
 *
 */
public class PasswordNotPasswordRule extends AbstractPasswordRule {

	private static final String PASSWORD = "password";

	@Override
	public void validate() throws PasswordRuleException {
		boolean enabled = false;
				
		PolicyAttribute attribute = getAttribute("PWD_EQ_PWD");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			enabled =  Boolean.parseBoolean(attribute.getValue1());
		}
		if (enabled) {
			String pswd = password.toLowerCase();
			if (pswd.contains(PASSWORD)) {
				throw new PasswordRuleException(ResponseCode.FAIL_NEQ_PASSWORD, new Object[] {PASSWORD});
			}
		}
	}

	@Override
	public PasswordRuleException createException() {
		boolean enabled = false;
		
		PolicyAttribute attribute = getAttribute("PWD_EQ_PWD");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			enabled =  Boolean.parseBoolean(attribute.getValue1());
		}
		if (enabled) {
			return new PasswordRuleException(ResponseCode.FAIL_NEQ_PASSWORD, new Object[] {PASSWORD});
		} else {
			return null;
		}
	}

	@Override
	public PasswordRule createRule() {
		boolean enabled = false;
		
		PolicyAttribute attribute = getAttribute("PWD_EQ_PWD");
		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			enabled =  Boolean.parseBoolean(attribute.getValue1());
		}
		if (enabled) {
			return new PasswordRule(ResponseCode.FAIL_NEQ_PASSWORD, new Object[] {PASSWORD});
		} else {
			return null;
		}
	}
}
