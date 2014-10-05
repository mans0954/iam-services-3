package org.openiam.am.srvc.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
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
        entity.setId(searchBean.getKey());
        entity.setName(searchBean.getName());
        if(StringUtils.isNotBlank(searchBean.getAmAttributeId())) {
        	entity.setAmAttribute(new AuthResourceAMAttributeEntity());
        	entity.getAmAttribute().setId(searchBean.getAmAttributeId());
        }
        if(StringUtils.isNotBlank(searchBean.getProviderId())) {
        	entity.setProvider(new AuthProviderEntity());
        	entity.getProvider().setId(searchBean.getProviderId());
        }
        return entity;
    }
}
