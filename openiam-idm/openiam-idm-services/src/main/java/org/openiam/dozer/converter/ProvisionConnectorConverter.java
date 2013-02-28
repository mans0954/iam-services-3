package org.openiam.dozer.converter;

import org.openiam.dozer.AbstractIdmDozerEntityConverter;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("provisionConnectorConverter")
public class ProvisionConnectorConverter extends AbstractIdmDozerEntityConverter<ProvisionConnectorDto, ProvisionConnectorEntity> {

    @Override
    public ProvisionConnectorEntity convertEntity(ProvisionConnectorEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, ProvisionConnectorEntity.class);
    }

    @Override
    public ProvisionConnectorDto convertDTO(ProvisionConnectorDto entity, boolean isDeep) {
        return convert(entity, isDeep, ProvisionConnectorDto.class);
    }

    @Override
    public ProvisionConnectorEntity convertToEntity(ProvisionConnectorDto entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ProvisionConnectorEntity.class);
    }

    @Override
    public ProvisionConnectorDto convertToDTO(ProvisionConnectorEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ProvisionConnectorDto.class);
    }

    @Override
    public List<ProvisionConnectorEntity> convertToEntityList(List<ProvisionConnectorDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ProvisionConnectorEntity.class);
    }

    @Override
    public List<ProvisionConnectorDto> convertToDTOList(List<ProvisionConnectorEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ProvisionConnectorDto.class);
    }
}
