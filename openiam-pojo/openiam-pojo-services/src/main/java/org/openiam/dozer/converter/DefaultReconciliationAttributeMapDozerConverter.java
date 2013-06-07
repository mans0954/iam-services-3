package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.DefaultReconciliationAttributeMap;
import org.springframework.stereotype.Component;

@Component("defaultReconciliationAttributeMapDozerConverter")
public class DefaultReconciliationAttributeMapDozerConverter
        extends
        AbstractDozerEntityConverter<DefaultReconciliationAttributeMap, DefaultReconciliationAttributeMapEntity> {
    @Override
    public DefaultReconciliationAttributeMapEntity convertEntity(
            DefaultReconciliationAttributeMapEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep,
                DefaultReconciliationAttributeMapEntity.class);
    }

    @Override
    public DefaultReconciliationAttributeMap convertDTO(
            DefaultReconciliationAttributeMap entity, boolean isDeep) {
        return convert(entity, isDeep, DefaultReconciliationAttributeMap.class);
    }

    @Override
    public DefaultReconciliationAttributeMapEntity convertToEntity(
            DefaultReconciliationAttributeMap entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                DefaultReconciliationAttributeMapEntity.class);
    }

    @Override
    public DefaultReconciliationAttributeMap convertToDTO(
            DefaultReconciliationAttributeMapEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep,
                DefaultReconciliationAttributeMap.class);
    }

    @Override
    public List<DefaultReconciliationAttributeMapEntity> convertToEntityList(
            List<DefaultReconciliationAttributeMap> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                DefaultReconciliationAttributeMapEntity.class);
    }

    @Override
    public List<DefaultReconciliationAttributeMap> convertToDTOList(
            List<DefaultReconciliationAttributeMapEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                DefaultReconciliationAttributeMap.class);
    }
}
