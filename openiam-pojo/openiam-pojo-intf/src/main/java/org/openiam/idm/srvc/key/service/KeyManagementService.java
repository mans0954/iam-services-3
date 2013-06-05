package org.openiam.idm.srvc.key.service;

import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
public interface KeyManagementService {
    public  byte[] getUserKey(String userId, String keyName) throws EncryptionException;

    public Long generateUserKeys(String userId)throws Exception;

    public Long generateUserKeys(UserEntity user) throws Exception;

    public byte[] getCookieKey()throws Exception;
    public byte[] generateCookieKey()throws Exception;

    public void generateMasterKey() throws Exception;
    public void migrateData(String oldSecretKey)throws Exception;
}
