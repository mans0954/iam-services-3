package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("contentProviderSearchBeanConverter")
public class ContentProviderSearchBeanConverter implements
        SearchBeanConverter<ContentProviderEntity, ContentProviderSearchBean> {
    @Override
    public ContentProviderEntity convert(ContentProviderSearchBean searchBean) {
        final ContentProviderEntity entity = new ContentProviderEntity();
        entity.setId(searchBean.getKey());
        entity.setName(searchBean.getProviderName());
        /*entity.setContextPath(searchBean.getContextPath());*/
        entity.setDomainPattern(searchBean.getDomainPattern());
        entity.setIsSSL(searchBean.isSSL());

        return entity;
    }
}
