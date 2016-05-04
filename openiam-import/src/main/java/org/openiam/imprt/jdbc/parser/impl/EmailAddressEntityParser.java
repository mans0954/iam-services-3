package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class EmailAddressEntityParser extends BaseParser<EmailAddressEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.EMAIL_ADDRESS_EMAIL_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.EMAIL_ADDRESS;
    }

    @Override
    protected Class<EmailAddressEntity> getClazz() {
        return EmailAddressEntity.class;
    }

    @Override
    protected void parseToEntry(EmailAddressEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case EMAIL_ADDRESS_EMAIL_ID:
                    entity.setEmailId(value);
                    break;
                case EMAIL_ADDRESS_NAME:
                    entity.setName(value);
                    break;
                case EMAIL_ADDRESS_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case EMAIL_ADDRESS_EMAIL_ADDRESS:
                    entity.setEmailAddress(value);
                    break;
                case EMAIL_ADDRESS_IS_DEFAULT:
                    entity.setIsDefault(value.equals("Y") ? true : false);
                    break;
                case EMAIL_ADDRESS_ACTIVE:
                    entity.setIsActive(value.equals("Y") ? true : false);
                    break;
                case EMAIL_ADDRESS_PARENT_ID:
                    UserEntity user = new UserEntity();
                    user.setId(value);
                    entity.setParent(user);
                    break;
                case EMAIL_ADDRESS_LAST_UPDATE:
                    entity.setLastUpdate(Utils.getDate(value));
                    break;
                case EMAIL_ADDRESS_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case EMAIL_ADDRESS_TYPE_ID:
                    MetadataTypeEntity m = new MetadataTypeEntity();
                    m.setId(value);
                    entity.setMetadataType(m);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, EmailAddressEntity entity) {
        switch (column) {
            case EMAIL_ADDRESS_EMAIL_ID: {
                list.add(entity.getEmailId());
                break;
            }
            case EMAIL_ADDRESS_NAME: {
                list.add(entity.getName());
                break;
            }
            case EMAIL_ADDRESS_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case EMAIL_ADDRESS_EMAIL_ADDRESS: {
                list.add(entity.getEmailAddress());
                break;
            }
            case EMAIL_ADDRESS_IS_DEFAULT: {
                list.add(entity.getIsDefault() ? "Y" : "N");
                break;
            }
            case EMAIL_ADDRESS_ACTIVE: {
                list.add(entity.getIsActive() ? "Y" : "N");
                break;
            }
            case EMAIL_ADDRESS_PARENT_ID: {
                if (entity.getParent() == null) {
                    list.add(null);
                } else {
                    list.add(entity.getParent().getId());
                }
                break;
            }
            case EMAIL_ADDRESS_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case EMAIL_ADDRESS_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case EMAIL_ADDRESS_TYPE_ID: {
                if (entity.getMetadataType() == null) {
                    list.add(null);
                } else {
                    list.add(entity.getMetadataType().getId());
                }
                break;
            }
            default:
                break;
        }

    }
}
