package org.openiam.am.srvc.groovy;

import java.net.URI;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

public abstract class AbstractRedirectURLGroovyProcessor {
	
	protected ApplicationContext context;

	protected AbstractRedirectURLGroovyProcessor() {
		
	}
	
	public void setContext(final ApplicationContext context) {
		this.context = context;
	}

	/**
	 * This method should be overridden by the developer
	 * @param userId - the current user id
	 * @param contentProvider - the current content provider
	 * @param pattern - the current URI Pattern
	 * @param method - the current URIPatternMethod.  Can be null
	 * @return a String starting with '/', 'http', or 'https'.  If null is returned,
	 * it is assumed that redirect does not occur.
	 */
	public abstract String getRedirectURL(final String userId, 
										  final ContentProvider contentProvider, 
										  final URIPattern pattern,
										  final URIPatternMethod method);
}
