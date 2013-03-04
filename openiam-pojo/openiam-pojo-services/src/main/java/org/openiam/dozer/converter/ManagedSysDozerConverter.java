package org.openiam.dozer.converter;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("managedSysDozerConverter")
public class ManagedSysDozerConverter extends AbstractDozerEntityConverter<ManagedSysDto, ManagedSysEntity> {
    @Override
    public ManagedSysEntity convertEntity(ManagedSysEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ManagedSysEntity.class);
    }

    @Override
    public ManagedSysDto convertDTO(ManagedSysDto entity, boolean isDeep) {
        return convert(entity, isDeep, ManagedSysDto.class);
    }

    @Override
    public ManagedSysEntity convertToEntity(ManagedSysDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ManagedSysEntity.class);
    }

    @Override
    public ManagedSysDto convertToDTO(ManagedSysEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ManagedSysDto.class);
    }

    @Override
    public List<ManagedSysEntity> convertToEntityList(List<ManagedSysDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSysEntity.class);
    }

    @Override
    public List<ManagedSysDto> convertToDTOList(List<ManagedSysEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSysDto.class);
    }
}
