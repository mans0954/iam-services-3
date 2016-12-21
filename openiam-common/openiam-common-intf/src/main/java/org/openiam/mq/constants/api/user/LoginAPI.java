package org.openiam.mq.constants.api.user;

import org.openiam.mq.constants.api.OpenIAMAPI;

/**
 * Created by aduckardt on 2016-12-16.
 */
public enum LoginAPI implements OpenIAMAPI {
    EncryptPassword, DecryptPassword, Validate, LockLogin, ActivateLogin, DeActivateLogin, BulkUnLock, RemoveLogin, ResetPassword, UnLockLogin, IsPasswordEq, LoginExists, GetPrimaryIdentity, FindById, FindBeans, Count, BulkResetPasswordChangeCount, GetLockedUserSince, GetInactiveUsers, GetUserNearPswdExpiration, DeleteLogin, ForgotUsername, Save
}
