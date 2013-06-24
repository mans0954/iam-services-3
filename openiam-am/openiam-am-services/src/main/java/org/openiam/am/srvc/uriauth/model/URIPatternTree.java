package org.openiam.am.srvc.uriauth.model;

import java.net.URI;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;

public class URIPatternTree {

	private URIPatternNode root = new URIPatternNode();
	
	public void addPattern(final URIPattern pattern) throws InvalidPatternException {
		root.addURI(pattern);
	}
	
	public URIPatternSearchResult find(final URI uri) {
		return root.find(uri);
	}
	
	@Override
	public String toString() {
		return String.format("URIPatternTree [root=%s]", root);
	}
}
