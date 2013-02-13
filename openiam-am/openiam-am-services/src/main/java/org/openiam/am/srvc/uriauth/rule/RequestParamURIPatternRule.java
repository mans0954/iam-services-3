package org.openiam.am.srvc.uriauth.rule;

import java.net.URI;
import java.util.Set;

import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleToken;
import org.springframework.stereotype.Component;

@Component("requestParamURIPatternRule")
public class RequestParamURIPatternRule implements URIPatternRule {

	@Override
	public URIPatternRuleToken process(final String userId, final URI uri,
									   final URIPatternMetaType metaType, 
									   final Set<URIPatternMetaValue> valueSet) {
		return null;
	}

}
