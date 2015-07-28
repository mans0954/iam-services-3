package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.am.srvc.dto.OAuthToken;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alexander on 20/07/15.
 */
@Component("oauthTokenDozerConverter")
public class OAuthTokenDozerConverter extends AbstractDozerEntityConverter<OAuthToken, OAuthTokenEntity> {
    @Override
    public OAuthTokenEntity convertEntity(OAuthTokenEntity oauthTokenEntity, boolean isDeep) {
        return convert(oauthTokenEntity, isDeep, OAuthTokenEntity.class);
    }

    @Override
    public OAuthToken convertDTO(OAuthToken entity, boolean isDeep) {
        return convert(entity, isDeep, OAuthToken.class);
    }

    @Override
    public OAuthTokenEntity convertToEntity(OAuthToken entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, OAuthTokenEntity.class);
    }

    @Override
    public OAuthToken convertToDTO(OAuthTokenEntity oAuthTokenEntity, boolean isDeep) {
        return convertToCrossEntity(oAuthTokenEntity, isDeep, OAuthToken.class);
    }

    @Override
    public List<OAuthTokenEntity> convertToEntityList(List<OAuthToken> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthTokenEntity.class);
    }

    @Override
    public List<OAuthToken> convertToDTOList(List<OAuthTokenEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthToken.class);
    }
}
