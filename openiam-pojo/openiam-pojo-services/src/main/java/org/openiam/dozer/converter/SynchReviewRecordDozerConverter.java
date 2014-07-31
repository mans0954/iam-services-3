package org.openiam.dozer.converter;

import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("synchReviewRecordDozerConverter")
public class SynchReviewRecordDozerConverter extends AbstractDozerEntityConverter<SynchReviewRecord, SynchReviewRecordEntity> {
    @Override
    public SynchReviewRecordEntity convertEntity(SynchReviewRecordEntity entity, boolean isDeep) {
        return convert(entity, isDeep, SynchReviewRecordEntity.class);
    }

    @Override
    public SynchReviewRecord convertDTO(SynchReviewRecord entity, boolean isDeep) {
        return convert(entity, isDeep, SynchReviewRecord.class);
    }

    @Override
    public SynchReviewRecordEntity convertToEntity(SynchReviewRecord entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchReviewRecordEntity.class);
    }

    @Override
    public SynchReviewRecord convertToDTO(SynchReviewRecordEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchReviewRecord.class);
    }

    @Override
    public List<SynchReviewRecordEntity> convertToEntityList(List<SynchReviewRecord> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchReviewRecordEntity.class);
    }

    @Override
    public List<SynchReviewRecord> convertToDTOList(List<SynchReviewRecordEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchReviewRecord.class);
    }

    @Override
    public Set<SynchReviewRecordEntity> convertToEntitySet(Set<SynchReviewRecord> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchReviewRecordEntity.class);
    }

    @Override
    public Set<SynchReviewRecord> convertToDTOSet(Set<SynchReviewRecordEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchReviewRecord.class);
    }
}
