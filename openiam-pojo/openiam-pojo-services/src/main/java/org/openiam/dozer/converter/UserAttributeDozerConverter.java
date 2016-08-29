package org.openiam.dozer.converter;

import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("userAttributeDozerConverter")
public class UserAttributeDozerConverter extends AbstractDozerEntityConverter<UserAttribute, UserAttributeEntity> {
    @Override
    public UserAttributeEntity convertEntity(UserAttributeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserAttributeEntity.class);
    }

    @Override
    public UserAttribute convertDTO(UserAttribute entity, boolean isDeep) {
        return convert(entity, isDeep, UserAttribute.class);
    }

    @Override
    public UserAttributeEntity convertToEntity(UserAttribute dto, boolean isDeep) {
        UserAttributeEntity attributeEntity = convertToCrossEntity(dto, isDeep, UserAttributeEntity.class);
        if(dto.getMetadataId() == null) {
            attributeEntity.setMetadataElementId(null);
        }
        return attributeEntity;
    }

    @Override
    public UserAttribute convertToDTO(UserAttributeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, UserAttribute.class);
    }

    @Override
    public List<UserAttributeEntity> convertToEntityList(List<UserAttribute> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserAttributeEntity.class);
    }

    @Override
    public List<UserAttribute> convertToDTOList(List<UserAttributeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserAttribute.class);
    }

    @Override
    public Set<UserAttributeEntity> convertToEntitySet(Set<UserAttribute> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UserAttributeEntity.class);
    }

    @Override
    public Set<UserAttribute> convertToDTOSet(Set<UserAttributeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UserAttribute.class);
    }
}
