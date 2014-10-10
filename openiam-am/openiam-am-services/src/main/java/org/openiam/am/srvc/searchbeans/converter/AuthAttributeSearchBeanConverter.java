package org.openiam.am.srvc.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authAttributeSearchBeanConverter")
public class AuthAttributeSearchBeanConverter implements SearchBeanConverter<AuthAttributeEntity, AuthAttributeSearchBean> {

    @Override
    public AuthAttributeEntity convert(AuthAttributeSearchBean searchBean) {
        final AuthAttributeEntity entity = new AuthAttributeEntity();
        if(searchBean != null) {
	        entity.setId(searchBean.getKey());
	        entity.setName(searchBean.getAttributeName());
	        if(StringUtils.isNotBlank(searchBean.getProviderType())) {
	        	entity.setType(new AuthProviderTypeEntity());
	        	entity.getType().setId(searchBean.getProviderType());
	        }
        }
        return entity;
    }
}
