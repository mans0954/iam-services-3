package org.openiam.am.srvc.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("uriPatternSearchBeanConverter")
public class URIPatternSearchBeanConverter implements
        SearchBeanConverter<URIPatternEntity, URIPatternSearchBean> {
    @Override
    public URIPatternEntity convert(final URIPatternSearchBean searchBean) {
    	final URIPatternEntity entity = new URIPatternEntity();
        if(searchBean != null) {
	        entity.setPattern(searchBean.getPattern());
	        entity.setId(searchBean.getKey());
	
	        if(StringUtils.isNotBlank(searchBean.getContentProviderId())){
	            final ContentProviderEntity cp = new ContentProviderEntity();
	            cp.setId(searchBean.getContentProviderId());
	            entity.setContentProvider(cp);
	        }
	        
	        if(StringUtils.isNotBlank(searchBean.getAuthProviderId())) {
	        	final AuthProviderEntity authProvider = new AuthProviderEntity();
	        	authProvider.setId(searchBean.getAuthProviderId());
	        	entity.setAuthProvider(authProvider);
	        }
	        entity.setShowOnApplicationPage(searchBean.getShowOnApplicationPage());
        }
        return entity;
    }
}
