package org.openiam.am.srvc.uriauth.model;

import java.net.URI;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;

public class ContentProviderNode {
	
	private static Logger LOG = Logger.getLogger(ContentProviderNode.class);

	private ContentProvider contentProvider;
	private URIPatternTree patternTree;
	
	public ContentProviderNode(final ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		if(contentProvider != null) {
			if(CollectionUtils.isNotEmpty(contentProvider.getPatternSet())) {
				patternTree = new URIPatternTree();
				for(final URIPattern pattern : contentProvider.getPatternSet()) {
					try {
						patternTree.addPattern(pattern);
					} catch (InvalidPatternException e) {
						LOG.warn(String.format("URI Pattern %s for CP %s not valid", pattern, contentProvider), e);
					}
				}
			}
		}
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}

	public URIPatternTree getPatternTree() {
		return patternTree;
	}
	
	public boolean matches(final URI uri) {
		final boolean isSSL = StringUtils.equalsIgnoreCase("https", uri.getScheme());
		final int port = uri.getPort();
		final StringBuilder domain = new StringBuilder(uri.getHost());
		if(port != -1 && port != 80 && port != 443) {
			domain.append(":").append(port);
		}
		
		boolean matchesSSL = false;
		if(contentProvider.getIsSSL() == null) {
			matchesSSL = true;
		} else {
			matchesSSL = contentProvider.getIsSSL().equals(Boolean.valueOf(isSSL));
		}
		
		boolean matchesDomain = contentProvider.getDomainPattern().equals(domain);
		
		return (matchesSSL && matchesDomain);
	}
}
