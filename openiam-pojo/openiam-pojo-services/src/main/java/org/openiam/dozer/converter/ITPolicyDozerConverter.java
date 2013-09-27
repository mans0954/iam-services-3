package org.openiam.dozer.converter;

import org.openiam.idm.srvc.policy.domain.ITPolicyEntity;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("itPolicyDozerConverter")
public class ITPolicyDozerConverter extends AbstractDozerEntityConverter<ITPolicy, ITPolicyEntity> {

    @Override
    public ITPolicyEntity convertEntity(ITPolicyEntity entity, boolean isDeep) {
        return convert(entity, isDeep, ITPolicyEntity.class);
    }

    @Override
    public ITPolicy convertDTO(ITPolicy entity, boolean isDeep) {
        return convert(entity, isDeep, ITPolicy.class);
    }

    @Override
    public ITPolicyEntity convertToEntity(ITPolicy entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ITPolicyEntity.class);
    }

    @Override
    public ITPolicy convertToDTO(ITPolicyEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, ITPolicy.class);
    }

    @Override
    public List<ITPolicyEntity> convertToEntityList(List<ITPolicy> list,boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ITPolicyEntity.class);
    }

    @Override
    public List<ITPolicy> convertToDTOList(List<ITPolicyEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, ITPolicy.class);
    }

    @Override
    public Set<ITPolicyEntity> convertToEntitySet(Set<ITPolicy> set,boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ITPolicyEntity.class);
    }

    @Override
    public Set<ITPolicy> convertToDTOSet(Set<ITPolicyEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ITPolicy.class);
    }

}
