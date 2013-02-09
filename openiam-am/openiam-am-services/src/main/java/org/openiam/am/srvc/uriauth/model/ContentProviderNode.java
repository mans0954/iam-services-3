package org.openiam.am.srvc.uriauth.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;

public class ContentProviderNode {
	
	private static Logger LOG = Logger.getLogger(ContentProviderNode.class);

	private ContentProvider contentProvider;
	private URIPatternTree patternTree;
	
	public ContentProviderNode(final ContentProvider contentProvider) {
		if(contentProvider != null) {
			this.contentProvider = contentProvider;
			patternTree = new URIPatternTree();
			if(CollectionUtils.isNotEmpty(contentProvider.getPatternSet())) {
				for(final URIPattern pattern : contentProvider.getPatternSet()) {
					if(pattern != null) {
						try {
							patternTree.addPattern(pattern);
						} catch (InvalidPatternException e) {
							LOG.warn(String.format("Can't add URI Pattern: %s", pattern), e);
						}
					}
				}
			}
		}
	}
}
