package org.openiam.mq.constants.api.user;

import org.openiam.mq.constants.api.OpenIAMAPI;

/**
 * Created by aduckardt on 2016-12-16.
 */
public enum PasswordAPI implements OpenIAMAPI {
    GeneratePasswordResetToken, ValidateResetToken, GetPasswordResetToken, GetPasswordPolicy, Validate
}
