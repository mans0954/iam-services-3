package org.openiam.dozer.converter;

import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("passwordHistoryDozerMapper")
public class PasswordHistoryDozerConverter extends AbstractDozerEntityConverter<PasswordHistory, PasswordHistoryEntity> {

    @Override
    public PasswordHistoryEntity convertEntity(PasswordHistoryEntity entity, boolean isDeep) {
        return convert(entity, isDeep, PasswordHistoryEntity.class);
    }

    @Override
    public PasswordHistory convertDTO(PasswordHistory entity, boolean isDeep) {
        return convert(entity, isDeep, PasswordHistory.class);
    }

    @Override
    public PasswordHistoryEntity convertToEntity(PasswordHistory entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PasswordHistoryEntity.class);
    }

    @Override
    public PasswordHistory convertToDTO(PasswordHistoryEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PasswordHistory.class);
    }

    @Override
    public List<PasswordHistoryEntity> convertToEntityList(List<PasswordHistory> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PasswordHistoryEntity.class);
    }

    @Override
    public List<PasswordHistory> convertToDTOList(List<PasswordHistoryEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PasswordHistory.class);
    }

    @Override
    public Set<PasswordHistoryEntity> convertToEntitySet(Set<PasswordHistory> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, PasswordHistoryEntity.class);
    }

    @Override
    public Set<PasswordHistory> convertToDTOSet(Set<PasswordHistoryEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, PasswordHistory.class);
    }
}
