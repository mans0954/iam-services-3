package org.openiam.dozer.converter;

import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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

    @Override
    public Set<ReportSubCriteriaParamEntity> convertToEntitySet(Set<ReportSubCriteriaParamDto> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportSubCriteriaParamEntity.class);
    }

    @Override
    public Set<ReportSubCriteriaParamDto> convertToDTOSet(Set<ReportSubCriteriaParamEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportSubCriteriaParamDto.class);
    }
}
