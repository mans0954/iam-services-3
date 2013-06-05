package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dto.URIPattern;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("uriPatternDozerConverter")
public class URIPatternDozerConverter extends AbstractDozerEntityConverter<URIPattern, URIPatternEntity> {
    @Override
    public URIPatternEntity convertEntity(URIPatternEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, URIPatternEntity.class);
    }

    @Override
    public URIPattern convertDTO(URIPattern entity, boolean isDeep) {
        return convert(entity, isDeep, URIPattern.class);
    }

    @Override
    public URIPatternEntity convertToEntity(URIPattern entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, URIPatternEntity.class);
    }

    @Override
    public URIPattern convertToDTO(URIPatternEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, URIPattern.class);
    }

    @Override
    public List<URIPatternEntity> convertToEntityList(List<URIPattern> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternEntity.class);
    }

    @Override
    public List<URIPattern> convertToDTOList(List<URIPatternEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPattern.class);
    }
}
