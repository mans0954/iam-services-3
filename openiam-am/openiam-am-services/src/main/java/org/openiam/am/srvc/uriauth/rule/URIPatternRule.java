package org.openiam.am.srvc.uriauth.rule;

import java.net.URI;
import java.util.Set;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleToken;

/**
 * @author Lev Bornovalov
 * Processes a Rule, the value of which will be returned to the Proxy
 * BE CAREFUL WHEN CHANGING SPRING BEAN NAMES that implement this interface, as their name is stored in the database to provide runtime configuration
 */
public interface URIPatternRule {

	public URIPatternRuleToken process(final String userId, 
									   final URI uri, 
									   final URIPatternMetaType metaType, 
									   final Set<URIPatternMetaValue> valueSet,
									   final URIPattern pattern,
									   final ContentProvider contentProvider) throws Exception;
}
