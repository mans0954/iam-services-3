package org.openiam.xacml.srvc.dozer.converter;

import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;
import org.openiam.xacml.srvc.dto.XACMLTargetDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Component("xacmlTargetDozerConverter")
public class XACMLTargetDozerConverter extends AbstractDozerEntityConverter<XACMLTargetDTO, XACMLTargetEntity> {


    @Override
    public XACMLTargetEntity convertEntity(XACMLTargetEntity policyEntity, boolean isDeep) {
        return convert(policyEntity, isDeep, XACMLTargetEntity.class);
    }

    @Override
    public XACMLTargetDTO convertDTO(XACMLTargetDTO entity, boolean isDeep) {
        return convert(entity, isDeep, XACMLTargetDTO.class);
    }

    @Override
    public XACMLTargetEntity convertToEntity(XACMLTargetDTO entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, XACMLTargetEntity.class);
    }

    @Override
    public XACMLTargetDTO convertToDTO(XACMLTargetEntity policyEntity, boolean isDeep) {
        return this.convertToCrossEntity(policyEntity, isDeep, XACMLTargetDTO.class);
    }

    @Override
    public List<XACMLTargetEntity> convertToEntityList(List<XACMLTargetDTO> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLTargetEntity.class);
    }

    @Override
    public List<XACMLTargetDTO> convertToDTOList(List<XACMLTargetEntity> list, boolean isDeep) {
        return this.convertListToCrossEntity(list, isDeep, XACMLTargetDTO.class);
    }

    @Override
    public Set<XACMLTargetEntity> convertToEntitySet(Set<XACMLTargetDTO> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLTargetEntity.class);
    }

    @Override
    public Set<XACMLTargetDTO> convertToDTOSet(Set<XACMLTargetEntity> set, boolean isDeep) {
        return this.convertSetToCrossEntity(set, isDeep, XACMLTargetDTO.class);
    }
}
