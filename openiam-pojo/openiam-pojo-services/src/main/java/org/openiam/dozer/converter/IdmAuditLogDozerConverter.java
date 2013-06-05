package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.springframework.stereotype.Component;

@Component("idmAuditLogDozerMapper")
public class IdmAuditLogDozerConverter extends
        AbstractDozerEntityConverter<IdmAuditLog, IdmAuditLogEntity> {

    @Override
    public IdmAuditLogEntity convertEntity(IdmAuditLogEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, IdmAuditLogEntity.class);
    }

    @Override
    public IdmAuditLog convertDTO(IdmAuditLog entity, boolean isDeep) {
        return convert(entity, isDeep, IdmAuditLog.class);
    }

    @Override
    public IdmAuditLogEntity convertToEntity(IdmAuditLog entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, IdmAuditLogEntity.class);
    }

    @Override
    public IdmAuditLog convertToDTO(IdmAuditLogEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, IdmAuditLog.class);
    }

    @Override
    public List<IdmAuditLogEntity> convertToEntityList(List<IdmAuditLog> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, IdmAuditLogEntity.class);
    }

    @Override
    public List<IdmAuditLog> convertToDTOList(List<IdmAuditLogEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, IdmAuditLog.class);
    }

}
