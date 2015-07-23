package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.springframework.stereotype.Component;

@Component("provisionConnectorConverter")
public class ProvisionConnectorConverter extends AbstractDozerEntityConverter<ProvisionConnectorDto, ProvisionConnectorEntity> {

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

	@Override
	public Set<ProvisionConnectorEntity> convertToEntitySet(
			Set<ProvisionConnectorDto> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, ProvisionConnectorEntity.class);
	}

	@Override
	public Set<ProvisionConnectorDto> convertToDTOSet(
			Set<ProvisionConnectorEntity> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, ProvisionConnectorDto.class);
	}
}
