package org.openiam.am.srvc.searchbeans.converter;

import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("uriPatternSearchBeanConverter")
public class URIPatternSearchBeanConverter implements
        SearchBeanConverter<URIPatternEntity, URIPatternSearchBean> {
    @Override
    public URIPatternEntity convert(URIPatternSearchBean searchBean) {
        URIPatternEntity entity = new URIPatternEntity();
        entity.setPattern(searchBean.getPattern());
        entity.setId(searchBean.getKey());

        if(searchBean.getContentProviderId()!=null && !searchBean.getContentProviderId().trim().isEmpty()){
            ContentProviderEntity cp = new ContentProviderEntity();
            cp.setId(searchBean.getContentProviderId());
            entity.setContentProvider(cp);
        }
        return entity;
    }
}
