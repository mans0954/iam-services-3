package org.openiam.dozer.converter;

import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("reportSubscriptionDozerMapper")
public class ReportSubscriptionDozerConverter extends AbstractDozerEntityConverter<ReportSubscriptionDto, ReportSubscriptionEntity>{
    @Override
    public ReportSubscriptionEntity convertEntity(ReportSubscriptionEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReportSubscriptionEntity.class);
    }

    @Override
    public ReportSubscriptionDto convertDTO(ReportSubscriptionDto entity, boolean isDeep) {
        return convert(entity, isDeep, ReportSubscriptionDto.class);
    }

    @Override
    public ReportSubscriptionEntity convertToEntity(ReportSubscriptionDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportSubscriptionEntity.class);
    }

    @Override
    public ReportSubscriptionDto convertToDTO(ReportSubscriptionEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportSubscriptionDto.class);
    }

    @Override
    public List<ReportSubscriptionEntity> convertToEntityList(List<ReportSubscriptionDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportSubscriptionEntity.class);
    }

    @Override
    public List<ReportSubscriptionDto> convertToDTOList(List<ReportSubscriptionEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportSubscriptionDto.class);
    }

}
