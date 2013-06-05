package org.openiam.am.srvc.uriauth.comparator;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.am.srvc.uriauth.model.ContentProviderToken;

public class ContentProviderSSSLComparator implements Comparator<ContentProviderNode> {

	/* 
	 * a content provider unique key consists of (isSSL, contextPath, domain).  The only
	 * potential culprit, and the only reason why we use a TreeSet instead of a HashSet
	 * (hence, the Comparator), is because isSSL can be null, meanin that we 'don't care'.
	 * In this case, we have to do a 'most specific' match, as we can have a Content Provider
	 * defined with the same domain, same contextPath, but isSSL = null, and isSSL = true,
	 * which diverging patterns
	 */
	@Override
	public int compare(final ContentProviderNode node1, final ContentProviderNode node2) {
		final ContentProviderToken token1 = new ContentProviderToken(node1);
		final ContentProviderToken token2 = new ContentProviderToken(node2);
		
		if(token1.getIsSSL() == null && token2.getIsSSL() == null) {
			return 0;
		} else if(token1.getIsSSL() != null && token2.getIsSSL() == null) {
			return 1;
		} else if(token1.getIsSSL() == null && token2.getIsSSL() != null) {
			return -1;
		} else {
			return token1.getIsSSL().compareTo(token2.getIsSSL());
		}
	}
}
