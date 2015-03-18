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
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.*;
import org.openiam.idm.srvc.user.domain.UserEntity;


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
    PasswordValidationResponse isPasswordValid(Password pswd) throws ObjectNotFoundException;


    /**
     * Determines if the password conforms to the policy associated with this user.
     *
     * @param pswd
     * @param user
     * @param lg
     * @return
     * @throws ObjectNotFoundException
     */
    PasswordValidationResponse isPasswordValidForUser(Password pswd, UserEntity user, LoginEntity lg) throws ObjectNotFoundException;

    /**
     * Returns if the password conforms to selected password policy
     *
     * @param pswd
     * @param user
     * @param lg
     * @param policy
     * @return
     * @throws ObjectNotFoundException
     */
    PasswordValidationResponse isPasswordValidForUserAndPolicy(Password pswd, UserEntity user, LoginEntity lg, Policy policy) throws ObjectNotFoundException;

    /**
     * Determines if the user is allowed to change their password based on the policy and the number of times that password
     * has already been changed.
     *
     * @param principal
     * @param managedSysId
     * @return
     */
    boolean isPasswordChangeAllowed(String principal, String managedSysId);

    /**
     * Number of days in which the password will expire for this user
     *
     * @param principal
     * @param managedSysId
     * @return
     */
    int daysToPasswordExpiration(String principal, String managedSysId);

    /**
     * Number of times the password has changed today
     *
     * @param principal
     * @param managedSysId
     * @return
     */
    int passwordChangeCount(String principal, String managedSysId);

    /**
     * use getPasswordPolicyUsingContentProvider(final String principal, final String managedSysId, final String contentProviderId);
     * @param principal
     * @param managedSysId
     * @return
     */
    @Deprecated
    Policy getPasswordPolicy(String principal, String managedSysId);

    /**
     * Returns the password policy for this user based on their identity
     *
     * @param principal
     * @param managedSysId
     * @param contentProviderId
     * @return
     */
    Policy getPasswordPolicyUsingContentProvider(final String principal, final String managedSysId, final String contentProviderId);

    /**
     * Returns the global password policy
     *
     * @return
     */
    Policy getGlobalPasswordPolicy();

//    /**
//     * Gets the password policy based on the User object
//     * @param domainId
//     * @param user
//     * @return
//     */
//    Policy getPasswordPolicyByUser(String domainId, UserEntity user);

    /**
     * This method exists to REPLACE <b>getPasswordPolicyByUser</b>.  The functionality is the same.
     *
     * @param passwordPolicyAssocSearchBean
     * @return
     */
    Policy getPasswordPolicyForUser(final PasswordPolicyAssocSearchBean passwordPolicyAssocSearchBean);

    /**
     * Checks to see if a password exists in the history log based on the policy
     *
     * @return 1 - In History, 0 - Not in history, -1 No policy defined
     */
    /*
    int passwordInHistory(Password pswd, Policy policy);
    */

    /**
     * Generates a temporary token that can be used as part of Secure challenge response
     *
     * @param request
     * @return
     */

    PasswordResetTokenResponse generatePasswordResetToken(PasswordResetTokenRequest request);

    /**
     * Validates that the temporary token generated by <code>generatePasswordResetToken</code> is still valid
     *
     * @param token
     * @return
     */
    ValidatePasswordResetTokenResponse validatePasswordResetToken(String token);


}
