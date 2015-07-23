package org.openiam.dozer.converter;

import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("mngSysPolicyDozerConverter")
public class MngSysPolicyDozerConverter extends AbstractDozerEntityConverter<MngSysPolicyDto, MngSysPolicyEntity> {

    @Override
    public MngSysPolicyEntity convertEntity(MngSysPolicyEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, MngSysPolicyEntity.class);
    }

    @Override
    public MngSysPolicyDto convertDTO(MngSysPolicyDto entity, boolean isDeep) {
        return convert(entity, isDeep, MngSysPolicyDto.class);
    }

    @Override
    public MngSysPolicyEntity convertToEntity(MngSysPolicyDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, MngSysPolicyEntity.class);
    }

    @Override
    public MngSysPolicyDto convertToDTO(MngSysPolicyEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, MngSysPolicyDto.class);
    }

    @Override
    public List<MngSysPolicyEntity> convertToEntityList(List<MngSysPolicyDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, MngSysPolicyEntity.class);
    }

    @Override
    public List<MngSysPolicyDto> convertToDTOList(List<MngSysPolicyEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, MngSysPolicyDto.class);
    }

    @Override
    public Set<MngSysPolicyEntity> convertToEntitySet(Set<MngSysPolicyDto> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, MngSysPolicyEntity.class);
    }

    @Override
    public Set<MngSysPolicyDto> convertToDTOSet(Set<MngSysPolicyEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, MngSysPolicyDto.class);
    }
}
