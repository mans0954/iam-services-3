package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.springframework.stereotype.Component;

@Component("metadataTypeDozerMapper")
public class MetaDataTypeDozerConverter extends
        AbstractDozerEntityConverter<MetadataType, MetadataTypeEntity> {

    @Override
    public MetadataTypeEntity convertEntity(MetadataTypeEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, MetadataTypeEntity.class);
    }

    @Override
    public MetadataType convertDTO(MetadataType entity, boolean isDeep) {
        return convert(entity, isDeep, MetadataType.class);
    }

    @Override
    public MetadataTypeEntity convertToEntity(MetadataType entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, MetadataTypeEntity.class);
    }

    @Override
    public MetadataType convertToDTO(MetadataTypeEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, MetadataType.class);
    }

    @Override
    public List<MetadataTypeEntity> convertToEntityList(
            List<MetadataType> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, MetadataTypeEntity.class);
    }

    @Override
    public List<MetadataType> convertToDTOList(List<MetadataTypeEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, MetadataType.class);
    }

}
