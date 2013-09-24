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
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationCode;

/**
 * Validates a password to ensure that a character in password does not repeat
 * more times than what is defined in the password policy
 * 
 * @author Ekta
 * 
 */
public class LimitNumberRepeatCharRule extends AbstractPasswordRule {

	public PasswordValidationCode isValid() {
		PasswordValidationCode retval = PasswordValidationCode.SUCCESS;
		int numberOfRepeatingChar = 0;

		PolicyAttribute attribute = policy
				.getAttribute("LIMIT_NUM_REPEAT_CHAR");

		if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
			numberOfRepeatingChar = Integer.parseInt(attribute.getValue1());
		}

		// check for every char
		if (password == null) {
			return PasswordValidationCode.FAIL_LIMIT_NUM_REPEAT_CHAR;
		}

		char charAtPosition;

		if(numberOfRepeatingChar > 0) {
			for (int counter = 0; counter < password.length(); counter++) {
				charAtPosition = password.charAt(counter);
				int count = 0;
				for (int i = counter; i < password.length(); i++) {
					if (charAtPosition == password.charAt(i)) {
						count++;
						if (count > numberOfRepeatingChar) {
							return PasswordValidationCode.FAIL_LIMIT_NUM_REPEAT_CHAR;
						}
					} else {
						count = 0;
					}
				}
			}
		}
		return retval;
	}

}
