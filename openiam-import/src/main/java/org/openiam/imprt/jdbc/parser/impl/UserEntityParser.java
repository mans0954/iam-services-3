package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 26/04/16.
 */
public class UserEntityParser extends BaseParser<UserEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.USERS_USER_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.USERS;
    }

    @Override
    protected Class<UserEntity> getClazz() {
        return UserEntity.class;
    }

    @Override
    protected void parseToEntry(UserEntity userEntity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case USERS_USER_ID:
                    userEntity.setId(value);
                    break;
                case USERS_FIRST_NAME:
                    userEntity.setFirstName(value);
                case USERS_LAST_NAME:
                    userEntity.setLastName(value);
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, UserEntity entity) {

    }
}
