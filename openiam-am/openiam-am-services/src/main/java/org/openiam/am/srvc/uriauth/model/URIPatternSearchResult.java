package org.openiam.am.srvc.uriauth.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.URIPattern;

public class URIPatternSearchResult {

	private Set<URIPattern> foundPatterns;
	
	public void addPattern(final URIPattern pattern) {
		if(pattern != null) {
			if(foundPatterns == null) {
				foundPatterns = new LinkedHashSet<URIPattern>();
			}
			foundPatterns.add(pattern);
		}
	}
	
	public void addPatterns(final Set<URIPattern> patternSet) {
		if(CollectionUtils.isNotEmpty(patternSet)) {
			if(foundPatterns == null) {
				foundPatterns = new LinkedHashSet<URIPattern>();
			}
			for(final URIPattern pattern : patternSet) {
				if(pattern != null) {
					foundPatterns.add(pattern);
				}
			}
		}
	}
	
	public boolean hasPatterns() {
		return CollectionUtils.isNotEmpty(foundPatterns);
	}
}
