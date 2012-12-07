package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("policyObjectAssocDozerMapper")
public class PolicyObjectAssocDozerConverter extends
        AbstractDozerEntityConverter<PolicyObjectAssoc, PolicyObjectAssocEntity> {

    @Override
    public PolicyObjectAssocEntity convertEntity(
            PolicyObjectAssocEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, PolicyObjectAssocEntity.class);
    }

    @Override
    public PolicyObjectAssoc convertDTO(PolicyObjectAssoc entity, boolean isDeep) {
        return convert(entity, isDeep, PolicyObjectAssoc.class);
    }

    @Override
    public PolicyObjectAssocEntity convertToEntity(PolicyObjectAssoc entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                PolicyObjectAssocEntity.class);
    }

    @Override
    public PolicyObjectAssoc convertToDTO(PolicyObjectAssocEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyObjectAssoc.class);
    }

    @Override
    public List<PolicyObjectAssocEntity> convertToEntityList(
            List<PolicyObjectAssoc> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep,
                PolicyObjectAssocEntity.class);
    }

    @Override
    public List<PolicyObjectAssoc> convertToDTOList(
            List<PolicyObjectAssocEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PolicyObjectAssoc.class);
    }

}
