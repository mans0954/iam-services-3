package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysRuleDto;
import org.springframework.stereotype.Component;

@Component("managedSysRuleDozerConverter")
public class ManagedSysRuleDozerConverter extends
        AbstractDozerEntityConverter<ManagedSysRuleDto, ManagedSysRuleEntity> {
    @Override
    public ManagedSysRuleEntity convertEntity(ManagedSysRuleEntity userEntity,
            boolean isDeep) {
        return convert(userEntity, isDeep, ManagedSysRuleEntity.class);
    }

    @Override
    public ManagedSysRuleDto convertDTO(ManagedSysRuleDto entity, boolean isDeep) {
        return convert(entity, isDeep, ManagedSysRuleDto.class);
    }

    @Override
    public ManagedSysRuleEntity convertToEntity(ManagedSysRuleDto entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ManagedSysRuleEntity.class);
    }

    @Override
    public ManagedSysRuleDto convertToDTO(ManagedSysRuleEntity userEntity,
            boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, ManagedSysRuleDto.class);
    }

    @Override
    public List<ManagedSysRuleEntity> convertToEntityList(
            List<ManagedSysRuleDto> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                ManagedSysRuleEntity.class);
    }

    @Override
    public List<ManagedSysRuleDto> convertToDTOList(
            List<ManagedSysRuleEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ManagedSysRuleDto.class);
    }
}
