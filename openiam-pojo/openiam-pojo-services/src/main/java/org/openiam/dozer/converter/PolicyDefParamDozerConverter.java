package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("policyDefParamParamDozerMapper")
public class PolicyDefParamDozerConverter extends
        AbstractDozerEntityConverter<PolicyDefParam, PolicyDefParamEntity> {

    @Override
    public PolicyDefParamEntity convertEntity(PolicyDefParamEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, PolicyDefParamEntity.class);
    }

    @Override
    public PolicyDefParam convertDTO(PolicyDefParam entity, boolean isDeep) {
        return convert(entity, isDeep, PolicyDefParam.class);
    }

    @Override
    public PolicyDefParamEntity convertToEntity(PolicyDefParam entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyDefParamEntity.class);
    }

    @Override
    public PolicyDefParam convertToDTO(PolicyDefParamEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, PolicyDefParam.class);
    }

    @Override
    public List<PolicyDefParamEntity> convertToEntityList(
            List<PolicyDefParam> list,
            boolean isDeep) {
		return convertListToCrossEntity(list, isDeep,
                PolicyDefParamEntity.class);
    }

    @Override
    public List<PolicyDefParam> convertToDTOList(
            List<PolicyDefParamEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, PolicyDefParam.class);
    }

}
