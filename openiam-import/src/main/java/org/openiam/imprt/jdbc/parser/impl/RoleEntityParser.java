package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class RoleEntityParser extends BaseParser<RoleEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.ROLE_ROLE_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.ROLE;
    }

    @Override
    protected Class<RoleEntity> getClazz() {
        return RoleEntity.class;
    }

    @Override
    protected void parseToEntry(RoleEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case ROLE_ROLE_NAME:
                    entity.setName(value);
                    break;
                case ROLE_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case ROLE_CREATED_BY:
                    entity.setCreatedBy(value);
                    break;
                case ROLE_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case ROLE_STATUS:
                    entity.setStatus(value);
                    break;
                case ROLE_ROLE_ID:
                    entity.setId(value);
                    break;
//                case ROLE_MANAGED_SYS_ID:
//                    entity.setManagedSystem(new ManagedSysEntity());
//                    break;
//                case ROLE_ADMIN_RESOURCE_ID:
//                    entity.setAdminResourceId(value);
//                    break;
                case ROLE_TYPE_ID:
                    entity.setType(new MetadataTypeEntityParser().getById(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, RoleEntity entity) {
        switch (column) {
            case ROLE_ROLE_NAME: {
                list.add(entity.getName());
                break;
            }
            case ROLE_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case ROLE_CREATED_BY: {
                list.add(entity.getCreatedBy());
                break;
            }
            case ROLE_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case ROLE_STATUS: {
                list.add(entity.getStatus());
                break;
            }
            case ROLE_ROLE_ID: {
                list.add(entity.getId());
                break;
            }
            case ROLE_MANAGED_SYS_ID: {
                list.add(entity.getManagedSystem().getId());
                break;
            }
            case ROLE_ADMIN_RESOURCE_ID: {
                list.add(entity.getAdminResource().getId());
                break;
            }
            case ROLE_TYPE_ID: {
                list.add(entity.getType().getId());
                break;
            }
            default:
                break;
        }

    }
}
