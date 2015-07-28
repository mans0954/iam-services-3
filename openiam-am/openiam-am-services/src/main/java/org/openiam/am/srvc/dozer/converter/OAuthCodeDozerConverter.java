package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.dto.OAuthCode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alexander on 20/07/15.
 */
@Component("oauthCodeDozerConverter")
public class OAuthCodeDozerConverter extends AbstractDozerEntityConverter<OAuthCode, OAuthCodeEntity> {
    @Override
    public OAuthCodeEntity convertEntity(OAuthCodeEntity oauthTokenEntity, boolean isDeep) {
        return convert(oauthTokenEntity, isDeep, OAuthCodeEntity.class);
    }

    @Override
    public OAuthCode convertDTO(OAuthCode entity, boolean isDeep) {
        return convert(entity, isDeep, OAuthCode.class);
    }

    @Override
    public OAuthCodeEntity convertToEntity(OAuthCode entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, OAuthCodeEntity.class);
    }

    @Override
    public OAuthCode convertToDTO(OAuthCodeEntity oAuthTokenEntity, boolean isDeep) {
        return convertToCrossEntity(oAuthTokenEntity, isDeep, OAuthCode.class);
    }

    @Override
    public List<OAuthCodeEntity> convertToEntityList(List<OAuthCode> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthCodeEntity.class);
    }

    @Override
    public List<OAuthCode> convertToDTOList(List<OAuthCodeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, OAuthCode.class);
    }
}
