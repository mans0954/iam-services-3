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

	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		final StringBuilder retVal = new StringBuilder();
		retVal.append("CP: ").append(contentProvider).append(ls);
		retVal.append("Tree: ").append(patternTree).append(ls);
		return retVal.toString();
	}
	
	
}
