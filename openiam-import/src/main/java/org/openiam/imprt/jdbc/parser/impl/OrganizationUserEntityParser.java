package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class OrganizationUserEntityParser extends BaseParser<OrganizationUserEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.USER_AFFILIATION_COMPANY_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.USER_AFFILIATION;
    }

    @Override
    protected Class<OrganizationUserEntity> getClazz() {
        return OrganizationUserEntity.class;
    }

    @Override
    protected void parseToEntry(OrganizationUserEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case USER_AFFILIATION_COMPANY_ID:
                    entity.setOrganization(new CompanyEntityParser().getById(value));
                    break;
                case USER_AFFILIATION_USER_ID:
                    entity.setUser(new UserEntityParser().getById(value));
                    break;
                case USER_AFFILIATION_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case USER_AFFILIATION_METADATA_TYPE_ID:
                    entity.setMetadataTypeEntity(new MetadataTypeEntityParser().getById(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, OrganizationUserEntity entity) {
        switch (column) {
            case USER_AFFILIATION_COMPANY_ID: {
                list.add(entity.getOrganization().getId());
                break;
            }
            case USER_AFFILIATION_USER_ID: {
                list.add(entity.getUser().getId());
                break;
            }
            case USER_AFFILIATION_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case USER_AFFILIATION_METADATA_TYPE_ID: {
                list.add(entity.getMetadataTypeEntity().getId());
                break;
            }
            default:
                break;
        }

    }
}
