package org.openiam.am.srvc.uriauth.rule;

import java.net.URI;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dto.AbstractMeta;
import org.openiam.am.srvc.dto.AbstractPatternMetaValue;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.idm.srvc.auth.service.AuthAttributeProcessor;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.response.URIPatternRuleToken;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractURIPatternRule implements URIPatternRule {

    @Autowired
    protected AuthAttributeProcessor authAttributeProcessor;
	
    @Autowired
    private AuthProviderService authProviderService;
    
	@Override
	@Transactional
	public URIPatternRuleToken process(final String userId, 
									   final URI uri,
									   final URIPatternMetaType metaType, 
									   final Set<AbstractPatternMetaValue> valueSet,
									   final URIPattern pattern,
									   final URIPatternMethod method,
									   final ContentProvider contentProvider,
									   final AbstractMeta meta) throws Exception {
		final URIPatternRuleToken token = new URIPatternRuleToken(metaType, meta);
		if(CollectionUtils.isNotEmpty(valueSet)) {
			for(final AbstractPatternMetaValue metaValue : valueSet) {
				final String key = StringUtils.trimToNull(metaValue.getName());
				if(metaValue.isEmptyValue()) {
					token.addValue(key, null, metaValue.isPropagateThroughProxy(), metaValue.isPropagateOnError(), false);
				} else {
                    String value = null;
                    final URIFederationGroovyProcessor groovyProcessor = metaValue.getGroovyProcessor();
                    if(groovyProcessor != null) {
                        value = groovyProcessor.getValue(userId, contentProvider, pattern, metaValue, uri);
                    }

                    if(value == null) {
                        value = StringUtils.trimToNull(metaValue.getStaticValue());
                    }
                    if(value == null) {
                        if(metaValue.getAmAttribute() != null) {
                            final AuthProviderEntity authProvider = authProviderService.getAuthProvider(contentProvider.getAuthProviderId());
                            final ManagedSysEntity managedSystem = authProvider.getManagedSystem();
                            value = authAttributeProcessor.process(metaValue.getAmAttribute().getReflectionKey(), userId, managedSystem.getId());
                        }
                    }
                    
                    if(value == null) {
                    	value = metaValue.getFetchedValue();
                    }

                    if(value != null) {
                        token.addValue(key, value, metaValue.isPropagateThroughProxy(), metaValue.isPropagateOnError(), metaValue.getFetchedValue() != null);
                    }
				}
			}
		}
		postProcess(userId, uri, metaType, valueSet, token, pattern, method, contentProvider);
		return token;
	}

	/* can be overridden */
	protected void postProcess(final String userId, 
							   final URI uri, 
							   final URIPatternMetaType metaType, 
							   final Set<AbstractPatternMetaValue> valueSet,
							   final URIPatternRuleToken token,
							   final URIPattern pattern,
							   final URIPatternMethod method,
							   final ContentProvider contentProvider) {
		
	}
}
