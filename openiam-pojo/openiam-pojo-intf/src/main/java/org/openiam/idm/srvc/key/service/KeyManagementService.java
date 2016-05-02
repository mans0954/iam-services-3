package org.openiam.idm.srvc.key.service;

import org.openiam.core.domain.UserKey;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
public interface KeyManagementService {
    public List<UserKey> getByUserIdsKeyName(List<String> userIds, String keyName);
    public  byte[] getUserKey(String userId, String keyName) throws EncryptionException;
    public byte[] getUserKey(UserKey uk) throws EncryptionException;
    public  byte[] getSystemUserKey(String keyName) throws EncryptionException;

    public Long generateUserKeys(String userId)throws Exception;

    public Long generateUserKeys(UserEntity user) throws Exception;

    public byte[] getCookieKey()throws Exception;
    public byte[] generateCookieKey()throws Exception;
    public byte[] getCommonKey() throws Exception;
    public byte[] generateCommonKey()throws Exception;

    public void generateMasterKey() throws Exception;
    public void initKeyManagement() throws Exception;
    public void migrateData(String oldSecretKey)throws Exception;

    public String encryptData(String data)throws Exception;
    public String decryptData(String encryptedData)throws Exception;
    public String encryptData(String userId, String data)throws Exception;
    public String decryptData(String userId, String encryptedData)throws Exception;

    public String encrypt(String userId, KeyName keyName, String data)throws Exception;
    public String encrypt(byte[] key, String data)throws Exception;
    public String decrypt(String userId, KeyName keyName, String encryptedData)throws Exception;
    public String decrypt(byte[] key, String encryptedData)throws Exception;

    public void generateKeysForUserList(List<String> userIds)throws Exception;

}
