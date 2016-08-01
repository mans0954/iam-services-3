package org.openiam.util;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Created by: Alexander Duckardt
 * Date: 9/15/14.
 */
public class AttributeUtil {

    public static UserAttributeEntity buildUserAttribute(UserEntity user, MetadataElementEntity metadataElementEntity){
        UserAttributeEntity attribute = new UserAttributeEntity();
        attribute.setUser(user);

        if(metadataElementEntity!=null){
            attribute.setMetadataElementId(metadataElementEntity.getId());
            attribute.setName(metadataElementEntity.getAttributeName());
            attribute.setValue(StringUtils.isNotBlank(metadataElementEntity.getStaticDefaultValue()) ? metadataElementEntity.getStaticDefaultValue():null);
        }
        return attribute;
    }


    public static RoleAttributeEntity buildRoleAttribute(RoleEntity role, MetadataElementEntity metadataElementEntity){
        RoleAttributeEntity attribute = new RoleAttributeEntity();
        attribute.setRole(role);
        attribute.setMetadataElementId(metadataElementEntity.getId());
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }

    public static GroupAttributeEntity buildGroupAttribute(GroupEntity group, MetadataElementEntity metadataElementEntity) {
        GroupAttributeEntity attribute = new GroupAttributeEntity();
        attribute.setGroup(group);
        attribute.setMetadataElementId(metadataElementEntity.getId());
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }
    public static OrganizationAttributeEntity buildOrgAttribute(OrganizationEntity org,
                                                          MetadataElementEntity metadataElementEntity) {
        OrganizationAttributeEntity attribute = new OrganizationAttributeEntity();
        attribute.setOrganization(org);
        attribute.setMetadataElementId(metadataElementEntity.getId());
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }
    public static ResourcePropEntity buildResAttribute(ResourceEntity res, MetadataElementEntity metadataElementEntity) {
        ResourcePropEntity attribute = new ResourcePropEntity();
        attribute.setResource(res);
        attribute.setMetadataElementId(metadataElementEntity.getId());
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }
}
