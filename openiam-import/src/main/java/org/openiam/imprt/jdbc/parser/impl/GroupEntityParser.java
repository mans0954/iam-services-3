package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.Resource;
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
                    ManagedSysEntity managedSysEntity = new ManagedSysEntity();
                    managedSysEntity.setId(value);
                    entity.setManagedSystem(managedSysEntity);
                    break;
                case GRP_ADMIN_RESOURCE_ID:
                    ResourceEntity res = new ResourceEntity();
                    res.setId(value);
                    entity.setAdminResource(res);
                    break;
                case GRP_TYPE_ID: {
                    entity.setType(getMetadataType(value));
                    break;
                }
                case GRP_GRP_CLASSIFICATION: {
                    entity.setClassification(getMetadataType(value));
                    break;
                }
                case GRP_AD_GRP_TYPE: {
                    entity.setAdGroupType(getMetadataType(value));
                    break;
                }
                case GRP_AD_GRP_SCOPE:
                    entity.setAdGroupScope(getMetadataType(value));
                    break;
                case GRP_GRP_RISK:
                    entity.setRisk(getMetadataType(value));
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

    public List<GroupEntity> getGroupsWithDN() throws Exception{
        return this.getGroupsInFormatWithDN();
    }

    private String getMetadataTypeValue(MetadataTypeEntity mt) {
        return mt == null ? null : mt.getId();
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
                if (entity.getManagedSystem() != null) {
                    list.add(entity.getManagedSystem().getId());
                } else
                    list.add(null);
                break;
            }
            case GRP_ADMIN_RESOURCE_ID: {
                if (entity.getAdminResource() != null) {
                    list.add(entity.getAdminResource().getId());
                } else {
                    list.add(null);
                }
                break;
            }
            case GRP_TYPE_ID: {
                list.add(getMetadataTypeValue(entity.getType()));
                break;
            }
            case GRP_GRP_CLASSIFICATION: {
                list.add(getMetadataTypeValue(entity.getClassification()));
                break;
            }
            case GRP_AD_GRP_TYPE: {
                list.add(getMetadataTypeValue(entity.getAdGroupType()));
                break;
            }
            case GRP_AD_GRP_SCOPE: {
                list.add(getMetadataTypeValue(entity.getAdGroupScope()));
                break;
            }
            case GRP_GRP_RISK: {
                list.add(getMetadataTypeValue(entity.getRisk()));
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
