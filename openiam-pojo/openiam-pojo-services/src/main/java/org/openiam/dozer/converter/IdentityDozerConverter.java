package org.openiam.dozer.converter;

import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("identityDozerConverter")
public class IdentityDozerConverter extends AbstractDozerEntityConverter<IdentityDto, IdentityEntity> {

    @Override
    public IdentityEntity convertEntity(IdentityEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, IdentityEntity.class);
    }

    @Override
    public IdentityDto convertDTO(IdentityDto entity, boolean isDeep) {
        return convert(entity, isDeep, IdentityDto.class);
    }

    @Override
    public IdentityEntity convertToEntity(IdentityDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, IdentityEntity.class);
    }

    @Override
    public IdentityDto convertToDTO(IdentityEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, IdentityDto.class);
    }

    @Override
    public List<IdentityEntity> convertToEntityList(List<IdentityDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, IdentityEntity.class);
    }

    @Override
    public List<IdentityDto> convertToDTOList(List<IdentityEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, IdentityDto.class);
    }

    @Override
    public Set<IdentityEntity> convertToEntitySet(Set<IdentityDto> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityEntity.class);
    }

    @Override
    public Set<IdentityDto> convertToDTOSet(Set<IdentityEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityDto.class);
    }



}
