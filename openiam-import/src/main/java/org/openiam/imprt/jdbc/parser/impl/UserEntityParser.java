package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;

import java.util.List;

/**
 * Created by alexander on 26/04/16.
 */
public class UserEntityParser extends BaseParser<UserEntity>  {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return null;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return null;
    }

    @Override
    protected ImportPropertiesKey[] getColumnsName() {
        return new ImportPropertiesKey[0];
    }

    @Override
    protected Class<UserEntity> getClazz() {
        return UserEntity.class;
    }

    @Override
    protected void parseToEntry(UserEntity userEntity, ImportPropertiesKey key, String value) throws Exception {

    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, UserEntity entity) {

    }
}
