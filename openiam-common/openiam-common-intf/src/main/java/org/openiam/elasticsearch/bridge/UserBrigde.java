package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Created by: Alexander Duckardt
 * Date: 9/17/14.
 */
public class UserBrigde implements ElasticsearchBrigde {
    @Override
    public String objectToString(Object object) {
        String retVal = null;
        if(object instanceof UserEntity) {
            retVal = ((UserEntity)object).getId();
        }
        return retVal;
    }

    @Override
    public Object stringToObject(String stringValue) {
        final UserEntity entity = new UserEntity();
        entity.setId(stringValue);
        return entity;
    }
}
