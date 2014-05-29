package org.openiam.dozer.converter;

import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;
import org.openiam.idm.srvc.org.dto.OrgType2OrgTypeXref;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("orgType2OrgTypeXrefConverter")
public class OrgType2OrgTypeXrefConverter extends AbstractDozerEntityConverter<OrgType2OrgTypeXref, OrgType2OrgTypeXrefEntity> {

    @Override
    public OrgType2OrgTypeXrefEntity convertEntity(
            final OrgType2OrgTypeXrefEntity entity, final boolean isDeep) {
        return convert(entity, isDeep, OrgType2OrgTypeXrefEntity.class);
    }

    @Override
    public OrgType2OrgTypeXref convertDTO(final OrgType2OrgTypeXref entity,
                                            final boolean isDeep) {
        return convert(entity, isDeep, OrgType2OrgTypeXref.class);
    }

    @Override
    public OrgType2OrgTypeXrefEntity convertToEntity(
            final OrgType2OrgTypeXref entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                OrgType2OrgTypeXrefEntity.class);
    }

    @Override
    public OrgType2OrgTypeXref convertToDTO(
            final OrgType2OrgTypeXrefEntity entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, OrgType2OrgTypeXref.class);
    }

    @Override
    public List<OrgType2OrgTypeXrefEntity> convertToEntityList(
            final List<OrgType2OrgTypeXref> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                OrgType2OrgTypeXrefEntity.class);
    }

    @Override
    public List<OrgType2OrgTypeXref> convertToDTOList(
            final List<OrgType2OrgTypeXrefEntity> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                OrgType2OrgTypeXref.class);
    }

    @Override
    public Set<OrgType2OrgTypeXrefEntity> convertToEntitySet(
            final Set<OrgType2OrgTypeXref> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                OrgType2OrgTypeXrefEntity.class);
    }

    @Override
    public Set<OrgType2OrgTypeXref> convertToDTOSet(
            final Set<OrgType2OrgTypeXrefEntity> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                OrgType2OrgTypeXref.class);
    }

}