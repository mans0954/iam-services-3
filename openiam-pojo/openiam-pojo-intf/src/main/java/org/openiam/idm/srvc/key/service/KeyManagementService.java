package org.openiam.idm.srvc.key.service;

import java.util.List;
import java.util.Set;

import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
public interface KeyManagementService {
    byte[] getUserKey(String userId, String keyName) throws EncryptionException;
    //public byte[] getUserKey(UserKey uk) throws EncryptionException;
    byte[] getSystemUserKey(String keyName) throws EncryptionException;

    Set<UserKey> generateUserKeys(UserEntity user) throws Exception;

    byte[] getCookieKey()throws Exception;
    byte[] generateCookieKey()throws Exception;
    byte[] getCommonKey() throws Exception;
    byte[] generateCommonKey()throws Exception;

    void generateMasterKey() throws Exception;
    void initKeyManagement() throws Exception;
    void migrateData(String oldSecretKey)throws Exception;

    String encryptData(String data)throws Exception;
    String decryptData(String encryptedData)throws Exception;
    String encryptData(String userId, String data)throws Exception;
    String decryptData(String userId, String encryptedData)throws Exception;

    String encrypt(String userId, KeyName keyName, String data)throws Exception;
    String encrypt(byte[] key, String data)throws Exception;
    String decrypt(String userId, KeyName keyName, String encryptedData)throws Exception;
    String decrypt(byte[] key, String encryptedData)throws Exception;

}
