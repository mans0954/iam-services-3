package org.openiam.dozer.converter;

import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("synchConfigDozerConverter")
public class SynchConfigDozerConverter extends AbstractDozerEntityConverter<SynchConfig, SynchConfigEntity> {
    @Override
    public SynchConfigEntity convertEntity(SynchConfigEntity entity, boolean isDeep) {
        return convert(entity, isDeep, SynchConfigEntity.class);
    }

    @Override
    public SynchConfig convertDTO(SynchConfig entity, boolean isDeep) {
        return convert(entity, isDeep, SynchConfig.class);
    }

    @Override
    public SynchConfigEntity convertToEntity(SynchConfig entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchConfigEntity.class);
    }

    @Override
    public SynchConfig convertToDTO(SynchConfigEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchConfig.class);
    }

    @Override
    public List<SynchConfigEntity> convertToEntityList(List<SynchConfig> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchConfigEntity.class);
    }

    @Override
    public List<SynchConfig> convertToDTOList(List<SynchConfigEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchConfig.class);
    }
}

