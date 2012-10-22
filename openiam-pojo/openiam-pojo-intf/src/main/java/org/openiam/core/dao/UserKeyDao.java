package org.openiam.core.dao;

import org.openiam.core.domain.UserKey;

/**
 * Created by: Alexander Duckardt
 * Date: 08.10.12
 */
public interface UserKeyDao extends BaseDao<UserKey, String> {
    void deleteByUserId(String userId) throws Exception;
    UserKey getByUserIdKeyName(String userId, String keyName)throws Exception;
}
