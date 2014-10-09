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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;

/**
 * Validates a password to ensure that the repetition of words in passphrase is
 * as defined in the password policy
 * 
 * @author Ekta
 * 
 */
public class PassphraseRepeatWordRule extends AbstractPasswordRule {

    @Override
    public String getAttributeName() {
        return "REPEAT_SAME_WORD_PASSPHRASE";
    }

    @Override
	public void validate(PolicyAttribute attribute) throws PasswordRuleException {
		boolean enabled = true;

		if(attribute != null) {
			if (StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), attribute.getValue1())) {
				enabled = false;
			}
		}
		if (!enabled){
			// should not allow repetition of words in passphrase
			StringTokenizer tokenizer = new StringTokenizer(password);
			List<String> words = new ArrayList<String>();
			while (tokenizer.hasMoreTokens()){
				words.add(tokenizer.nextToken().toUpperCase());
			}
			//for faster search
			Collections.sort(words);
			for (int i=0; i< words.size() -1; i++){
				//only one comparison needed, as list is sorted
				if (words.get(i).equalsIgnoreCase(words.get(i+1))){
					throw new PasswordRuleException(ResponseCode.PASSPHRASE_WORD_REPEAT_RULE);
				}
			}
		}
	}

	@Override
	public PasswordRuleException createException(PolicyAttribute attribute) {
		boolean enabled = true;

		if(attribute != null) {
			if (StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), attribute.getValue1())) {
				enabled = false;
			}
		}
		if (!enabled){
			return new PasswordRuleException(ResponseCode.PASSPHRASE_WORD_REPEAT_RULE);
		} else {
			return null;
		}
	}

	@Override
	public PasswordRule createRule(PolicyAttribute attribute) {
		boolean enabled = true;

		if(attribute != null) {
			if (StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), attribute.getValue1())) {
				enabled = false;
			}
		}
		if (!enabled){
			return new PasswordRule(ResponseCode.PASSPHRASE_WORD_REPEAT_RULE);
		} else {
			return null;
		}
	}

}
