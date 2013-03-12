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
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.pswd.service;

import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenRequest;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenResponse;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationCode;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.pswd.dto.ValidatePasswordResetTokenResponse;

import org.openiam.base.ws.Response;


/**
 * Password service provides operations to manage passwords. This includes validation against policy,
 * as well as information such as days to expiration, the number of times a password was changed in
 * day, etc.
 *
 * @author Suneet Shah
 */

public interface PasswordService {

    /**
     * Determines if a password associated with a principal is valid based on the policies for a security domain.
     *
     * @param pswd
     * @return
     */
    PasswordValidationCode isPasswordValid(Password pswd) throws ObjectNotFoundException;


    /**
     * Determines if the password conforms to the policy associated with this user.
     * @param pswd
     * @param user
     * @param lg
     * @return
     * @throws ObjectNotFoundException
     */
    PasswordValidationCode isPasswordValidForUser(Password pswd, UserEntity user, LoginEntity lg) throws ObjectNotFoundException;

    /**
     * Returns if the password conforms to selected password policy
     * @param pswd
     * @param user
     * @param lg
     * @param policy
     * @return
     * @throws ObjectNotFoundException
     */
    PasswordValidationCode isPasswordValidForUserAndPolicy(Password pswd, UserEntity user, LoginEntity lg, Policy policy) throws ObjectNotFoundException;

    /**
     * Determines if the user is allowed to change their password based on the policy and the number of times that password
     * has already been changed.
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    boolean isPasswordChangeAllowed(String domainId, String principal, String managedSysId);

    /**
     * Number of days in which the password will expire for this user
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    int daysToPasswordExpiration(String domainId, String principal, String managedSysId);

    /**
     * Number of times the password has changed today
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    int passwordChangeCount(String domainId, String principal, String managedSysId);


    /**
     * Returns the password policy for this user based on their identity
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    Policy getPasswordPolicy(String domainId, String principal, String managedSysId);

    /**
     * Gets the password policy based on the User object
     * @param domainId
     * @param user
     * @return
     */
    Policy getPasswordPolicyByUser(String domainId, UserEntity user);
    
    /**
     * This method exists to REPLACE <b>getPasswordPolicyByUser</b>.  The functionality is the same.
     * @param domainId
     * @param user
     * @return
     */
    PolicyEntity getPasswordPolicyForUser(final String domainId, final UserEntity user);

    /**
     * Checks to see if a password exists in the history log based on the policy
     *
     * @return 1 - In History, 0 - Not in history, -1 No policy defined
     */
    int passwordInHistory(Password pswd, Policy policy);

    /**
     * Generates a temporary token that can be used as part of Secure challenge response
     *
     * @param request
     * @return
     */

    PasswordResetTokenResponse generatePasswordResetToken(PasswordResetTokenRequest request);

    /**
     * Validates that the temporary token generated by <code>generatePasswordResetToken</code> is still valid
     * @param token
     * @return
     */
    ValidatePasswordResetTokenResponse validatePasswordResetToken(String token);


}
