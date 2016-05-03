package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.imprt.constant.ImportPropertiesKey;

import java.util.List;


public class MetadataTypeEntityParser extends BaseParser<MetadataTypeEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.METADATA_TYPE_TYPE_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.METADATA_TYPE;
    }

    @Override
    protected Class<MetadataTypeEntity> getClazz() {
        return MetadataTypeEntity.class;
    }

    @Override
    protected void parseToEntry(MetadataTypeEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case METADATA_TYPE_TYPE_ID:
                    entity.setId(value);
                    break;
                case METADATA_TYPE_DESCRIPTION:
                    entity.setDescription(value);
                    break;
                case METADATA_TYPE_ACTIVE:
                    entity.setActive((value.equals("Y")?true:false));
                    break;
                case METADATA_TYPE_SYNC_MANAGED_SYS:
                    entity.setSyncManagedSys((value.equals("Y")?true:false));
                    break;
                case METADATA_TYPE_GROUPING:
                    entity.setGrouping(MetadataTypeGrouping.getByName(value));
                    break;
                case METADATA_TYPE_IS_BINARY:
                    entity.setBinary((value.equals("Y")?true:false));
                    break;
                case METADATA_TYPE_IS_SENSITIVE:
                    entity.setSensitive((value.equals("Y") ? true : false));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, MetadataTypeEntity entity) {
        switch (column) {
            case METADATA_TYPE_TYPE_ID: {
                list.add(entity.getId());
                break;
            }
            case METADATA_TYPE_DESCRIPTION: {
                list.add(entity.getDescription());
                break;
            }
            case METADATA_TYPE_ACTIVE: {
                list.add((entity.getActive())?"Y":"N");
                break;
            }
            case METADATA_TYPE_SYNC_MANAGED_SYS: {
                list.add((entity.isSyncManagedSys())?"Y":"N");
                break;
            }
            case METADATA_TYPE_GROUPING: {
                list.add(entity.getGrouping());
                break;
            }
            case METADATA_TYPE_IS_BINARY: {
                list.add((entity.isBinary())?"Y":"N");
                break;
            }
            case METADATA_TYPE_IS_SENSITIVE: {
                list.add((entity.isSensitive())?"Y":"N");
                break;
            }
            default:
                break;
        }

    }
}
