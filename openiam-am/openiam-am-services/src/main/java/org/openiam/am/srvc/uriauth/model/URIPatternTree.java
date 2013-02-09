package org.openiam.am.srvc.uriauth.model;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;

public class URIPatternTree {

	private URIPatternNode root = new URIPatternNode();
	
	public void addPattern(final URIPattern pattern) throws InvalidPatternException {
		root.addURI(pattern);
	}
	
	public URIPatternSearchResult find(final String patternURI) {
		return root.find(patternURI);
	}
	
	@Override
	public String toString() {
		return String.format("URIPatternTree [root=%s]", root);
	}
}
