package org.openiam.am.srvc.model;

import java.util.List;

import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.service.ContentProviderServiceImpl;

public class URIPatternJSONWrapper {

	private List<URIPatternEntity> patterns;
	
	public URIPatternJSONWrapper() {}

	public List<URIPatternEntity> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<URIPatternEntity> patterns) {
		this.patterns = patterns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patterns == null) ? 0 : patterns.hashCode());
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
		URIPatternJSONWrapper other = (URIPatternJSONWrapper) obj;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		} else if (!patterns.equals(other.patterns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("URIPatternJSONWrapper [patterns=%s]", patterns);
	}

	
	
}
