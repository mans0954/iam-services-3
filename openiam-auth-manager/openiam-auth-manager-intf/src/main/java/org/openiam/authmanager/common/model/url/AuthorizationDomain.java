package org.openiam.authmanager.common.model.url;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationResource;

@Deprecated
public class AuthorizationDomain {

	private boolean isPublic;
	private String minAuthLevel;
	private boolean ssl;
	private String domain;
	private Set<AuthorizationResource> resourceSet = null;

	private URIPatternTree tree = new URIPatternTree();
	
	public AuthorizationDomain() {
	}
	
	public boolean isSSL() {
		return ssl;
	}

	public String getDomain() {
		return domain;
	}
	
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setMinAuthLevel(String minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	public void setSSL(boolean ssl) {
		this.ssl = ssl;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public URIPatternTree getTree() {
		return tree;
	}
	
	public Set<AuthorizationURIPattern> find(final String pattern) {
		return tree.find(pattern);
	}
	
	public void addPattern(final String uriPattern, final AuthorizationResource resource) throws InvalidPatternException {
		if(uriPattern != null && resource != null) {
			final AuthorizationURIPattern pattern = new AuthorizationURIPattern();
			pattern.setMinAuthLevel(minAuthLevel);
			pattern.setPattern(uriPattern);
			pattern.setPublic(isPublic);
			pattern.setResource(resource);
			tree.addPattern(pattern);
			
			if(resourceSet == null) {
				resourceSet = new HashSet<AuthorizationResource>();
			}
			resourceSet.add(resource);
		}
	}
	
	public boolean matches(final URL url) {
		boolean retVal = false;
		if(url != null) {
			final String host = StringUtils.lowerCase(StringUtils.trimToNull(url.getHost()));
			boolean isSSL = StringUtils.equalsIgnoreCase("https", url.getProtocol());
			if(!isSSL) {
				final int port = url.getPort();
				if(port == 443) {
					isSSL = true;
				}
			}
			retVal = ((domain.equals(host)) && (ssl == isSSL));
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + (ssl ? 1231 : 1237);
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
		AuthorizationDomain other = (AuthorizationDomain) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (ssl != other.ssl)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthorizationDomain [isPublic=%s, minAuthLevel=%s, ssl=%s, domain=%s, resourceSet=%s]",
						isPublic, minAuthLevel, ssl, domain, resourceSet);
	}
	
	
}
