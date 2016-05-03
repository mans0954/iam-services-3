package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class OrganizationTypeEntityParser extends BaseParser<OrganizationTypeEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.ORGANIZATION_TYPE_ORG_TYPE_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.ORGANIZATION_TYPE;
    }

    @Override
    protected Class<OrganizationTypeEntity> getClazz() {
        return OrganizationTypeEntity.class;
    }

    @Override
    protected void parseToEntry(OrganizationTypeEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case ORGANIZATION_TYPE_ORG_TYPE_ID:
                    entity.setId(value);
                    break;
                case ORGANIZATION_TYPE_NAME:
                    entity.setName(value);
                    break;
                case ORGANIZATION_TYPE_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, OrganizationTypeEntity entity) {
        switch (column) {
            case ORGANIZATION_TYPE_ORG_TYPE_ID: {
                list.add(entity.getId());
                break;
            }
            case ORGANIZATION_TYPE_NAME: {
                list.add(entity.getName());
                break;
            }
            case ORGANIZATION_TYPE_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            default:
                break;
        }
    }
}
