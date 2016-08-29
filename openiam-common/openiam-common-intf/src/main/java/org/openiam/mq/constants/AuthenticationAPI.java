package org.openiam.mq.constants;

/**
 * Created by alexander on 24/08/16.
 */
public enum AuthenticationAPI implements OpenIAMAPI {
    GlobalLogoutRequest,
    Authenticate,
    ClearOTPActiveStatus,
    SendOTPToken,
    ConfirmOTPToken,
    GetOTPSecretKey,
    RenewToken,
    FindAuthState,
    IsOTPActive,
    SaveAuthState;
}
