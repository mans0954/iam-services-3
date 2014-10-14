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
 *
 * @author suneet
 */
public class PasswordNotNameRule extends AbstractPasswordRule {


    @Override
    public String getAttributeName() {
        return "PWD_NAME";
    }

    @Override
    public void validate(PolicyAttribute attribute) throws PasswordRuleException {
        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = Boolean.parseBoolean(attribute.getValue1());

        }
        if (enabled) {

            if (user.getFirstName() != null && user.getLastName() != null) {
                String upperPassword = password.toUpperCase();
                if (upperPassword.contains(user.getFirstName().toUpperCase()) ||
                        upperPassword.contains(user.getLastName().toUpperCase())) {
                    throw new PasswordRuleException(ResponseCode.FAIL_NEQ_NAME);
                }

            }
        }
    }

    @Override
    public PasswordRuleException createException(PolicyAttribute attribute) {
        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = Boolean.parseBoolean(attribute.getValue1());

        }
        if (enabled) {
            return new PasswordRuleException(ResponseCode.FAIL_NEQ_NAME);
        } else {
            return null;
        }
    }

    @Override
    public PasswordRule createRule(PolicyAttribute attribute) {
        boolean enabled = false;

        if (attribute != null && StringUtils.isNotBlank(attribute.getValue1())) {
            enabled = Boolean.parseBoolean(attribute.getValue1());

        }
        if (enabled) {
            return new PasswordRule(ResponseCode.FAIL_NEQ_NAME);
        } else {
            return null;
        }
    }
}
