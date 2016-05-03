package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class ResourceEntityParser extends BaseParser<ResourceEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.RES_RESOURCE_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.RES;
    }

    @Override
    protected Class<ResourceEntity> getClazz() {
        return ResourceEntity.class;
    }

    @Override
    protected void parseToEntry(ResourceEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case RES_RESOURCE_ID:
                    entity.setId(value);
                    break;
                case RES_RESOURCE_TYPE_ID:
                    entity.setResourceTypeId(value);
                    break;
                case RES_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case RES_NAME:
                    entity.setName(value);
                    break;
                case RES_DISPLAY_ORDER:
                    entity.setDisplayOrder(Integer.valueOf(value));
                    break;
                case RES_URL:
                    entity.setURL(value);
                    break;
                case RES_MIN_AUTH_LEVEL:
                    entity.setMinAuthLevel(value);
                    break;
                case RES_IS_PUBLIC:
                    entity.setIsPublic(value.equals("Y") ? true : false);
                    break;
                case RES_ADMIN_RESOURCE_ID:
                    entity.setAdminResourceId(value);
                    break;
                case RES_RISK:
                    entity.setRisk(value);
                    break;
                case RES_TYPE_ID:
                    entity.setTypeId(value);
                    break;
                case RES_COORELATED_NAME:
                    entity.setCoorelatedName(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, ResourceEntity entity) {
        switch (column) {
            case RES_RESOURCE_ID: {
                list.add(entity.getId());
                break;
            }
            case RES_RESOURCE_TYPE_ID: {
                list.add(entity.getResourceType().getId());
                break;
            }
            case RES_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case RES_NAME: {
                list.add(entity.getName());
                break;
            }
            case RES_DISPLAY_ORDER: {
                list.add(entity.getDisplayOrder());
                break;
            }
            case RES_URL: {
                list.add(entity.getURL());
                break;
            }
            case RES_MIN_AUTH_LEVEL: {
                list.add(entity.getMinAuthLevel());
                break;
            }
            case RES_IS_PUBLIC: {
                list.add((entity.getIsPublic()?"Y":"N"));
                break;
            }
            case RES_ADMIN_RESOURCE_ID: {
                list.add(entity.getAdminResource().getId());
                break;
            }
            case RES_RISK: {
                list.add(entity.getRisk());
                break;
            }
            case RES_TYPE_ID: {
                list.add(entity.getType().getId());
                break;
            }
            case RES_COORELATED_NAME: {
                list.add(entity.getCoorelatedName());
                break;
            }
            default:
                break;
        }

    }
}
