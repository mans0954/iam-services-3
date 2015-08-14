package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alexander on 18/07/15.
 */
@Component("oauthUserClientXrefDozerConverter")
public class OAuthUserClientXrefDozerConverter extends AbstractDozerEntityConverter<OAuthUserClientXref, OAuthUserClientXrefEntity> {
    @Override
    public OAuthUserClientXrefEntity convertEntity(OAuthUserClientXrefEntity oAuthUserClientXrefEntity, boolean isDeep) {
        return convert(oAuthUserClientXrefEntity, isDeep, OAuthUserClientXrefEntity.class);
    }

    @Override
    public OAuthUserClientXref convertDTO(OAuthUserClientXref entity, boolean isDeep) {
        return convert(entity, isDeep, OAuthUserClientXref.class);
    }

    @Override
    public OAuthUserClientXrefEntity convertToEntity(OAuthUserClientXref entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, OAuthUserClientXrefEntity.class);
    }

    @Override
    public OAuthUserClientXref convertToDTO(OAuthUserClientXrefEntity oAuthUserClientXrefEntity, boolean isDeep) {
        return convertToCrossEntity(oAuthUserClientXrefEntity, isDeep, OAuthUserClientXref.class);
    }

    @Override
    public List<OAuthUserClientXrefEntity> convertToEntityList(List<OAuthUserClientXref> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthUserClientXrefEntity.class);
    }

    @Override
    public List<OAuthUserClientXref> convertToDTOList(List<OAuthUserClientXrefEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthUserClientXref.class);
    }
}
