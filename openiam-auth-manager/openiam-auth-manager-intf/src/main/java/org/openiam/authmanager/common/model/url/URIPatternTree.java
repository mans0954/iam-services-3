package org.openiam.authmanager.common.model.url;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.common.model.AuthorizationResource;

public class URIPatternTree {

	private URIPatternNode root = new URIPatternNode();
	
	public void addPattern(final AuthorizationURIPattern pattern) throws InvalidPatternException {
		root.addURI(pattern);
	}
	
	public Set<AuthorizationURIPattern> find(final String pattern) {
		final Set<AuthorizationURIPattern> retVal = root.find(pattern);
		return (CollectionUtils.isNotEmpty(retVal)) ? new HashSet<AuthorizationURIPattern>(retVal) : null;
	}
	
	@Override
	public String toString() {
		return String.format("URIPatternTree [root=%s]", root);
	}
}
