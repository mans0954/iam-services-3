package org.openiam.dozer.converter;

import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("managedSystemObjectMatchDozerConverter")
public class ManagedSystemObjectMatchDozerConverter extends AbstractDozerEntityConverter<ManagedSystemObjectMatch, ManagedSystemObjectMatchEntity> {
    @Override
    public ManagedSystemObjectMatchEntity convertEntity(ManagedSystemObjectMatchEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ManagedSystemObjectMatchEntity.class);
    }

    @Override
    public ManagedSystemObjectMatch convertDTO(ManagedSystemObjectMatch entity, boolean isDeep) {
        return convert(entity, isDeep, ManagedSystemObjectMatch.class);
    }

    @Override
    public ManagedSystemObjectMatchEntity convertToEntity(ManagedSystemObjectMatch entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ManagedSystemObjectMatchEntity.class);
    }

    @Override
    public ManagedSystemObjectMatch convertToDTO(ManagedSystemObjectMatchEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ManagedSystemObjectMatch.class);
    }

    @Override
    public List<ManagedSystemObjectMatchEntity> convertToEntityList(List<ManagedSystemObjectMatch> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSystemObjectMatchEntity.class);
    }

    @Override
    public List<ManagedSystemObjectMatch> convertToDTOList(List<ManagedSystemObjectMatchEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSystemObjectMatch.class);
    }
}
