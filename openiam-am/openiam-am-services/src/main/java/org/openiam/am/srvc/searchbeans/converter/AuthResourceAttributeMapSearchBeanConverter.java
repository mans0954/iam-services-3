package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.searchbeans.AuthResourceAttributeMapSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authResourceAttributeMapSearchBeanConverter")
public class AuthResourceAttributeMapSearchBeanConverter implements
        SearchBeanConverter<AuthResourceAttributeMapEntity, AuthResourceAttributeMapSearchBean> {

    @Override
    public AuthResourceAttributeMapEntity convert(AuthResourceAttributeMapSearchBean searchBean) {
        final AuthResourceAttributeMapEntity entity = new AuthResourceAttributeMapEntity();
        entity.setAttributeMapId(searchBean.getKey());
        entity.setTargetAttributeName(searchBean.getTargetAttributeName());
        entity.setAmResAttributeId(searchBean.getAmAttributeId());
        entity.setProviderId(searchBean.getProviderId());
        return entity;
    }
}
