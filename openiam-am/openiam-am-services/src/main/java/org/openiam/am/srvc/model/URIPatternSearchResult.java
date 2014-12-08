package org.openiam.am.srvc.model;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMethod;

public class URIPatternSearchResult {

	private boolean uriPatternFound;
	private URIPattern pattern;
	private URIPatternMethod method;
	public boolean isUriPatternFound() {
		return uriPatternFound;
	}
	public void setUriPatternFound(boolean uriPatternFound) {
		this.uriPatternFound = uriPatternFound;
	}
	public URIPattern getPattern() {
		return pattern;
	}
	public void setPattern(URIPattern pattern) {
		this.pattern = pattern;
	}
	public URIPatternMethod getMethod() {
		return method;
	}
	public void setMethod(URIPatternMethod method) {
		this.method = method;
	}
	
	
}
