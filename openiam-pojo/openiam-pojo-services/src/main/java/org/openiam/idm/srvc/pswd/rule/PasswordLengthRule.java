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
 * Validates a password to ensure the lenght is consistent with the lenght defined in the password policy
 *
 * @author suneet
 */
public class PasswordLengthRule extends AbstractPasswordRule {


    @Override
    public String getAttributeName() {
        return "PWD_LEN";
    }

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {
        int minlen = getValue1(attribute);
        int maxlen = getValue2(attribute);
        final PasswordRuleException ex = createException();
        if (ex == null) {
            return;
        }

        if (password == null) {
            throw ex;
        }

        if (minlen > 0) {
            if (password.length() < minlen) {
                throw ex;
            }
        }
        if (maxlen > 0) {
            if (password.length() > maxlen) {
                throw ex;
            }
        }

    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        int minlen = getValue1(attribute);
        int maxlen = getValue2(attribute);
        return createException(ResponseCode.FAIL_LENGTH_RULE, minlen, maxlen);
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        int minlen = getValue1(attribute);
        int maxlen = getValue2(attribute);
        if (minlen <= 0 && maxlen <= 0) {
            return null;
        } else {
            return createRule(ResponseCode.FAIL_LENGTH_RULE, minlen, maxlen);
        }
    }
}