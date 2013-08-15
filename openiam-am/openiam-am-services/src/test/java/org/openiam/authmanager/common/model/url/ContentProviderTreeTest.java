package org.openiam.authmanager.common.model.url;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.testng.annotations.Test;

public class ContentProviderTreeTest {

	private static final Log log = LogFactory.getLog(ContentProviderTreeTest.class);
	
	@Test
	public void testSSLComparisons() throws URISyntaxException {
		final Map<URI, ContentProvider> cpMap = new HashMap<URI, ContentProvider>();
		cpMap.put(new URI("https://www.google.com/foo/bar.html"), getContentProvider("www.google.com", Boolean.TRUE));
		cpMap.put(new URI("https://google.com/foo/bar.html"), getContentProvider("google.com", Boolean.TRUE));
		cpMap.put(new URI("http://www.google.com/foo/bar.html"), getContentProvider("www.google.com", Boolean.FALSE));
		cpMap.put(new URI("http://google.com/foo/bar.html"), getContentProvider("google.com", Boolean.FALSE));
		cpMap.put(new URI("http://www.foobar.com/foo/bar.html"), getContentProvider("www.foobar.com", Boolean.FALSE));
		cpMap.put(new URI("https://www.foobar.com/foo/bar.html"), getContentProvider("www.foobar.com", Boolean.TRUE));
		final ContentProvider sslIgnorantCP = getContentProvider("www.dontcare.com", null);
		cpMap.put(new URI("https://www.dontcare.com/foo/bar.html"), sslIgnorantCP);
		cpMap.put(new URI("http://www.dontcare.com/foo/bar.html"), sslIgnorantCP);
		
		final ContentProviderTree tree = new ContentProviderTree();
		final Set<ContentProvider> processedSet = new HashSet<ContentProvider>();
		for(final URI uri : cpMap.keySet()) {
			final ContentProvider cp = cpMap.get(uri);
			if(!processedSet.contains(cp)) {
				tree.addContentProvider(cp);
				processedSet.add(cp);
			}
		}
		
		tree.addContentProvider(getContentProvider("google.com", null));
		for(final URI uri : cpMap.keySet()) {
			final ContentProvider cp = cpMap.get(uri);
			final ContentProviderNode node = tree.find(uri);
			Assert.assertNotNull(String.format("Null Node for URI: %s", uri), node);
			Assert.assertTrue(String.format("Should have been equal: %s, %s", cp, node.getContentProvider()), cp.equals(node.getContentProvider()));
		}
	}
	
	private ContentProvider getContentProvider(final String domain,final Boolean isSSL) {
		final ContentProvider cp = new ContentProvider();
		cp.setDomainPattern(domain);
		cp.setIsSSL(isSSL);
		/*cp.setContextPath(contextPath);*/
		cp.setId(RandomStringUtils.randomAlphabetic(5));
		cp.setResourceId(RandomStringUtils.randomAlphabetic(5));
		return cp;
	}
}
