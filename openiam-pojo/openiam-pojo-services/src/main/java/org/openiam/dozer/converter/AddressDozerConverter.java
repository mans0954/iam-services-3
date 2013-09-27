package org.openiam.dozer.converter;

import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("addressDozerConverter")
public class AddressDozerConverter extends AbstractDozerEntityConverter<Address, AddressEntity> {
    @Override
    public AddressEntity convertEntity(AddressEntity entity, boolean isDeep) {
        return convert(entity, isDeep, AddressEntity.class);
    }

    @Override
    public Address convertDTO(Address entity, boolean isDeep) {
        return convert(entity, isDeep, Address.class);
    }

    @Override
    public AddressEntity convertToEntity(Address entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AddressEntity.class);
    }

    @Override
    public Address convertToDTO(AddressEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, Address.class);
    }

    @Override
    public List<AddressEntity> convertToEntityList(List<Address> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AddressEntity.class);
    }

    @Override
    public List<Address> convertToDTOList(List<AddressEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Address.class);
    }

    @Override
    public Set<AddressEntity> convertToEntitySet(Set<Address> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, AddressEntity.class);
    }

    @Override
    public Set<Address> convertToDTOSet(Set<AddressEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Address.class);
    }
}
