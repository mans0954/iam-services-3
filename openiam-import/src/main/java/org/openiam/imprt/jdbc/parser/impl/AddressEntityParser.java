package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class AddressEntityParser extends BaseParser<AddressEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.ADDRESS_ADDRESS_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.ADDRESS;
    }

    @Override
    protected Class<AddressEntity> getClazz() {
        return AddressEntity.class;
    }

    @Override
    protected void parseToEntry(AddressEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case ADDRESS_ADDRESS_ID:
                    entity.setAddressId(value);
                    break;
                case ADDRESS_NAME:
                    entity.setName(value);
                    break;
                case ADDRESS_COUNTRY:
                    entity.setCountry(value);
                    break;
                case ADDRESS_BLDG_NUM:
                    entity.setBldgNumber(value);
                    break;
                case ADDRESS_STREET_DIRECTION:
                    entity.setStreetDirection(value);
                    break;
                case ADDRESS_SUITE:
                    entity.setSuite(value);
                    break;
                case ADDRESS_ADDRESS1:
                    entity.setAddress1(value);
                    break;
                case ADDRESS_ADDRESS2:
                    entity.setAddress2(value);
                    break;
                case ADDRESS_ADDRESS3:
                    entity.setAddress3(value);
                    break;
                case ADDRESS_ADDRESS4:
                    entity.setAddress4(value);
                    break;
                case ADDRESS_ADDRESS5:
                    entity.setAddress5(value);
                    break;
                case ADDRESS_ADDRESS6:
                    entity.setAddress6(value);
                    break;
                case ADDRESS_ADDRESS7:
                    entity.setAddress7(value);
                    break;
                case ADDRESS_CITY:
                    entity.setCity(value);
                    break;
                case ADDRESS_STATE:
                    entity.setState(value);
                    break;
                case ADDRESS_POSTAL_CD:
                    entity.setPostalCd(value);
                    break;
                case ADDRESS_IS_DEFAULT:
                    entity.setIsDefault(value.equals("Y") ? true : false);
                    break;
                case ADDRESS_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case ADDRESS_ACTIVE:
                    entity.setIsActive(value.equals("Y") ? true : false);
                    break;
                case ADDRESS_PARENT_ID:
                    UserEntity user = new UserEntity();
                    user.setId(value);
                    entity.setParent(user);
                    break;
                case ADDRESS_LAST_UPDATE:
                    entity.setLastUpdate(Utils.getDate(value));
                    break;
                case ADDRESS_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case ADDRESS_TYPE_ID:
                    entity.setMetadataType(this.getMetadataType(value));
                    break;
                case ADDRESS_COPY_FROM_LOCATION_ID:
                    entity.setLocationId(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, AddressEntity entity) {
        switch (column) {
            case ADDRESS_ADDRESS_ID: {
                list.add(entity.getAddressId());
                break;
            }
            case ADDRESS_NAME: {
                list.add(entity.getName());
                break;
            }
            case ADDRESS_COUNTRY: {
                list.add(entity.getCountry());
                break;
            }
            case ADDRESS_BLDG_NUM: {
                list.add(entity.getBldgNumber());
                break;
            }
            case ADDRESS_STREET_DIRECTION: {
                list.add(entity.getStreetDirection());
                break;
            }
            case ADDRESS_SUITE: {
                list.add(entity.getSuite());
                break;
            }
            case ADDRESS_ADDRESS1: {
                list.add(entity.getAddress1());
                break;
            }
            case ADDRESS_ADDRESS2: {
                list.add(entity.getAddress2());
                break;
            }
            case ADDRESS_ADDRESS3: {
                list.add(entity.getAddress3());
                break;
            }
            case ADDRESS_ADDRESS4: {
                list.add(entity.getAddress4());
                break;
            }
            case ADDRESS_ADDRESS5: {
                list.add(entity.getAddress5());
                break;
            }
            case ADDRESS_ADDRESS6: {
                list.add(entity.getAddress6());
                break;
            }
            case ADDRESS_ADDRESS7: {
                list.add(entity.getAddress7());
                break;
            }
            case ADDRESS_CITY: {
                list.add(entity.getCity());
                break;
            }
            case ADDRESS_STATE: {
                list.add(entity.getState());
                break;
            }
            case ADDRESS_POSTAL_CD: {
                list.add(entity.getPostalCd());
                break;
            }
            case ADDRESS_IS_DEFAULT: {
                list.add(entity.getIsDefault() ? "Y" : "N");
                break;
            }
            case ADDRESS_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case ADDRESS_ACTIVE: {
                list.add(entity.getIsActive() ? "Y" : "N");
                break;
            }
            case ADDRESS_PARENT_ID: {
                list.add(entity.getParent().getId());
                break;
            }
            case ADDRESS_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case ADDRESS_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case ADDRESS_TYPE_ID: {
                list.add(entity.getMetadataType().getId());
                break;
            }
            case ADDRESS_COPY_FROM_LOCATION_ID: {
                list.add(entity.getLocationId());
                break;
            }
            default:
                break;
        }

    }
}
