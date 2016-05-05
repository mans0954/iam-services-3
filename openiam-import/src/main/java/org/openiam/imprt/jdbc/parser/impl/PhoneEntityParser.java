package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class PhoneEntityParser extends BaseParser<PhoneEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.PHONE_PHONE_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.PHONE;
    }

    @Override
    protected Class<PhoneEntity> getClazz() {
        return PhoneEntity.class;
    }

    @Override
    protected void parseToEntry(PhoneEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case PHONE_PHONE_ID:
                    entity.setPhoneId(value);
                    break;
                case PHONE_NAME:
                    entity.setName(value);
                    break;
                case PHONE_AREA_CD:
                    entity.setAreaCd(value);
                    break;
                case PHONE_COUNTRY_CD:
                    entity.setCountryCd(value);
                    break;
                case PHONE_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case PHONE_PHONE_NBR:
                    entity.setPhoneNbr(value);
                    break;
                case PHONE_PHONE_EXT:
                    entity.setPhoneExt(value);
                    break;
                case PHONE_IS_DEFAULT:
                    entity.setIsDefault(value.equals("Y") ? true : false);
                    break;
                case PHONE_ACTIVE:
                    entity.setIsActive(value.equals("Y") ? true : false);
                    break;
                case PHONE_PARENT_ID:
                    UserEntity user = new UserEntity();
                    user.setId(value);
                    entity.setParent(user);
                    break;
                case PHONE_LAST_UPDATE:
                    entity.setLastUpdate(Utils.getDate(value));
                    break;
                case PHONE_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case PHONE_TYPE_ID:
                    entity.setMetadataType(this.getMetadataType(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, PhoneEntity entity) {
        switch (column) {
            case PHONE_PHONE_ID: {
                list.add(entity.getPhoneId());
                break;
            }
            case PHONE_NAME: {
                list.add(entity.getName());
                break;
            }
            case PHONE_AREA_CD: {
                list.add(entity.getAreaCd());
                break;
            }
            case PHONE_COUNTRY_CD: {
                list.add(entity.getCountryCd());
                break;
            }
            case PHONE_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case PHONE_PHONE_NBR: {
                list.add(entity.getPhoneNbr());
                break;
            }
            case PHONE_PHONE_EXT: {
                list.add(entity.getPhoneExt());
                break;
            }
            case PHONE_IS_DEFAULT: {
                list.add(entity.getIsDefault());
                break;
            }
            case PHONE_ACTIVE: {
                list.add(entity.getIsActive());
                break;
            }
            case PHONE_PARENT_ID: {
                list.add(entity.getParent().getId());
                break;
            }
            case PHONE_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case PHONE_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case PHONE_TYPE_ID: {
                list.add(entity.getMetadataType().getId());
                break;
            }
            default:
                break;
        }

    }
}
