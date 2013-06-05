package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.URIPatternMetaTypeEntity;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("uriPatternMetaTypeDozerConverter")
public class URIPatternMetaTypeDozerConverter extends AbstractDozerEntityConverter<URIPatternMetaType, URIPatternMetaTypeEntity> {
    @Override
    public URIPatternMetaTypeEntity convertEntity(URIPatternMetaTypeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, URIPatternMetaTypeEntity.class);
    }

    @Override
    public URIPatternMetaType convertDTO(URIPatternMetaType entity, boolean isDeep) {
        return convert(entity, isDeep, URIPatternMetaType.class);
    }

    @Override
    public URIPatternMetaTypeEntity convertToEntity(URIPatternMetaType entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, URIPatternMetaTypeEntity.class);
    }

    @Override
    public URIPatternMetaType convertToDTO(URIPatternMetaTypeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, URIPatternMetaType.class);
    }

    @Override
    public List<URIPatternMetaTypeEntity> convertToEntityList(List<URIPatternMetaType> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMetaTypeEntity.class);
    }

    @Override
    public List<URIPatternMetaType> convertToDTOList(List<URIPatternMetaTypeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMetaType.class);
    }
}
