package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.policy.domain.PolicyDefEntity;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("policyDefDozerMapper")
public class PolicyDefDozerConverter extends
        AbstractDozerEntityConverter<PolicyDef, PolicyDefEntity> {

    @Override
    public PolicyDefEntity convertEntity(PolicyDefEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, PolicyDefEntity.class);
    }

    @Override
    public PolicyDef convertDTO(PolicyDef entity, boolean isDeep) {
        return convert(entity, isDeep, PolicyDef.class);
    }

    @Override
    public PolicyDefEntity convertToEntity(PolicyDef entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyDefEntity.class);
    }

    @Override
    public PolicyDef convertToDTO(PolicyDefEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyDef.class);
    }

    @Override
    public List<PolicyDefEntity> convertToEntityList(List<PolicyDef> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep,
 PolicyDefEntity.class);
    }

    @Override
    public List<PolicyDef> convertToDTOList(List<PolicyDefEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PolicyDef.class);
    }

}
