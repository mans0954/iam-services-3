package org.openiam.dozer.converter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.util.StringUtils;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt Date: 16.11.12
 */
@Component("userDozerConverter")
public class UserDozerConverter extends AbstractDozerEntityConverter<User, UserEntity> {
    @Override
    public UserEntity convertEntity(UserEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserEntity.class);
    }

    @Override
    public User convertDTO(User entity, boolean isDeep) {
        return convert(entity, isDeep, User.class);
    }

    @Override
    public UserEntity convertToEntity(User dto, boolean isDeep) {
        UserEntity userEntity = convertToCrossEntity(dto, isDeep, UserEntity.class);
        if (isDeep) {
            for (EmailAddressEntity emailAddressEntity : userEntity.getEmailAddresses()) {
                emailAddressEntity.setParent(userEntity);
            }
            for (AddressEntity addressEntity : userEntity.getAddresses()) {
                addressEntity.setParent(userEntity);
            }
            for (PhoneEntity phoneEntity : userEntity.getPhones()) {
                phoneEntity.setParent(userEntity);
            }
            for (UserNoteEntity userNoteEntity : userEntity.getUserNotes()) {
                userNoteEntity.setUser(userEntity);
            }
            for (Map.Entry<String, UserAttributeEntity> attributeEntityEntry : userEntity.getUserAttributes().entrySet()) {
                attributeEntityEntry.getValue().setUserId(userEntity.getId());
                UserAttribute userAttributeSrc = dto.getUserAttributes().get(attributeEntityEntry.getKey());
                if (StringUtils.isEmpty(userAttributeSrc.getMetadataId())) {
                    attributeEntityEntry.getValue().setElement(null);
                }
            }
        }
        return userEntity;
    }

    @Override
    public User convertToDTO(UserEntity userEntity, boolean isDeep) {
        if (userEntity == null)
            return null;
        return convertToCrossEntity(userEntity, isDeep, User.class);
    }

    @Override
    public List<UserEntity> convertToEntityList(List<User> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserEntity.class);
    }

    @Override
    public List<User> convertToDTOList(List<UserEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, User.class);
    }

    @Override
    public Set<UserEntity> convertToEntitySet(Set<User> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UserEntity.class);
    }

    @Override
    public Set<User> convertToDTOSet(Set<UserEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, User.class);
    }
}
