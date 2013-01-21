package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.am.srvc.searchbeans.AuthResourceAttributeSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authResourceAttributeSearchBeanConverter")
public class AuthResourceAttributeSearchBeanConverter implements
        SearchBeanConverter<AuthResourceAttributeEntity, AuthResourceAttributeSearchBean> {

    @Override
    public AuthResourceAttributeEntity convert(AuthResourceAttributeSearchBean searchBean) {
        final AuthResourceAttributeEntity entity = new AuthResourceAttributeEntity();
        entity.setAttributeMapId(searchBean.getKey());
        entity.setTargetAttributeName(searchBean.getTargetAttributeName());
        entity.setAmAttributeName(searchBean.getAmAttributeName());
        entity.setResourceId(searchBean.getResourceId());
        return entity;
    }
}
