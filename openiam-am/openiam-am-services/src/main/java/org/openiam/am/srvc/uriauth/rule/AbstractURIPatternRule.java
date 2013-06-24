package org.openiam.am.srvc.uriauth.rule;

import java.net.URI;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.service.AuthAttributeProcessor;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleToken;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractURIPatternRule implements URIPatternRule {

    @Autowired
    protected AuthAttributeProcessor authAttributeProcessor;
	
	@Override
	public URIPatternRuleToken process(final String userId, final URI uri,
									   final URIPatternMetaType metaType, 
									   final Set<URIPatternMetaValue> valueSet,
									   final URIPattern pattern,
									   final ContentProvider contentProvider) throws Exception {
		final URIPatternRuleToken token = new URIPatternRuleToken(metaType);
		if(CollectionUtils.isNotEmpty(valueSet)) {
			for(final URIPatternMetaValue metaValue : valueSet) {
				final String key = StringUtils.trimToNull(metaValue.getName());
				String value = StringUtils.trimToNull(metaValue.getStaticValue());
				if(value == null) {
					if(metaValue.getAmAttribute() != null) {
						value = authAttributeProcessor.process(metaValue.getAmAttribute().getAmAttributeId(), userId);
					}
				}
				
				if(value != null) {
					token.addValue(key, value);
				}
			}
		}
		postProcess(userId, uri, metaType, valueSet, token, pattern, contentProvider);
		return token;
	}

	/* can be overridden */
	protected void postProcess(final String userId, 
							   final URI uri, 
							   final URIPatternMetaType metaType, 
							   final Set<URIPatternMetaValue> valueSet,
							   final URIPatternRuleToken token,
							   final URIPattern pattern,
							   final ContentProvider contentProvider) {
		
	}
}
