package org.openiam.dozer.converter;

import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("emailAddressDozerConverter")
public class EmailAddressDozerConverter extends AbstractDozerEntityConverter<EmailAddress, EmailAddressEntity> {
    @Override
    public EmailAddressEntity convertEntity(EmailAddressEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, EmailAddressEntity.class);
    }

    @Override
    public EmailAddress convertDTO(EmailAddress entity, boolean isDeep) {
        return convert(entity, isDeep, EmailAddress.class);
    }

    @Override
    public EmailAddressEntity convertToEntity(EmailAddress entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, EmailAddressEntity.class);
    }

    @Override
    public EmailAddress convertToDTO(EmailAddressEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, EmailAddress.class);
    }

    @Override
    public List<EmailAddressEntity> convertToEntityList(List<EmailAddress> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, EmailAddressEntity.class);
    }

    @Override
    public List<EmailAddress> convertToDTOList(List<EmailAddressEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, EmailAddress.class);
    }
}
