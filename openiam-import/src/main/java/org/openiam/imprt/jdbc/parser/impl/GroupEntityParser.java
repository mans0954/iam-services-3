package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class GroupEntityParser extends BaseParser<GroupEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.GRP_GRP_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.GRP;
    }

    @Override
    protected Class<GroupEntity> getClazz() {
        return GroupEntity.class;
    }

    @Override
    protected void parseToEntry(GroupEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case GRP_GRP_ID:
                    entity.setId(value);
                    break;
                case GRP_GRP_NAME:
                    entity.setName(value);
                    break;
                case GRP_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case GRP_CREATED_BY:
                    entity.setCreatedBy(value);
                    break;
                case GRP_GROUP_DESC:
                    entity.setDescription(value);
                    break;
                case GRP_STATUS:
                    entity.setStatus(value);
                    break;
                case GRP_LAST_UPDATE:
                    entity.setLastUpdate(Utils.getDate(value));
                    break;
                case GRP_LAST_UPDATED_BY:
                    entity.setLastUpdatedBy(value);
                    break;
                case GRP_MANAGED_SYS_ID:
                    entity.setManagedSystemId(value);
                    break;
                case GRP_ADMIN_RESOURCE_ID:
                    entity.setAdminResource(new ResourceEntityParser().getById(value));
                    break;
                case GRP_TYPE_ID:
                    entity.setType(new MetadataTypeEntityParser().getById(value));
                    break;
                case GRP_GRP_CLASSIFICATION:
                    entity.setClassification(new MetadataTypeEntityParser().getById(value));
                    break;
                case GRP_AD_GRP_TYPE:
                    entity.setAdGroupType(new MetadataTypeEntityParser().getById(value));
                    break;
                case GRP_AD_GRP_SCOPE:
                    entity.setAdGroupScope(new MetadataTypeEntityParser().getById(value));
                    break;
                case GRP_GRP_RISK:
                    entity.setRisk(new MetadataTypeEntityParser().getById(value));
                    break;
                case GRP_MAX_USER_NUMBER:
                    entity.setMaxUserNumber(Integer.valueOf(value));
                    break;
                case GRP_MEMBERSHIP_DURATION_SECONDS:
                    entity.setMembershipDuration(Long.valueOf(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, GroupEntity entity) {
        switch (column) {
            case GRP_GRP_ID: {
                list.add(entity.getId());
                break;
            }
            case GRP_GRP_NAME: {
                list.add(entity.getName());
                break;
            }
            case GRP_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case GRP_CREATED_BY: {
                list.add(entity.getCreatedBy());
                break;
            }
            case GRP_GROUP_DESC: {
                list.add(entity.getDescription());
                break;
            }
            case GRP_STATUS: {
                list.add(entity.getStatus());
                break;
            }
            case GRP_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case GRP_LAST_UPDATED_BY: {
                list.add(entity.getLastUpdatedBy());
                break;
            }
            case GRP_MANAGED_SYS_ID: {
                list.add(entity.getManagedSystem().getId());
                break;
            }
            case GRP_ADMIN_RESOURCE_ID: {
                list.add(entity.getAdminResource().getId());
                break;
            }
            case GRP_TYPE_ID: {
                list.add(entity.getType().getId());
                break;
            }
            case GRP_GRP_CLASSIFICATION: {
                list.add(entity.getClassification());
                break;
            }
            case GRP_AD_GRP_TYPE: {
                list.add(entity.getAdGroupType());
                break;
            }
            case GRP_AD_GRP_SCOPE: {
                list.add(entity.getAdGroupScope());
                break;
            }
            case GRP_GRP_RISK: {
                list.add(entity.getRisk());
                break;
            }
            case GRP_MAX_USER_NUMBER: {
                list.add(entity.getMaxUserNumber());
                break;
            }
            case GRP_MEMBERSHIP_DURATION_SECONDS: {
                list.add(entity.getMembershipDuration());
                break;
            }
            default:
                break;
        }

    }
}
