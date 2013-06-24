package org.openiam.am.srvc.uriauth.model;

import java.util.Iterator;
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
	
	public Set<URIPattern> getFoundPatterns() {
		return foundPatterns;
	}
	
	public boolean hasPatterns() {
		return CollectionUtils.isNotEmpty(foundPatterns);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((foundPatterns == null) ? 0 : foundPatterns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternSearchResult other = (URIPatternSearchResult) obj;
		if (foundPatterns == null) {
			if (other.foundPatterns != null)
				return false;
		} else if (!foundPatterns.equals(other.foundPatterns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("URIPatternSearchResult [foundPatterns=%s]",
				foundPatterns);
	}
	
	
}
