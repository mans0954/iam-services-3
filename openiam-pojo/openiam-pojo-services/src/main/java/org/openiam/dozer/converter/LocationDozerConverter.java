package org.openiam.dozer.converter;


import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component("locationDozerConverter")
public class LocationDozerConverter extends AbstractDozerEntityConverter<Location, LocationEntity> {
    @Override
    public LocationEntity convertEntity(LocationEntity entity, boolean isDeep) {
        return convert(entity, isDeep, LocationEntity.class);
    }

    @Override
    public Location convertDTO(Location entity, boolean isDeep) {
        return convert(entity, isDeep, Location.class);
    }

    @Override
    public LocationEntity convertToEntity(Location entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LocationEntity.class);
    }

    @Override
    public Location convertToDTO(LocationEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, Location.class);
    }

    @Override
    public List<LocationEntity> convertToEntityList(List<Location> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LocationEntity.class);
    }

    @Override
    public List<Location> convertToDTOList(List<LocationEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Location.class);
    }

    @Override
    public Set<LocationEntity> convertToEntitySet(Set<Location> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, LocationEntity.class);
    }

    @Override
    public Set<Location> convertToDTOSet(Set<LocationEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Location.class);
    }
}
