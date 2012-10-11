package org.openiam.core.dao;

import org.openiam.core.domain.UserKey;
import org.springframework.stereotype.Repository;

/**
 * Created by: Alexander Duckardt
 * Date: 08.10.12
 */
@Repository
public class UserKeyImpl extends BaseDaoImpl<UserKey, String> implements UserKeyDao  {
    @Override
    protected String getPKfieldName() {
        return "userKeyId";
    }
}
