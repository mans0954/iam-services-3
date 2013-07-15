package org.openiam.dozer.converter;

import org.openiam.idm.srvc.policy.domain.ITPolicyEntity;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

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

}
