package org.openiam.am.srvc.uriauth.model;

import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.uriauth.comparator.ContentProviderNodeComparator;

public class ContentProviderTree {
	
	final Comparator<ContentProviderNode> comparator = new ContentProviderNodeComparator();
	
	private TreeSet<ContentProviderNode> sortedSet = new TreeSet<ContentProviderNode>(comparator);

	public void addContentProvider(final ContentProvider contentProvider) {
		sortedSet.add(new ContentProviderNode(contentProvider));
	}
	
	public ContentProviderNode find(final URI uri) {
		ContentProviderNode retVal = null;
		for(final Iterator<ContentProviderNode> it = sortedSet.descendingIterator(); it.hasNext();) {
			final ContentProviderNode node = it.next();
			if(node.matches(uri)) {
				retVal = node;
				break;
			}
		}
		return retVal;
	}
	
	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		final StringBuilder retVal = new StringBuilder();
		retVal.append("Content Provider Tree:").append(ls).append(ls);
		for(final Iterator<ContentProviderNode> it = sortedSet.iterator(); it.hasNext();) {
			final ContentProviderNode node = it.next();
			retVal.append(node.getContentProvider());
		}
		return retVal.toString();
	}
}
