package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("policyAttributeDozerMapper")
public class PolicyAttributeDozerConverter extends
		AbstractDozerEntityConverter<PolicyAttribute, PolicyAttributeEntity> {

    @Override
	public PolicyAttributeEntity convertEntity(PolicyAttributeEntity entity,
            boolean isDeep) {
		return convert(entity, isDeep, PolicyAttributeEntity.class);
    }

    @Override
	public PolicyAttribute convertDTO(PolicyAttribute entity, boolean isDeep) {
		return convert(entity, isDeep, PolicyAttribute.class);
    }

    @Override
	public PolicyAttributeEntity convertToEntity(PolicyAttribute entity,
            boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, PolicyAttributeEntity.class);
    }

    @Override
	public PolicyAttribute convertToDTO(PolicyAttributeEntity entity,
            boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, PolicyAttribute.class);
    }

    @Override
	public List<PolicyAttributeEntity> convertToEntityList(
			List<PolicyAttribute> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep,
				PolicyAttributeEntity.class);
    }

    @Override
	public List<PolicyAttribute> convertToDTOList(
			List<PolicyAttributeEntity> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, PolicyAttribute.class);
    }

}
