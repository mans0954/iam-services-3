package org.openiam.dozer.converter;

import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("reportSubCriteriaParamDozerMapper")
public class ReportSubCriteriaParamDozerConverter extends AbstractDozerEntityConverter<ReportSubCriteriaParamDto, ReportSubCriteriaParamEntity>{

    @Override
    public ReportSubCriteriaParamEntity convertEntity(ReportSubCriteriaParamEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReportSubCriteriaParamEntity.class);
    }

    @Override
    public ReportSubCriteriaParamDto convertDTO(ReportSubCriteriaParamDto entity, boolean isDeep) {
        return convert(entity, isDeep, ReportSubCriteriaParamDto.class);
    }

    @Override
    public ReportSubCriteriaParamEntity convertToEntity(ReportSubCriteriaParamDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportSubCriteriaParamEntity.class);
    }

    @Override
    public ReportSubCriteriaParamDto convertToDTO(ReportSubCriteriaParamEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportSubCriteriaParamDto.class);
    }

    @Override
    public List<ReportSubCriteriaParamEntity> convertToEntityList(List<ReportSubCriteriaParamDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportSubCriteriaParamEntity.class);
    }

    @Override
    public List<ReportSubCriteriaParamDto> convertToDTOList(List<ReportSubCriteriaParamEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportSubCriteriaParamDto.class);
    }
}
