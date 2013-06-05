package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.dto.ContentProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("contentProviderDozerConverter")
public class ContentProviderDozerConverter extends AbstractDozerEntityConverter<ContentProvider, ContentProviderEntity> {
    @Override
    public ContentProviderEntity convertEntity(ContentProviderEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ContentProviderEntity.class);
    }

    @Override
    public ContentProvider convertDTO(ContentProvider entity, boolean isDeep) {
        return convert(entity, isDeep, ContentProvider.class);
    }

    @Override
    public ContentProviderEntity convertToEntity(ContentProvider entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ContentProviderEntity.class);
    }

    @Override
    public ContentProvider convertToDTO(ContentProviderEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ContentProvider.class);
    }

    @Override
    public List<ContentProviderEntity> convertToEntityList(List<ContentProvider> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ContentProviderEntity.class);
    }

    @Override
    public List<ContentProvider> convertToDTOList(List<ContentProviderEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ContentProvider.class);
    }
}
