package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.springframework.stereotype.Component;

@Component("idmAuditLogCustomDozerMapper")
public class IdmAuditLogCustomDozerConverter
        extends
        AbstractDozerEntityConverter<IdmAuditLogCustom, IdmAuditLogCustomEntity> {

    @Override
    public IdmAuditLogCustomEntity convertEntity(
            IdmAuditLogCustomEntity entity, boolean isDeep) {
        return convert(entity, isDeep, IdmAuditLogCustomEntity.class);
    }

    @Override
    public IdmAuditLogCustom convertDTO(IdmAuditLogCustom entity, boolean isDeep) {
        return convert(entity, isDeep, IdmAuditLogCustom.class);
    }

    @Override
    public IdmAuditLogCustomEntity convertToEntity(IdmAuditLogCustom entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                IdmAuditLogCustomEntity.class);
    }

    @Override
    public IdmAuditLogCustom convertToDTO(IdmAuditLogCustomEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, IdmAuditLogCustom.class);
    }

    @Override
    public List<IdmAuditLogCustomEntity> convertToEntityList(
            List<IdmAuditLogCustom> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                IdmAuditLogCustomEntity.class);
    }

    @Override
    public List<IdmAuditLogCustom> convertToDTOList(
            List<IdmAuditLogCustomEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, IdmAuditLogCustom.class);
    }

    @Override
    public Set<IdmAuditLogCustomEntity> convertToEntitySet(Set<IdmAuditLogCustom> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdmAuditLogCustomEntity.class);
    }

    @Override
    public Set<IdmAuditLogCustom> convertToDTOSet(Set<IdmAuditLogCustomEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdmAuditLogCustom.class);
    }
}
