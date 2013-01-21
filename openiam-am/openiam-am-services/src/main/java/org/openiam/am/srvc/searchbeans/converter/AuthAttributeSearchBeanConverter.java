package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authAttributeSearchBeanConverter")
public class AuthAttributeSearchBeanConverter implements SearchBeanConverter<AuthAttributeEntity, AuthAttributeSearchBean> {

    @Override
    public AuthAttributeEntity convert(AuthAttributeSearchBean searchBean) {
        final AuthAttributeEntity entity = new AuthAttributeEntity();
        entity.setAuthAttributeId(searchBean.getKey());
        entity.setAttributeName(searchBean.getAttributeName());
        entity.setProviderType(searchBean.getProviderType());
        return entity;
    }
}
