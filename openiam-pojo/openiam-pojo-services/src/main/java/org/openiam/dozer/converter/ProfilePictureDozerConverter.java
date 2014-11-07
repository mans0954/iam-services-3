package org.openiam.dozer.converter;


import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;
import org.openiam.idm.srvc.user.dto.ProfilePicture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("profilePictureDozerConverter")
public class ProfilePictureDozerConverter extends AbstractDozerEntityConverter<ProfilePicture, ProfilePictureEntity> {

    @Override
    public ProfilePictureEntity convertEntity(ProfilePictureEntity entity,
                                        boolean isDeep) {
        return convert(entity, isDeep, ProfilePictureEntity.class);
    }

    @Override
    public ProfilePicture convertDTO(ProfilePicture entity, boolean isDeep) {
        return convert(entity, isDeep, ProfilePicture.class);
    }

    @Override
    public ProfilePictureEntity convertToEntity(ProfilePicture entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ProfilePictureEntity.class);
    }

    @Override
    public ProfilePicture convertToDTO(ProfilePictureEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ProfilePicture.class);
    }

    @Override
    public List<ProfilePictureEntity> convertToEntityList(List<ProfilePicture> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ProfilePictureEntity.class);
    }

    @Override
    public List<ProfilePicture> convertToDTOList(List<ProfilePictureEntity> list,  boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ProfilePicture.class);
    }

    @Override
    public Set<ProfilePictureEntity> convertToEntitySet(Set<ProfilePicture> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ProfilePictureEntity.class);
    }

    @Override
    public Set<ProfilePicture> convertToDTOSet(Set<ProfilePictureEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ProfilePicture.class);
    }

}
