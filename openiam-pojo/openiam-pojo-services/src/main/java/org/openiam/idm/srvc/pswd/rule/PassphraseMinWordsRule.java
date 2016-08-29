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

import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.PasswordRuleException;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;

/**
 * Validates a password to ensure that there are minimum words in passphrase as
 * defined in the password policy
 *
 * @author Ekta
 */
public class PassphraseMinWordsRule extends AbstractPasswordRule {


    @Override
    public String getAttributeName() {
        return "MIN_WORDS_PASSPHRASE";
    }

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {
        int minWords = getValue1(attribute);
        if (password == null) {
            throw new PasswordRuleException(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE, new Object[]{minWords});
        }

        if (minWords > 0) {
            StringTokenizer tokenizer = new StringTokenizer(password);

            if (tokenizer.countTokens() < minWords) {
                throw new PasswordRuleException(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE, new Object[]{minWords});
            }
        }
    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        int minWords = getValue1(attribute);
        if (minWords > 0) {
            return new PasswordRuleException(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE, new Object[]{minWords});
        } else {
            return null;
        }
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        int minWords = getValue1(attribute);
        if (minWords > 0) {
            return new PasswordRule(ResponseCode.FAIL_MIN_WORDS_PASSPHRASE_RULE, new Object[]{minWords});
        } else {
            return null;
        }
    }
}
