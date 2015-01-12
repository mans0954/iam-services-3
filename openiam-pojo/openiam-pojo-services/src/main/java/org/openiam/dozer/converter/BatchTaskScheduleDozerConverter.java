package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.springframework.stereotype.Component;

@Component
public class BatchTaskScheduleDozerConverter extends AbstractDozerEntityConverter<BatchTaskSchedule, BatchTaskScheduleEntity> {

	@Override
	public BatchTaskScheduleEntity convertEntity(
			BatchTaskScheduleEntity entity, boolean isDeep) {
		return convert(entity, isDeep, BatchTaskScheduleEntity.class);
	}

	@Override
	public BatchTaskSchedule convertDTO(BatchTaskSchedule entity, boolean isDeep) {
		return convert(entity, isDeep, BatchTaskSchedule.class);
	}

	@Override
	public BatchTaskScheduleEntity convertToEntity(BatchTaskSchedule entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, BatchTaskScheduleEntity.class);
	}

	@Override
	public BatchTaskSchedule convertToDTO(BatchTaskScheduleEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, BatchTaskSchedule.class);
	}

	@Override
	public List<BatchTaskScheduleEntity> convertToEntityList(
			List<BatchTaskSchedule> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, BatchTaskScheduleEntity.class);
	}

	@Override
	public List<BatchTaskSchedule> convertToDTOList(
			List<BatchTaskScheduleEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, BatchTaskSchedule.class);
	}

	@Override
	public Set<BatchTaskScheduleEntity> convertToEntitySet(
			Set<BatchTaskSchedule> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, BatchTaskScheduleEntity.class);
	}

	@Override
	public Set<BatchTaskSchedule> convertToDTOSet(
			Set<BatchTaskScheduleEntity> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, BatchTaskSchedule.class);
	}

}
