package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.imprt.constant.ImportPropertiesKey;

import java.util.List;


public class GroupAttributeEntityParser extends BaseParser<GroupAttributeEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.T_GRP_ATTRIBUTES_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.T_GRP_ATTRIBUTES;
    }

    @Override
    protected Class<GroupAttributeEntity> getClazz() {
        return GroupAttributeEntity.class;
    }

    @Override
    protected void parseToEntry(GroupAttributeEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case T_GRP_ATTRIBUTES_GRP_ID:
                    GroupEntity gp = new GroupEntity();
                    gp.setId(value);
                    entity.setGroup(gp);
                    break;
                case T_GRP_ATTRIBUTES_NAME:
                    entity.setName(value);
                    break;
                case T_GRP_ATTRIBUTES_ATTR_VALUE:
                    entity.setValue(value);
                    break;
                case T_GRP_ATTRIBUTES_ID:
                    entity.setId(value);
                    break;
                case T_GRP_ATTRIBUTES_METADATA_ID:
                    entity.setElement(this.getMetadataElementEntity(value));
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, GroupAttributeEntity entity) {
        switch (column) {
            case COMPANY_COMPANY_ID: {
                list.add(entity.getId());
                break;
            }
            case T_COMPANY_ATTRIBUTE_COMPANY_ID:
                if (entity.getElement() != null) {
                    list.add(entity.getElement().getId());
                } else {
                    list.add(null);
                }
                break;
            case T_COMPANY_ATTRIBUTE_NAME:
                list.add(entity.getName());
                break;
            case T_COMPANY_ATTRIBUTE_VALUE:
                list.add(entity.getValue());
                break;
            case T_COMPANY_ATTRIBUTE_ID:
                list.add(entity.getId());
                break;
            default:
                break;
        }
    }
}
