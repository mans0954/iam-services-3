package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.springframework.stereotype.Component;

@Component
public class BatchTaskDozerConverter extends AbstractDozerEntityConverter<BatchTask, BatchTaskEntity> {

	@Override
	public BatchTaskEntity convertEntity(BatchTaskEntity entity, boolean isDeep) {
		return convert(entity, isDeep, BatchTaskEntity.class);
	}

	@Override
	public BatchTask convertDTO(BatchTask entity, boolean isDeep) {
		return convert(entity, isDeep, BatchTask.class);
	}

	@Override
	public BatchTaskEntity convertToEntity(BatchTask entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, BatchTaskEntity.class);
	}

	@Override
	public BatchTask convertToDTO(BatchTaskEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, BatchTask.class);
	}

	@Override
	public List<BatchTaskEntity> convertToEntityList(List<BatchTask> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, BatchTaskEntity.class);
	}

	@Override
	public List<BatchTask> convertToDTOList(List<BatchTaskEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, BatchTask.class);
	}

}
