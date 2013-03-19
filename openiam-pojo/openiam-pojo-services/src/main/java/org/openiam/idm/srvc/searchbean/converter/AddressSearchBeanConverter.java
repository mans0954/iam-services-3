package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component("addressSearchBeanConverter")
public class AddressSearchBeanConverter implements SearchBeanConverter<AddressEntity, AddressSearchBean> {

    @Override
    public AddressEntity convert(AddressSearchBean searchBean) {
        final AddressEntity address = new AddressEntity();
        address.setAddressId(searchBean.getKey());

        if(searchBean.getParentId() != null && searchBean.getParentId().trim().length() > 0) {
            final UserEntity parent = new UserEntity();
            parent.setUserId(searchBean.getParentId());
            address.setParent(parent);
        }
        return address;
    }
}
