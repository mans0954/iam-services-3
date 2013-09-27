package org.openiam.dozer.converter;

import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("phoneDozerConverter")
public class PhoneDozerConverter  extends AbstractDozerEntityConverter<Phone, PhoneEntity> {
    @Override
    public PhoneEntity convertEntity(PhoneEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, PhoneEntity.class);
    }

    @Override
    public Phone convertDTO(Phone entity, boolean isDeep) {
        return convert(entity, isDeep, Phone.class);
    }

    @Override
    public PhoneEntity convertToEntity(Phone entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PhoneEntity.class);
    }

    @Override
    public Phone convertToDTO(PhoneEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, Phone.class);
    }

    @Override
    public List<PhoneEntity> convertToEntityList(List<Phone> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PhoneEntity.class);
    }

    @Override
    public List<Phone> convertToDTOList(List<PhoneEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Phone.class);
    }

    @Override
    public Set<PhoneEntity> convertToEntitySet(Set<Phone> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, PhoneEntity.class);
    }

    @Override
    public Set<Phone> convertToDTOSet(Set<PhoneEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Phone.class);
    }
}
