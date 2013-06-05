package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("uriPatternMetaDozerConverter")
public class URIPatternMetaDozerConverter extends AbstractDozerEntityConverter<URIPatternMeta, URIPatternMetaEntity> {
    @Override
    public URIPatternMetaEntity convertEntity(URIPatternMetaEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, URIPatternMetaEntity.class);
    }

    @Override
    public URIPatternMeta convertDTO(URIPatternMeta entity, boolean isDeep) {
        return convert(entity, isDeep, URIPatternMeta.class);
    }

    @Override
    public URIPatternMetaEntity convertToEntity(URIPatternMeta entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, URIPatternMetaEntity.class);
    }

    @Override
    public URIPatternMeta convertToDTO(URIPatternMetaEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, URIPatternMeta.class);
    }

    @Override
    public List<URIPatternMetaEntity> convertToEntityList(List<URIPatternMeta> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMetaEntity.class);
    }

    @Override
    public List<URIPatternMeta> convertToDTOList(List<URIPatternMetaEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMeta.class);
    }
}
