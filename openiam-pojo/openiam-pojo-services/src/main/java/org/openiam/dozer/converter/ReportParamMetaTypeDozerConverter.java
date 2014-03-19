package org.openiam.dozer.converter;

import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;
import org.openiam.idm.srvc.report.dto.ReportParamMetaTypeDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("reportParamMetaTypeDozerMapper")
public class ReportParamMetaTypeDozerConverter extends AbstractDozerEntityConverter<ReportParamMetaTypeDto, ReportParamMetaTypeEntity>{

    @Override
    public ReportParamMetaTypeEntity convertEntity(ReportParamMetaTypeEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReportParamMetaTypeEntity.class);
    }

    @Override
    public ReportParamMetaTypeDto convertDTO(ReportParamMetaTypeDto entity, boolean isDeep) {
        return convert(entity, isDeep, ReportParamMetaTypeDto.class);
    }

    @Override
    public ReportParamMetaTypeEntity convertToEntity(ReportParamMetaTypeDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportParamMetaTypeEntity.class);
    }

    @Override
    public ReportParamMetaTypeDto convertToDTO(ReportParamMetaTypeEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportParamMetaTypeDto.class);
    }

    @Override
    public List<ReportParamMetaTypeEntity> convertToEntityList(List<ReportParamMetaTypeDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportParamMetaTypeEntity.class);
    }

    @Override
    public List<ReportParamMetaTypeDto> convertToDTOList(List<ReportParamMetaTypeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportParamMetaTypeDto.class);
    }

    @Override
    public Set<ReportParamMetaTypeEntity> convertToEntitySet(Set<ReportParamMetaTypeDto> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportParamMetaTypeEntity.class);
    }

    @Override
    public Set<ReportParamMetaTypeDto> convertToDTOSet(Set<ReportParamMetaTypeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportParamMetaTypeDto.class);
    }
}
