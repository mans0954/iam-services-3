package org.openiam.dozer.converter;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("managedSysDozerConverter")
public class ManagedSysDozerConverter extends AbstractDozerEntityConverter<ManagedSys, ManagedSysEntity> {
    @Override
    public ManagedSysEntity convertEntity(ManagedSysEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ManagedSysEntity.class);
    }

    @Override
    public ManagedSys convertDTO(ManagedSys entity, boolean isDeep) {
        return convert(entity, isDeep, ManagedSys.class);
    }

    @Override
    public ManagedSysEntity convertToEntity(ManagedSys entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ManagedSysEntity.class);
    }

    @Override
    public ManagedSys convertToDTO(ManagedSysEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ManagedSys.class);
    }

    @Override
    public List<ManagedSysEntity> convertToEntityList(List<ManagedSys> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSysEntity.class);
    }

    @Override
    public List<ManagedSys> convertToDTOList(List<ManagedSysEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSys.class);
    }
}
