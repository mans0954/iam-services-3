package org.openiam.xacml.srvc.dozer.converter;

import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;
import org.openiam.xacml.srvc.dto.XACMLPolicySetDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Component("xacmlPolicySetDozerConverter")
public class XACMLPolicySetDozerConverter extends AbstractDozerEntityConverter<XACMLPolicySetDTO, XACMLPolicySetEntity> {


    @Override
    public XACMLPolicySetEntity convertEntity(XACMLPolicySetEntity policySetEntity, boolean isDeep) {
        return convert(policySetEntity, isDeep, XACMLPolicySetEntity.class);
    }

    @Override
    public XACMLPolicySetDTO convertDTO(XACMLPolicySetDTO entity, boolean isDeep) {
        return convert(entity, isDeep, XACMLPolicySetDTO.class);
    }

    @Override
    public XACMLPolicySetEntity convertToEntity(XACMLPolicySetDTO entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, XACMLPolicySetEntity.class);
    }

    @Override
    public XACMLPolicySetDTO convertToDTO(XACMLPolicySetEntity policySetEntity, boolean isDeep) {
        return this.convertToCrossEntity(policySetEntity, isDeep, XACMLPolicySetDTO.class);
    }

    @Override
    public List<XACMLPolicySetEntity> convertToEntityList(List<XACMLPolicySetDTO> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLPolicySetEntity.class);
    }

    @Override
    public List<XACMLPolicySetDTO> convertToDTOList(List<XACMLPolicySetEntity> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLPolicySetDTO.class);
    }

    @Override
    public Set<XACMLPolicySetEntity> convertToEntitySet(Set<XACMLPolicySetDTO> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLPolicySetEntity.class);
    }

    @Override
    public Set<XACMLPolicySetDTO> convertToDTOSet(Set<XACMLPolicySetEntity> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLPolicySetDTO.class);
    }
}
