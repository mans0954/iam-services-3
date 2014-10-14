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
import org.openiam.exception.CreateException;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;

/**
 * Validates a password to ensure that a character in password does not repeat
 * more times than what is defined in the password policy
 *
 * @author Ekta
 */
public class LimitNumberRepeatCharRule extends AbstractPasswordRule {

    @Override
    public String getAttributeName() {
        return "LIMIT_NUM_REPEAT_CHAR";
    }

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {
        int numberOfRepeatingChar = getValue1(attribute);

        // check for every char
        if (password == null) {
            throw new PasswordRuleException(ResponseCode.FAIL_LIMIT_NUM_REPEAT_CHAR);
        }

        char charAtPosition;

        if (numberOfRepeatingChar > 0) {
            for (int counter = 0; counter < password.length(); counter++) {
                charAtPosition = password.charAt(counter);
                int count = 0;
                for (int i = counter; i < password.length(); i++) {
                    if (charAtPosition == password.charAt(i)) {
                        count++;
                        if (count > numberOfRepeatingChar) {
                            throw createException();
                        }
                    } else {
                        count = 0;
                    }
                }
            }
        }
    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        int numberOfRepeatingChar = getValue1(attribute);
        if (numberOfRepeatingChar > 0) {
            return new PasswordRuleException(ResponseCode.FAIL_LIMIT_NUM_REPEAT_CHAR, new Object[]{numberOfRepeatingChar});
        } else {
            return null;
        }
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        int numberOfRepeatingChar = getValue1(attribute);
        if (numberOfRepeatingChar > 0) {
            return new PasswordRule(ResponseCode.FAIL_LIMIT_NUM_REPEAT_CHAR, new Object[]{numberOfRepeatingChar});
        } else {
            return null;
        }
    }

}
