package org.openiam.xacml.srvc.dozer.converter;

import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Component("xacmlPolicyDozerConverter")
public class XACMLPolicyDozerConverter extends AbstractDozerEntityConverter<XACMLPolicyDTO, XACMLPolicyEntity> {


    @Override
    public XACMLPolicyEntity convertEntity(XACMLPolicyEntity policyEntity, boolean isDeep) {
        return convert(policyEntity, isDeep, XACMLPolicyEntity.class);
    }

    @Override
    public XACMLPolicyDTO convertDTO(XACMLPolicyDTO entity, boolean isDeep) {
        return convert(entity, isDeep, XACMLPolicyDTO.class);
    }

    @Override
    public XACMLPolicyEntity convertToEntity(XACMLPolicyDTO entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, XACMLPolicyEntity.class);
    }

    @Override
    public XACMLPolicyDTO convertToDTO(XACMLPolicyEntity policyEntity, boolean isDeep) {
        return this.convertToCrossEntity(policyEntity, isDeep, XACMLPolicyDTO.class);
    }

    @Override
    public List<XACMLPolicyEntity> convertToEntityList(List<XACMLPolicyDTO> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLPolicyEntity.class);
    }

    @Override
    public List<XACMLPolicyDTO> convertToDTOList(List<XACMLPolicyEntity> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLPolicyDTO.class);
    }

    @Override
    public Set<XACMLPolicyEntity> convertToEntitySet(Set<XACMLPolicyDTO> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLPolicyEntity.class);
    }

    @Override
    public Set<XACMLPolicyDTO> convertToDTOSet(Set<XACMLPolicyEntity> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLPolicyDTO.class);
    }
}
