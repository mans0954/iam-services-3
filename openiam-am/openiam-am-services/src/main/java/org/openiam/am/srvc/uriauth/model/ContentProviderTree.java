package org.openiam.am.srvc.uriauth.model;

import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.uriauth.comparator.ContentProviderSSSLComparator;

import com.mchange.v1.util.ArrayUtils;

public class ContentProviderTree {
	
	final Map<String, TreeSet<ContentProviderNode>> sortedSetMap = new HashMap<String, TreeSet<ContentProviderNode>>();
	
	final Comparator<ContentProviderNode> comparator = new ContentProviderSSSLComparator();

	public void addContentProvider(final ContentProvider contentProvider) {
		final String key = getKey(contentProvider);
		if(!sortedSetMap.containsKey(key)) {
			sortedSetMap.put(key, new TreeSet<ContentProviderNode>(comparator));
		}
		sortedSetMap.get(key).add(new ContentProviderNode(contentProvider));
	}
	
	public ContentProviderNode find(final URI uri) {
		ContentProviderNode retVal = null;
		final String key = getKey(uri);
		final TreeSet<ContentProviderNode> nodeSet = sortedSetMap.get(key);
		if(CollectionUtils.isNotEmpty(nodeSet)) {
			final boolean isSSL = StringUtils.equalsIgnoreCase("https", uri.getScheme());
			for(final Iterator<ContentProviderNode> it = nodeSet.descendingIterator(); it.hasNext();) {
				final ContentProviderNode node = it.next();
				final ContentProvider cp = node.getContentProvider();
				if(Boolean.TRUE.equals(cp.getIsSSL())) {
					if(isSSL) {
						retVal = node;
						break;
					}
				} else if(Boolean.FALSE.equals(cp.getIsSSL())) {
					if(!isSSL) {
						retVal = node;
						break;
					}
				} else {
					retVal = node;
					break;
				}
			}
		}
		return retVal;
	}
	
	private String getKey(final ContentProvider cp) {
		return getKey(cp.getDomainPattern(), cp.getContextPath());
	}
	
	private String getKey(final URI uri) {
		final String domain = uri.getHost();
		final String[] slashNodes = StringUtils.split(uri.getPath(), "/");
		final String contextPath = slashNodes.length > 0 ? slashNodes[0] : null;
		return getKey(domain, contextPath);
	}
	
	private String getKey(final String domain, final String path) {
		return new StringBuilder(domain).append(":").append(path).toString();
	}
	
	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		final StringBuilder retVal = new StringBuilder();
		retVal.append("Content Provider Tree:").append(ls).append(ls);
		for(final String key : sortedSetMap.keySet()) {
			retVal.append(key).append("=>").append(sortedSetMap.get(key));
			retVal.append(ls);
		}
		return retVal.toString();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(ArrayUtils.toString(StringUtils.split(new URI("http://www.google.com/a").getPath(), "/")));
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
