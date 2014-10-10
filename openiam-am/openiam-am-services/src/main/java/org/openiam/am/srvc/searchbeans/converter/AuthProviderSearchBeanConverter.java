package org.openiam.am.srvc.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("authProviderSearchBeanConverter")
public class AuthProviderSearchBeanConverter implements
        SearchBeanConverter<AuthProviderEntity, AuthProviderSearchBean> {
    @Override
    public AuthProviderEntity convert(AuthProviderSearchBean searchBean) {
        final AuthProviderEntity entity = new AuthProviderEntity();
        if(searchBean != null) {
	        entity.setId(searchBean.getKey());
	        entity.setName(searchBean.getName());
	        if(StringUtils.isNotBlank(searchBean.getProviderType())) {
	        	entity.setType(new AuthProviderTypeEntity());
	        	entity.getType().setId(searchBean.getProviderType());
	        }
	        if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
	        	entity.setManagedSystem(new ManagedSysEntity());
	        	entity.getManagedSystem().setId(searchBean.getManagedSysId());
	        }
        }
        return entity;
    }
}
