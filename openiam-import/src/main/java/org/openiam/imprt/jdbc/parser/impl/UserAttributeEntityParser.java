package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;

import java.util.List;


public class UserAttributeEntityParser extends BaseParser<UserAttributeEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.USER_ATTRIBUTES_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.USER_ATTRIBUTES;
    }

    @Override
    protected Class<UserAttributeEntity> getClazz() {
        return UserAttributeEntity.class;
    }

    @Override
    protected void parseToEntry(UserAttributeEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case USER_ATTRIBUTES_ID:
                    entity.setId(value);
                    break;
                case USER_ATTRIBUTES_USER_ID:
                    entity.setUserId(value);
                    break;
                case USER_ATTRIBUTES_METADATA_ID:
                    entity.setElement(new MetadataElementEntityParser().getById(value));
                    break;
                case USER_ATTRIBUTES_NAME:
                    entity.setName(value);
                    break;
                case USER_ATTRIBUTES_VALUE:
                    entity.setValue(value);
                    break;
                case USER_ATTRIBUTES_VALUE_AS_BYTE_ARRAY:
                    entity.setValueAsByteArray(value);
                    break;
                case USER_ATTRIBUTES_IS_MULTIVALUED:
                    entity.setIsMultivalued((value.equals("Y") ? true : false));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, UserAttributeEntity entity) {
        switch (column) {
            case USER_ATTRIBUTES_ID: {
                list.add(entity.getId());
                break;
            }
            case USER_ATTRIBUTES_USER_ID: {
                list.add(entity.getUserId());
                break;
            }
            case USER_ATTRIBUTES_METADATA_ID: {
                list.add(entity.getElement().getId());
                break;
            }
            case USER_ATTRIBUTES_NAME: {
                list.add(entity.getName());
                break;
            }
            case USER_ATTRIBUTES_VALUE: {
                list.add(entity.getValue());
                break;
            }
            case USER_ATTRIBUTES_VALUE_AS_BYTE_ARRAY: {
                list.add(entity.getValueAsByteArray());
                break;
            }
            case USER_ATTRIBUTES_IS_MULTIVALUED: {
                list.add((entity.getIsMultivalued()?"Y":"N");
                break;
            }
            default:
                break;
        }

    }
}
