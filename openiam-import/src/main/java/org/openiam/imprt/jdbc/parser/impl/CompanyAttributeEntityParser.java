package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class CompanyAttributeEntityParser extends BaseParser<OrganizationAttributeEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.COMPANY_COMPANY_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.COMPANY;
    }

    @Override
    protected Class<OrganizationAttributeEntity> getClazz() {
        return OrganizationAttributeEntity.class;
    }

    @Override
    protected void parseToEntry(OrganizationAttributeEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case COMPANY_ATTRIBUTE_COMPANY_ID:
                    OrganizationEntity organizationEntity = new OrganizationEntity();
                    organizationEntity.setId(value);
                    entity.setOrganization(organizationEntity);
                    break;
                case COMPANY_ATTRIBUTE_NAME:
                    entity.setName(value);
                    break;
                case COMPANY_ATTRIBUTE_VALUE:
                    entity.setValue(value);
                    break;
                case COMPANY_ATTRIBUTE_ID:
                    entity.setId(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, OrganizationAttributeEntity entity) {
        switch (column) {
            case COMPANY_COMPANY_ID: {
                list.add(entity.getId());
                break;
            }
            case COMPANY_ATTRIBUTE_COMPANY_ID:
                if (entity.getOrganization() != null) {
                    list.add(entity.getOrganization().getId());
                }
                break;
            case COMPANY_ATTRIBUTE_NAME:
                list.add(entity.getName());
                break;
            case COMPANY_ATTRIBUTE_VALUE:
                list.add(entity.getValue());
                break;
            case COMPANY_ATTRIBUTE_ID:
                list.add(entity.getId());
                break;
            default:
                break;
        }
    }
}
