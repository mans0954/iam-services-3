package org.openiam.idm.srvc.key.service;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
public interface KeyManagementService {
    String getUserKey(String userId, String keyName) throws Exception;

    Long refreshKeys()throws Exception;;

    Long refreshUserKey(String userId)throws Exception;;

    void generateUserKeys(String userId)throws Exception;;
}
