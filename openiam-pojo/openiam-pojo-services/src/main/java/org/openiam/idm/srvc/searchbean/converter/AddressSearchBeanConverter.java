package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component("addressSearchBeanConverter")
public class AddressSearchBeanConverter implements SearchBeanConverter<AddressEntity, AddressSearchBean> {

    @Override
    public AddressEntity convert(AddressSearchBean searchBean) {
        final AddressEntity address = new AddressEntity();
        address.setId(searchBean.getKey());

        if(StringUtils.isNotBlank(searchBean.getParentId())) {
            final UserEntity parent = new UserEntity();
            parent.setId(searchBean.getParentId());
            address.setParent(parent);
        }

        if(StringUtils.isNotBlank(searchBean.getMetadataTypeId())) {
            final MetadataTypeEntity type = new MetadataTypeEntity();
            type.setId(searchBean.getMetadataTypeId());
            address.setType(type);
        }
        return address;
    }
}
