package org.openiam.dozer.converter;

import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("synchReviewDozerConverter")
public class SynchReviewDozerConverter extends AbstractDozerEntityConverter<SynchReview, SynchReviewEntity> {

    @Override
    public SynchReviewEntity convertEntity(SynchReviewEntity entity, boolean isDeep) {
        return convert(entity, isDeep, SynchReviewEntity.class);
    }

    @Override
    public SynchReview convertDTO(SynchReview entity, boolean isDeep) {
        return convert(entity, isDeep, SynchReview.class);
    }

    @Override
    public SynchReviewEntity convertToEntity(SynchReview entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchReviewEntity.class);
    }

    @Override
    public SynchReview convertToDTO(SynchReviewEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SynchReview.class);
    }

    @Override
    public List<SynchReviewEntity> convertToEntityList(List<SynchReview> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchReviewEntity.class);
    }

    @Override
    public List<SynchReview> convertToDTOList(List<SynchReviewEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SynchReview.class);
    }

    @Override
    public Set<SynchReviewEntity> convertToEntitySet(Set<SynchReview> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchReviewEntity.class);
    }

    @Override
    public Set<SynchReview> convertToDTOSet(Set<SynchReviewEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SynchReview.class);
    }
}
