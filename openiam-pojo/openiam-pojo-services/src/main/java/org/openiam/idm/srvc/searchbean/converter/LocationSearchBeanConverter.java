package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component("locationSearchBeanConverter")
public class LocationSearchBeanConverter implements SearchBeanConverter<LocationEntity, LocationSearchBean> {

    @Override
    public LocationEntity convert(LocationSearchBean searchBean) {
        final LocationEntity location = new LocationEntity();
        location.setLocationId(searchBean.getKey());

        if(StringUtils.isNotBlank(searchBean.getOrganizationId())) {
            location.setOrganizationId(searchBean.getOrganizationId());
        }

        if(StringUtils.isNotBlank(searchBean.getCountry())) {
            location.setCountry(searchBean.getCountry());
        }

        if(StringUtils.isNotBlank(searchBean.getName())) {
            location.setName(searchBean.getName());
        }

        return location;
    }
}
