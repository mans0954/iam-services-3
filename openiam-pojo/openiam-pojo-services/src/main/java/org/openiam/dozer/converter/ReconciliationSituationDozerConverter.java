package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.recon.domain.ReconciliationSituationEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.springframework.stereotype.Component;

@Component("reconSituationDozerMapper")
public class ReconciliationSituationDozerConverter
        extends
        AbstractDozerEntityConverter<ReconciliationSituation, ReconciliationSituationEntity> {

    @Override
    public ReconciliationSituationEntity convertEntity(
            ReconciliationSituationEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReconciliationSituationEntity.class);
    }

    @Override
    public ReconciliationSituation convertDTO(ReconciliationSituation entity,
            boolean isDeep) {
        return convert(entity, isDeep, ReconciliationSituation.class);
    }

    @Override
    public ReconciliationSituationEntity convertToEntity(
            ReconciliationSituation entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                ReconciliationSituationEntity.class);
    }

    @Override
    public ReconciliationSituation convertToDTO(ReconciliationSituationEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReconciliationSituation.class);
    }

    @Override
    public List<ReconciliationSituationEntity> convertToEntityList(
            List<ReconciliationSituation> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationSituationEntity.class);
    }

    @Override
    public List<ReconciliationSituation> convertToDTOList(
            List<ReconciliationSituationEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ReconciliationSituation.class);
    }

}
