package org.openiam.thread;

import java.util.Collection;

public interface BatchDatabaseProcess<T> {
	public void process(final Collection<T> collection);
}
