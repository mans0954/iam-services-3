package org.openiam.am.srvc.uriauth.comparator;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;

public class ContentProviderNodeComparator implements Comparator<ContentProviderNode> {

	@Override
	public int compare(final ContentProviderNode node1, final ContentProviderNode node2) {
		final ContentProivderToken token1 = new ContentProivderToken(node1);
		final ContentProivderToken token2 = new ContentProivderToken(node2);
		
		if(token1.getDomain() == null || token2.getDomain() == null) {
			throw new IllegalArgumentException(String.format("A Content Provider with no Domain: %s, %s - illegal state", token1, token2));
		}
		
		if(token1.getDomain().equals(token2.getDomain())) { /* if domains are equal, this means that the distinction is SSL */
			if(token1.getIsSSL() != null && token2.getIsSSL() == null) { /* token1 more specific.  token1 > token 2 */
				return 1;
			} else if(token1.getIsSSL() == null && token2.getIsSSL() != null) { /* token2 is more specific.  token2 > token2 */
				return -1;
			} else { /* DB constraing would be violated */
				throw new IllegalArgumentException(String.format("Two identical content providers: %s, %s - illegal state", token1, token2));
			}
		} else { /* different strings */
			/* check for subdomains.  As a rule, Subdomains are more specific */
			if(token1.getDomain().endsWith(token2.getDomain())) { /* token1 is a subdomain of token 2.  Token1 > Token 2  */
				return 1;
			} else if(token2.getDomain().endsWith(token1.getDomain())) { /* token2 is a subdomain of token1.  Token2 > Token1 */
				return -1;
			} else { /* strings have nothing to do with each other */
				if(token1.getDomain().length() > token2.getDomain().length()) {
					return 1;
				} else if(token2.getDomain().length() > token1.getDomain().length()) {
					return -1;
				} else { /* equal lengths, but different Strings - DO NOT RETURN 0!!!! */
					return token1.getDomain().compareTo(token2.getDomain());
				}
			}
		}
	}

	private class ContentProivderToken {
		private String domain;
		private Boolean isSSL;
		
		private ContentProivderToken(final ContentProviderNode node) {
			final ContentProvider cp = node.getContentProvider();
			this.domain = StringUtils.lowerCase(StringUtils.trimToNull(cp.getDomainPattern()));
			this.isSSL = cp.getIsSSL();
		}

		public String getDomain() {
			return domain;
		}

		public Boolean getIsSSL() {
			return isSSL;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((domain == null) ? 0 : domain.hashCode());
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
			ContentProivderToken other = (ContentProivderToken) obj;
			if (!getOuterType().equals(other.getOuterType()))
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

		private ContentProviderNodeComparator getOuterType() {
			return ContentProviderNodeComparator.this;
		}
		
		
	}
}
