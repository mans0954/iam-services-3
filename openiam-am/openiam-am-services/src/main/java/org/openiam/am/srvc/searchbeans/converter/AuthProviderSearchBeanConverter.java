package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authProviderSearchBeanConverter")
public class AuthProviderSearchBeanConverter implements
        SearchBeanConverter<AuthProviderEntity, AuthProviderSearchBean> {
    @Override
    public AuthProviderEntity convert(AuthProviderSearchBean searchBean) {
        final AuthProviderEntity entity = new AuthProviderEntity();
        entity.setProviderId(searchBean.getKey());
        entity.setName(searchBean.getProviderName());
        entity.setProviderType(searchBean.getProviderType());
        entity.setManagedSysId(searchBean.getManagedSysId());
        return entity;
    }
}
