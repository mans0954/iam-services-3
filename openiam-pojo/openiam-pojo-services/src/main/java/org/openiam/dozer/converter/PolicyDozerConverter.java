package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("policyDozerMapper")
public class PolicyDozerConverter extends
        AbstractDozerEntityConverter<Policy, PolicyEntity> {

    @Override
    public PolicyEntity convertEntity(PolicyEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, PolicyEntity.class);
    }

    @Override
    public Policy convertDTO(Policy entity, boolean isDeep) {
        return convert(entity, isDeep, Policy.class);
    }

    @Override
    public PolicyEntity convertToEntity(Policy entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyEntity.class);
    }

    @Override
    public Policy convertToDTO(PolicyEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, Policy.class);
    }

    @Override
    public List<PolicyEntity> convertToEntityList(List<Policy> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep,
 PolicyEntity.class);
    }

    @Override
    public List<Policy> convertToDTOList(List<PolicyEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Policy.class);
    }

}
