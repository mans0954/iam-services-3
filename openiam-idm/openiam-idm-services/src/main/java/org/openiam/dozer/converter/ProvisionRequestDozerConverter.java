package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.dozer.AbstractIdmDozerEntityConverter;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.springframework.stereotype.Component;

@Component
public class ProvisionRequestDozerConverter extends AbstractIdmDozerEntityConverter<ProvisionRequest, ProvisionRequestEntity> {

	@Override
	public ProvisionRequestEntity convertEntity(ProvisionRequestEntity entity,
			boolean isDeep) {
		return convert(entity, isDeep, ProvisionRequestEntity.class);
	}

	@Override
	public ProvisionRequest convertDTO(ProvisionRequest entity, boolean isDeep) {
		return convert(entity, isDeep, ProvisionRequest.class);
	}

	@Override
	public ProvisionRequestEntity convertToEntity(ProvisionRequest entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ProvisionRequestEntity.class);
	}

	@Override
	public ProvisionRequest convertToDTO(ProvisionRequestEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ProvisionRequest.class);
	}

	@Override
	public List<ProvisionRequestEntity> convertToEntityList(
			List<ProvisionRequest> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ProvisionRequestEntity.class);
	}

	@Override
	public List<ProvisionRequest> convertToDTOList(
			List<ProvisionRequestEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ProvisionRequest.class);
	}

}
