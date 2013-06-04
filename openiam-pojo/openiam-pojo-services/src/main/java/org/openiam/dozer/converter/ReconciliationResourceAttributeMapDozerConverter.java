package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.ReconciliationResourceAttributeMap;
import org.springframework.stereotype.Component;

@Component("reconciliationResourceAttributeMapDozerConverter")
public class ReconciliationResourceAttributeMapDozerConverter
        extends
        AbstractDozerEntityConverter<ReconciliationResourceAttributeMap, ReconciliationResourceAttributeMapEntity> {
    @Override
    public ReconciliationResourceAttributeMapEntity convertEntity(
            ReconciliationResourceAttributeMapEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep,
                ReconciliationResourceAttributeMapEntity.class);
    }

    @Override
    public ReconciliationResourceAttributeMap convertDTO(
            ReconciliationResourceAttributeMap entity, boolean isDeep) {
        return convert(entity, isDeep, ReconciliationResourceAttributeMap.class);
    }

    @Override
    public ReconciliationResourceAttributeMapEntity convertToEntity(
            ReconciliationResourceAttributeMap entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                ReconciliationResourceAttributeMapEntity.class);
    }

    @Override
    public ReconciliationResourceAttributeMap convertToDTO(
            ReconciliationResourceAttributeMapEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep,
                ReconciliationResourceAttributeMap.class);
    }

    @Override
    public List<ReconciliationResourceAttributeMapEntity> convertToEntityList(
            List<ReconciliationResourceAttributeMap> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationResourceAttributeMapEntity.class);
    }

    @Override
    public List<ReconciliationResourceAttributeMap> convertToDTOList(
            List<ReconciliationResourceAttributeMapEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationResourceAttributeMap.class);
    }
}
