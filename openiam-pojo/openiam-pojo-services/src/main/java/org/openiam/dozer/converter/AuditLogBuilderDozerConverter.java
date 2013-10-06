package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.dto.AuditLogBuilderDto;
import org.springframework.stereotype.Component;

@Component
public class AuditLogBuilderDozerConverter extends AbstractDozerEntityConverter<AuditLogBuilderDto, AuditLogBuilder> {

	@Override
	public AuditLogBuilder convertEntity(AuditLogBuilder entity, boolean isDeep) {
		return convert(entity, isDeep, AuditLogBuilder.class);
	}

	@Override
	public AuditLogBuilderDto convertDTO(AuditLogBuilderDto entity, boolean isDeep) {
		return convert(entity, isDeep, AuditLogBuilderDto.class);
	}

	@Override
	public AuditLogBuilder convertToEntity(AuditLogBuilderDto entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuditLogBuilder.class);
	}

	@Override
	public AuditLogBuilderDto convertToDTO(AuditLogBuilder entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuditLogBuilderDto.class);
	}

	@Override
	public List<AuditLogBuilder> convertToEntityList(List<AuditLogBuilderDto> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuditLogBuilder.class);
	}

	@Override
	public List<AuditLogBuilderDto> convertToDTOList(List<AuditLogBuilder> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuditLogBuilderDto.class);
	}

	@Override
	public Set<AuditLogBuilder> convertToEntitySet(Set<AuditLogBuilderDto> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, AuditLogBuilder.class);
	}

	@Override
	public Set<AuditLogBuilderDto> convertToDTOSet(Set<AuditLogBuilder> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, AuditLogBuilderDto.class);
	}

}
