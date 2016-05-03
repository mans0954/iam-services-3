package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class LocationEntityParser extends BaseParser<LocationEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.LOCATION_LOCATION_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.LOCATION;
    }

    @Override
    protected Class<LocationEntity> getClazz() {
        return LocationEntity.class;
    }

    @Override
    protected void parseToEntry(LocationEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case LOCATION_LOCATION_ID:
                    entity.setLocationId(value);
                    break;
                case LOCATION_NAME:
                    entity.setName(value);
                    break;
                case LOCATION_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case LOCATION_COUNTRY:
                    entity.setCountry(value);
                    break;
                case LOCATION_BLDG_NUM:
                    entity.setBldgNum(value);
                    break;
                case LOCATION_STREET_DIRECTION:
                    entity.setStreetDirection(value);
                    break;
                case LOCATION_ADDRESS1:
                    entity.setAddress1(value);
                    break;
                case LOCATION_ADDRESS2:
                    entity.setAddress1(value);
                    break;
                case LOCATION_ADDRESS3:
                    entity.setAddress1(value);
                    break;
                case LOCATION_CITY:
                    entity.setCity(value);
                    break;
                case LOCATION_STATE:
                    entity.setState(value);
                    break;
                case LOCATION_POSTAL_CD:
                    entity.setPostalCd(value);
                    break;
                case LOCATION_ORGANIZATION_ID:
                    entity.setOrganizationId(value);
                    break;
                case LOCATION_INTERNAL_LOCATION_ID:
                    entity.setInternalLocationId(value);
                    break;
                case LOCATION_ACTIVE:
                    entity.setIsActive(value.equals("Y")?true:false);
                    break;
                case LOCATION_SENSITIVE_LOCATION:
                    entity.setSensitiveLocation(Integer.valueOf(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, LocationEntity entity) {
        switch (column) {
            case LOCATION_LOCATION_ID: {
                list.add(entity.getLocationId());
                break;
            }
            case LOCATION_NAME: {
                list.add(entity.getName());
                break;
            }
            case LOCATION_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case LOCATION_COUNTRY: {
                list.add(entity.getCountry());
                break;
            }
            case LOCATION_BLDG_NUM: {
                list.add(entity.getBldgNum());
                break;
            }
            case LOCATION_STREET_DIRECTION: {
                list.add(entity.getStreetDirection());
                break;
            }
            case LOCATION_ADDRESS1: {
                list.add(entity.getAddress1());
                break;
            }
            case LOCATION_ADDRESS2: {
                list.add(entity.getAddress2());
                break;
            }
            case LOCATION_ADDRESS3: {
                list.add(entity.getAddress3());
                break;
            }
            case LOCATION_CITY: {
                list.add(entity.getCity());
                break;
            }
            case LOCATION_STATE: {
                list.add(entity.getState());
                break;
            }
            case LOCATION_POSTAL_CD: {
                list.add(entity.getPostalCd());
                break;
            }
            case LOCATION_ORGANIZATION_ID: {
                list.add(entity.getOrganizationId());
                break;
            }
            case LOCATION_INTERNAL_LOCATION_ID: {
                list.add(entity.getInternalLocationId());
                break;
            }
            case LOCATION_ACTIVE: {
                list.add(entity.getIsActive() ? "Y" : "N");
                break;
            }
            case LOCATION_SENSITIVE_LOCATION: {
                list.add(entity.getSensitiveLocation());
                break;
            }
            default:
                break;
        }

    }
}
