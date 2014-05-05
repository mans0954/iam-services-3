package org.openiam.dozer.converter;

import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("idmAuditLogTargetDozerMapper")
public class IdmAuditLogTargetDozerConverter extends
        AbstractDozerEntityConverter<AuditLogTarget, AuditLogTargetEntity> {

    @Override
    public AuditLogTargetEntity convertEntity(AuditLogTargetEntity entity,
                                           boolean isDeep) {
        return convert(entity, isDeep, AuditLogTargetEntity.class);
    }

    @Override
    public AuditLogTarget convertDTO(AuditLogTarget entity, boolean isDeep) {
        return convert(entity, isDeep, AuditLogTarget.class);
    }

    @Override
    public AuditLogTargetEntity convertToEntity(AuditLogTarget entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuditLogTargetEntity.class);
    }

    @Override
    public AuditLogTarget convertToDTO(AuditLogTargetEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuditLogTarget.class);
    }

    @Override
    public List<AuditLogTargetEntity> convertToEntityList(List<AuditLogTarget> list,
                                                       boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuditLogTargetEntity.class);
    }

    @Override
    public List<AuditLogTarget> convertToDTOList(List<AuditLogTargetEntity> list,
                                              boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuditLogTarget.class);
    }

    @Override
    public Set<AuditLogTargetEntity> convertToEntitySet(Set<AuditLogTarget> set,
                                                     boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, AuditLogTargetEntity.class);
    }

    @Override
    public Set<AuditLogTarget> convertToDTOSet(Set<AuditLogTargetEntity> set,
                                            boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, AuditLogTarget.class);
    }
}
