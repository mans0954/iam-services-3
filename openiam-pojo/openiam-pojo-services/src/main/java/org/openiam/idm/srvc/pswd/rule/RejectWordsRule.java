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


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Validates a password to ensure that it does not contain the characters defined in the rule
 * the password.
 *
 * @author suneet
 */

public class RejectWordsRule extends AbstractPasswordRule {
    private final static String excludedWordsPath = System.getProperty("confpath", "data/openiam").concat("/conf/iamscripts/prov-helper/excludedWords.txt");
    private static final Log log = LogFactory.getLog(RejectWordsRule.class);

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {
        List<String> listExcludedWords = null;
        if (attribute != null && attribute.isRequired() && "true".equalsIgnoreCase(attribute.getValue1())) {
            listExcludedWords = getFromFile();
        }
        if (CollectionUtils.isEmpty(listExcludedWords)) {
            return;
        }
        final PasswordRuleException ex = new PasswordRuleException(ResponseCode.FAIL_REJECT_WORDS_IN_PSWD, new Object[]{StringUtils.join(listExcludedWords, ',')});
        if (password == null) {
            throw ex;
        }

        for (String word : listExcludedWords) {
            if (password.contains(word)) {
                throw ex;
            }
        }
    }

    @Override
    public String getAttributeName() {
        return "REJECT_WORDS_IN_PSWD";
    }

    private List<String> getFromFile() {
        List<String> result = new ArrayList<>();
        File f = new File(excludedWordsPath);
        if (f.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            } catch (Exception e) {
                log.error("Can't work with file. " + excludedWordsPath + "Exception:" + e);
            }
        }
        return result;
    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        List<String> excludeWordsList = null;
        if (attribute != null && attribute.isRequired() && "true".equals(attribute.getValue1())) {
            excludeWordsList = this.getFromFile();
        }

        if (excludeWordsList == null) {
            return null;
        } else {
            return new PasswordRuleException(ResponseCode.FAIL_REJECT_WORDS_IN_PSWD, new Object[]{StringUtils.join(excludeWordsList, ',')});
        }
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        List<String> excludeWordsList = null;
        if (attribute != null && attribute.isRequired() && "true".equals(attribute.getValue1())) {
            excludeWordsList = this.getFromFile();
        }
        if (excludeWordsList == null) {
            return null;
        } else {
            return new PasswordRule(ResponseCode.FAIL_REJECT_WORDS_IN_PSWD, new Object[]{StringUtils.join(excludeWordsList, ',')});
        }
    }
}
