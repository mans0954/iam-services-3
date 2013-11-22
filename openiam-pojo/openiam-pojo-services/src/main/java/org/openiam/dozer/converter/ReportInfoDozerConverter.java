package org.openiam.dozer.converter;

import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("reportInfoDozerMapper")
public class ReportInfoDozerConverter extends AbstractDozerEntityConverter<ReportInfoDto, ReportInfoEntity>{
    @Override
    public ReportInfoEntity convertEntity(ReportInfoEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ReportInfoEntity.class);
    }

    @Override
    public ReportInfoDto convertDTO(ReportInfoDto entity, boolean isDeep) {
        return convert(entity, isDeep, ReportInfoDto.class);
    }

    @Override
    public ReportInfoEntity convertToEntity(ReportInfoDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportInfoEntity.class);
    }

    @Override
    public ReportInfoDto convertToDTO(ReportInfoEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ReportInfoDto.class);
    }

    @Override
    public List<ReportInfoEntity> convertToEntityList(List<ReportInfoDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportInfoEntity.class);
    }

    @Override
    public List<ReportInfoDto> convertToDTOList(List<ReportInfoEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ReportInfoDto.class);
    }

    @Override
    public Set<ReportInfoEntity> convertToEntitySet(Set<ReportInfoDto> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportInfoEntity.class);
    }

    @Override
    public Set<ReportInfoDto> convertToDTOSet(Set<ReportInfoEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ReportInfoDto.class);
    }
}
