package org.openiam.dozer.converter;

import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.dto.Org2OrgXref;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("org2OrgXrefConverter")
public class Org2OrgXrefConverter extends AbstractDozerEntityConverter<Org2OrgXref, Org2OrgXrefEntity> {

    @Override
    public Org2OrgXrefEntity convertEntity(
            final Org2OrgXrefEntity entity, final boolean isDeep) {
        return convert(entity, isDeep, Org2OrgXrefEntity.class);
    }

    @Override
    public Org2OrgXref convertDTO(final Org2OrgXref entity,
                                          final boolean isDeep) {
        return convert(entity, isDeep, Org2OrgXref.class);
    }

    @Override
    public Org2OrgXrefEntity convertToEntity(
            final Org2OrgXref entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                Org2OrgXrefEntity.class);
    }

    @Override
    public Org2OrgXref convertToDTO(
            final Org2OrgXrefEntity entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, Org2OrgXref.class);
    }

    @Override
    public List<Org2OrgXrefEntity> convertToEntityList(
            final List<Org2OrgXref> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                Org2OrgXrefEntity.class);
    }

    @Override
    public List<Org2OrgXref> convertToDTOList(
            final List<Org2OrgXrefEntity> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                Org2OrgXref.class);
    }

    @Override
    public Set<Org2OrgXrefEntity> convertToEntitySet(
            final Set<Org2OrgXref> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                Org2OrgXrefEntity.class);
    }

    @Override
    public Set<Org2OrgXref> convertToDTOSet(
            final Set<Org2OrgXrefEntity> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                Org2OrgXref.class);
    }

}
