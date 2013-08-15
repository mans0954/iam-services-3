package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.springframework.stereotype.Component;

@Component("reconConfigDozerMapper")
public class ReconciliationConfigDozerConverter
        extends
        AbstractDozerEntityConverter<ReconciliationConfig, ReconciliationConfigEntity> {

    @Override
    public ReconciliationConfigEntity convertEntity(
            ReconciliationConfigEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReconciliationConfigEntity.class);
    }

    @Override
    public ReconciliationConfig convertDTO(ReconciliationConfig entity,
            boolean isDeep) {
        return convert(entity, isDeep, ReconciliationConfig.class);
    }

    @Override
    public ReconciliationConfigEntity convertToEntity(
            ReconciliationConfig entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                ReconciliationConfigEntity.class);
    }

    @Override
    public ReconciliationConfig convertToDTO(ReconciliationConfigEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReconciliationConfig.class);
    }

    @Override
    public List<ReconciliationConfigEntity> convertToEntityList(
            List<ReconciliationConfig> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationConfigEntity.class);
    }

    @Override
    public List<ReconciliationConfig> convertToDTOList(
            List<ReconciliationConfigEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationConfig.class);
    }

}
