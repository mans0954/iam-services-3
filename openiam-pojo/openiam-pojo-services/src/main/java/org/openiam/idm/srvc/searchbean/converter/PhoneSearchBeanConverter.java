package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component("phoneSearchBeanConverter")
public class PhoneSearchBeanConverter implements SearchBeanConverter<PhoneEntity, PhoneSearchBean> {

    @Override
    public PhoneEntity convert(PhoneSearchBean searchBean) {
        final PhoneEntity phone = new PhoneEntity();
        phone.setPhoneId(searchBean.getKey());

        if(searchBean.getParentId() != null && searchBean.getParentId().trim().length() > 0) {
            final UserEntity parent = new UserEntity();
            parent.setUserId(searchBean.getParentId());
            phone.setParent(parent);
        }

        if(searchBean.getMetadataTypeId() != null && searchBean.getMetadataTypeId().trim().length() > 0) {
            final MetadataTypeEntity type = new MetadataTypeEntity();
            type.setMetadataTypeId(searchBean.getMetadataTypeId());
            phone.setMetadataType(type);
        }

        return phone;
    }
}
