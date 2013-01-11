package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component("emailAddressSearchBeanConverter")
public class EmailAddressSearchBeanConverter implements SearchBeanConverter<EmailAddressEntity, EmailSearchBean> {

    @Override
    public EmailAddressEntity convert(EmailSearchBean searchBean) {
        final EmailAddressEntity email = new EmailAddressEntity();
        email.setEmailId(searchBean.getKey());
        email.setName(searchBean.getName());
        email.setParentType(searchBean.getParentType());

        if(searchBean.getParentId() != null && searchBean.getParentId().trim().length() > 0) {
            final UserEntity parent = new UserEntity();
            parent.setUserId(searchBean.getParentId());
            email.setParent(parent);
        }
        return email;
    }
}
