package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("uriPatternMetaValueDozerConverter")
public class URIPatternMetaValueDozerConverter extends AbstractDozerEntityConverter<URIPatternMetaValue, URIPatternMetaValueEntity> {
    @Override
    public URIPatternMetaValueEntity convertEntity(URIPatternMetaValueEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, URIPatternMetaValueEntity.class);
    }

    @Override
    public URIPatternMetaValue convertDTO(URIPatternMetaValue entity, boolean isDeep) {
        return convert(entity, isDeep, URIPatternMetaValue.class);
    }

    @Override
    public URIPatternMetaValueEntity convertToEntity(URIPatternMetaValue entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, URIPatternMetaValueEntity.class);
    }

    @Override
    public URIPatternMetaValue convertToDTO(URIPatternMetaValueEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, URIPatternMetaValue.class);
    }

    @Override
    public List<URIPatternMetaValueEntity> convertToEntityList(List<URIPatternMetaValue> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMetaValueEntity.class);
    }

    @Override
    public List<URIPatternMetaValue> convertToDTOList(List<URIPatternMetaValueEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, URIPatternMetaValue.class);
    }
}
