package org.openiam.mq.constants.api.common;

import org.openiam.mq.constants.api.OpenIAMAPI;

/**
 * Created by aduckardt on 2016-12-09.
 */
public enum EncryptionAPI implements OpenIAMAPI {
    GenerateMasterKey, MigrateData, GetCookieKey, GenerateCookieKey, EncryptData, DecryptData, InitKeyManagement
}
