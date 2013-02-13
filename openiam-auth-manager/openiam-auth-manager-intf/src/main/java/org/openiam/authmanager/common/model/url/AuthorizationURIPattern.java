package org.openiam.authmanager.common.model.url;

import org.openiam.authmanager.common.model.AuthorizationResource;

@Deprecated
public class AuthorizationURIPattern {
	
	private boolean isPublic;
	private String minAuthLevel;
	private AuthorizationResource resource;
	private String pattern;

	public AuthorizationURIPattern() {

	}

	public AuthorizationResource getResource() {
		return resource;
	}

	public void setResource(AuthorizationResource resource) {
		this.resource = resource;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(String minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		AuthorizationURIPattern other = (AuthorizationURIPattern) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthorizationURIPattern [isPublic=%s, minAuthLevel=%s, resource=%s, pattern=%s]",
						isPublic, minAuthLevel, resource, pattern);
	}


}
