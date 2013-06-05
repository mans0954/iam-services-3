package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("contentProviderServerDoserConverter")
public class ContentProviderServerDoserConverter extends AbstractDozerEntityConverter<ContentProviderServer, ContentProviderServerEntity> {
    @Override
    public ContentProviderServerEntity convertEntity(ContentProviderServerEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ContentProviderServerEntity.class);
    }

    @Override
    public ContentProviderServer convertDTO(ContentProviderServer entity, boolean isDeep) {
        return convert(entity, isDeep, ContentProviderServer.class);
    }

    @Override
    public ContentProviderServerEntity convertToEntity(ContentProviderServer entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ContentProviderServerEntity.class);
    }

    @Override
    public ContentProviderServer convertToDTO(ContentProviderServerEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ContentProviderServer.class);
    }

    @Override
    public List<ContentProviderServerEntity> convertToEntityList(List<ContentProviderServer> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ContentProviderServerEntity.class);
    }

    @Override
    public List<ContentProviderServer> convertToDTOList(List<ContentProviderServerEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ContentProviderServer.class);
    }
}
