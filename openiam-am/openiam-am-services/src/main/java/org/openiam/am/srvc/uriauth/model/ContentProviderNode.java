package org.openiam.am.srvc.uriauth.model;

import java.net.URI;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.protocol.UriPatternMatcher;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;

public class ContentProviderNode {
	
	private static Logger LOG = Logger.getLogger(ContentProviderNode.class);

	private ContentProvider contentProvider;
	private UriPatternMatcher<URIPattern> patternMatcher = new UriPatternMatcher<>();
	
	public static void validate(final String pattern) throws InvalidPatternException {
		boolean isValid = false;
		if(pattern != null) {
			if(!pattern.contains("*")) {
				isValid = true;
			} else {
				if(pattern.startsWith("*")) {
					if(!pattern.endsWith("*")) {
						isValid = true;
					}
				} else if(pattern.endsWith("*")) {
					isValid = true;
				}
			}
		}
		if(!isValid) {
			throw new InvalidPatternException(String.format("'%s' is an invalid pattern", pattern));
		}
	}
	
	public ContentProviderNode(final ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		if(contentProvider != null) {
			if(CollectionUtils.isNotEmpty(contentProvider.getPatternSet())) {
				for(final URIPattern pattern : contentProvider.getPatternSet()) {
					try {
						final String patten = pattern.getPattern();
						validate(patten);
						patternMatcher.register(StringUtils.lowerCase(patten), pattern);
					} catch (InvalidPatternException e) {
						LOG.error(String.format("URI Pattern %s for CP %s not valid", pattern, contentProvider), e);
					}
				}
			}
		}
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}

	public URIPattern getURIPattern(final URI uri) {
		return getURIPattern(uri.getPath());
	}
	
	public URIPattern getURIPattern(final String path) {
		if(path != null) {
			return patternMatcher.lookup(StringUtils.lowerCase(path));
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		final StringBuilder retVal = new StringBuilder();
		retVal.append("CP: ").append(contentProvider).append(ls);
		retVal.append("Matcher: ").append(patternMatcher).append(ls);
		return retVal.toString();
	}
	
	
}
