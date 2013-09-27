package org.openiam.dozer.converter;

import org.openiam.idm.srvc.synch.domain.SynchConfigDataMappingEntity;
import org.openiam.idm.srvc.synch.dto.SynchConfigDataMapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("synchConfigDataMappingDozerConverter")
public class SynchConfigDataMappingDozerConverter extends AbstractDozerEntityConverter<SynchConfigDataMapping, SynchConfigDataMappingEntity> {
    @Override
    public SynchConfigDataMappingEntity convertEntity(SynchConfigDataMappingEntity entity, boolean isDeep) {
        return convert(entity, isDeep, SynchConfigDataMappingEntity.class);
    }

    @Override
    public SynchConfigDataMapping convertDTO(SynchConfigDataMapping entity, boolean isDeep) {
        return convert(entity, isDeep, SynchConfigDataMapping.class);
    }

    @Override
    public SynchConfigDataMappingEntity convertToEntity(SynchConfigDataMapping entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchConfigDataMappingEntity.class);
    }

    @Override
    public SynchConfigDataMapping convertToDTO(SynchConfigDataMappingEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchConfigDataMapping.class);
    }

    @Override
    public List<SynchConfigDataMappingEntity> convertToEntityList(List<SynchConfigDataMapping> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchConfigDataMappingEntity.class);
    }

    @Override
    public List<SynchConfigDataMapping> convertToDTOList(List<SynchConfigDataMappingEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchConfigDataMapping.class);
    }

    @Override
    public Set<SynchConfigDataMappingEntity> convertToEntitySet(Set<SynchConfigDataMapping> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchConfigDataMappingEntity.class);
    }

    @Override
    public Set<SynchConfigDataMapping> convertToDTOSet(Set<SynchConfigDataMappingEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchConfigDataMapping.class);
    }
}

