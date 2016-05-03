package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class CompanyEntityParser extends BaseParser<OrganizationEntity> {
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
    protected Class<OrganizationEntity> getClazz() {
        return OrganizationEntity.class;
    }

    @Override
    protected void parseToEntry(OrganizationEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case COMPANY_COMPANY_ID:
                    entity.setId(value);
                    break;
                case COMPANY_COMPANY_NAME:
                    entity.setName(value);
                    break;
                case COMPANY_LST_UPDATE:
                    entity.setLstUpdate(Utils.getDate(value));
                    break;
                case COMPANY_LST_UPDATED_BY:
                    entity.setLstUpdatedBy(value);
                    break;
                case COMPANY_PARENT_ID:
                    entity.setParentOrganizationsId(value);
                    break;
                case COMPANY_STATUS:
                    entity.setStatus(value);
                    break;
                case COMPANY_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case COMPANY_CREATED_BY:
                    entity.setCreatedBy(value);
                    break;
                case COMPANY_ALIAS:
                    entity.setAlias(value);
                    break;
                case COMPANY_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case COMPANY_DOMAIN_NAME:
                    entity.setDomainName(value);
                    break;
                case COMPANY_LDAP_STR:
                    entity.setLdapStr(value);
                    break;
                case COMPANY_CLASSIFICATION:
                    entity.setClassification(value);
                    break;
                case COMPANY_INTERNAL_COMPANY_ID:
                    entity.setInternalOrgId(value);
                    break;
                case COMPANY_ABBREVIATION:
                    entity.setAbbreviation(value);
                    break;
                case COMPANY_SYMBOL:
                    entity.setSymbol(value);
                    break;
                case COMPANY_ORG_TYPE_ID:
                    entity.setOrganizationTypeId(value);
                    break;
                case COMPANY_ADMIN_RESOURCE_ID:
                    entity.setAdminResourceId(value);
                    break;
                case COMPANY_IS_SELECTABLE:
                    entity.setSelectable(value.equals("Y")?true:false);
                    break;
                case COMPANY_TYPE_ID:
                    entity.setType(new MetadataTypeEntityParser().getById(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, OrganizationEntity entity) {
        switch (column) {
            case COMPANY_COMPANY_ID: {
                list.add(entity.getId());
                break;
            }
            case COMPANY_COMPANY_NAME: {
                list.add(entity.getName());
                break;
            }
            case COMPANY_LST_UPDATE: {
                list.add(entity.getLstUpdate());
                break;
            }
            case COMPANY_LST_UPDATED_BY: {
                list.add(entity.getLstUpdatedBy());
                break;
            }
            case COMPANY_PARENT_ID: {
                list.add(entity.getParentOrganizationsId());
                break;
            }
            case COMPANY_STATUS: {
                list.add(entity.getStatus());
                break;
            }
            case COMPANY_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case COMPANY_CREATED_BY: {
                list.add(entity.getCreatedBy());
                break;
            }
            case COMPANY_ALIAS: {
                list.add(entity.getAlias());
                break;
            }
            case COMPANY_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case COMPANY_DOMAIN_NAME: {
                list.add(entity.getDomainName());
                break;
            }
            case COMPANY_LDAP_STR: {
                list.add(entity.getLdapStr());
                break;
            }
            case COMPANY_CLASSIFICATION: {
                list.add(entity.getClassification());
                break;
            }
            case COMPANY_INTERNAL_COMPANY_ID: {
                list.add(entity.getInternalOrgId());
                break;
            }
            case COMPANY_ABBREVIATION: {
                list.add(entity.getAbbreviation());
                break;
            }
            case COMPANY_SYMBOL: {
                list.add(entity.getSymbol());
                break;
            }
            case COMPANY_ORG_TYPE_ID: {
                list.add(entity.getOrganizationType().getId());
                break;
            }
            case COMPANY_ADMIN_RESOURCE_ID: {
                list.add(entity.getAdminResource().getId());
                break;
            }
            case COMPANY_IS_SELECTABLE: {
                list.add(entity.isSelectable());
                break;
            }
            case COMPANY_TYPE_ID: {
                list.add(entity.getType().getId());
                break;
            }
            default:
                break;
        }
    }
}
