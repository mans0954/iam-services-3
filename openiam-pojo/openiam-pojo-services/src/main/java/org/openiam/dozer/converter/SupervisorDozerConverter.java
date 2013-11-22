package org.openiam.dozer.converter;

import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("supervisorDozerConverter")
public class SupervisorDozerConverter extends AbstractDozerEntityConverter<Supervisor, SupervisorEntity> {
    @Override
    public SupervisorEntity convertEntity(SupervisorEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, SupervisorEntity.class);
    }

    @Override
    public Supervisor convertDTO(Supervisor entity, boolean isDeep) {
        return convert(entity, isDeep, Supervisor.class);
    }

    @Override
    public SupervisorEntity convertToEntity(Supervisor entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, SupervisorEntity.class);
    }

    @Override
    public Supervisor convertToDTO(SupervisorEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, Supervisor.class);
    }

    @Override
    public List<SupervisorEntity> convertToEntityList(List<Supervisor> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, SupervisorEntity.class);
    }

    @Override
    public List<Supervisor> convertToDTOList(List<SupervisorEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Supervisor.class);
    }

    @Override
    public Set<SupervisorEntity> convertToEntitySet(Set<Supervisor> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, SupervisorEntity.class);
    }

    @Override
    public Set<Supervisor> convertToDTOSet(Set<SupervisorEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Supervisor.class);
    }
}
