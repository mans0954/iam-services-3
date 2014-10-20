package org.openiam.am.srvc.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("contentProviderSearchBeanConverter")
public class ContentProviderSearchBeanConverter implements
        SearchBeanConverter<ContentProviderEntity, ContentProviderSearchBean> {
    @Override
    public ContentProviderEntity convert(ContentProviderSearchBean searchBean) {
        final ContentProviderEntity entity = new ContentProviderEntity();
        if(searchBean != null) {
        	entity.setId(searchBean.getKey());
        	entity.setName(searchBean.getProviderName());
        	entity.setDomainPattern(searchBean.getDomainPattern());
        	entity.setIsSSL(searchBean.isSSL());
        	if(StringUtils.isNotBlank(searchBean.getAuthProviderId())) {
        		final AuthProviderEntity authProvider = new AuthProviderEntity();
        		authProvider.setId(searchBean.getAuthProviderId());
        		entity.setAuthProvider(authProvider);
        	}
        	if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
        		final ManagedSysEntity managedSystem = new ManagedSysEntity();
        		managedSystem.setId(searchBean.getManagedSysId());
        		entity.setManagedSystem(managedSystem);
        	}
        }
        return entity;
    }
}
