package org.openiam.am.srvc.uriauth.model;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;

public class ContentProviderToken {

	private String domain;
	private Boolean isSSL;
	private String contextPath;
	
	
	public ContentProviderToken(final ContentProviderNode node) {
		final ContentProvider cp = node.getContentProvider();
		this.domain = StringUtils.lowerCase(StringUtils.trimToNull(cp.getDomainPattern()));
		this.isSSL = cp.getIsSSL();
	}
	
	public ContentProviderToken(final ContentProvider contentProvider) {
		this.domain = StringUtils.lowerCase(StringUtils.trimToNull(contentProvider.getDomainPattern()));
		this.isSSL = contentProvider.getIsSSL();
		this.contextPath = StringUtils.trimToNull(contentProvider.getContextPath());
	}

	public String getDomain() {
		return domain;
	}

	public Boolean getIsSSL() {
		return isSSL;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contextPath == null) ? 0 : contextPath.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
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
		ContentProviderToken other = (ContentProviderToken) obj;
		if (contextPath == null) {
			if (other.contextPath != null)
				return false;
		} else if (!contextPath.equals(other.contextPath))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (isSSL == null) {
			if (other.isSSL != null)
				return false;
		} else if (!isSSL.equals(other.isSSL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"ContentProviderToken [domain=%s, isSSL=%s, contextPath=%s]",
				domain, isSSL, contextPath);
	}

	
}
