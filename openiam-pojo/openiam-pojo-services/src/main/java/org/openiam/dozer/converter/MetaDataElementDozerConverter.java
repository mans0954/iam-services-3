package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.springframework.stereotype.Component;

@Component("metadataElementDozerMapper")
public class MetaDataElementDozerConverter extends
        AbstractDozerEntityConverter<MetadataElement, MetadataElementEntity> {

    @Override
    public MetadataElementEntity convertEntity(MetadataElementEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, MetadataElementEntity.class);
    }

    @Override
    public MetadataElement convertDTO(MetadataElement entity, boolean isDeep) {
        return convert(entity, isDeep, MetadataElement.class);
    }

    @Override
    public MetadataElementEntity convertToEntity(MetadataElement entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, MetadataElementEntity.class);
    }

    @Override
    public MetadataElement convertToDTO(MetadataElementEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, MetadataElement.class);
    }

    @Override
    public List<MetadataElementEntity> convertToEntityList(
            List<MetadataElement> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                MetadataElementEntity.class);
    }

    @Override
    public List<MetadataElement> convertToDTOList(
            List<MetadataElementEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, MetadataElement.class);
    }

    @Override
    public Set<MetadataElementEntity> convertToEntitySet(
            Set<MetadataElement> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                MetadataElementEntity.class);
    }

    @Override
    public Set<MetadataElement> convertToDTOSet(
            Set<MetadataElementEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, MetadataElement.class);
    }

}
